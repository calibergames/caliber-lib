package com.calibermc.caliberlib.assets.client;

import com.calibermc.caliberlib.assets.GeneratedPack;
import com.calibermc.caliberlib.assets.Material;
import com.calibermc.caliberlib.assets.PackIndex;
import com.calibermc.caliberlib.assets.ShapeSpec;
import com.calibermc.caliberlib.assets.ShapeSpecs;
import com.calibermc.caliberlib.assets.Template;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;

/**
 * The client pack of one resource reload. Its file list comes from the
 * families alone; a file's content is made when the game reads it. Only
 * the per-family texture lookup reads the other packs, once per family.
 */
final class ClientGenerator {

	static final Logger LOGGER = LoggerFactory.getLogger("CaliberLib/assets");
	static final String FALLBACK_MODEL = "caliberlib:block/generated_fallback";
	private static final byte[] FALLBACK_BLOCKSTATE = ("{\"variants\":{\"\":{\"model\":\"" + FALLBACK_MODEL + "\"}}}")
			.getBytes(StandardCharsets.UTF_8);
	private static final byte[] FALLBACK_MODEL_JSON = ("{\"parent\":\"" + FALLBACK_MODEL + "\"}").getBytes(StandardCharsets.UTF_8);

	private final Map<ResourceLocation, FutureTask<Optional<TextureResolver.Resolved>>> resolved = new ConcurrentHashMap<>();
	private final Set<ResourceLocation> warnedDoors = ConcurrentHashMap.newKeySet();
	private volatile TextureResolver resolver;

	private ClientGenerator() {
	}

	/** Opens the client pack for these families. */
	static GeneratedPack open(List<Material> materials) {
		ClientGenerator generator = new ClientGenerator();
		PackIndex index = generator.index(materials, ShapeSpecs.get());
		return new GeneratedPack(PackType.CLIENT_RESOURCES, index, generator.resolved::clear);
	}

	private PackIndex index(List<Material> materials, Map<String, ShapeSpec> specs) {
		PackIndex index = new PackIndex();
		Map<String, Map<String, String>> lang = new TreeMap<>();
		Map<ResourceLocation, Supplier<Optional<byte[]>>> metadata = new ConcurrentHashMap<>();
		for (Material m : materials) {
			Supplier<Optional<TextureResolver.Resolved>> textures = () -> resolve(m);
			for (Material.Shape shape : m.shapes()) {
				ResourceLocation block = shape.block();
				String ns = block.getNamespace();
				String path = block.getPath();
				ShapeSpec spec = specs.get(shape.variant());
				if (spec == null) {
					index.add(ns, "blockstates/" + path + ".json", () -> FALLBACK_BLOCKSTATE);
					index.add(ns, "models/item/" + path + ".json", () -> FALLBACK_MODEL_JSON);
					continue;
				}
				lang.computeIfAbsent(ns, n -> new TreeMap<>())
						.put("block." + ns + "." + path, spec.name().replace("{m}", m.displayName()));
				index.add(ns, "blockstates/" + path + ".json", () -> textures.get()
						.map(r -> spec.blockstate().render(key -> value(key, block, r)))
						.orElse(FALLBACK_BLOCKSTATE));
				for (Map.Entry<String, Template> model : spec.models().entrySet()) {
					index.add(ns, "models/block/" + path + model.getKey() + ".json", () -> textures.get()
							.map(r -> model.getValue().render(key -> value(key, block, r)))
							.orElse(FALLBACK_MODEL_JSON));
				}
				index.add(ns, "models/item/" + path + ".json", () -> textures.get()
						.map(r -> spec.item().render(key -> value(key, block, r)))
						.orElse(FALLBACK_MODEL_JSON));
				if (shape.variant().equals("tall_door")) {
					for (ImageComposer.Part part : ImageComposer.Part.values()) {
						String suffix = "_" + part.name().toLowerCase(Locale.ROOT);
						index.add(ns, "textures/block/" + path + suffix + ".png", () -> doorPart(m, textures.get(), part));
						metadata.put(new ResourceLocation(ns, "textures/block/" + path + suffix + ".png.mcmeta"),
								() -> doorMetadata(textures.get()));
					}
					index.add(ns, "textures/item/" + path + ".png", () -> doorIcon(m, textures.get()));
				}
			}
		}
		lang.forEach((ns, entries) -> index.add(ns, "lang/en_us.json", () -> {
			JsonObject json = new JsonObject();
			entries.forEach(json::addProperty);
			return json.toString().getBytes(StandardCharsets.UTF_8);
		}));
		index.unlisted(location -> {
			Supplier<Optional<byte[]>> meta = metadata.get(location);
			if (meta == null) {
				return null;
			}
			Optional<byte[]> bytes = meta.get();
			return bytes.isPresent() ? bytes::get : null;
		});
		return index;
	}

