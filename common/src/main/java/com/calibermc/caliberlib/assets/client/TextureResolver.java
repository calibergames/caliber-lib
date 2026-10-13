package com.calibermc.caliberlib.assets.client;

import com.calibermc.caliberlib.assets.Material;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Finds a family's textures in the other packs of the current reload, from
 * its full block's blockstate and model. No name rules: what the source
 * mod's own model says is what the shapes use, unless the family sets a
 * slot by hand.
 */
final class TextureResolver {

	private static final int MAX_PARENTS = 16;
	private static final int MAX_REFERENCES = 8;
	private static final List<String> SIDE = List.of("side", "all", "texture", "north", "particle");
	private static final List<String> TOP = List.of("top", "end", "up", "all");
	private static final List<String> BOTTOM = List.of("bottom", "end", "down");
	private static final List<String> END = List.of("end", "top");
	private static final List<String> ATLAS_FOLDERS = List.of("block/", "item/", "entity/signs/");

	/** What a family's models need: texture slots and the full-block model. */
	record Resolved(Map<String, String> slots, String baseModel, @Nullable DoorTextures door) {
	}

	/** The source door's textures, for the tall door. */
	record DoorTextures(ResourceLocation bottom, ResourceLocation top, @Nullable ResourceLocation item) {
	}

	private final ResourceManager resources;

	TextureResolver(ResourceManager resources) {
		this.resources = resources;
	}

	/** Resolves a family; throws when its side texture cannot be found. */
	Resolved resolve(Material m) {
		Block source = BuiltInRegistries.BLOCK.get(m.source());
		String defaultModel = defaultModel(m.source(), source.defaultBlockState());
		Map<String, String> textures = defaultModel != null ? textures(defaultModel) : Map.of();
		Map<String, String> slots = new HashMap<>();
		pick(slots, "side", textures, SIDE, null);
		pick(slots, "top", textures, TOP, slots.get("side"));
		pick(slots, "bottom", textures, BOTTOM, slots.get("top"));
		pick(slots, "end", textures, END, slots.get("side"));
		pick(slots, "particle", textures, List.of("particle"), slots.get("side"));
		m.textures().forEach((slot, texture) -> slots.put(slot, texture.toString()));
		if (slots.get("side") == null) {
			throw new IllegalStateException("No side texture for " + m.id() + " in the model of " + m.source());
		}
		slots.putIfAbsent("top", slots.get("side"));
		slots.putIfAbsent("bottom", slots.get("top"));
		slots.putIfAbsent("end", slots.get("side"));
		slots.putIfAbsent("particle", slots.get("side"));
		String base = m.baseModel() != null ? m.baseModel().toString() : defaultModel != null ? defaultModel : "minecraft:block/stone";
		return new Resolved(Map.copyOf(slots), base, m.door() != null ? door(m.door()) : null);
	}

	private void pick(Map<String, String> slots, String slot, Map<String, String> textures, List<String> keys,
			@Nullable String fallback) {
		for (String key : keys) {
			String value = textures.get(key);
			if (value != null && exists(value)) {
				slots.put(slot, value);
				return;
			}
		}
		if (fallback != null) {
			slots.put(slot, fallback);
		}
	}

	@Nullable
	private DoorTextures door(ResourceLocation doorItem) {
		Item item = BuiltInRegistries.ITEM.get(doorItem);
		if (!(item instanceof BlockItem blockItem)) {
			return null;
		}
		Block block = blockItem.getBlock();
		String model = defaultModel(BuiltInRegistries.BLOCK.getKey(block), block.defaultBlockState());
		if (model == null) {
			return null;
		}
		Map<String, String> textures = textures(model);
		String bottom = textures.get("bottom");
		String top = textures.get("top");
		if (bottom == null || top == null || !exists(bottom) || !exists(top)) {
			return null;
		}
		Map<String, String> itemTextures = textures(doorItem.getNamespace() + ":item/" + doorItem.getPath());
		String icon = itemTextures.get("layer0");
		return new DoorTextures(new ResourceLocation(bottom), new ResourceLocation(top),
				icon != null && exists(icon) ? new ResourceLocation(icon) : null);
	}

