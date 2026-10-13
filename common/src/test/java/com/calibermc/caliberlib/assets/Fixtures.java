package com.calibermc.caliberlib.assets;

import com.calibermc.caliberlib.block.management.ModBlockHelper;
import com.calibermc.caliberlib.data.ModBlockFamily;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * The test families: four of Caliber's own (as Caliber Mod 0.3.1 registers
 * them) and two in a made-up namespace, {@code fixturemod}, whose textures
 * ship with the tests.
 */
public final class Fixtures {

	static final String FIXTURE = "fixturemod";
	private static final Set<String> FIVE_LAYERS = Set.of("corner", "corner_slab", "corner_slab_vertical", "eighth",
			"pillar", "quarter", "quarter_vertical");

	private Fixtures() {
	}

	public static void bootstrap() {
		com.calibermc.caliberlib.testing.GameBootstrap.boot();
	}

	public static List<Material> families() {
		List<Material> out = new ArrayList<>();
		out.add(family("caliber", "andesite", "andesite", MaterialKind.STONE, "Andesite", "minecraft:andesite", null,
				null, Map.of(), Map.of(), ModBlockHelper.STONE_VARIANTS_WITHOUT_SLAB_STAIRS_WALL));
		out.add(family("caliber", "oak", "oak_planks", MaterialKind.WOOD, "Oak", "minecraft:oak_planks", "minecraft:oak_door",
				null, Map.of(), Map.of("slab", "minecraft:oak_slab"), ModBlockHelper.PLANK_VARIANTS));
		ResourceLocation log = new ResourceLocation("minecraft", "block/stripped_oak_log");
		out.add(family("caliber", "stripped_oak", "stripped_oak_wood", MaterialKind.WOOD, "Stripped Oak",
				"minecraft:stripped_oak_wood", null, log, Map.of("side", log, "top", log, "bottom", log), Map.of(),
				ModBlockHelper.STRIPPED_WOOD_VARIANTS));
		out.add(family("caliber", "oak_shingle", "oak_shingle", MaterialKind.WOOD, "Oak Shingle", "minecraft:oak_planks",
				null, null, Map.of(), Map.of(), ModBlockHelper.ROOF_VARIANTS));
		out.add(family(FIXTURE, "fixture_stone", "fixture_stone", MaterialKind.STONE, "Fixture Stone", "fixturemod:fixture_stone",
				null, null, Map.of(), Map.of("slab", "fixturemod:fixture_stone_slab"),
				variants("arch", "layer", "corner", "pillar", "slab_vertical", "wall")));
		out.add(family(FIXTURE, "fixture", "fixture_planks", MaterialKind.WOOD, "Fixture", "fixturemod:fixture_planks",
				"fixturemod:fixture_door", null, Map.of(), Map.of(),
				variants("wall", "layer", "slab_vertical", "tall_door", "beam_vertical")));
		return out;
	}

	private static List<ModBlockFamily.Variant> variants(String... names) {
		List<ModBlockFamily.Variant> out = new ArrayList<>();
		for (String name : names) {
			out.add(ModBlockFamily.Variant.valueOf(name.toUpperCase()));
		}
		return out;
	}

	private static Material family(String ns, String prefix, String manager, MaterialKind kind, String display,
			String source, @Nullable String door, @Nullable ResourceLocation baseModel,
			Map<String, ResourceLocation> textures, Map<String, String> extraSiblings, List<ModBlockFamily.Variant> variants) {
		List<Material.Shape> shapes = new ArrayList<>();
		Map<String, ResourceLocation> siblings = new LinkedHashMap<>();
		for (ModBlockFamily.Variant v : variants) {
			if (v == ModBlockFamily.Variant.BASE) {
				continue;
			}
			ResourceLocation id = new ResourceLocation(ns, prefix + "_" + v.getName());
			shapes.add(new Material.Shape(v.getName(), id));
			siblings.put(v.getName(), id);
		}
		extraSiblings.forEach((variant, id) -> siblings.putIfAbsent(variant, new ResourceLocation(id)));
		ResourceLocation src = new ResourceLocation(source);
		return new Material(new ResourceLocation(ns, prefix), "caliber", manager, kind, display, src, src,
				door == null ? null : new ResourceLocation(door), baseModel, textures, siblings, shapes, false, null);
	}

	/** The registries of a game with Caliber's families and the fixture mod loaded. */
	public static GameView game(List<Material> families) {
		Set<ResourceLocation> items = new HashSet<>();
		Map<ResourceLocation, String> variants = new LinkedHashMap<>();
		for (Material m : families) {
			for (Material.Shape s : m.shapes()) {
				items.add(s.block());
				variants.put(s.block(), s.variant());
			}
		}
		for (String id : List.of("fixture_stone", "fixture_stone_slab", "fixture_planks", "fixture_door")) {
			items.add(new ResourceLocation(FIXTURE, id));
		}
		ResourceLocation door = new ResourceLocation(FIXTURE, "fixture_door");
		return new GameView() {
			@Override
			public boolean hasItem(ResourceLocation item) {
				return items.contains(item) || GameView.LIVE.hasItem(item);
			}

			@Override
			public boolean hasRecipeSerializer(ResourceLocation serializer) {
				return serializer.equals(DataGenerator.WOODCUTTING) || GameView.LIVE.hasRecipeSerializer(serializer);
			}

			@Override
			public List<Integer> layers(ResourceLocation block) {
				String variant = variants.get(block);
				if ("layer".equals(variant) || "layer_vertical".equals(variant)) {
					return IntStream.rangeClosed(1, 8).boxed().toList();
				}
				return FIVE_LAYERS.contains(variant) ? IntStream.rangeClosed(1, 5).boxed().toList() : List.of();
			}

			@Override
			public Map<String, String> defaultState(ResourceLocation block) {
				if (block.equals(door)) {
					return Map.of("facing", "north", "half", "lower", "hinge", "left", "open", "false", "powered", "false");
				}
				return block.getNamespace().equals(FIXTURE) ? Map.of() : GameView.LIVE.defaultState(block);
			}

			@Nullable
			@Override
			public ResourceLocation blockOf(ResourceLocation item) {
				return item.equals(door) ? door : GameView.LIVE.blockOf(item);
			}
		};
	}
}
