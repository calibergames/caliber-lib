package com.calibermc.caliberlib.assets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;

/** Loot tables, recipes, advancements and tags of the opted-in families. */
final class DataGenerator {

	static final ResourceLocation WOODCUTTING = new ResourceLocation("caliber", "woodcutting");
	private static final String NON_FLAMMABLE_WOOD = "minecraft:non_flammable_wood";

	private DataGenerator() {
	}

	static PackIndex index(List<Material> materials, Map<String, ShapeSpec> specs, GameView game) {
		PackIndex index = new PackIndex();
		boolean woodcutter = game.hasRecipeSerializer(WOODCUTTING);
		Map<ResourceLocation, Set<String>> blockTags = new TreeMap<>();
		Map<ResourceLocation, Set<String>> itemTags = new TreeMap<>();
		for (Material m : materials) {
			List<ResourceLocation> rewards = new ArrayList<>();
			// Wood that fire does not take (crimson, warped) is never furnace fuel, as vanilla's tag says.
			boolean nonFlammable = m.kind() == MaterialKind.WOOD && !game.burns(m.source());
			for (Material.Shape shape : m.shapes()) {
				ShapeSpec spec = specs.get(shape.variant());
				if (spec == null) {
					continue;
				}
				ResourceLocation block = shape.block();
				String id = block.toString();
				for (String tag : spec.blockTags()) {
					blockTags.computeIfAbsent(new ResourceLocation(tag), t -> new LinkedHashSet<>()).add(id);
				}
				String mineable = m.miningTool().tag();
				if (mineable != null) {
					blockTags.computeIfAbsent(new ResourceLocation(mineable), t -> new LinkedHashSet<>()).add(id);
				}
				if (!game.hasItem(block)) {
					continue;
				}
				for (String tag : spec.itemTags().get(m.kind())) {
					itemTags.computeIfAbsent(new ResourceLocation(tag), t -> new LinkedHashSet<>()).add(id);
				}
				if (nonFlammable) {
					itemTags.computeIfAbsent(new ResourceLocation(NON_FLAMMABLE_WOOD), t -> new LinkedHashSet<>()).add(id);
				}
				index.add(block.getNamespace(), "loot_tables/blocks/" + block.getPath() + ".json",
						() -> bytes(loot(spec.loot(), block, game)));
				for (ShapeSpec.Recipe recipe : spec.recipes()) {
					recipes(m, block, recipe, woodcutter, game, (rid, json) -> {
						index.add(rid.getNamespace(), "recipes/" + rid.getPath() + ".json", () -> bytes(json.get()));
						rewards.add(rid);
					});
				}
			}
			if (!rewards.isEmpty()) {
				String folder = m.owner().equals(m.namespace()) ? "" : m.namespace() + "/";
				index.add(m.owner(), "advancements/recipes/" + folder + m.manager() + ".json",
						() -> bytes(advancement(m, rewards)));
			}
		}
		blockTags.forEach((tag, values) -> index.add(tag.getNamespace(), "tags/blocks/" + tag.getPath() + ".json",
				() -> bytes(tag(tag, values))));
		itemTags.forEach((tag, values) -> index.add(tag.getNamespace(), "tags/items/" + tag.getPath() + ".json",
				() -> bytes(tag(tag, values))));
		return index;
	}

	interface RecipeSink {
		void accept(ResourceLocation id, Supplier<JsonObject> json);
	}

	static void recipes(Material m, ResourceLocation result, ShapeSpec.Recipe r, boolean woodcutter, GameView game,
			RecipeSink sink) {
		if (r.kind() != null && r.kind() != m.kind()) {
			return;
		}
		boolean wood = m.kind() == MaterialKind.WOOD;
		if (wood && !woodcutter && !r.type().equals("shaped") && !r.type().equals("tall_door")) {
			return;
		}
		String cutType = wood ? WOODCUTTING.toString() : "minecraft:stonecutting";
		String cut = wood ? "woodcutting" : "stonecutting";
		String path = result.getPath();
		switch (r.type()) {
			case "cut" -> {
				if (game.hasItem(m.ingredient())) {
					sink.accept(recipeId(m, path + "_from_" + m.manager() + "_" + cut),
							() -> cutting(cutType, m.ingredient(), result, r.count()));
				}
			}
			case "cut_sibling" -> {
				ResourceLocation sibling = m.siblings().get(r.sibling());
				if (sibling != null && game.hasItem(sibling)) {
					sink.accept(recipeId(m, path + "_from_" + m.manager() + "_" + r.sibling() + "_" + cut),
							() -> cutting(cutType, sibling, result, r.count()));
				}
			}
			case "shaped" -> {
				if (game.hasItem(m.ingredient())) {
					sink.accept(recipeId(m, path + "_from_" + m.manager() + "_shaped"),
							() -> shaped("building", r.pattern(), Map.of('#', m.ingredient()), result, r.count()));
				}
			}
			case "tall_door" -> {
				String prefix = m.id().getPath();
				if (m.door() != null && game.hasItem(m.door()) && game.hasItem(m.ingredient())) {
					sink.accept(recipeId(m, path + "_from_" + prefix + "_door_and_" + prefix + "_shaped"),
							() -> shaped("redstone", List.of("X", "#"), Map.of('X', m.door(), '#', m.ingredient()), result, 1));
				}
			}
			case "convert" -> {
				ResourceLocation sibling = m.siblings().get(r.sibling());
				if (sibling != null && game.hasItem(sibling)) {
					sink.accept(recipeId(m, path + "_from_" + sibling.getPath() + "_" + cut),
							() -> cutting(cutType, sibling, result, 1));
					sink.accept(recipeId(m, sibling.getPath() + "_from_" + path + "_" + cut),
							() -> cutting(cutType, result, sibling, 1));
				}
			}
			default -> throw new IllegalArgumentException("Unknown recipe type " + r.type());
		}
	}

