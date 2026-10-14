package com.calibermc.caliberlib.assets;

import com.calibermc.caliberlib.assets.client.ClientAssets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Generates every file of the test families offline and compares them with
 * the goldens in {@code goldens/}; checks that every reference in them
 * resolves. {@code gradlew :common:test -PwriteGoldens} rewrites the goldens and
 * fails, so the rewrite never passes as a check.
 */
class EngineGoldenTest {

	private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
	private static Map<String, String> client;
	private static Map<String, String> data;
	private static GameView game;

	@BeforeAll
	static void generate() {
		Fixtures.bootstrap();
		List<Material> families = Fixtures.families();
		game = Fixtures.game(families);
		client = read(ClientAssets.index(families, new ClasspathResources(), game));
		data = read(AssetEngine.dataIndex(families, game));
	}

	@Test
	void familiesHaveTheReleasedShapeCounts() {
		Map<String, Integer> counts = new TreeMap<>();
		Fixtures.families().forEach(m -> counts.put(m.id().toString(), m.shapes().size()));
		assertEquals(19, counts.get("caliber:andesite"));
		assertEquals(16, counts.get("caliber:oak"));
		assertEquals(24, counts.get("caliber:stripped_oak"));
		assertEquals(4, counts.get("caliber:oak_shingle"));
	}

	@Test
	void clientFilesMatchTheGoldens() throws IOException {
		compare("client", client);
	}

	@Test
	void dataFilesMatchTheGoldens() throws IOException {
		compare("data", data);
	}

	@Test
	void noTwoShapedRecipesShareAPatternAndIngredients() {
		Map<String, String> seen = new TreeMap<>();
		data.forEach((path, json) -> {
			if (!path.contains(":recipes/")) {
				return;
			}
			JsonObject recipe = JsonParser.parseString(json).getAsJsonObject();
			if (!recipe.get("type").getAsString().equals("minecraft:crafting_shaped")) {
				return;
			}
			// The grid with each symbol replaced by its ingredient.
			JsonObject key = recipe.getAsJsonObject("key");
			StringBuilder grid = new StringBuilder();
			for (JsonElement row : recipe.getAsJsonArray("pattern")) {
				for (char c : row.getAsString().toCharArray()) {
					grid.append(c == ' ' ? "_" : GSON.toJson(sort(key.get(String.valueOf(c))))).append('|');
				}
				grid.append('/');
			}
			String clash = seen.put(grid.toString(), path);
			assertTrue(clash == null, path + " has the same pattern and ingredients as " + clash);
		});
		assertTrue(seen.size() >= 3, "shaped recipes checked: " + seen.size());
	}

	@Test
	void everyClientReferenceResolves() {
		List<String> missing = new ArrayList<>();
		client.forEach((path, content) -> {
			if (!path.endsWith(".json")) {
				return;
			}
			JsonElement json = JsonParser.parseString(content);
			if (path.contains(":blockstates/")) {
				walk(json, "model", model -> checkModel(path, model, missing));
			} else if (path.contains(":models/")) {
				JsonObject o = json.getAsJsonObject();
				if (o.has("parent") && !o.get("parent").getAsString().startsWith("builtin/")) {
					checkModel(path, o.get("parent").getAsString(), missing);
				}
				if (o.has("textures")) {
					for (Map.Entry<String, JsonElement> e : o.getAsJsonObject("textures").entrySet()) {
						String texture = e.getValue().getAsString();
						if (!texture.startsWith("#") && !texture.equals("minecraft:missingno")
								&& !exists(texture, "textures/", ".png")) {
							missing.add(path + " texture " + texture);
						}
					}
				}
			}
		});
		assertEquals(List.of(), missing);
	}

	@Test
	void everyDataReferenceResolves() {
		List<String> missing = new ArrayList<>();
		TreeSet<String> recipes = new TreeSet<>();
		data.keySet().stream().filter(p -> p.contains(":recipes/"))
				.forEach(p -> recipes.add(p.replace(":recipes/", ":").replace(".json", "")));
		data.forEach((path, content) -> {
			JsonObject json = JsonParser.parseString(content).getAsJsonObject();
			if (path.contains(":loot_tables/") || path.contains(":recipes/")) {
				walk(json, "item", id -> checkItem(path, id, missing));
				walk(json, "name", id -> checkItem(path, id, missing));
				if (json.has("result") && json.get("result").isJsonPrimitive()) {
					checkItem(path, json.get("result").getAsString(), missing);
				}
			} else if (path.contains(":advancements/")) {
				for (JsonElement r : json.getAsJsonObject("rewards").getAsJsonArray("recipes")) {
					if (!recipes.contains(r.getAsString())) {
						missing.add(path + " reward " + r.getAsString());
					}
				}
			} else if (path.contains(":tags/")) {
				for (JsonElement v : json.getAsJsonArray("values")) {
					String id = v.isJsonObject() ? v.getAsJsonObject().get("id").getAsString() : v.getAsString();
					checkItem(path, id, missing);
				}
			}
		});
		assertEquals(List.of(), missing);
		assertTrue(recipes.size() > 50, "recipes: " + recipes.size());
	}

