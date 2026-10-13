package com.calibermc.caliberlib.fabric.client;

import com.calibermc.caliberlib.assets.client.ObjModel;
import com.calibermc.caliberlib.assets.client.ObjUnbakedModel;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads block models whose parent is one of the library's OBJ templates
 * (the 67-degree roofs, which Forge loads with its built-in OBJ loader).
 * One instance per resource reload.
 */
public final class ObjModelProvider implements ModelResourceProvider {

	/** Only models with this in their path are looked at; the OBJ templates are the roof_67 ones. */
	private static final String MARK = "roof_67";

	private final ResourceManager resources;
	private final Map<ResourceLocation, ObjModel> objs = new ConcurrentHashMap<>();

	public ObjModelProvider(ResourceManager resources) {
		this.resources = resources;
	}

	@Nullable
	@Override
	public UnbakedModel loadModelResource(ResourceLocation id, ModelProviderContext context) {
		if (!id.getPath().contains(MARK)) {
			return null;
		}
		JsonObject model = read(modelFile(id));
		if (model == null || !model.has("parent")) {
			return null;
		}
		JsonObject template = read(modelFile(new ResourceLocation(model.get("parent").getAsString())));
		if (template == null || !template.has("loader") || !template.get("loader").getAsString().endsWith(":obj")) {
			return null;
		}
		ResourceLocation objFile = new ResourceLocation(template.get("model").getAsString());
		ObjModel obj = objs.computeIfAbsent(objFile, this::loadObj);
		if (obj == null) {
			return null;
		}
		Map<String, String> textures = new HashMap<>();
		putTextures(textures, template);
		putTextures(textures, model);
		// Forge's OBJ loader options and their defaults.
		ObjModel.Options options = new ObjModel.Options(flag(template, "flip_v", false), flag(template, "emissive_ambient", true),
				flag(template, "automatic_culling", true));
		return new ObjUnbakedModel(obj, textures, options);
	}

	private static boolean flag(JsonObject json, String key, boolean fallback) {
		return json.has(key) ? json.get(key).getAsBoolean() : fallback;
	}

	private static void putTextures(Map<String, String> out, JsonObject model) {
		if (model.has("textures")) {
			for (Map.Entry<String, JsonElement> e : model.getAsJsonObject("textures").entrySet()) {
				out.put(e.getKey(), e.getValue().getAsString());
			}
		}
	}

	@Nullable
	private ObjModel loadObj(ResourceLocation objFile) {
		String obj = text(objFile);
		if (obj == null) {
			return null;
		}
		String mtl = "";
		for (String line : obj.split("\n")) {
			if (line.startsWith("mtllib ")) {
				String path = objFile.getPath();
				String folder = path.substring(0, path.lastIndexOf('/') + 1);
				String text = text(new ResourceLocation(objFile.getNamespace(), folder + line.substring(7).trim()));
				mtl = text != null ? text : "";
			}
		}
		return ObjModel.parse(obj, mtl);
	}

	private static ResourceLocation modelFile(ResourceLocation model) {
		return new ResourceLocation(model.getNamespace(), "models/" + model.getPath() + ".json");
	}

	@Nullable
	private JsonObject read(ResourceLocation file) {
		Optional<Resource> resource = resources.getResource(file);
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

	@Nullable
	private String text(ResourceLocation file) {
		Optional<Resource> resource = resources.getResource(file);
		if (resource.isEmpty()) {
			return null;
		}
		try (InputStream in = resource.get().open()) {
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			return null;
		}
	}
}
