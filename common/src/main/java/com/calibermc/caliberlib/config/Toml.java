package com.calibermc.caliberlib.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The part of TOML that config files use: tables (also with quoted names),
 * {@code key = value} lines with bare or quoted keys, comments, and values
 * that are strings, booleans, integers, floats or one-line arrays of
 * strings. Anything else is an error naming its line.
 */
public final class Toml {

	/** A parsed file: values by key path ("compat.mods.chipped"), and where each table's lines end. */
	public record Document(Map<String, Object> values, Map<String, Integer> tableEnds, List<String> lines) {
	}

	/** A line the parser does not accept. */
	public static final class TomlException extends Exception {
		public final int line;

		TomlException(int line, String message) {
			super("line " + line + ": " + message);
			this.line = line;
		}
	}

	private Toml() {
	}

	public static Document parse(String text) throws TomlException {
		List<String> lines = text.isEmpty() ? new ArrayList<>() : new ArrayList<>(List.of(text.split("\r?\n", -1)));
		if (!lines.isEmpty() && lines.get(lines.size() - 1).isEmpty()) {
			lines.remove(lines.size() - 1);
		}
		Map<String, Object> values = new LinkedHashMap<>();
		Map<String, Integer> tableEnds = new LinkedHashMap<>();
		String table = "";
		tableEnds.put(table, 0);
		for (int i = 0; i < lines.size(); i++) {
			int number = i + 1;
			Cursor c = new Cursor(lines.get(i), number);
			c.space();
			if (c.end() || c.peek() == '#') {
				continue;
			}
			if (c.peek() == '[') {
				c.next();
				if (!c.end() && c.peek() == '[') {
					throw new TomlException(number, "arrays of tables are not supported");
				}
				table = String.join(".", c.keyPath());
				c.space();
				c.expect(']');
				c.rest();
				if (tableEnds.containsKey(table) && !table.isEmpty()) {
					throw new TomlException(number, "table [" + table + "] defined twice");
				}
				tableEnds.put(table, number);
				continue;
			}
			List<String> key = c.keyPath();
			c.space();
			c.expect('=');
			c.space();
			Object value = c.value();
			c.rest();
			String path = table.isEmpty() ? String.join(".", key) : table + "." + String.join(".", key);
			if (values.containsKey(path)) {
				throw new TomlException(number, "key " + path + " defined twice");
			}
			values.put(path, value);
			tableEnds.put(table, number);
		}
		return new Document(values, tableEnds, lines);
	}

	/** A key or table name as the file writes it: bare when it can be, quoted otherwise. */
	public static String key(String part) {
		return part.matches("[A-Za-z0-9_-]+") ? part : quote(part);
	}

	/** A value as the file writes it. */
	public static String value(Object value) {
		if (value instanceof String s) {
			return quote(s);
		}
		if (value instanceof List<?> list) {
			List<String> out = new ArrayList<>();
			for (Object o : list) {
				out.add(quote((String) o));
			}
			return "[" + String.join(", ", out) + "]";
		}
		return String.valueOf(value);
	}

	private static String quote(String s) {
		StringBuilder b = new StringBuilder("\"");
		for (char ch : s.toCharArray()) {
			switch (ch) {
				case '"' -> b.append("\\\"");
				case '\\' -> b.append("\\\\");
				case '\n' -> b.append("\\n");
				case '\t' -> b.append("\\t");
				default -> b.append(ch);
			}
		}
		return b.append('"').toString();
	}

	private static final class Cursor {
		private final String s;
		private final int line;
		private int i;

		Cursor(String s, int line) {
			this.s = s;
			this.line = line;
		}

		boolean end() {
			return i >= s.length();
		}

		char peek() {
			return s.charAt(i);
		}

		char next() {
			return s.charAt(i++);
		}

		void space() {
			while (!end() && (peek() == ' ' || peek() == '\t')) {
				i++;
			}
		}

		void expect(char ch) throws TomlException {
			if (end() || next() != ch) {
				throw new TomlException(line, "expected '" + ch + "'");
			}
		}

		/** Only a comment may follow. */
		void rest() throws TomlException {
			space();
			if (!end() && peek() != '#') {
				throw new TomlException(line, "unexpected text: " + s.substring(i).trim());
			}
		}

		List<String> keyPath() throws TomlException {
			List<String> parts = new ArrayList<>();
			do {
				space();
				if (end()) {
					throw new TomlException(line, "missing key");
				}
				if (peek() == '"') {
					parts.add(basicString());
				} else if (peek() == '\'') {
					parts.add(literalString());
				} else {
					int start = i;
					while (!end() && (Character.isLetterOrDigit(peek()) || peek() == '_' || peek() == '-')) {
						i++;
					}
					if (start == i) {
						throw new TomlException(line, "bad key");
					}
					parts.add(s.substring(start, i));
				}
				space();
			} while (!end() && peek() == '.' && next() == '.');
			return parts;
		}

		Object value() throws TomlException {
			if (end()) {
				throw new TomlException(line, "missing value");
			}
			char ch = peek();
			if (ch == '"') {
				return basicString();
			}
			if (ch == '\'') {
				return literalString();
			}
			if (ch == '[') {
				next();
				List<String> list = new ArrayList<>();
				space();
				while (!end() && peek() != ']') {
					Object v = value();
					if (!(v instanceof String str)) {
						throw new TomlException(line, "only arrays of strings are supported");
					}
					list.add(str);
					space();
					if (!end() && peek() == ',') {
						next();
						space();
					}
				}
				expect(']');
				return list;
			}
			int start = i;
			while (!end() && peek() != ' ' && peek() != '\t' && peek() != '#' && peek() != ',' && peek() != ']') {
				i++;
			}
			String word = s.substring(start, i);
			if (word.equals("true") || word.equals("false")) {
				return Boolean.parseBoolean(word);
			}
			String number = word.replace("_", "");
			try {
				if (number.matches("[+-]?\\d+")) {
					return Long.parseLong(number);
				}
				if (number.matches("[+-]?(\\d+\\.\\d+([eE][+-]?\\d+)?|\\d+[eE][+-]?\\d+)")) {
					return Double.parseDouble(number);
				}
			} catch (NumberFormatException e) {
				throw new TomlException(line, "bad number: " + word);
			}
			throw new TomlException(line, "unsupported value: " + word);
		}

		private String basicString() throws TomlException {
			expect('"');
			StringBuilder b = new StringBuilder();
			while (true) {
				if (end()) {
					throw new TomlException(line, "unterminated string");
				}
				char ch = next();
				if (ch == '"') {
					return b.toString();
				}
				if (ch == '\\') {
					if (end()) {
						throw new TomlException(line, "unterminated string");
					}
					char e = next();
					switch (e) {
						case '"' -> b.append('"');
						case '\\' -> b.append('\\');
						case 'n' -> b.append('\n');
						case 't' -> b.append('\t');
						case 'r' -> b.append('\r');
						case 'u' -> {
							if (i + 4 > s.length()) {
								throw new TomlException(line, "bad escape");
							}
							try {
								b.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
							} catch (NumberFormatException bad) {
								throw new TomlException(line, "bad escape \\u" + s.substring(i, i + 4));
							}
							i += 4;
						}
						default -> throw new TomlException(line, "bad escape \\" + e);
					}
				} else {
					b.append(ch);
				}
			}
		}

		private String literalString() throws TomlException {
			expect('\'');
			int close = s.indexOf('\'', i);
			if (close < 0) {
				throw new TomlException(line, "unterminated string");
			}
			String out = s.substring(i, close);
			i = close + 1;
			return out;
		}
	}
}