	private static void checkModel(String path, String model, List<String> missing) {
		if (!exists(model, "models/", ".json")) {
			missing.add(path + " model " + model);
		}
	}

	private static void checkItem(String path, String id, List<String> missing) {
		if (!game.hasItem(new ResourceLocation(id))) {
			missing.add(path + " item " + id);
		}
	}

	/** Whether a model or texture id resolves to a generated file or a classpath resource. */
	private static boolean exists(String id, String folder, String extension) {
		ResourceLocation rl = new ResourceLocation(id);
		ResourceLocation file = new ResourceLocation(rl.getNamespace(), folder + rl.getPath() + extension);
		return client.containsKey(file.toString()) || ClasspathResources.url(file) != null;
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

	/** Path to normalised content: JSON with sorted keys and tag values as sorted sets; PNGs as a SHA-256. */
	static Map<String, String> read(PackIndex index) {
		Map<String, String> out = new TreeMap<>();
		index.forEach((location, content) -> {
			byte[] bytes = content.get();
			String path = location.toString();
			out.put(path, path.endsWith(".png") ? "sha256:" + sha256(bytes) : normalise(path, bytes));
		});
		return out;
	}

	private static String normalise(String path, byte[] bytes) {
		JsonElement json = JsonParser.parseString(new String(bytes, StandardCharsets.UTF_8));
		if (path.contains(":tags/")) {
			JsonArray values = json.getAsJsonObject().getAsJsonArray("values");
			TreeMap<String, JsonElement> sorted = new TreeMap<>();
			values.forEach(v -> sorted.put(GSON.toJson(v), v));
			JsonArray out = new JsonArray();
			sorted.values().forEach(out::add);
			json.getAsJsonObject().add("values", out);
		}
		return GSON.toJson(sort(json));
	}

	private static JsonElement sort(JsonElement json) {
		if (json.isJsonObject()) {
			JsonObject out = new JsonObject();
			new TreeMap<>(json.getAsJsonObject().asMap()).forEach((k, v) -> out.add(k, sort(v)));
			return out;
		}
		if (json.isJsonArray()) {
			JsonArray out = new JsonArray();
			json.getAsJsonArray().forEach(e -> out.add(sort(e)));
			return out;
		}
		return json;
	}

	private static String sha256(byte[] bytes) {
		try {
			return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}

	private static void compare(String name, Map<String, String> actual) throws IOException {
		String write = System.getProperty("caliberlib.goldens.write");
		if (write != null) {
			Path file = Path.of(write, name + ".json");
			Files.createDirectories(file.getParent());
			JsonObject json = new JsonObject();
			actual.forEach((path, content) -> json.addProperty(path, content));
			Files.writeString(file, new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(json) + "\n");
			// A run that rewrites the goldens checks nothing; failing keeps it from passing as a check.
			throw new AssertionError("Rewrote goldens/" + name + ".json; check the diff and run again without -PwriteGoldens");
		}
		Map<String, String> expected = new TreeMap<>();
		try (InputStream in = EngineGoldenTest.class.getClassLoader().getResourceAsStream("goldens/" + name + ".json")) {
			if (in == null) {
				throw new AssertionError("No goldens/" + name + ".json; run with -PwriteGoldens");
			}
			JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject().entrySet()
					.forEach(e -> expected.put(e.getKey(), e.getValue().getAsString()));
		}
		List<String> differences = new ArrayList<>();
		for (String path : new TreeSet<>(expected.keySet())) {
			if (!actual.containsKey(path)) {
				differences.add("missing " + path);
			} else if (!actual.get(path).equals(expected.get(path))) {
				differences.add("changed " + path);
			}
		}
		actual.keySet().stream().filter(p -> !expected.containsKey(p)).forEach(p -> differences.add("new " + p));
		assertEquals(List.of(), differences);
	}
}
