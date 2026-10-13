package com.calibermc.caliberlib.assets.client;

import com.calibermc.caliberlib.assets.GameView;
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
import java.util.TreeSet;
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
	private final PackIndex.Timer time = new PackIndex.Timer();
	private final Supplier<ResourceManager> others;
	private final GameView game;
	private volatile TextureResolver resolver;

	/**
	 * @param others the other packs of the reload, asked for on first use
	 */
	ClientGenerator(Supplier<ResourceManager> others, GameView game) {
		this.others = others;
		this.game = game;
	}

	/** Opens the client pack for these families. */
	static GeneratedPack open(List<Material> materials) {
		ClientGenerator generator = new ClientGenerator(ClientGenerator::reloadPacks, GameView.LIVE);
		PackIndex index = generator.index(materials, ShapeSpecs.get());
		index.timed(generator.time);
		return new GeneratedPack(PackType.CLIENT_RESOURCES, index, () -> {
			LOGGER.info("Generated {} client files for {} families in {} ms of thread time", generator.time.count(),
					materials.size(), generator.time.millis());
			generator.resolved.clear();
		});
	}

	/**
	 * The other packs of this reload. Vanilla swaps in the new resource
	 * manager before any reload listener runs, so it already lists them.
	 */
	private static ResourceManager reloadPacks() {
		List<PackResources> packs = Minecraft.getInstance().getResourceManager().listPacks()
				.filter(p -> !(p instanceof GeneratedPack)).toList();
		// Never closed: closing it would close the packs it shares with the game.
		return new MultiPackResourceManager(PackType.CLIENT_RESOURCES, packs);
	}

	PackIndex index(List<Material> materials, Map<String, ShapeSpec> specs) {
		PackIndex index = new PackIndex();
		// Language, then namespace, then key to name.
		Set<String> languages = new TreeSet<>();
		specs.values().forEach(spec -> languages.addAll(spec.names().keySet()));
		Map<String, Map<String, Map<String, String>>> lang = new TreeMap<>();
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
				for (String language : languages) {
					// Other languages name only what they translate; the game falls back to en_us for the rest,
					// so a higher pack's en_us name is never hidden behind a copy of ours.
					if (language.equals(ShapeSpec.DEFAULT_LANGUAGE) || spec.names().containsKey(language)) {
						lang.computeIfAbsent(language, l -> new TreeMap<>()).computeIfAbsent(ns, n -> new TreeMap<>())
								.put("block." + ns + "." + path, spec.name(language).replace("{m}", m.displayName()));
					}
				}
				index.add(ns, "blockstates/" + path + ".json", () -> textures.get()
						.map(r -> spec.blockstate().render(key -> value(key, block, r)))
						.orElse(FALLBACK_BLOCKSTATE));
				for (Map.Entry<String, Template> model : spec.models().entrySet()) {
					index.add(ns, "models/block/" + path + model.getKey() + ".json", () -> textures.get()
							.map(r -> cutout(m, model.getValue().render(key -> value(key, block, r))))
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
		lang.forEach((language, byNamespace) -> byNamespace.forEach((ns, entries) -> index.add(ns, "lang/" + language + ".json", () -> {
			JsonObject json = new JsonObject();
			entries.forEach(json::addProperty);
			return json.toString().getBytes(StandardCharsets.UTF_8);
		})));
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

	private static final byte[] CUTOUT = "{\"render_type\":\"minecraft:cutout\",".getBytes(StandardCharsets.UTF_8);

	/**
	 * A cut-out family's block models name their render type, which Forge
	 * and NeoForge read from the model (Fabric sets it in code, and vanilla
	 * ignores the key).
	 */
	private static byte[] cutout(Material m, byte[] model) {
		if (!m.cutout() || model.length < 2 || model[0] != '{' || model[1] == '}') {
			return model;
		}
		byte[] out = new byte[model.length + CUTOUT.length - 1];
		System.arraycopy(CUTOUT, 0, out, 0, CUTOUT.length);
		System.arraycopy(model, 1, out, CUTOUT.length, model.length - 1);
		return out;
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

	private TextureResolver resolver() {
		TextureResolver r = resolver;
		if (r == null) {
			synchronized (this) {
				r = resolver;
				if (r == null) {
					r = resolver = new TextureResolver(others.get(), game);
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
