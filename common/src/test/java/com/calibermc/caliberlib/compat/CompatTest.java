package com.calibermc.caliberlib.compat;

import com.calibermc.caliberlib.data.ModBlockFamily.Variant;
import com.calibermc.caliberlib.testing.GameBootstrap;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompatTest {

	private static final CompatSource BUNDLED = CompatSource.bundled("extra.json");
	private static final CompatSource RELEASED = CompatSource.bundled("released-0.3.json");
	private static final CompatSource BOP_JAR = CompatSource.jar("biomesoplenty");
	private static final CompatSource CONFIG = CompatSource.config("mine.json");

	@BeforeAll
	static void boot() {
		GameBootstrap.boot();
	}

	private static List<CompatEntry> parse(String entries, CompatSource source, CompatLog log) {
		return CompatParser.parse("{\"format\": 1, \"entries\": [" + entries + "]}", source, log);
	}

	private static boolean logged(CompatLog log, String part) {
		return log.lines().stream().anyMatch(l -> l.contains(part));
	}

	private static CompatPlan.Game game(String... ids) {
		Set<ResourceLocation> registered = new HashSet<>();
		for (String id : ids) {
			registered.add(new ResourceLocation(id));
		}
		return new CompatPlan.Game() {
			@Override
			public boolean registered(ResourceLocation id) {
				return registered.contains(id);
			}

			@Override
			public boolean allowed(Variant variant) {
				return true;
			}
		};
	}

	// Parsing

	@Test
	void aFullEntryParses() {
		CompatLog log = new CompatLog(false);
		List<CompatEntry> e = parse("""
				{"block": "biomesoplenty:fir_planks", "type": "planks", "variants": ["slab_vertical", "tall_door"],
				 "name": "biomesoplenty:fir", "textures": {"door_top": "biomesoplenty:block/fir_door_top"},
				 "made_for": "biomesoplenty 19.0.0.96"}""", BOP_JAR, log);
		assertEquals(1, e.size());
		CompatEntry entry = e.get(0);
		assertEquals(new ResourceLocation("biomesoplenty:fir_planks"), entry.block());
		assertEquals(List.of("slab_vertical", "tall_door"), entry.variants());
		assertEquals(new ResourceLocation("biomesoplenty:fir"), entry.name());
		assertEquals(Map.of("door_top", new ResourceLocation("biomesoplenty:block/fir_door_top")), entry.textures());
		assertEquals(List.of(), log.lines());
	}

	@Test
	void badFilesAndEntriesAreSkippedWithTheirPlace() {
		CompatLog log = new CompatLog(false);
		assertEquals(List.of(), CompatParser.parse("{not json", CONFIG, log));
		assertEquals(List.of(), CompatParser.parse("{\"format\": 2, \"entries\": []}", CONFIG, log));
		assertEquals(List.of(), CompatParser.parse("{\"entries\": []}", CONFIG, log));
		assertTrue(logged(log, "not valid JSON"));
		assertTrue(logged(log, "format 2 is not one this version reads"));
		assertTrue(logged(log, "no \"format\""));

		log = new CompatLog(false);
		List<CompatEntry> e = parse("""
				{"type": "stone"},
				{"block": "nonamespace", "type": "stone"},
				{"block": "a:b"},
				{"block": "a:b", "type": "marble"},
				{"block": "a:b", "type": "wool"},
				{"block": "a:b", "type": "stone", "variants": ["arch", "spiral"], "colour": "red"},
				7""", CONFIG, log);
		assertEquals(1, e.size());
		assertEquals(List.of("arch"), e.get(0).variants());
		assertTrue(logged(log, "config/calibermod-compat/mine.json entry 0: no \"block\""));
		assertTrue(logged(log, "entry 1: \"block\" is not a namespaced id"));
		assertTrue(logged(log, "entry 2: no \"type\""));
		assertTrue(logged(log, "entry 3: unknown type \"marble\""));
		assertTrue(logged(log, "entry 4: type \"wool\" is not supported yet"));
		assertTrue(logged(log, "entry 5: unknown variant \"spiral\" left out"));
		assertTrue(logged(log, "entry 6: not an object"));
		assertTrue(logged(log, "unknown fields ignored: colour"));
	}

	@Test
	void anEntryWithoutVariantsGetsTheDefaultSetAndAHint() {
		CompatLog log = new CompatLog(false);
		List<CompatEntry> e = parse("{\"block\": \"a:b\", \"type\": \"shingle\"}", CONFIG, log);
		assertNull(e.get(0).variants());
		assertEquals(4, CompatMerge.variants(e.get(0)).size());
		assertTrue(logged(log, "entry 0: no \"variants\"; the type's default set applies, untrimmed"));
	}

	@Test
	void aRemoveThatIsNotABooleanIsReported() {
		CompatLog log = new CompatLog(false);
		parse("{\"block\": \"a:b\", \"type\": \"stone\", \"remove\": \"true\"}", CONFIG, log);
		assertTrue(logged(log, "entry 0: \"remove\" should be true or false; ignored"));
	}

	@Test
	void aModsFileListsOnlyItsOwnBlocksAndNames() {
		CompatLog log = new CompatLog(false);
		List<CompatEntry> e = parse("""
				{"block": "create:asurine", "type": "stone"},
				{"block": "biomesoplenty:fir_planks", "type": "planks", "name": "minecraft:fir"},
				{"block": "biomesoplenty:fir_planks", "type": "planks", "textures": {"side": "minecraft:block/stone"}},
				{"block": "biomesoplenty:fir_planks", "type": "planks", "remove": true}""", BOP_JAR, log);
		assertEquals(1, e.size());
		assertEquals(Map.of(), e.get(0).textures());
		assertTrue(logged(log, "create:asurine is not a block of biomesoplenty"));
		assertTrue(logged(log, "name minecraft:fir is outside biomesoplenty's namespace"));
		assertTrue(logged(log, "texture minecraft:block/stone is not the source mod's own"));
		assertTrue(logged(log, "\"remove\" works only in the config folder"));
	}

	// Merging

	@Test
	void laterSourcesReplaceAndConfigRemoves() {
		CompatLog log = new CompatLog(false);
		List<CompatEntry> all = new ArrayList<>();
		all.addAll(parse("{\"block\": \"a:x\", \"type\": \"stone\", \"variants\": [\"arch\"]}, {\"block\": \"a:y\", \"type\": \"stone\"}", BUNDLED, log));
		all.addAll(parse("{\"block\": \"a:x\", \"type\": \"stone\", \"variants\": [\"corner\"]}, {\"block\": \"a:y\", \"remove\": true}", CONFIG, log));
		List<CompatEntry> merged = CompatMerge.merge(all, log);
		assertEquals(1, merged.size());
		assertEquals(List.of("corner"), merged.get(0).variants());
		assertEquals(CONFIG, merged.get(0).source());
		assertTrue(logged(log, "replaces a:x (stone) from bundled extra.json"));
		assertTrue(logged(log, "removes a:y (stone)"));
	}

	@Test
	void releasedEntriesGainButNeverLoseShapesOrTheirName() {
		CompatLog log = new CompatLog(false);
		List<CompatEntry> all = new ArrayList<>();
		all.addAll(parse("{\"block\": \"create:asurine\", \"type\": \"stone\", \"name\": \"create:asurine_block\", \"variants\": [\"arch\", \"corner\"]}", RELEASED, log));
		all.addAll(parse("{\"block\": \"create:asurine\", \"type\": \"stone\", \"name\": \"create:asurine\", \"variants\": [\"arch\", \"pillar\"]}", CompatSource.jar("create"), log));
		CompatEntry merged = CompatMerge.merge(all, log).get(0);
		assertEquals(List.of("arch", "corner", "pillar"), merged.variants());
		assertEquals(new ResourceLocation("create:asurine_block"), merged.name());
		assertTrue(merged.source().released());
		assertTrue(logged(log, "its shapes [corner] stay"));
		assertTrue(logged(log, "its name create:asurine_block stays"));

		// A config remove is the player's choice, also for released entries.
		all.addAll(parse("{\"block\": \"create:asurine\", \"remove\": true}", CONFIG, log));
		assertEquals(List.of(), CompatMerge.merge(all, log));
	}

	// Naming rules

	@Test
	void namingRulesFollowTheSpec() {
		assertEquals(new ResourceLocation("biomesoplenty:brimstone_brick"), CompatTypes.defaultName(new ResourceLocation("biomesoplenty:brimstone_bricks"), "stone"));
		assertEquals(new ResourceLocation("biomesoplenty:fir"), CompatTypes.defaultName(new ResourceLocation("biomesoplenty:fir_planks"), "planks"));
		assertEquals(new ResourceLocation("biomesoplenty:fir_shingle"), CompatTypes.defaultName(new ResourceLocation("biomesoplenty:fir_planks"), "shingle"));
		assertEquals(new ResourceLocation("regions_unexplored:stripped_blue_bioshroom"),
				CompatTypes.defaultName(new ResourceLocation("regions_unexplored:stripped_blue_bioshroom_hyphae"), "stripped_wood"));
		assertEquals("Dark Fir", CompatTypes.displayName(new ResourceLocation("a:dark_fir")));
		assertEquals(new ResourceLocation("regions_unexplored:block/stripped_blue_bioshroom_stem"),
				CompatTypes.baseModel(new ResourceLocation("regions_unexplored:stripped_blue_bioshroom_hyphae"), "stripped_wood"));
		assertNull(CompatTypes.baseModel(new ResourceLocation("a:stone"), "stone"));
		// Siblings and doors come from the source block's name, whatever the entry's name.
		assertEquals(new ResourceLocation("regions_unexplored:chalk_slab"), CompatTypes.sibling(new ResourceLocation("regions_unexplored:chalk"), Variant.SLAB));
		assertEquals(new ResourceLocation("biomesoplenty:fir_door"), CompatTypes.door(new ResourceLocation("biomesoplenty:fir_planks")));
		assertEquals("biomesoplenty:fir", CompatTypes.blockSetTypeName(new ResourceLocation("biomesoplenty:fir_planks")));
		assertEquals(Variant.SLAB_VERTICAL, CompatTypes.variant("slab_vertical"));
		assertNull(CompatTypes.variant("SLAB_VERTICAL"));
	}

	@Test
	void typeDefaultsAreTheLibrarysVariantSets() {
		assertEquals(22, CompatTypes.type("stone").defaults().size());
		assertEquals(16, CompatTypes.type("planks").defaults().size());
		assertTrue(CompatTypes.type("planks").defaults().contains(Variant.TALL_DOOR));
		assertEquals(4, CompatTypes.type("shingle").defaults().size());
		assertEquals(24, CompatTypes.type("stripped_wood").defaults().size());
	}

	// Planning

	@Test
	void absentBlocksSwitchesAndTakenIdsAreSkipped() {
		CompatLog log = new CompatLog(false);
		List<CompatEntry> entries = CompatMerge.merge(parse("""
				{"block": "a:stone_bricks", "type": "stone", "variants": ["arch", "slab", "wall"]},
				{"block": "a:missing", "type": "stone", "variants": ["arch"]},
				{"block": "chipped:fancy", "type": "stone", "variants": ["arch"]},
				{"block": "b:cobble", "type": "stone", "variants": ["arch"]},
				{"block": "a:fir_planks", "type": "planks", "variants": ["tall_door", "slab_vertical", "button"]},
				{"block": "a:other_stone_bricks", "type": "stone", "name": "a:stone_brick", "variants": ["arch", "corner"]}""", BUNDLED, log), log);
		CompatPlan.Game game = game("a:stone_bricks", "a:stone_brick_wall", "a:stone_brick_slab", "chipped:fancy", "b:cobble",
				"a:fir_planks", "a:other_stone_bricks");
		CompatPlan.Settings settings = new CompatPlan.Settings(Map.of("b", false), Map.of(), 20_000);
		CompatPlan.Result r = CompatPlan.plan(entries, settings, game, log);
		assertEquals(3, r.planned().size());
		CompatPlan.Planned stone = r.planned().get(0);
		assertEquals(List.of(Variant.ARCH), stone.variants(), "the source mod's own slab and wall win");
		assertEquals(Map.of(Variant.SLAB, new ResourceLocation("a:stone_brick_slab"), Variant.WALL, new ResourceLocation("a:stone_brick_wall")), stone.siblings());
		assertEquals("stone_bricks", stone.family());
		CompatPlan.Planned fir = r.planned().get(1);
		assertEquals(List.of(Variant.SLAB_VERTICAL), fir.variants(), "no door: no tall door; buttons are the source mod's");
		CompatPlan.Planned other = r.planned().get(2);
		assertEquals(List.of(Variant.CORNER), other.variants(), "the earlier entry keeps a:stone_brick_arch");
		assertEquals(1, r.absent());
		assertEquals(2, r.switchedOff(), "chipped is off by default, b by the config");
		assertEquals(3, r.shapes());
		assertTrue(logged(log, "a:stone_brick_slab is already registered by its mod"));
		assertTrue(logged(log, "a:fir_planks has no door a:fir_door"));
		assertTrue(logged(log, "a:stone_brick_arch is already made by bundled extra.json entry 0"));
	}

	@Test
	void theCeilingRefusesWholeEntriesButNeverReleasedOnes() {
		CompatLog log = new CompatLog(false);
		List<CompatEntry> all = new ArrayList<>();
		all.addAll(parse("{\"block\": \"a:r\", \"type\": \"stone\", \"variants\": [\"arch\", \"corner\", \"pillar\"]}", RELEASED, log));
		all.addAll(parse("""
				{"block": "a:x", "type": "stone", "variants": ["arch"]},
				{"block": "a:y", "type": "stone", "variants": ["arch", "corner"]},
				{"block": "a:z", "type": "stone", "variants": ["arch"]}""", BUNDLED, log));
		all.addAll(parse("{\"block\": \"a:q\", \"type\": \"stone\", \"variants\": [\"arch\"]}", RELEASED, log));
		CompatPlan.Result r = CompatPlan.plan(CompatMerge.merge(all, log), new CompatPlan.Settings(Map.of(), Map.of(), 4),
				game("a:r", "a:x", "a:y", "a:z", "a:q"), log);
		List<String> planned = r.planned().stream().map(p -> p.entry().block().getPath()).toList();
		assertEquals(List.of("r", "x", "q"), planned, "y would pass 4 shapes, so y and z are refused; released q is not");
		assertEquals(2, r.overCeiling());
		assertEquals(5, r.shapes());
		assertTrue(logged(log, "2 entries refused, past the ceiling of 4 shapes"));
	}

	@Test
	void twoEntriesOfOneSourceGetDistinctFamilies() {
		CompatLog log = new CompatLog(false);
		List<CompatEntry> entries = CompatMerge.merge(parse("""
				{"block": "a:fir_planks", "type": "planks", "variants": ["arch"]},
				{"block": "a:fir_planks", "type": "shingle"},
				{"block": "a:fir_planks", "type": "stone", "name": "a:fir_stone", "variants": ["arch"]},
				{"block": "a:fir_planks", "type": "stripped_wood", "name": "a:fir_x", "variants": ["arch"]},
				{"block": "a:fir_planks", "type": "planks", "name": "a:fir_y", "variants": ["corner"]}""", BUNDLED, log), log);
		CompatPlan.Result r = CompatPlan.plan(entries, CompatPlan.Settings.defaults(), game("a:fir_planks"), log);
		// The fifth entry has the same key as the first, so it replaces it; four families remain, all names distinct.
		List<String> names = r.planned().stream().map(CompatPlan.Planned::family).toList();
		assertEquals(List.of("fir_planks", "fir_shingle", "fir_planks_stone", "fir_planks_stripped_wood"), names);
		assertEquals(names.size(), Set.copyOf(names).size());
		assertEquals(Set.of(Variant.ROOF_22, Variant.ROOF_45, Variant.ROOF_67, Variant.ROOF_PEAK), Set.copyOf(r.planned().get(1).variants()));

		// A third clash: "fir_planks" and "fir_planks_stone" are both taken before the stone entry of a:fir_planks.
		log = new CompatLog(false);
		entries = CompatMerge.merge(parse("""
				{"block": "a:fir_planks", "type": "planks", "variants": ["arch"]},
				{"block": "a:fir_planks_stone", "type": "planks", "name": "a:fps", "variants": ["arch"]},
				{"block": "a:fir_planks", "type": "stone", "name": "a:fs", "variants": ["arch"]}""", BUNDLED, log), log);
		r = CompatPlan.plan(entries, CompatPlan.Settings.defaults(), game("a:fir_planks", "a:fir_planks_stone"), log);
		assertEquals(List.of("fir_planks", "fir_planks_stone", "fir_planks_stone_2"), r.planned().stream().map(CompatPlan.Planned::family).toList());
	}

	// The hash

	@Test
	void theHashCoversWhatRegistersInAnyOrder() {
		CompatLog log = new CompatLog(false);
		CompatPlan.Game game = game("a:x", "a:y");
		String one = hash(parse("""
				{"block": "a:x", "type": "stone", "variants": ["arch", "corner"]},
				{"block": "a:y", "type": "stone", "variants": ["arch"]}""", BUNDLED, log), game, log);
		String reordered = hash(parse("""
				{"block": "a:y", "type": "stone", "variants": ["arch"]},
				{"block": "a:x", "type": "stone", "variants": ["corner", "arch"], "textures": {"side": "a:block/z"}}""", CONFIG, log), game, log);
		assertEquals(one, reordered, "order, sources and textures do not change it");
		String changed = hash(parse("""
				{"block": "a:x", "type": "stone", "variants": ["arch"]},
				{"block": "a:y", "type": "stone", "variants": ["arch"]}""", BUNDLED, log), game, log);
		assertNotEquals(one, changed);
		assertEquals(64, one.length());
		assertEquals(hash(List.of(), game, log), CompatHash.of(List.of()), "the empty list has a hash too");
		assertFalse(CompatHash.shortHashes(CompatPlan.plan(CompatMerge.merge(parse("{\"block\": \"a:x\", \"type\": \"stone\"}", BUNDLED, log), log),
				CompatPlan.Settings.defaults(), game, log).planned()).isEmpty());
	}

	@Test
	void woodShapesTakeTheSourceModsBlockSetType() {
		CompatLog log = new CompatLog(false);
		List<CompatEntry> entries = CompatMerge.merge(parse("""
				{"block": "minecraft:crimson_planks", "type": "planks", "variants": ["arch"], "name": "a:crimson"},
				{"block": "a:fir_planks", "type": "planks", "variants": ["arch"]},
				{"block": "minecraft:stone_bricks", "type": "stone", "variants": ["arch"], "name": "a:stone_brick"}""", BUNDLED, log), log);
		List<CompatPlan.Planned> p = CompatPlan.plan(entries, CompatPlan.Settings.defaults(),
				game("minecraft:crimson_planks", "a:fir_planks", "minecraft:stone_bricks"), log).planned();
		assertEquals(net.minecraft.world.level.block.state.properties.BlockSetType.CRIMSON, CompatRegistration.blockSetType(p.get(0)));
		assertEquals(net.minecraft.world.level.block.state.properties.BlockSetType.OAK, CompatRegistration.blockSetType(p.get(1)), "no a:fir type: oak");
		assertEquals(net.minecraft.world.level.block.state.properties.BlockSetType.STONE, CompatRegistration.blockSetType(p.get(2)));
	}

	// The login check

	@Test
	void theLoginPayloadRoundTripsAndTheReasonReads() {
		net.minecraft.network.FriendlyByteBuf buf = new net.minecraft.network.FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
		CompatLogin.write(buf);
		CompatLogin.Remote remote = CompatLogin.Remote.read(buf);
		assertEquals(CompatHash.of(List.of()), remote.hash(), "without enable, the empty list's hash");
		assertTrue(remote.matches());
		assertFalse(CompatLists.active());
		assertEquals("Caliber compat lists differ from this server's (1 entry only on the server, 0 only in your game). "
				+ "The mods or the Caliber compat settings differ; see the logs for which.", CompatLogin.differs(1, 0).getString());
		assertTrue(CompatLogin.differs(2, 1).getString().contains("(2 entries only on the server, 1 only in your game)"));
	}

	private static String hash(List<CompatEntry> entries, CompatPlan.Game game, CompatLog log) {
		return CompatHash.of(CompatPlan.plan(CompatMerge.merge(entries, log), CompatPlan.Settings.defaults(), game, log).planned());
	}
}
