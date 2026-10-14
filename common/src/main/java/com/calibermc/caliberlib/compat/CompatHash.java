package com.calibermc.caliberlib.compat;

import com.calibermc.caliberlib.data.ModBlockFamily.Variant;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.TreeMap;

/**
 * The compat list's fingerprint, which server and client compare at login:
 * SHA-256 over the canonical form of every planned entry, sorted by block
 * and type. It covers what registers (block, type, name, shapes), not
 * textures, which change only how a client draws the shapes.
 */
public final class CompatHash {

	private CompatHash() {
	}

	/** One entry's canonical form: fixed key order, shapes sorted. */
	public static String canonical(CompatPlan.Planned p) {
		List<String> variants = new ArrayList<>();
		for (Variant v : p.variants()) {
			variants.add(v.getName());
		}
		variants.sort(null);
		return "{\"block\":\"" + p.entry().block() + "\",\"type\":\"" + p.entry().type() + "\",\"name\":\"" + p.name()
				+ "\",\"variants\":[\"" + String.join("\",\"", variants) + "\"]}";
	}

	/** The entries' canonical forms, sorted by block and type. */
	public static List<String> canonicalList(List<CompatPlan.Planned> planned) {
		TreeMap<String, String> sorted = new TreeMap<>();
		for (CompatPlan.Planned p : planned) {
			sorted.put(p.entry().block() + " " + p.entry().type(), canonical(p));
		}
		return List.copyOf(sorted.values());
	}

	/** The list's hash, hex. The empty list has a hash too. */
	public static String of(List<CompatPlan.Planned> planned) {
		MessageDigest sha = sha256();
		for (String line : canonicalList(planned)) {
			sha.update(line.getBytes(StandardCharsets.UTF_8));
			sha.update((byte) '\n');
		}
		return HexFormat.of().formatHex(sha.digest());
	}

	/** Eight bytes per entry, in the sorted order: enough to tell which entries differ between two lists. */
	public static List<Long> shortHashes(List<CompatPlan.Planned> planned) {
		List<Long> out = new ArrayList<>();
		for (String line : canonicalList(planned)) {
			out.add(shortHash(line));
		}
		out.sort(Comparator.naturalOrder());
		return out;
	}

	public static long shortHash(String canonical) {
		byte[] d = sha256().digest(canonical.getBytes(StandardCharsets.UTF_8));
		long v = 0;
		for (int i = 0; i < 8; i++) {
			v = (v << 8) | (d[i] & 0xff);
		}
		return v;
	}

	private static MessageDigest sha256() {
		try {
			return MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 is part of every Java runtime", e);
		}
	}
}
