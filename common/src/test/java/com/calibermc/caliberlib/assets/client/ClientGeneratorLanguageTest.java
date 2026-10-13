package com.calibermc.caliberlib.assets.client;

import com.calibermc.caliberlib.assets.ClasspathResources;
import com.calibermc.caliberlib.assets.Fixtures;
import com.calibermc.caliberlib.assets.GameView;
import com.calibermc.caliberlib.assets.Material;
import com.calibermc.caliberlib.assets.PackIndex;
import com.calibermc.caliberlib.assets.ShapeSpec;
import com.calibermc.caliberlib.assets.ShapeSpecs;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientGeneratorLanguageTest {

	@Test
	void otherLanguagesHoldOnlyTheirOwnNames() {
		Fixtures.bootstrap();
		List<Material> families = Fixtures.families();
		GameView game = Fixtures.game(families);
		Map<String, ShapeSpec> specs = new HashMap<>(ShapeSpecs.get());
		ShapeSpec layer = specs.get("layer");
		specs.put("layer", new ShapeSpec(layer.variant(), layer.blockstate(), layer.models(), layer.item(), layer.usesBase(),
				layer.loot(), layer.recipes(), layer.blockTags(), layer.itemTags(),
				Map.of("en_us", layer.name(), "de_de", "{m}-Schicht")));
		PackIndex index = new ClientGenerator(ClasspathResources::new, game).index(families, specs);
		Map<String, JsonObject> lang = new TreeMap<>();
		index.forEach((location, content) -> {
			if (location.getPath().startsWith("lang/")) {
				lang.put(location.toString(), JsonParser.parseString(new String(content.get(), StandardCharsets.UTF_8)).getAsJsonObject());
			}
		});
		JsonObject german = lang.get("fixturemod:lang/de_de.json");
		JsonObject english = lang.get("fixturemod:lang/en_us.json");
		assertEquals("Fixture Stone-Schicht", german.get("block.fixturemod.fixture_stone_layer").getAsString());
		assertEquals("Fixture Stone Layer", english.get("block.fixturemod.fixture_stone_layer").getAsString());
		// Shapes without a German name are left to the game's fallback to en_us.
		assertFalse(german.has("block.fixturemod.fixture_stone_arch"));
		assertTrue(english.has("block.fixturemod.fixture_stone_arch"));
		lang.forEach((file, json) -> {
			if (file.endsWith("/de_de.json")) {
				json.keySet().forEach(key -> assertTrue(key.endsWith("_layer"), file + " " + key));
			}
		});
	}
}
