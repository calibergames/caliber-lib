package com.calibermc.caliberlib.assets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Everything the engine writes for one shape variant, for any material:
 * the blockstate, block and item models (as templates), the loot form, the
 * recipe forms, the tags and the name. Specs ship in the jar under
 * {@code caliberlib/engine/shapes/} and are read once.
 *
 * @param models   model suffix to template, e.g. {@code "_top"}
 * @param usesBase whether the blockstate shows the family's full-block model
 * @param loot     {@code self}, {@code slab}, {@code layers} or {@code tall_door}
 * @param names    language code to name, with {@code {m}} for the material;
 *                 always has {@code en_us}, which other languages fall back to
 */
public record ShapeSpec(String variant, Template blockstate, Map<String, Template> models, Template item,
		boolean usesBase, String loot, List<Recipe> recipes, List<String> blockTags,
		Map<MaterialKind, List<String>> itemTags, Map<String, String> names) {

	/** The language every shape has a name in. */
	public static final String DEFAULT_LANGUAGE = "en_us";

	/** The English name, with {@code {m}} for the material. */
	public String name() {
		return names.get(DEFAULT_LANGUAGE);
	}

	/** The name in a language, or the English one when the spec has none for it. */
	public String name(String language) {
		return names.getOrDefault(language, name());
	}

	/**
	 * One recipe form.
	 *
	 * @param type    {@code cut}, {@code cut_sibling}, {@code shaped}, {@code tall_door} or {@code convert}
	 * @param sibling for {@code cut_sibling} and {@code convert}: the other shape's variant
	 * @param kind    the only kind this form applies to, or null for both
	 */
	public record Recipe(String type, int count, List<String> pattern, String sibling, MaterialKind kind) {
	}

	private static final Set<String> KEYS = Set.of("variant", "reference", "blockstate", "models", "item", "uses_base",
			"loot", "recipes", "tags", "name");
	private static final Set<String> RECIPE_TYPES = Set.of("cut", "cut_sibling", "shaped", "tall_door", "convert");
	private static final Set<String> LOOT_FORMS = Set.of("self", "slab", "layers", "tall_door");

	static ShapeSpec read(InputStream in) throws IOException {
		try (InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
			return parse(JsonParser.parseReader(reader).getAsJsonObject());
		}
	}

	static ShapeSpec parse(JsonObject json) {
		for (String key : json.keySet()) {
			if (!KEYS.contains(key)) {
				throw new IllegalArgumentException("Unknown spec key " + key);
			}
		}
		String variant = json.get("variant").getAsString();
		Map<String, Template> models = new LinkedHashMap<>();
		for (Map.Entry<String, JsonElement> e : json.getAsJsonObject("models").entrySet()) {
			models.put(e.getKey(), Template.compile(e.getValue()));
		}
		String loot = json.get("loot").getAsString();
		if (!LOOT_FORMS.contains(loot)) {
			throw new IllegalArgumentException(variant + ": unknown loot form " + loot);
		}
		List<Recipe> recipes = new ArrayList<>();
		for (JsonElement e : json.getAsJsonArray("recipes")) {
			JsonObject r = e.getAsJsonObject();
			String type = r.get("type").getAsString();
			if (!RECIPE_TYPES.contains(type)) {
				throw new IllegalArgumentException(variant + ": unknown recipe type " + type);
			}
			List<String> pattern = new ArrayList<>();
			if (r.has("pattern")) {
				r.getAsJsonArray("pattern").forEach(p -> pattern.add(p.getAsString()));
			}
			recipes.add(new Recipe(type, r.has("count") ? r.get("count").getAsInt() : 1, List.copyOf(pattern),
					r.has("sibling") ? r.get("sibling").getAsString() : null,
					r.has("kind") ? kind(r.get("kind").getAsString()) : null));
		}
		JsonObject tags = json.getAsJsonObject("tags");
		Map<MaterialKind, List<String>> itemTags = new LinkedHashMap<>();
		JsonObject items = tags.getAsJsonObject("item");
		for (MaterialKind kind : MaterialKind.values()) {
			itemTags.put(kind, strings(items.getAsJsonArray(kind.key())));
		}
		return new ShapeSpec(variant, Template.compile(json.get("blockstate")), Map.copyOf(models),
				Template.compile(json.get("item")), json.get("uses_base").getAsBoolean(), loot, List.copyOf(recipes),
				strings(tags.getAsJsonArray("block")), Map.copyOf(itemTags), names(variant, json.getAsJsonObject("name")));
	}

	private static Map<String, String> names(String variant, JsonObject json) {
		Map<String, String> names = new LinkedHashMap<>();
		json.entrySet().forEach(e -> names.put(e.getKey(), e.getValue().getAsString()));
		if (!names.containsKey(DEFAULT_LANGUAGE)) {
			throw new IllegalArgumentException(variant + ": no " + DEFAULT_LANGUAGE + " name");
		}
		return Map.copyOf(names);
	}

	private static MaterialKind kind(String key) {
		for (MaterialKind kind : MaterialKind.values()) {
			if (kind.key().equals(key)) {
				return kind;
			}
		}
		throw new IllegalArgumentException("Unknown kind " + key);
	}

	private static List<String> strings(JsonArray array) {
		List<String> out = new ArrayList<>();
		array.forEach(e -> out.add(e.getAsString()));
		return List.copyOf(out);
	}
}
