package com.calibermc.caliberlib.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A TOML config file with defined keys. Loading writes a missing file with
 * every default and its comment, and adds the keys an existing file lacks
 * to their tables, leaving every other line as it was. A file that does not
 * parse is reported by line and left alone, and the defaults apply.
 */
public final class ConfigFile {

	private static final Logger LOGGER = LoggerFactory.getLogger("CaliberLib/config");

	private record Entry(String table, String key, Object value, String comment) {
		String path() {
			return table.isEmpty() ? key : table + "." + key;
		}
	}

	private final Path path;
	private final Map<String, Entry> entries = new LinkedHashMap<>();
	private final Map<String, String> tableComments = new LinkedHashMap<>();
	private Map<String, Object> values = Map.of();

	public ConfigFile(Path path) {
		this.path = path;
	}

	/** Defines a key in {@code table} ("" for the top level; dotted for nested tables). */
	public ConfigFile define(String table, String key, Object value, String comment) {
		if (!(value instanceof String || value instanceof Boolean || value instanceof Long || value instanceof Double
				|| value instanceof List<?> list && list.stream().allMatch(String.class::isInstance))) {
			throw new IllegalArgumentException("Unsupported default for " + key + ": " + value);
		}
		Entry e = new Entry(table, key, value, comment);
		entries.put(e.path(), e);
		return this;
	}

	/** A comment written above the table's header when the table is created. */
	public ConfigFile comment(String table, String comment) {
		tableComments.put(table, comment);
		return this;
	}

	/** Reads the file, writing what it lacks. Never throws: problems are logged and the defaults apply. */
	public ConfigFile load() {
		Map<String, Object> read = Map.of();
		List<String> lines;
		Map<String, Integer> tableEnds;
		String newline;
		try {
			String text = Files.exists(path) ? Files.readString(path, StandardCharsets.UTF_8) : "";
			newline = text.contains("\r\n") ? "\r\n" : "\n";
			Toml.Document doc = Toml.parse(text);
			read = doc.values();
			lines = new ArrayList<>(doc.lines());
			tableEnds = doc.tableEnds();
		} catch (Toml.TomlException e) {
			LOGGER.error("{} is not valid ({}); using the defaults and leaving the file as it is", path, e.getMessage());
			values = Map.of();
			return this;
		} catch (IOException | RuntimeException e) {
			LOGGER.error("Cannot read {}; using the defaults", path, e);
			values = Map.of();
			return this;
		}
		Map<String, Object> found = read;
		values = found;
		List<Entry> missing = entries.values().stream().filter(e -> !found.containsKey(e.path())).toList();
		if (!missing.isEmpty()) {
			write(lines, tableEnds, missing, newline);
		}
		return this;
	}

	private void write(List<String> lines, Map<String, Integer> tableEnds, List<Entry> missing, String newline) {
		// Insert from the bottom up, so earlier line numbers stay valid.
		Map<Integer, List<String>> inserts = new TreeMap<>((a, b) -> b - a);
		Map<String, List<Entry>> newTables = new LinkedHashMap<>();
		for (Entry e : missing) {
			Integer end = tableEnds.get(e.table());
			if (end != null && (end > 0 || e.table().isEmpty() && !lines.isEmpty())) {
				inserts.computeIfAbsent(end, k -> new ArrayList<>()).addAll(render(e));
			} else if (e.table().isEmpty()) {
				inserts.computeIfAbsent(0, k -> new ArrayList<>()).addAll(render(e));
			} else {
				newTables.computeIfAbsent(e.table(), k -> new ArrayList<>()).add(e);
			}
		}
		inserts.forEach((after, added) -> {
			List<String> block = new ArrayList<>(added);
			lines.addAll(after, block);
		});
		for (Map.Entry<String, List<Entry>> t : newTables.entrySet()) {
			if (!lines.isEmpty() && !lines.get(lines.size() - 1).isBlank()) {
				lines.add("");
			}
			String comment = tableComments.get(t.getKey());
			if (comment != null) {
				comment.lines().forEach(l -> lines.add("# " + l));
			}
			lines.add("[" + tableName(t.getKey()) + "]");
			for (Entry e : t.getValue()) {
				lines.addAll(render(e));
			}
		}
		try {
			// Write beside the file and move it into place, so a crash mid-write never leaves half a file.
			Files.createDirectories(path.toAbsolutePath().getParent());
			Path temp = path.resolveSibling(path.getFileName() + ".tmp");
			Files.writeString(temp, String.join(newline, lines) + newline, StandardCharsets.UTF_8);
			try {
				Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
			} catch (AtomicMoveNotSupportedException e) {
				Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			LOGGER.error("Cannot write {}", path, e);
		}
	}

	private static String tableName(String table) {
		List<String> parts = new ArrayList<>();
		for (String p : table.split("\\.")) {
			parts.add(Toml.key(p));
		}
		return String.join(".", parts);
	}

	private static List<String> render(Entry e) {
		List<String> out = new ArrayList<>();
		if (e.comment() != null && !e.comment().isEmpty()) {
			e.comment().lines().forEach(l -> out.add("# " + l));
		}
		out.add(Toml.key(e.key()) + " = " + Toml.value(e.value()));
		return out;
	}

	@Nullable
	private Object raw(String path) {
		return values.get(path);
	}

	private Entry entry(String path) {
		Entry e = entries.get(path);
		if (e == null) {
			throw new IllegalArgumentException("Undefined config key " + path);
		}
		return e;
	}

	public boolean getBoolean(String path) {
		Object v = raw(path);
		return v instanceof Boolean b ? b : warnDefault(path, v, Boolean.class);
	}

	public long getLong(String path) {
		Object v = raw(path);
		return v instanceof Long l ? l : warnDefault(path, v, Long.class);
	}

	public String getString(String path) {
		Object v = raw(path);
		return v instanceof String s ? s : warnDefault(path, v, String.class);
	}

	/** Every boolean key in a table, defined or not (for open lists such as per-mod switches). */
	public Map<String, Boolean> booleans(String table) {
		Map<String, Boolean> out = new TreeMap<>();
		String prefix = table + ".";
		entries.values().stream().filter(e -> e.table().equals(table) && e.value() instanceof Boolean)
				.forEach(e -> out.put(e.key(), (Boolean) e.value()));
		values.forEach((k, v) -> {
			if (k.startsWith(prefix) && !k.substring(prefix.length()).contains(".")) {
				if (v instanceof Boolean b) {
					out.put(k.substring(prefix.length()), b);
				} else {
					LOGGER.warn("{}: {} should be true or false; ignored", path, k);
				}
			}
		});
		return out;
	}

	private <T> T warnDefault(String key, @Nullable Object found, Class<T> type) {
		Entry e = entry(key);
		if (found != null) {
			LOGGER.warn("{}: {} should be a {}; using the default {}", path, key, type.getSimpleName().toLowerCase(), e.value());
		}
		return type.cast(e.value());
	}
}
