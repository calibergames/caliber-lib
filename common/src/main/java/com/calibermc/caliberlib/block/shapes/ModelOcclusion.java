package com.calibermc.caliberlib.block.shapes;

import com.calibermc.caliberlib.CaliberLib;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Occlusion shapes taken from a shape's own models: the solid parts of the
 * model a state shows, turned as its blockstate turns it. Light and face
 * hiding then follow what is drawn. Solid parts are a JSON model's
 * unrotated elements, or an OBJ model's cuboid objects; the slanted
 * surface pieces are left out. Two tolerances close what the shingles
 * cover: a solid part ending {@link #SHINGLE_GAP} pixels or less below the
 * top counts as reaching it, and bounds snap to an eighth of a pixel.
 */
public final class ModelOcclusion {

	private static final String SPECS = "caliberlib/engine/shapes/";
	/** Variants by shape name; empty when the shape's spec cannot be read. */
	private static final Map<String, Optional<Variants>> VARIANTS = new ConcurrentHashMap<>();
	private static final Map<String, List<double[]>> SOLIDS = new ConcurrentHashMap<>();
	private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();

	/** Solid parts ending this close below the top are taken to reach it: the shingles close the rest. */
	static final double SHINGLE_GAP = 2;

	private ModelOcclusion() {
	}

	/**
	 * The occlusion shape of a state of a shape block of this variant
	 * ({@code roof_22}); {@code fallback} when its models have no solid parts
	 * or cannot be read.
	 */
	public static VoxelShape of(String variant, BlockState state, Supplier<VoxelShape> fallback) {
		Variants.Entry entry = variants(variant).map(v -> v.find(state)).orElse(null);
		VoxelShape shape = entry == null ? null : entry.shape();
		return shape == null || shape.isEmpty() ? fallback.get() : shape;
	}

	/** The shape of one blockstate variant key, e.g. {@code facing=east,half=bottom,type=straight}; for tests. */
	@Nullable
	static VoxelShape ofKey(String variant, String key) {
		return variants(variant).flatMap(v -> v.entries.stream().filter(e -> e.key.equals(key)).findFirst())
				.map(Variants.Entry::shape).orElse(null);
	}

	/** Every variant key of a shape's blockstate. */
	static List<String> keys(String variant) {
		return variants(variant).map(v -> v.entries.stream().map(e -> e.key).toList()).orElse(List.of());
	}

	/** The keys of a shape's blockstate that match this state; for tests. */
	static List<String> matchingKeys(String variant, BlockState state) {
		return variants(variant).map(v -> v.entries.stream().filter(e -> e.matches(state)).map(e -> e.key).toList())
				.orElse(List.of());
	}

	private static Optional<Variants> variants(String variant) {
		return VARIANTS.computeIfAbsent(variant, v -> Optional.ofNullable(read(v)));
	}

	private record Variants(List<Entry> entries, @Nullable List<String> properties, Map<String, Entry> byKey) {

		/**
		 * Indexes the entries by key when they all name the same properties in
		 * the same order (blockstate files list them so); otherwise lookups scan.
		 */
		static Variants of(List<Entry> entries) {
			List<String> properties = entries.isEmpty() ? null : entries.get(0).propertyNames();
			Map<String, Entry> byKey = new java.util.HashMap<>();
			for (Entry entry : entries) {
				if (properties != null && !entry.propertyNames().equals(properties)) {
					properties = null;
				}
				byKey.put(entry.key, entry);
			}
			return new Variants(entries, properties, Map.copyOf(byKey));
		}

		/** The entry for a state, or null when none matches. */
		@Nullable
		Entry find(BlockState state) {
			if (properties != null) {
				StringBuilder key = new StringBuilder();
				for (String name : properties) {
					Property<?> property = state.getBlock().getStateDefinition().getProperty(name);
					if (property == null) {
						return null;
					}
					if (key.length() > 0) {
						key.append(',');
					}
					key.append(name).append('=').append(name(state, property));
				}
				return byKey.get(key.toString());
			}
			for (Entry entry : entries) {
				if (entry.matches(state)) {
					return entry;
				}
			}
			return null;
		}

		private static final class Entry {
			final String key;
			/** The key's property names and values, in pairs. */
			final String[][] pairs;
			final String template;
			final int y;
			private volatile VoxelShape shape;

			Entry(String key, String template, int y) {
				this.key = key;
				this.template = template;
				this.y = y;
				String[] parts = key.isEmpty() ? new String[0] : key.split(",");
				this.pairs = new String[parts.length][];
				for (int i = 0; i < parts.length; i++) {
					int eq = parts[i].indexOf('=');
					pairs[i] = eq < 0 ? new String[] {parts[i], null} : new String[] {parts[i].substring(0, eq), parts[i].substring(eq + 1)};
				}
			}

			List<String> propertyNames() {
				return java.util.Arrays.stream(pairs).map(p -> p[0]).toList();
			}

			boolean matches(BlockState state) {
				for (String[] pair : pairs) {
					Property<?> property = state.getBlock().getStateDefinition().getProperty(pair[0]);
					if (property == null || pair[1] == null || !pair[1].equals(name(state, property))) {
						return false;
					}
				}
				return true;
			}

			VoxelShape shape() {
				VoxelShape s = shape;
				if (s == null) {
					VoxelShape out = Shapes.empty();
					for (double[] box : solids(template)) {
						double[] r = rotate(box, y);
						out = Shapes.or(out, Block.box(r[0], r[1], r[2], r[3], r[4], r[5]));
					}
					s = shape = out.optimize();
				}
				return s;
			}
		}
	}

	@Nullable
	private static Variants read(String variant) {
		try {
			JsonObject spec = json(SPECS + variant + ".json");
			if (spec == null) {
				warn("No shape spec " + variant);
				return null;
			}
			JsonObject models = spec.getAsJsonObject("models");
			List<Variants.Entry> entries = new ArrayList<>();
			for (Map.Entry<String, JsonElement> e : spec.getAsJsonObject("blockstate").getAsJsonObject("variants").entrySet()) {
				JsonObject v = e.getValue().getAsJsonObject();
				String model = v.get("model").getAsString();
				if (!model.startsWith("@m") || !models.has(model.substring(2))) {
					continue;
				}
				String parent = models.getAsJsonObject(model.substring(2)).get("parent").getAsString();
				entries.add(new Variants.Entry(e.getKey(), parent, v.has("y") ? v.get("y").getAsInt() : 0));
			}
			return Variants.of(List.copyOf(entries));
		} catch (RuntimeException e) {
			warn("Unreadable shape spec " + variant + ": " + e);
			return null;
		}
	}

	/** Boxes {x1, y1, z1, x2, y2, z2} in pixels, in the model's own orientation. */
	private static List<double[]> solids(String template) {
		return SOLIDS.computeIfAbsent(template, t -> {
			List<double[]> out = new ArrayList<>();
			try {
				ResourceLocation id = new ResourceLocation(t);
				JsonObject model = json("assets/" + id.getNamespace() + "/models/" + id.getPath() + ".json");
				if (model == null) {
					warn("No model " + t);
				} else if (model.has("loader") && model.get("loader").getAsString().endsWith(":obj")) {
					ResourceLocation obj = new ResourceLocation(model.get("model").getAsString());
					objSolids("assets/" + obj.getNamespace() + "/" + obj.getPath(), out);
				} else if (model.has("elements")) {
					for (JsonElement el : model.getAsJsonArray("elements")) {
						JsonObject e = el.getAsJsonObject();
						if (e.has("rotation") && e.getAsJsonObject("rotation").get("angle").getAsDouble() != 0) {
							continue;
						}
						double[] from = vec(e.getAsJsonArray("from"));
						double[] to = vec(e.getAsJsonArray("to"));
						add(out, from[0], from[1], from[2], to[0], to[1], to[2]);
					}
				}
			} catch (RuntimeException e) {
				warn("Unreadable model " + t + ": " + e);
				out.clear();
			}
			return List.copyOf(out);
		});
	}

	/**
	 * The bounds of each cuboid object of an OBJ file, in pixels: objects
	 * whose every face lies flat on an axis. Slanted shingles and hip caps
	 * are left out.
	 */
	static void objSolids(String path, List<double[]> out) {
		String text = text(path);
		if (text == null) {
			warn("No OBJ model " + path);
			return;
		}
		objSolidsOf(text, out);
	}

	/** {@link #objSolids} for the text of an OBJ file. */
	static void objSolidsOf(String text, List<double[]> out) {
		List<double[]> vertices = new ArrayList<>();
		List<List<double[]>> faces = new ArrayList<>();
		for (String raw : text.split("\n")) {
			String[] t = raw.trim().split("\\s+");
			switch (t[0]) {
				case "o" -> {
					cuboid(faces, out);
					faces = new ArrayList<>();
				}
				case "v" -> vertices.add(new double[] {Double.parseDouble(t[1]) * 16, Double.parseDouble(t[2]) * 16,
						Double.parseDouble(t[3]) * 16});
				case "f" -> {
					List<double[]> face = new ArrayList<>();
					for (int k = 1; k < t.length; k++) {
						face.add(vertices.get(Integer.parseInt(t[k].split("/")[0]) - 1));
					}
					faces.add(face);
				}
				default -> {
				}
			}
		}
		cuboid(faces, out);
	}

	/** Adds the object's bounds when every face is flat on an axis. */
	private static void cuboid(List<List<double[]>> faces, List<double[]> out) {
		if (faces.isEmpty()) {
			return;
		}
		double[] b = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE};
		for (List<double[]> face : faces) {
			boolean flat = false;
			for (int axis = 0; axis < 3 && !flat; axis++) {
				flat = true;
				for (double[] v : face) {
					flat &= Math.abs(v[axis] - face.get(0)[axis]) < 1.0E-3;
				}
			}
			if (!flat) {
				return;
			}
			for (double[] v : face) {
				for (int k = 0; k < 3; k++) {
					b[k] = Math.min(b[k], v[k]);
					b[k + 3] = Math.max(b[k + 3], v[k]);
				}
			}
		}
		add(out, b[0], b[1], b[2], b[3], b[4], b[5]);
	}

	/** Adds a box clamped to the block; nothing when it is empty there. */
	static void add(List<double[]> out, double x1, double y1, double z1, double x2, double y2, double z2) {
		double[] b = {clamp(Math.min(x1, x2)), clamp(Math.min(y1, y2)), clamp(Math.min(z1, z2)),
				clamp(Math.max(x1, x2)), clamp(Math.max(y1, y2)), clamp(Math.max(z1, z2))};
		if (b[4] >= 16 - SHINGLE_GAP) {
			b[4] = 16;
		}
		if (b[3] - b[0] > 1.0E-3 && b[4] - b[1] > 1.0E-3 && b[5] - b[2] > 1.0E-3) {
			out.add(b);
		}
	}

	/**
	 * Clamps to the block and snaps to an eighth of a pixel: models leave
	 * slivers of a sixty-fourth (15.984 for 16, 3.99999 for 4) that would
	 * open seams in the shape.
	 */
	static double clamp(double v) {
		return Math.rint(Math.max(0, Math.min(16, v)) * 8) / 8;
	}

	/** Turns a box about the block's vertical axis, as a blockstate's {@code y} does (90: east to south). */
	static double[] rotate(double[] b, int y) {
		return switch (((y % 360) + 360) % 360) {
			case 90 -> new double[] {16 - b[5], b[1], b[0], 16 - b[2], b[4], b[3]};
			case 180 -> new double[] {16 - b[3], b[1], 16 - b[5], 16 - b[0], b[4], 16 - b[2]};
			case 270 -> new double[] {b[2], b[1], 16 - b[3], b[5], b[4], 16 - b[0]};
			default -> b;
		};
	}

	private static <T extends Comparable<T>> String name(BlockState state, Property<T> property) {
		return property.getName(state.getValue(property));
	}

	private static double[] vec(com.google.gson.JsonArray a) {
		return new double[] {a.get(0).getAsDouble(), a.get(1).getAsDouble(), a.get(2).getAsDouble()};
	}

	@Nullable
	private static JsonObject json(String path) {
		String text = text(path);
		if (text == null) {
			return null;
		}
		JsonElement json = JsonParser.parseString(text);
		return json.isJsonObject() ? json.getAsJsonObject() : null;
	}

	@Nullable
	private static String text(String path) {
		try (InputStream in = ModelOcclusion.class.getClassLoader().getResourceAsStream(path)) {
			return in == null ? null : new String(in.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			return null;
		}
	}

	private static void warn(String message) {
		if (WARNED.add(message)) {
			CaliberLib.LOGGER.warn("{}; the block keeps its own occlusion shape", message);
		}
	}
}