	private static String value(String key, ResourceLocation block, TextureResolver.Resolved r) {
		String ns = block.getNamespace();
		String path = block.getPath();
		if (key.startsWith("m")) {
			return ns + ":block/" + path + key.substring(1);
		}
		return switch (key) {
			case "base" -> r.baseModel();
			case "item" -> ns + ":item/" + path;
			case "door_bottom" -> ns + ":block/" + path + "_bottom";
			case "door_middle" -> ns + ":block/" + path + "_middle";
			case "door_top" -> ns + ":block/" + path + "_top";
			default -> {
				String texture = r.slots().get(key);
				if (texture == null) {
					throw new IllegalArgumentException("No texture slot " + key);
				}
				yield texture;
			}
		};
	}

	/** Resolves a family once per reload; a family that cannot be resolved gets the fallback model. */
	private Optional<TextureResolver.Resolved> resolve(Material m) {
		FutureTask<Optional<TextureResolver.Resolved>> task = resolved.computeIfAbsent(m.id(), id -> new FutureTask<>(() -> {
			try {
				return Optional.of(resolver().resolve(m));
			} catch (RuntimeException e) {
				LOGGER.warn("Shapes of {} use the fallback model: {}", m.id(), e.getMessage());
				return Optional.empty();
			}
		}));
		task.run();
		try {
			return task.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return Optional.empty();
		} catch (ExecutionException e) {
			return Optional.empty();
		}
	}

	/**
	 * The other packs of this reload. Vanilla swaps in the new resource
	 * manager before any reload listener runs, so it already lists them.
	 */
	private TextureResolver resolver() {
		TextureResolver r = resolver;
		if (r == null) {
			synchronized (this) {
				r = resolver;
				if (r == null) {
					List<PackResources> others = Minecraft.getInstance().getResourceManager().listPacks()
							.filter(p -> !(p instanceof GeneratedPack)).toList();
					// Never closed: closing it would close the packs it shares with the game.
					ResourceManager manager = new MultiPackResourceManager(PackType.CLIENT_RESOURCES, others);
					r = resolver = new TextureResolver(manager);
				}
			}
		}
		return r;
	}

	/** A tall door without a source door (none set, or its textures not found) shows the missing texture. */
	private byte[] doorPart(Material m, Optional<TextureResolver.Resolved> r, ImageComposer.Part part) {
		TextureResolver.DoorTextures door = r.map(TextureResolver.Resolved::door).orElse(null);
		if (door == null) {
			noDoor(m, r);
			return ImageComposer.missing();
		}
		try (InputStream bottom = open(door.bottom()); InputStream top = open(door.top())) {
			return ImageComposer.doorPart(bottom, top, part);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private byte[] doorIcon(Material m, Optional<TextureResolver.Resolved> r) {
		TextureResolver.DoorTextures door = r.map(TextureResolver.Resolved::door).orElse(null);
		if (door == null || door.item() == null) {
			noDoor(m, r);
			return ImageComposer.missing();
		}
		try (InputStream icon = open(door.item())) {
			return ImageComposer.doorIcon(icon);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	/** Warns once per family; a family already on the fallback model was warned about then. */
	private void noDoor(Material m, Optional<TextureResolver.Resolved> r) {
		if (r.isPresent() && warnedDoors.add(m.id())) {
			LOGGER.warn("Tall door of {} has no source door textures (door: {})", m.id(), m.door());
		}
	}

	/** A tall door is animated when the source door's bottom texture is. */
	private Optional<byte[]> doorMetadata(Optional<TextureResolver.Resolved> r) {
		TextureResolver.DoorTextures door = r.map(TextureResolver.Resolved::door).orElse(null);
		if (door == null) {
			return Optional.empty();
		}
		Optional<Resource> meta = resolver().resource(texture(door.bottom(), ".png.mcmeta"));
		if (meta.isEmpty()) {
			return Optional.empty();
		}
		try (InputStream in = meta.get().open()) {
			return Optional.of(in.readAllBytes());
		} catch (IOException e) {
			return Optional.empty();
		}
	}

	private InputStream open(ResourceLocation texture) throws IOException {
		Optional<Resource> resource = resolver().resource(texture(texture, ".png"));
		if (resource.isEmpty()) {
			throw new IOException("Missing texture " + texture);
		}
		return resource.get().open();
	}

	private static ResourceLocation texture(ResourceLocation id, String extension) {
		return new ResourceLocation(id.getNamespace(), "textures/" + id.getPath() + extension);
	}
}