	/** {@code owner:name} for the owner's own namespace, {@code owner:namespace/name} for another mod's. */
	static ResourceLocation recipeId(Material m, String name) {
		return new ResourceLocation(m.owner(), m.owner().equals(m.namespace()) ? name : m.namespace() + "/" + name);
	}

	static JsonObject cutting(String type, ResourceLocation ingredient, ResourceLocation result, int count) {
		JsonObject json = new JsonObject();
		json.addProperty("type", type);
		json.addProperty("count", count);
		json.add("ingredient", item(ingredient));
		json.addProperty("result", result.toString());
		return json;
	}

	static JsonObject shaped(String category, List<String> pattern, Map<Character, ResourceLocation> keys,
			ResourceLocation result, int count) {
		JsonObject json = new JsonObject();
		json.addProperty("type", "minecraft:crafting_shaped");
		json.addProperty("category", category);
		JsonObject key = new JsonObject();
		new TreeMap<>(keys).forEach((ch, item) -> key.add(String.valueOf(ch), item(item)));
		json.add("key", key);
		JsonArray rows = new JsonArray();
		pattern.forEach(rows::add);
		json.add("pattern", rows);
		JsonObject out = new JsonObject();
		if (count != 1) {
			out.addProperty("count", count);
		}
		out.addProperty("item", result.toString());
		json.add("result", out);
		json.addProperty("show_notification", true);
		return json;
	}

	private static JsonObject item(ResourceLocation id) {
		JsonObject json = new JsonObject();
		json.addProperty("item", id.toString());
		return json;
	}

	static JsonObject loot(String form, ResourceLocation id, GameView game) {
		JsonObject entry = new JsonObject();
		entry.addProperty("type", "minecraft:item");
		entry.addProperty("name", id.toString());
		switch (form) {
			case "tall_door" -> entry.add("conditions", array(stateCondition(id, "part", "bottom")));
			case "slab" -> entry.add("functions", array(setCount(id, "type", "double", 2)));
			case "layers" -> {
				JsonArray functions = new JsonArray();
				for (int n : game.layers(id)) {
					if (n > 1) {
						functions.add(setCount(id, "layers", String.valueOf(n), n));
					}
				}
				if (!functions.isEmpty()) {
					entry.add("functions", functions);
				}
			}
			default -> {
			}
		}
		JsonObject pool = new JsonObject();
		pool.addProperty("bonus_rolls", 0.0);
		JsonObject explosion = new JsonObject();
		explosion.addProperty("condition", "minecraft:survives_explosion");
		pool.add("conditions", array(explosion));
		pool.add("entries", array(entry));
		pool.addProperty("rolls", 1.0);
		JsonObject json = new JsonObject();
		json.addProperty("type", "minecraft:block");
		json.add("pools", array(pool));
		json.addProperty("random_sequence", id.getNamespace() + ":blocks/" + id.getPath());
		return json;
	}

	private static JsonObject setCount(ResourceLocation block, String property, String value, int count) {
		JsonObject f = new JsonObject();
		f.addProperty("function", "minecraft:set_count");
		f.addProperty("count", (float) count);
		f.addProperty("add", false);
		f.add("conditions", array(stateCondition(block, property, value)));
		return f;
	}

	private static JsonObject stateCondition(ResourceLocation block, String property, String value) {
		JsonObject c = new JsonObject();
		c.addProperty("condition", "minecraft:block_state_property");
		c.addProperty("block", block.toString());
		JsonObject props = new JsonObject();
		props.addProperty(property, value);
		c.add("properties", props);
		return c;
	}

	static JsonObject advancement(Material m, List<ResourceLocation> recipes) {
		String criterion = "has_" + m.manager();
		JsonObject json = new JsonObject();
		json.addProperty("parent", "minecraft:recipes/root");
		JsonObject items = new JsonObject();
		JsonArray ids = new JsonArray();
		ids.add(m.ingredient().toString());
		items.add("items", ids);
		JsonObject conditions = new JsonObject();
		conditions.add("items", array(items));
		JsonObject has = new JsonObject();
		has.add("conditions", conditions);
		has.addProperty("trigger", "minecraft:inventory_changed");
		JsonObject criteria = new JsonObject();
		criteria.add(criterion, has);
		json.add("criteria", criteria);
		JsonArray requirement = new JsonArray();
		requirement.add(criterion);
		JsonArray requirements = new JsonArray();
		requirements.add(requirement);
		json.add("requirements", requirements);
		JsonArray list = new JsonArray();
		recipes.forEach(r -> list.add(r.toString()));
		JsonObject rewards = new JsonObject();
		rewards.add("recipes", list);
		json.add("rewards", rewards);
		json.addProperty("sends_telemetry_event", false);
		return json;
	}

	/** Entries in Buildify's tags are optional, so they load whether or not Buildify is installed. */
	static JsonObject tag(ResourceLocation tag, Set<String> values) {
		boolean optional = tag.getNamespace().equals("buildify");
		JsonArray list = new JsonArray();
		for (String value : values) {
			if (optional) {
				JsonObject entry = new JsonObject();
				entry.addProperty("id", value);
				entry.addProperty("required", false);
				list.add(entry);
			} else {
				list.add(value);
			}
		}
		JsonObject json = new JsonObject();
		json.addProperty("replace", false);
		json.add("values", list);
		return json;
	}

	private static JsonArray array(JsonObject... objects) {
		JsonArray array = new JsonArray();
		for (JsonObject o : objects) {
			array.add(o);
		}
		return array;
	}

	static byte[] bytes(@Nullable JsonObject json) {
		return String.valueOf(json).getBytes(StandardCharsets.UTF_8);
	}
}
