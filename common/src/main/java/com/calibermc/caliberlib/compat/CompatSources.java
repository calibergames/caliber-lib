package com.calibermc.caliberlib.compat;

import com.calibermc.caliberlib.platform.ModFiles;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * Reads the compat files in load order: the owner's bundled folder (its
 * {@code released-*.json} files first, so released ids are planned before
 * anything else, then the others, each group sorted by name), then {@code data/<modid>/calibermod-compat.json} in each
 * mod's own file (sorted by mod id), then the config folder (sorted by
 * name).
 */
public final class CompatSources {

	public static final String FILE_NAME = "calibermod-compat.json";
	public static final String CONFIG_FOLDER = "calibermod-compat";

	private CompatSources() {
	}

	public static List<CompatEntry> read(String owner, String bundledFolder, List<ModFiles> mods, Path configDir, CompatLog log) {
		List<CompatEntry> out = new ArrayList<>();
		for (ModFiles mod : mods) {
			if (mod.modId().equals(owner)) {
				Map<String, Path> files = jsonFiles(mod.find(bundledFolder), log);
				for (boolean released : new boolean[]{true, false}) {
					for (Map.Entry<String, Path> f : files.entrySet()) {
						if (f.getKey().startsWith("released-") == released) {
							read(f.getValue(), CompatSource.bundled(f.getKey()), out, log);
						}
					}
				}
			}
		}
		for (ModFiles mod : mods) {
			for (Path p : mod.find("data/" + mod.modId() + "/" + FILE_NAME)) {
				read(p, CompatSource.jar(mod.modId()), out, log);
				break;
			}
		}
		Path config = configDir.resolve(CONFIG_FOLDER);
		if (Files.isDirectory(config)) {
			for (Map.Entry<String, Path> f : jsonFiles(List.of(config), log).entrySet()) {
				read(f.getValue(), CompatSource.config(f.getKey()), out, log);
			}
		}
		return out;
	}

	/** The {@code .json} files directly in these folders, by name; a name in two roots counts once (the first). */
	private static Map<String, Path> jsonFiles(List<Path> folders, CompatLog log) {
		Map<String, Path> out = new TreeMap<>();
		for (Path folder : folders) {
			if (!Files.isDirectory(folder)) {
				continue;
			}
			// Read the listing at once: some loaders' file systems give a listing that can be walked only once.
			List<Path> listed;
			try (Stream<Path> s = Files.list(folder)) {
				listed = s.toList();
			} catch (IOException e) {
				log.error("Cannot list " + folder + ": " + e.getMessage());
				continue;
			}
			for (Path p : listed) {
				String name = p.getFileName().toString().replace("/", "");
				if (name.endsWith(".json") && Files.isRegularFile(p)) {
					out.putIfAbsent(name, p);
				}
			}
		}
		return out;
	}

	private static void read(Path file, CompatSource source, List<CompatEntry> out, CompatLog log) {
		try {
			out.addAll(CompatParser.parse(Files.readString(file, StandardCharsets.UTF_8), source, log));
		} catch (IOException e) {
			log.error(source + ": cannot read (" + e.getMessage() + "); skipped");
		}
	}
}
