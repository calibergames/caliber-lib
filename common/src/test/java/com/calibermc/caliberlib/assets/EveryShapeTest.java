package com.calibermc.caliberlib.assets;

import com.calibermc.caliberlib.assets.client.ClientAssets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every shape the engine knows, for a stone and a wood family: each gets its
 * blockstate, models, item model, name, loot table and recipes, and every
 * model, parent and texture they name exists.
 */
class EveryShapeTest {

	private static Map<String, String> client;
	private static Map<String, String> data;
	private static List<Material> families;

	private static Material family(String prefix, String source, MaterialKind kind, String door) {
		List<Material.Shape> shapes = new ArrayList<>();
		Map<String, ResourceLocation> siblings = new LinkedHashMap<>();
		for (String variant : ShapeSpecs.load().keySet()) {
			if (variant.equals("tall_door") && door == null) {
				continue;
			}
			ResourceLocation id = new ResourceLocation("caliber", prefix + "_" + variant);
			shapes.add(new Material.Shape(variant, id));
			siblings.put(variant, id);
		}
		ResourceLocation src = new ResourceLocation(source);
		return new Material(new ResourceLocation("caliber", prefix), "caliber", src.getPath(), kind, prefix, src, src,
				door == null ? null : new ResourceLocation(door), null, Map.of(), siblings, shapes, false, null, null);
	}

	@BeforeAll
	static void generate() {
		Fixtures.bootstrap();
		families = List.of(family("every_stone", "minecraft:stone_bricks", MaterialKind.STONE, null),
				family("every_oak", "minecraft:oak_planks", MaterialKind.WOOD, "minecraft:oak_door"));
		GameView game = Fixtures.game(families);
		client = EngineGoldenTest.read(ClientAssets.index(families, new ClasspathResources(), game));
		data = EngineGoldenTest.read(AssetEngine.dataIndex(families, game));
	}

	@Test
	void everyShapeHasItsFiles() {
		JsonObject lang = JsonParser.parseString(client.get("caliber:lang/en_us.json")).getAsJsonObject();
		Set<String> recipeResults = new HashSet<>();
		data.forEach((path, json) -> {
			if (path.contains(":recipes/")) {
				JsonElement result = JsonParser.parseString(json).getAsJsonObject().get("result");
				recipeResults.add(result.isJsonObject() ? result.getAsJsonObject().get("item").getAsString() : result.getAsString());
			}
		});
		int shapes = 0;
		for (Material m : families) {
			for (Material.Shape s : m.shapes()) {
				String path = s.block().getPath();
				ShapeSpec spec = ShapeSpecs.load().get(s.variant());
				assertTrue(client.containsKey("caliber:blockstates/" + path + ".json"), path + " blockstate");
				assertTrue(client.containsKey("caliber:models/item/" + path + ".json"), path + " item model");
				assertTrue(lang.has("block.caliber." + path), path + " name");
				assertTrue(data.containsKey("caliber:loot_tables/blocks/" + path + ".json"), path + " loot table");
				if (!spec.recipes().isEmpty()) {
					assertTrue(recipeResults.contains(s.block().toString()), path + " recipe");
				}
				shapes++;
			}
		}
		assertEquals(ShapeSpecs.load().size() * 2 - 1, shapes);
	}

	@Test
	void everyReferenceResolves() {
		List<String> missing = new ArrayList<>();
		client.forEach((path, json) -> {
			if (!path.endsWith(".json") || path.contains(":lang/")) {
				return;
			}
			JsonElement root = JsonParser.parseString(json);
			walk(root, "model", model -> check(model, "models", ".json", missing, path));
			walk(root, "parent", parent -> {
				if (!parent.startsWith("builtin/") && !parent.equals("minecraft:item/generated")) {
					check(parent, "models", ".json", missing, path);
				}
			});
			if (root.isJsonObject() && root.getAsJsonObject().has("textures")) {
				root.getAsJsonObject().getAsJsonObject("textures").entrySet().forEach(e -> {
					String value = e.getValue().getAsString();
					if (!value.startsWith("#")) {
						check(value, "textures", ".png", missing, path);
					}
				});
			}
		});
		assertEquals(List.of(), missing);
	}

	private static void check(String id, String folder, String suffix, List<String> missing, String from) {
		ResourceLocation rl = new ResourceLocation(id);
		ResourceLocation file = new ResourceLocation(rl.getNamespace(), folder + "/" + rl.getPath() + suffix);
		if (!client.containsKey(file.toString()) && ClasspathResources.url(file) == null) {
			missing.add(from + " names " + id);
		}
	}

	private static void walk(JsonElement json, String key, Consumer<String> action) {
		if (json.isJsonObject()) {
			for (Map.Entry<String, JsonElement> e : json.getAsJsonObject().entrySet()) {
				if (e.getKey().equals(key) && e.getValue().isJsonPrimitive()) {
					action.accept(e.getValue().getAsString());
				} else {
					walk(e.getValue(), key, action);
				}
			}
		} else if (json.isJsonArray()) {
			json.getAsJsonArray().forEach(e -> walk(e, key, action));
		}
	}
}
