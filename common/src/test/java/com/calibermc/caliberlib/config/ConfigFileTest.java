package com.calibermc.caliberlib.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigFileTest {

	@TempDir
	Path dir;

	private ConfigFile config(Path file) {
		return new ConfigFile(file)
				.comment("compat", "Shapes for other mods' blocks.")
				.define("compat", "max_shapes", 20000L, "Most compat shapes to register.")
				.define("compat", "debug", false, "Lists every skipped entry.")
				.define("compat.mods", "chipped", false, "Chipped's shapes; off by default.");
	}

	@Test
	void parsesTheSubsetConfigFilesUse() throws Toml.TomlException {
		Toml.Document doc = Toml.parse("""
				# top comment
				top = "a \\"quoted\\" word"
				["Configs for Caliber Mod"]
				mode_blockstates = 1 # trailing comment
				[compat.mods]
				chipped = false
				'literal key' = 'C:\\path'
				list = ["a", "b"]
				ratio = 0.5
				big = 20_000
				""");
		Map<String, Object> v = doc.values();
		assertEquals("a \"quoted\" word", v.get("top"));
		assertEquals(1L, v.get("Configs for Caliber Mod.mode_blockstates"));
		assertEquals(false, v.get("compat.mods.chipped"));
		assertEquals("C:\\path", v.get("compat.mods.literal key"));
		assertEquals(List.of("a", "b"), v.get("compat.mods.list"));
		assertEquals(0.5, v.get("compat.mods.ratio"));
		assertEquals(20000L, v.get("compat.mods.big"));
	}

	@Test
	void reportsTheLineOfAnError() {
		Toml.TomlException e = assertThrows(Toml.TomlException.class, () -> Toml.parse("a = 1\n[t]\nb = what\n"));
		assertEquals(3, e.line);
		assertEquals(2, assertThrows(Toml.TomlException.class, () -> Toml.parse("a = 1\na = 2\n")).line);
		assertEquals(2, assertThrows(Toml.TomlException.class, () -> Toml.parse("[t]\n[t]\n")).line);
		assertEquals(1, assertThrows(Toml.TomlException.class, () -> Toml.parse("s = \"open\n")).line);
	}

	@Test
	void writesAMissingFileWithDefaultsAndComments() throws Exception {
		Path file = dir.resolve("sub/calibermod-common.toml");
		ConfigFile c = config(file).load();
		assertEquals(20000L, c.getLong("compat.max_shapes"));
		String text = Files.readString(file);
		assertTrue(text.contains("# Shapes for other mods' blocks.\n[compat]\n# Most compat shapes to register.\nmax_shapes = 20000\n"), text);
		assertTrue(text.contains("[compat.mods]\n# Chipped's shapes; off by default.\nchipped = false\n"), text);
		assertEquals(Map.of("chipped", false), config(file).load().booleans("compat.mods"));
	}

	@Test
	void keepsTheFileAndAddsOnlyWhatIsMissing() throws Exception {
		Path file = dir.resolve("calibermod-common.toml");
		String before = """
				# the player's notes stay
				["Configs for Caliber Mod"]
				mode_blockstates = 0

				[compat]
				max_shapes = 500 # mine
				[compat.mods]
				biomesoplenty = false
				""";
		Files.writeString(file, before);
		ConfigFile c = config(file).load();
		assertEquals(500L, c.getLong("compat.max_shapes"));
		assertFalse(c.getBoolean("compat.debug"));
		assertEquals(Map.of("biomesoplenty", false, "chipped", false), c.booleans("compat.mods"));
		String after = Files.readString(file);
		assertEquals("""
				# the player's notes stay
				["Configs for Caliber Mod"]
				mode_blockstates = 0

				[compat]
				max_shapes = 500 # mine
				# Lists every skipped entry.
				debug = false
				[compat.mods]
				biomesoplenty = false
				# Chipped's shapes; off by default.
				chipped = false
				""", after);
		config(file).load();
		assertEquals(after, Files.readString(file), "a second load changes nothing");
		Toml.parse(after);
	}

	@Test
	void aBadFileIsLeftAloneAndTheDefaultsApply() throws Exception {
		Path file = dir.resolve("bad.toml");
		Files.writeString(file, "[compat]\nmax_shapes = lots\n");
		ConfigFile c = config(file).load();
		assertEquals(20000L, c.getLong("compat.max_shapes"));
		assertEquals("[compat]\nmax_shapes = lots\n", Files.readString(file));
	}

	@Test
	void aBadEscapeIsAnErrorOfItsLine() throws Exception {
		assertEquals(2, assertThrows(Toml.TomlException.class, () -> Toml.parse("a = 1\nb = \"\\uZZZZ\"\n")).line);
		Path file = dir.resolve("escape.toml");
		Files.writeString(file, "[compat]\nname = \"\\uZZZZ\"\n");
		assertEquals(20000L, config(file).load().getLong("compat.max_shapes"));
		assertEquals("[compat]\nname = \"\\uZZZZ\"\n", Files.readString(file));
	}

	@Test
	void aWindowsFileKeepsItsLineEnds() throws Exception {
		Path file = dir.resolve("crlf.toml");
		Files.writeString(file, "[compat]\r\nmax_shapes = 7\r\n");
		assertEquals(7L, config(file).load().getLong("compat.max_shapes"));
		String text = Files.readString(file);
		assertTrue(text.startsWith("[compat]\r\nmax_shapes = 7\r\n"), text);
		assertFalse(text.replace("\r\n", "").contains("\n"), "every line ends the same way");
		assertFalse(Files.exists(dir.resolve("crlf.toml.tmp")));
	}

	@Test
	void aValueOfTheWrongTypeFallsBackToItsDefault() throws Exception {
		Path file = dir.resolve("typed.toml");
		Files.writeString(file, "[compat]\nmax_shapes = \"many\"\ndebug = 1\n[compat.mods]\nchipped = true\nx = 3\n");
		ConfigFile c = config(file).load();
		assertEquals(20000L, c.getLong("compat.max_shapes"));
		assertFalse(c.getBoolean("compat.debug"));
		assertEquals(Map.of("chipped", true), c.booleans("compat.mods"));
	}
}
