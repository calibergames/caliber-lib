package com.calibermc.caliberlib.compat;

import com.calibermc.caliberlib.platform.ModFiles;
import com.calibermc.caliberlib.testing.GameBootstrap;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompatSourcesTest {

	@TempDir
	Path dir;

	@BeforeAll
	static void boot() {
		GameBootstrap.boot();
	}

	private static ModFiles mod(String id, Path... roots) {
		return new ModFiles(id, relative -> {
			List<Path> found = new ArrayList<>();
			for (Path r : roots) {
				if (Files.exists(r.resolve(relative))) {
					found.add(r.resolve(relative));
				}
			}
			return found;
		});
	}

	private static void write(Path file, String block) throws IOException {
		Files.createDirectories(file.getParent());
		Files.writeString(file, "{\"format\": 1, \"entries\": [{\"block\": \"" + block + "\", \"type\": \"stone\"}]}");
	}

	@Test
	void filesAreReadInLoadOrder() throws IOException {
		Path caliber = dir.resolve("caliber");
		Path caliberResources = dir.resolve("caliber-resources");
		write(caliber.resolve("data/caliber/calibermod-compat/b.json"), "a:b");
		write(caliberResources.resolve("data/caliber/calibermod-compat/a.json"), "a:a");
		write(caliberResources.resolve("data/caliber/calibermod-compat/released-0.3.json"), "a:r");
		write(dir.resolve("zmod/data/zmod/calibermod-compat.json"), "zmod:z");
		write(dir.resolve("amod/data/amod/calibermod-compat.json"), "amod:a");
		write(dir.resolve("config/calibermod-compat/mine.json"), "a:m");
		Files.writeString(dir.resolve("config/calibermod-compat/notes.txt"), "not a compat file");
		List<ModFiles> mods = List.of(mod("amod", dir.resolve("amod")), mod("caliber", caliber, caliberResources), mod("zmod", dir.resolve("zmod")));
		CompatLog log = new CompatLog(false);
		List<CompatEntry> entries = CompatSources.read("caliber", "data/caliber/calibermod-compat", mods, dir.resolve("config"), log);
		// Released bundled files first, so their ids are planned before anything else.
		assertEquals(List.of("a:r", "a:a", "a:b", "amod:a", "zmod:z", "a:m"), entries.stream().map(e -> e.block().toString()).toList());
		assertEquals(List.of("bundled released-0.3.json", "bundled a.json", "bundled b.json", "amod jar", "zmod jar",
				"config/calibermod-compat/mine.json"), entries.stream().map(e -> e.source().label()).toList());
		assertTrue(entries.get(0).source().released());
		assertFalse(entries.get(1).source().released());
		assertEquals(List.of(), log.lines());
	}

	@Test
	void nothingToReadIsFine() {
		CompatLog log = new CompatLog(false);
		assertEquals(List.of(), CompatSources.read("caliber", "data/caliber/calibermod-compat", List.of(), dir.resolve("none"), log));
	}

	@SuppressWarnings("unchecked")
	private static <T> T field(BlockBehaviour.Properties p, String name) {
		try {
			Field f = BlockBehaviour.Properties.class.getDeclaredField(name);
			f.setAccessible(true);
			return (T) f.get(p);
		} catch (ReflectiveOperationException e) {
			throw new AssertionError(name, e);
		}
	}

	@Test
	void propertiesAreCopiedAsPlainValues() {
		// The redstone lamp's light depends on its "lit" property; a shape has no such property, so a copied
		// callback would fail on it. The plain copy gives a constant, here the unlit lamp's 0.
		BlockBehaviour.Properties lamp = CompatRegistration.properties(Blocks.REDSTONE_LAMP);
		ToIntFunction<BlockState> light = field(lamp, "lightEmission");
		assertEquals(0, light.applyAsInt(Blocks.STONE.defaultBlockState()));
		ToIntFunction<BlockState> glow = field(CompatRegistration.properties(Blocks.GLOWSTONE), "lightEmission");
		assertEquals(15, glow.applyAsInt(Blocks.STONE.defaultBlockState()));
		assertEquals(false, field(CompatRegistration.properties(Blocks.GLASS), "canOcclude"));
		BlockBehaviour.Properties stone = CompatRegistration.properties(Blocks.STONE);
		assertEquals(true, field(stone, "requiresCorrectToolForDrops"));
		assertEquals(Blocks.STONE.defaultDestroyTime(), (float) field(stone, "destroyTime"));
		assertEquals(Blocks.STONE.getExplosionResistance(), (float) field(stone, "explosionResistance"));
		assertEquals(Blocks.STONE.defaultBlockState().getSoundType(), field(stone, "soundType"));
		assertEquals(true, field(stone, "canOcclude"));
		assertEquals(Blocks.ICE.getFriction(), (float) field(CompatRegistration.properties(Blocks.ICE), "friction"));
		assertEquals(Blocks.SOUL_SAND.getSpeedFactor(), (float) field(CompatRegistration.properties(Blocks.SOUL_SAND), "speedFactor"));
		BlockBehaviour.Properties oak = CompatRegistration.properties(Blocks.OAK_PLANKS);
		assertEquals(true, field(oak, "ignitedByLava"));
		assertEquals(Blocks.OAK_PLANKS.defaultBlockState().instrument(), field(oak, "instrument"));
		Function<BlockState, MapColor> colour = field(oak, "mapColor");
		assertEquals(MapColor.WOOD, colour.apply(Blocks.STONE.defaultBlockState()));
	}

	@Test
	void theConfigHoldsTheCompatKeys() throws IOException {
		Path file = dir.resolve("calibermod-common.toml");
		Files.writeString(file, "[\"Configs for Caliber Mod\"]\nmode_blockstates = 1\n");
		var c = CompatLists.config(file).load();
		assertEquals(20_000, c.getLong("compat.max_shapes"));
		assertEquals(false, c.booleans("compat.mods").get("chipped"));
		assertEquals(4, c.booleans("compat.types").size());
		String text = Files.readString(file);
		assertTrue(text.startsWith("[\"Configs for Caliber Mod\"]\nmode_blockstates = 1\n"), text);
		// Deleting Chipped's line does not turn it on: the key comes back, off.
		Files.writeString(file, text.replace("chipped = false", ""));
		assertEquals(false, CompatLists.config(file).load().booleans("compat.mods").get("chipped"));
	}
}
