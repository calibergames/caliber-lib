package com.calibermc.caliberlib.assets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Families read from a family table: one JSON entry per family with the
 * engine's inputs, and the items the source mods register.
 */
final class FamilyTable {

	private static final Set<String> FIVE_LAYERS = Set.of("corner", "corner_slab", "corner_slab_vertical", "eighth",
			"pillar", "quarter", "quarter_vertical");
	/** For blocks the test game does not have: the value a default state usually takes, per property value. */
	private static final List<String> DEFAULT_VALUES = List.of("y", "north", "lower", "bottom", "false", "left", "0", "none",
			"floor", "single", "straight");

	final List<Material> families = new ArrayList<>();
	final Set<ResourceLocation> items = new HashSet<>();
	/** Sources the table says fire does not take ({@code "burns": false}); other mods' blocks burn otherwise. */
	final Set<ResourceLocation> nonBurning = new HashSet<>();

	FamilyTable(Path file) throws IOException {
		JsonObject json = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
		json.getAsJsonArray("items").forEach(i -> items.add(new ResourceLocation(i.getAsString())));
		for (JsonElement el : json.getAsJsonArray("families")) {
			JsonObject f = el.getAsJsonObject();
			List<Material.Shape> shapes = new ArrayList<>();
			Map<String, ResourceLocation> siblings = new LinkedHashMap<>();
			f.getAsJsonObject("shapes").entrySet().forEach(e -> {
				ResourceLocation id = new ResourceLocation(e.getValue().getAsString());
				shapes.add(new Material.Shape(e.getKey(), id));
				siblings.put(e.getKey(), id);
				items.add(id);
			});
			f.getAsJsonObject("siblings").entrySet()
					.forEach(e -> siblings.putIfAbsent(e.getKey(), new ResourceLocation(e.getValue().getAsString())));
			Map<String, ResourceLocation> textures = new LinkedHashMap<>();
			f.getAsJsonObject("textures").entrySet().forEach(e -> textures.put(e.getKey(), new ResourceLocation(e.getValue().getAsString())));
			MaterialKind kind = f.get("kind").getAsString().equals("wood") ? MaterialKind.WOOD : MaterialKind.STONE;
			if (f.has("burns") && !f.get("burns").getAsBoolean()) {
				nonBurning.add(id(f, "source"));
			}
			families.add(new Material(new ResourceLocation(f.get("id").getAsString()), f.get("owner").getAsString(),
					f.get("manager").getAsString(), kind, f.get("display").getAsString(), id(f, "source"), id(f, "ingredient"),
					id(f, "door"), id(f, "base_model"), textures, siblings, shapes, false, null, tool(f)));
		}
	}

	@Nullable
	private static MiningTool tool(JsonObject f) {
		JsonElement e = f.get("tool");
		return e == null || e.isJsonNull() ? null : MiningTool.valueOf(e.getAsString().toUpperCase(java.util.Locale.ROOT));
	}

	@Nullable
	private static ResourceLocation id(JsonObject f, String key) {
		JsonElement e = f.get(key);
		return e == null || e.isJsonNull() ? null : new ResourceLocation(e.getAsString());
	}

	/**
	 * The game as the families' mods would make it: their items, the shapes'
	 * layer counts, and default states, which for blocks of mods not loaded
	 * in the test come from the blockstate file's values.
	 */
	GameView game(JarResources resources) {
		Map<ResourceLocation, String> variants = new HashMap<>();
		families.forEach(m -> m.shapes().forEach(s -> variants.put(s.block(), s.variant())));
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
				if (block.getNamespace().equals("minecraft")) {
					return GameView.LIVE.defaultState(block);
				}
				return guessedDefault(resources, block);
			}

			@Override
			public boolean burns(ResourceLocation block) {
				return !nonBurning.contains(block) && Fixtures.burns(block);
			}

			@Nullable
			@Override
			public ResourceLocation blockOf(ResourceLocation item) {
				if (item.getNamespace().equals("minecraft")) {
					return GameView.LIVE.blockOf(item);
				}
				return resources.getResource(new ResourceLocation(item.getNamespace(), "blockstates/" + item.getPath() + ".json"))
						.isPresent() ? item : null;
			}
		};
	}

	/** Each property's most default-looking value among the blockstate file's variant keys. */
	private static Map<String, String> guessedDefault(JarResources resources, ResourceLocation block) {
		Map<String, Set<String>> values = new LinkedHashMap<>();
		resources.getResource(new ResourceLocation(block.getNamespace(), "blockstates/" + block.getPath() + ".json")).ifPresent(r -> {
			try (var reader = r.openAsReader()) {
				JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
				if (json.has("variants")) {
					for (String key : json.getAsJsonObject("variants").keySet()) {
						for (String pair : key.split(",")) {
							int eq = pair.indexOf('=');
							if (eq > 0) {
								values.computeIfAbsent(pair.substring(0, eq), k -> new java.util.LinkedHashSet<>()).add(pair.substring(eq + 1));
							}
						}
					}
				}
			} catch (IOException e) {
				throw new java.io.UncheckedIOException(e);
			}
		});
		Map<String, String> state = new LinkedHashMap<>();
		values.forEach((property, options) -> state.put(property, DEFAULT_VALUES.stream().filter(options::contains).findFirst()
				.orElse(options.iterator().next())));
		return state;
	}
}
