package com.calibermc.caliberlib.platform;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

/**
 * A loaded mod's own files, read before data packs load (e.g. compat lists
 * inside mod jars).
 *
 * @param modId  the mod
 * @param finder for a path such as {@code data/x/file.json}, the existing files or folders at that path in
 *               the mod's file (several in a development run, where a mod has more than one root)
 */
public record ModFiles(String modId, Function<String, List<Path>> finder) {

	public List<Path> find(String relative) {
		return finder.apply(relative);
	}
}
