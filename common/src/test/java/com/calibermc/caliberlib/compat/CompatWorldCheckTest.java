package com.calibermc.caliberlib.compat;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompatWorldCheckTest {

	private static final String CALCITE = "{\"block\":\"minecraft:calcite\",\"type\":\"stone\",\"name\":\"cltest:calcite\",\"variants\":[\"arch\",\"pillar\"]}";
	private static final String TUFF = "{\"block\":\"minecraft:tuff\",\"type\":\"stone\",\"name\":\"cltest:tuff\",\"variants\":[\"arch\",\"corner\"]}";
	private static final String TUFF_FEWER = "{\"block\":\"minecraft:tuff\",\"type\":\"stone\",\"name\":\"cltest:tuff\",\"variants\":[\"arch\"]}";

	@TempDir
	Path world;

	@Test
	void entriesThatAreGoneAreNamedWithTheirCause() {
		Map<CompatEntry.Key, String> why = Map.of(new CompatEntry.Key(new ResourceLocation("minecraft:tuff"), "stone"),
				"it is past the ceiling ([compat] max_shapes = 3)");
		assertEquals(List.of("minecraft:tuff (stone) is not registered now: it is past the ceiling ([compat] max_shapes = 3)"),
				CompatWorldCheck.check(List.of(CALCITE, TUFF), List.of(CALCITE), why));
		assertEquals(List.of("minecraft:tuff (stone) is not registered now: no compat file lists it any more"),
				CompatWorldCheck.check(List.of(CALCITE, TUFF), List.of(CALCITE), Map.of()));
		assertEquals(List.of("minecraft:tuff (stone) no longer registers these shapes: corner"),
				CompatWorldCheck.check(List.of(TUFF), List.of(TUFF_FEWER), Map.of()));
		assertEquals(List.of(), CompatWorldCheck.check(List.of(TUFF_FEWER), List.of(TUFF), Map.of()), "an entry that gains shapes is fine");
		assertEquals(List.of(), CompatWorldCheck.check(List.of(CALCITE), List.of(CALCITE, TUFF), Map.of()), "a new entry is fine");
		assertEquals(List.of("minecraft:tuff (stone) no longer registers these shapes: all (its name changed from cltest:tuff to cltest:tuff2)"),
				CompatWorldCheck.check(List.of(TUFF), List.of(TUFF.replace("cltest:tuff", "cltest:tuff2")), Map.of()));
	}

	@Test
	void anUnreadableRecordLineIsLeftOutNotFatal() throws Exception {
		Files.writeString(world.resolve(CompatWorldCheck.RECORD), "# record\n" + TUFF + "\n{\"block\":\"minecraft:tu\n{\"block\":\"Not An Id\",\"type\":\"stone\"}\n");
		CompatWorldCheck.worldLoading(world, List.of(CALCITE), Map.of());
		assertEquals(List.of(CALCITE), Files.readAllLines(world.resolve(CompatWorldCheck.RECORD)).subList(1, 2));
		assertFalse(CompatWorldCheck.readable("{\"block\":\"minecraft:tu"));
		assertEquals(List.of("minecraft:tuff (stone) is not registered now: no compat file lists it any more"),
				CompatWorldCheck.check(List.of(TUFF, "{broken"), List.of(), Map.of()));
	}

	@Test
	void theRecordFollowsTheWorld() throws Exception {
		CompatWorldCheck.worldLoading(world, List.of(), Map.of());
		assertFalse(Files.exists(world.resolve(CompatWorldCheck.RECORD)), "no record for a world that never had compat shapes");
		CompatWorldCheck.worldLoading(world, List.of(CALCITE, TUFF), Map.of());
		List<String> lines = Files.readAllLines(world.resolve(CompatWorldCheck.RECORD));
		assertTrue(lines.get(0).startsWith("#"));
		assertEquals(List.of(CALCITE, TUFF), lines.subList(1, lines.size()));
		CompatWorldCheck.worldLoading(world, List.of(CALCITE), Map.of());
		assertEquals(List.of(CALCITE), Files.readAllLines(world.resolve(CompatWorldCheck.RECORD)).subList(1, 2));
	}
}