	/** The model a block's default state shows, from its blockstate file. */
	@Nullable
	String defaultModel(ResourceLocation block, BlockState state) {
		JsonObject json = read(new ResourceLocation(block.getNamespace(), "blockstates/" + block.getPath() + ".json"));
		if (json == null) {
			return null;
		}
		if (json.has("variants")) {
			for (Map.Entry<String, JsonElement> e : json.getAsJsonObject("variants").entrySet()) {
				if (matches(e.getKey(), state)) {
					return model(e.getValue());
				}
			}
			return null;
		}
		if (json.has("multipart")) {
			JsonArray parts = json.getAsJsonArray("multipart");
			for (JsonElement part : parts) {
				if (!part.getAsJsonObject().has("when")) {
					return model(part.getAsJsonObject().get("apply"));
				}
			}
			return parts.isEmpty() ? null : model(parts.get(0).getAsJsonObject().get("apply"));
		}
		return null;
	}

	/** Whether a variant key such as {@code axis=y} matches the state; the empty key matches all. */
	static boolean matches(String key, BlockState state) {
		if (key.isEmpty()) {
			return true;
		}
		for (String pair : key.split(",")) {
			int eq = pair.indexOf('=');
			if (eq < 0) {
				return false;
			}
			Property<?> property = state.getBlock().getStateDefinition().getProperty(pair.substring(0, eq));
			if (property == null || !pair.substring(eq + 1).equals(valueName(state, property))) {
				return false;
			}
		}
		return true;
	}

	private static <T extends Comparable<T>> String valueName(BlockState state, Property<T> property) {
		return property.getName(state.getValue(property));
	}

	/** A variant entry's model: the first of a weighted list. */
	@Nullable
	private static String model(JsonElement entry) {
		JsonElement first = entry.isJsonArray() ? (entry.getAsJsonArray().isEmpty() ? null : entry.getAsJsonArray().get(0)) : entry;
		return first != null && first.isJsonObject() && first.getAsJsonObject().has("model")
				? first.getAsJsonObject().get("model").getAsString() : null;
	}

	/** The model's textures with its parents' merged in (the child wins), references resolved. */
	Map<String, String> textures(String model) {
		Map<String, String> textures = new HashMap<>();
		String current = model;
		for (int i = 0; i < MAX_PARENTS && current != null && !current.startsWith("builtin/"); i++) {
			ResourceLocation id = new ResourceLocation(current);
			JsonObject json = read(new ResourceLocation(id.getNamespace(), "models/" + id.getPath() + ".json"));
			if (json == null) {
				break;
			}
			if (json.has("textures")) {
				for (Map.Entry<String, JsonElement> e : json.getAsJsonObject("textures").entrySet()) {
					if (e.getValue().isJsonPrimitive()) {
						textures.putIfAbsent(e.getKey(), e.getValue().getAsString());
					}
				}
			}
			current = json.has("parent") ? json.get("parent").getAsString() : null;
		}
		Map<String, String> out = new HashMap<>();
		for (String key : textures.keySet()) {
			String value = textures.get(key);
			for (int i = 0; i < MAX_REFERENCES && value != null && value.startsWith("#"); i++) {
				value = textures.get(value.substring(1));
			}
			if (value != null && !value.startsWith("#")) {
				out.put(key, new ResourceLocation(value).toString());
			}
		}
		return out;
	}

	/** Whether a texture exists in an atlas folder. */
	boolean exists(String texture) {
		ResourceLocation id = ResourceLocation.tryParse(texture);
		if (id == null || ATLAS_FOLDERS.stream().noneMatch(id.getPath()::startsWith)) {
			return false;
		}
		return resources.getResource(new ResourceLocation(id.getNamespace(), "textures/" + id.getPath() + ".png")).isPresent();
	}

	Optional<Resource> resource(ResourceLocation location) {
		return resources.getResource(location);
	}

	@Nullable
	private JsonObject read(ResourceLocation location) {
		Optional<Resource> resource = resources.getResource(location);
		if (resource.isEmpty()) {
			return null;
		}
		try (Reader reader = resource.get().openAsReader()) {
			JsonElement json = JsonParser.parseReader(reader);
			return json.isJsonObject() ? json.getAsJsonObject() : null;
		} catch (IOException | RuntimeException e) {
			return null;
		}
	}
}
