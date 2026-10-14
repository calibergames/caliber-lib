package com.calibermc.caliberlib.neoforge;

import com.calibermc.caliberlib.compat.CompatLists;
import com.calibermc.caliberlib.compat.CompatLogin;
import net.minecraftforge.network.NetworkRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * The compat list's login check on NeoForge and Forge: a channel whose
 * version is the list's hash. Forge compares channel versions with the mod
 * list, before any registry data, so a client whose list differs is
 * refused on the mismatched channel {@code caliberlib:compat} rather than on
 * missing registry entries. A game without compat shapes accepts a side
 * without the channel.
 */
final class NeoForgeCompatLogin {

	private static final Logger LOGGER = LoggerFactory.getLogger("CaliberLib/compat");
	private static final Set<String> LOGGED = new HashSet<>();

	private NeoForgeCompatLogin() {
	}

	/** Creates the channel; once every block is registered (the hash is final) and before the network locks. */
	static void register() {
		String ours = CompatLists.hash();
		boolean active = CompatLists.active();
		Predicate<String> accept = version -> {
			if (version.equals(ours)) {
				return true;
			}
			boolean absent = version.equals(NetworkRegistry.ABSENT.version()) || version.equals(NetworkRegistry.ACCEPTVANILLA);
			if (!absent) {
				logOnce(version, ours);
			}
			return absent && !active;
		};
		NetworkRegistry.newSimpleChannel(CompatLogin.CHANNEL, () -> ours, accept, accept);
	}

	private static synchronized void logOnce(String theirs, String ours) {
		if (LOGGED.add(theirs)) {
			LOGGER.warn("Caliber compat lists differ: the other side's list hash is {}, this game's {}. The mods or the Caliber "
					+ "compat settings differ. This game's entries:\n  {}", theirs, ours, String.join("\n  ", CompatLists.canonical()));
		}
	}
}
