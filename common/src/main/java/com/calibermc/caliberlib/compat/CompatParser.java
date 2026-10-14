package com.calibermc.caliberlib.compat;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Reads one compat file (format 1). A bad file or entry is logged with the
 * file and the entry's index and skipped; nothing here throws.
 */
public final class CompatParser {

	public static final int FORMAT = 1;
	private static final Set<String> FIELDS = Set.of("block", "type", "variants", "name", "textures", "made_for", "remove");

	private CompatParser() {
	}

	public static List<CompatEntry> parse(String text, CompatSource source, CompatLog log) {
		JsonObject root;
		try {
			JsonElement e = JsonParser.parseString(text);
			if (!e.isJsonObject()) {
				log.error(source + ": not a JSON object; file skipped");
				return List.of();
			}
			root = e.getAsJsonObject();
		} catch (JsonParseException | IllegalStateException e) {
			log.error(source + ": not valid JSON (" + e.getMessage() + "); file skipped");
			return List.of();
		}
		JsonElement format = root.get("format");
		if (format == null || !format.isJsonPrimitive() || !format.getAsJsonPrimitive().isNumber()) {
			log.error(source + ": no \"format\" number; file skipped");
			return List.of();
		}
		if (format.getAsDouble() != FORMAT) {
			log.warn(source + ": format " + format + " is not one this version reads (" + FORMAT + "); file skipped");
			return List.of();
		}
		JsonElement entries = root.get("entries");
		if (entries == null || !entries.isJsonArray()) {
			log.error(source + ": no \"entries\" list; file skipped");
			return List.of();
		}
		Set<String> unknown = new TreeSet<>();
		for (String k : root.keySet()) {
			if (!k.equals("format") && !k.equals("entries")) {
				unknown.add(k);
			}
		}
		List<CompatEntry> out = new ArrayList<>();
		JsonArray list = entries.getAsJsonArray();
		for (int i = 0; i < list.size(); i++) {
			CompatEntry entry = entry(list.get(i), source, i, log, unknown);
			if (entry != null) {
				out.add(entry);
			}
		}
		if (!unknown.isEmpty()) {
			log.warn(source + ": unknown fields ignored: " + String.join(", ", unknown));
		}
		return out;
	}

	@Nullable
	private static CompatEntry entry(JsonElement element, CompatSource source, int index, CompatLog log, Set<String> unknown) {
		String where = source + " entry " + index;
		if (!element.isJsonObject()) {
			log.error(where + ": not an object; skipped");
			return null;
		}
		JsonObject o = element.getAsJsonObject();
		for (String k : o.keySet()) {
			if (!FIELDS.contains(k)) {
				unknown.add(k);
			}
		}
		ResourceLocation block = id(o, "block", where, log);
		if (block == null) {
			if (!o.has("block")) {
				log.error(where + ": no \"block\"; skipped");
			}
			return null;
		}
		boolean remove = o.has("remove") && bool(o.get("remove"));
		String type = string(o, "type");
		if (remove) {
			if (source.kind() != CompatSource.Kind.CONFIG) {
				log.warn(where + ": \"remove\" works only in the config folder; ignored");
				return null;
			}
			if (type != null && CompatTypes.type(type) == null) {
				log.warn(where + ": unknown type \"" + type + "\"; skipped");
				return null;
			}
			return new CompatEntry(block, type, null, null, Map.of(), null, true, source, index);
		}
		if (type == null) {
			log.error(where + ": no \"type\"; skipped");
			return null;
		}
		if (CompatTypes.NOT_YET.contains(type)) {
			log.warn(where + ": type \"" + type + "\" is not supported yet; skipped");
			return null;
		}
		if (CompatTypes.type(type) == null) {
			log.error(where + ": unknown type \"" + type + "\"; skipped");
			return null;
		}
		if (source.kind() == CompatSource.Kind.JAR && !block.getNamespace().equals(source.modId())) {
			log.warn(where + ": " + block + " is not a block of " + source.modId() + "; a mod's file lists only its own blocks; skipped");
			return null;
		}
		ResourceLocation name = null;
		if (o.has("name")) {
			name = id(o, "name", where, log);
			if (name == null) {
				return null;
			}
			if (source.kind() == CompatSource.Kind.JAR && !name.getNamespace().equals(source.modId())) {
				log.warn(where + ": name " + name + " is outside " + source.modId() + "'s namespace; skipped");
				return null;
			}
		}
		List<String> variants = null;
		if (o.has("variants")) {
			JsonElement v = o.get("variants");
			if (!v.isJsonArray()) {
				log.error(where + ": \"variants\" is not a list; skipped");
				return null;
			}
			variants = new ArrayList<>();
			for (JsonElement e : v.getAsJsonArray()) {
				String variant = e.isJsonPrimitive() ? e.getAsString() : String.valueOf(e);
				if (CompatTypes.variant(variant) == null) {
					log.warn(where + ": unknown variant \"" + variant + "\" left out");
				} else if (!variants.contains(variant)) {
					variants.add(variant);
				}
			}
		}
		Map<String, ResourceLocation> textures = new LinkedHashMap<>();
		if (o.has("textures")) {
			JsonElement t = o.get("textures");
			if (!t.isJsonObject()) {
				log.warn(where + ": \"textures\" is not an object; ignored");
			} else {
				for (Map.Entry<String, JsonElement> e : t.getAsJsonObject().entrySet()) {
					ResourceLocation texture = e.getValue().isJsonPrimitive() ? ResourceLocation.tryParse(e.getValue().getAsString()) : null;
					if (texture == null) {
						log.warn(where + ": texture \"" + e.getKey() + "\" is not an id; ignored");
					} else if (!texture.getNamespace().equals(block.getNamespace())) {
						log.warn(where + ": texture " + texture + " is not the source mod's own; ignored");
					} else {
						textures.put(e.getKey(), texture);
					}
				}
			}
		}
		String madeFor = string(o, "made_for");
		return new CompatEntry(block, type, variants == null ? null : List.copyOf(variants), name, Map.copyOf(textures),
				madeFor, false, source, index);
	}

	@Nullable
	private static ResourceLocation id(JsonObject o, String field, String where, CompatLog log) {
		JsonElement e = o.get(field);
		if (e == null) {
			return null;
		}
		String text = e.isJsonPrimitive() ? e.getAsString() : null;
		ResourceLocation id = text != null && text.contains(":") ? ResourceLocation.tryParse(text) : null;
		if (id == null) {
			log.error(where + ": \"" + field + "\" is not a namespaced id: " + e + "; skipped");
			return null;
		}
		return id;
	}

	@Nullable
	private static String string(JsonObject o, String field) {
		JsonElement e = o.get(field);
		return e != null && e.isJsonPrimitive() ? e.getAsString() : null;
	}

	private static boolean bool(JsonElement e) {
		return e.isJsonPrimitive() && e.getAsJsonPrimitive().isBoolean() && e.getAsBoolean();
	}
}
