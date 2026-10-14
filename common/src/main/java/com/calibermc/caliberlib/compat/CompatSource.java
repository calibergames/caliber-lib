package com.calibermc.caliberlib.compat;

import javax.annotation.Nullable;

/**
 * Where a compat file came from, in load order: the owner's bundled folder,
 * then other mods' jars, then the config folder.
 *
 * @param kind     the kind of source
 * @param label    the file, for logs (e.g. "biomesoplenty jar" or "config/calibermod-compat/mine.json")
 * @param modId    for a jar file, the mod whose jar it is; its entries may name only that mod's blocks
 * @param released for a bundled {@code released-*.json} file: entries that hold released ids
 */
public record CompatSource(Kind kind, String label, @Nullable String modId, boolean released) {

	public enum Kind {
		BUNDLED("bundled"), JAR("jars"), CONFIG("config");

		final String summary;

		Kind(String summary) {
			this.summary = summary;
		}
	}

	public static CompatSource bundled(String fileName) {
		return new CompatSource(Kind.BUNDLED, "bundled " + fileName, null, fileName.startsWith("released-"));
	}

	public static CompatSource jar(String modId) {
		return new CompatSource(Kind.JAR, modId + " jar", modId, false);
	}

	public static CompatSource config(String fileName) {
		return new CompatSource(Kind.CONFIG, "config/calibermod-compat/" + fileName, null, false);
	}

	@Override
	public String toString() {
		return label;
	}
}
