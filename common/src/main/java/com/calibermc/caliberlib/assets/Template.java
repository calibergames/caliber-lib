package com.calibermc.caliberlib.assets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A JSON file with placeholders, compiled once into text pieces. A string
 * value that starts with {@code @} is a placeholder; rendering puts in its
 * value as an escaped JSON string. There are no expressions.
 */
public final class Template {

	private final List<String> text;
	private final List<String> keys;

	private Template(List<String> text, List<String> keys) {
		this.text = text;
		this.keys = keys;
	}

	public static Template compile(JsonElement json) {
		Compiler c = new Compiler();
		c.write(json);
		c.text.add(c.current.toString());
		return new Template(List.copyOf(c.text), List.copyOf(c.keys));
	}

	/** The placeholder names, without the {@code @}. */
	public Set<String> keys() {
		return new LinkedHashSet<>(keys);
	}

	/**
	 * Renders the file. {@code values} gives each placeholder's value; a
	 * missing value is an error.
	 */
	public byte[] render(Function<String, String> values) {
		StringBuilder out = new StringBuilder(text.get(0).length() * (keys.size() + 1));
		for (int i = 0; i < keys.size(); i++) {
			out.append(text.get(i));
			String value = values.apply(keys.get(i));
			if (value == null) {
				throw new IllegalArgumentException("No value for @" + keys.get(i));
			}
			quote(value, out);
		}
		out.append(text.get(keys.size()));
		return out.toString().getBytes(StandardCharsets.UTF_8);
	}

	/** Writes a JSON string literal. */
	static void quote(String value, StringBuilder out) {
		out.append('"');
		for (int i = 0; i < value.length(); i++) {
			char ch = value.charAt(i);
			switch (ch) {
				case '"' -> out.append("\\\"");
				case '\\' -> out.append("\\\\");
				case '\n' -> out.append("\\n");
				case '\r' -> out.append("\\r");
				case '\t' -> out.append("\\t");
				default -> {
					if (ch < 0x20 || ch == ' ' || ch == ' ') {
						out.append(String.format("\\u%04x", (int) ch));
					} else {
						out.append(ch);
					}
				}
			}
		}
		out.append('"');
	}

	private static final class Compiler {
		final List<String> text = new ArrayList<>();
		final List<String> keys = new ArrayList<>();
		StringBuilder current = new StringBuilder();

		void write(JsonElement json) {
			if (json.isJsonObject()) {
				current.append('{');
				boolean first = true;
				for (Map.Entry<String, JsonElement> e : ((JsonObject) json).entrySet()) {
					if (!first) {
						current.append(',');
					}
					first = false;
					quote(e.getKey(), current);
					current.append(':');
					write(e.getValue());
				}
				current.append('}');
			} else if (json.isJsonArray()) {
				current.append('[');
				boolean first = true;
				for (JsonElement e : (JsonArray) json) {
					if (!first) {
						current.append(',');
					}
					first = false;
					write(e);
				}
				current.append(']');
			} else if (json.isJsonNull()) {
				current.append("null");
			} else {
				JsonPrimitive p = (JsonPrimitive) json;
				if (p.isString() && p.getAsString().startsWith("@")) {
					text.add(current.toString());
					keys.add(p.getAsString().substring(1));
					current = new StringBuilder();
				} else if (p.isString()) {
					quote(p.getAsString(), current);
				} else {
					current.append(p);
				}
			}
		}
	}
}
