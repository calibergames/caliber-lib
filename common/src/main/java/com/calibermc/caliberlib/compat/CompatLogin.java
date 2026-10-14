package com.calibermc.caliberlib.compat;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The login check of the compat list on loaders that check it with a login
 * query (Fabric and Quilt): the server sends its list's hash and one short
 * hash per entry; the client answers with its own; on a difference the
 * server refuses the login with a readable reason, before the registries
 * are compared, and both sides log which entries differ.
 */
public final class CompatLogin {

	public static final ResourceLocation CHANNEL = new ResourceLocation("caliberlib", "compat");

	private CompatLogin() {
	}

	/** This game's list: its hash and short hashes. */
	public static void write(FriendlyByteBuf buf) {
		buf.writeUtf(CompatLists.hash());
		List<Long> shorts = CompatLists.shortHashes();
		buf.writeVarInt(shorts.size());
		shorts.forEach(buf::writeLong);
	}

	/** The other side's list. */
	public record Remote(String hash, Set<Long> shortHashes) {
		public static Remote read(FriendlyByteBuf buf) {
			String hash = buf.readUtf();
			int n = buf.readVarInt();
			Set<Long> shorts = new HashSet<>();
			for (int i = 0; i < n; i++) {
				shorts.add(buf.readLong());
			}
			return new Remote(hash, shorts);
		}

		public boolean matches() {
			return hash.equals(CompatLists.hash());
		}
	}

	/** Logs the entries this side has and the other does not; returns how many differ each way (here, there). */
	public static int[] logDifference(Remote remote, String here, String there) {
		Map<Long, String> ours = new HashMap<>();
		for (String canonical : CompatLists.canonical()) {
			ours.put(CompatHash.shortHash(canonical), canonical);
		}
		List<String> onlyHere = new ArrayList<>();
		ours.forEach((h, c) -> {
			if (!remote.shortHashes().contains(h)) {
				onlyHere.add(c);
			}
		});
		int onlyThere = 0;
		for (Long h : remote.shortHashes()) {
			if (!ours.containsKey(h)) {
				onlyThere++;
			}
		}
		onlyHere.sort(null);
		CompatLog.LOGGER.warn("Caliber compat lists differ: {} only on the {}, {} only on the {}{}", entries(onlyHere.size()), here,
				onlyThere, there, onlyHere.isEmpty() ? "" : "; only on the " + here + ":\n  " + String.join("\n  ", onlyHere));
		return new int[]{onlyHere.size(), onlyThere};
	}

	/** The reason the server gives a client whose list differs. */
	public static Component differs(int onlyServer, int onlyClient) {
		return Component.literal("Caliber compat lists differ from this server's (" + entries(onlyServer) + " only on the server, "
				+ onlyClient + " only in your game). The mods or the Caliber compat settings differ; see the logs for which.");
	}

	private static String entries(int n) {
		return n + (n == 1 ? " entry" : " entries");
	}

	/** The reason when only the lists' hashes are known (NeoForge and Forge compare a channel version). */
	public static Component differs() {
		return Component.literal("Caliber compat lists differ from this server's. The mods or the Caliber compat settings differ; "
				+ "see the logs for which.");
	}

	/** The reason the server gives a client that does not check compat lists at all. */
	public static Component missing() {
		return Component.literal("This server adds Caliber shapes for other mods' blocks (calibermod-compat). Join with the same mods as the server.");
	}
}
