package com.calibermc.caliberlib.compat;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Warns when a world holds compat shapes that are not registered now. The
 * game turns such blocks into air as their chunks load, without saying why;
 * this names the entry and the setting before that happens. A small record
 * in the world folder holds what the world's last session registered.
 */
public final class CompatWorldCheck {

	public static final String RECORD = "caliberlib-compat.txt";

	private CompatWorldCheck() {
	}

	/**
	 * The warnings for a world whose last session registered {@code recorded}
	 * (canonical entries) when this game registers {@code current}.
	 */
	public static List<String> check(List<String> recorded, List<String> current, Map<CompatEntry.Key, String> notRegistered) {
		Map<CompatEntry.Key, JsonObject> now = new HashMap<>();
		for (String c : current) {
			JsonObject o = JsonParser.parseString(c).getAsJsonObject();
			now.put(key(o), o);
		}
		List<String> out = new ArrayList<>();
		for (String c : recorded) {
			if (!readable(c)) {
				continue;
			}
			JsonObject was = JsonParser.parseString(c).getAsJsonObject();
			CompatEntry.Key key = key(was);
			JsonObject is = now.get(key);
			if (is == null) {
				out.add(key + " is not registered now: " + notRegistered.getOrDefault(key, "no compat file lists it any more"));
				continue;
			}
			Set<String> lost = new TreeSet<>();
			if (was.get("name").equals(is.get("name"))) {
				was.getAsJsonArray("variants").forEach(v -> lost.add(v.getAsString()));
				is.getAsJsonArray("variants").forEach(v -> lost.remove(v.getAsString()));
			} else {
				lost.add("all (its name changed from " + was.get("name").getAsString() + " to " + is.get("name").getAsString() + ")");
			}
			if (!lost.isEmpty()) {
				out.add(key + " no longer registers these shapes: " + String.join(", ", lost));
			}
		}
		return out;
	}

	/** Checks the world against the record, warns, and records what this game registers. */
	public static void worldLoading(Path worldDir, List<String> current, Map<CompatEntry.Key, String> notRegistered) {
		Path record = worldDir.resolve(RECORD);
		List<String> recorded = new ArrayList<>();
		try {
			if (Files.exists(record)) {
				for (String line : Files.readAllLines(record, StandardCharsets.UTF_8)) {
					if (!line.startsWith("{")) {
						continue;
					}
					if (readable(line)) {
						recorded.add(line);
					} else {
						CompatLog.LOGGER.warn("{}: an unreadable line is left out of the check: {}", record, line);
					}
				}
			}
		} catch (IOException e) {
			CompatLog.LOGGER.warn("Cannot read {}: {}", record, e.getMessage());
		}
		List<String> warnings = check(recorded, current, notRegistered);
		if (!warnings.isEmpty()) {
			CompatLog.LOGGER.warn("This world holds Caliber compat shapes that this game does not register. Their blocks turn into air "
					+ "as their chunks load. To keep them, stop now and register them again:\n  {}", String.join("\n  ", warnings));
		}
		if (recorded.isEmpty() && current.isEmpty()) {
			return;
		}
		try {
			Files.writeString(record, "# The Caliber compat entries this world's last session registered.\n"
					+ String.join("\n", current) + (current.isEmpty() ? "" : "\n"), StandardCharsets.UTF_8);
		} catch (IOException e) {
			CompatLog.LOGGER.warn("Cannot write {}: {}", record, e.getMessage());
		}
	}

	/** Whether a record line has the canonical form (a hand-edited or cut-off line does not). */
	static boolean readable(String line) {
		try {
			JsonObject o = JsonParser.parseString(line).getAsJsonObject();
			key(o);
			o.get("name").getAsString();
			o.getAsJsonArray("variants").forEach(JsonElement::getAsString);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}

	private static CompatEntry.Key key(JsonObject o) {
		return new CompatEntry.Key(new ResourceLocation(o.get("block").getAsString()), o.get("type").getAsString());
	}
}
