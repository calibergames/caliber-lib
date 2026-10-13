package com.calibermc.caliberlib.assets;

import com.calibermc.caliberlib.data.ModBlockFamily;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShapeSpecsTest {

	private static final Set<String> MODEL_KEYS = Set.of("base", "item", "side", "top", "bottom", "door_bottom",
			"door_middle", "door_top");

	@Test
	void everyVariantHasASpecOrIsUnsupported() {
		Map<String, ShapeSpec> specs = ShapeSpecs.load();
		for (ModBlockFamily.Variant variant : ModBlockFamily.Variant.values()) {
			boolean unsupported = ShapeSpecs.UNSUPPORTED.contains(variant);
			assertEquals(!unsupported, specs.containsKey(variant.getName()), variant.getName());
		}
		assertEquals(34, specs.size());
	}

	@Test
	void templatesUseOnlyKnownPlaceholders() {
		for (ShapeSpec spec : ShapeSpecs.load().values()) {
			for (String key : spec.blockstate().keys()) {
				assertTrue(key.equals("base") ? spec.usesBase() : spec.models().containsKey(key.substring(1)) && key.startsWith("m"),
						spec.variant() + " blockstate @" + key);
			}
			spec.models().values().forEach(t -> t.keys().forEach(key ->
					assertTrue(MODEL_KEYS.contains(key), spec.variant() + " model @" + key)));
			for (String key : spec.item().keys()) {
				assertTrue(key.equals("item") || spec.models().containsKey(key.substring(1)), spec.variant() + " item @" + key);
			}
		}
	}

	@Test
	void shapeFixesAreInTheSpecs() {
		Map<String, ShapeSpec> specs = ShapeSpecs.load();
		String slab = render(specs.get("slab").blockstate());
		assertTrue(slab.contains("\"type=top\":{\"model\":\"ns:block/x_top\"}"), slab);
		String vertical = render(specs.get("slab_vertical").blockstate());
		assertTrue(vertical.contains("\"facing=east,waterlogged=false\":{\"model\":\"ns:block/x_1\",\"uvlock\":true,\"y\":90}"), vertical);
		String wallSide = new String(specs.get("wall").models().get("_side").render(k -> "T:" + k), StandardCharsets.UTF_8);
		assertTrue(wallSide.contains("\"wall\":\"T:side\""), wallSide);
		assertEquals("layers", specs.get("corner").loot());
		assertEquals("slab", specs.get("slab").loot());
		assertEquals("self", specs.get("beam_vertical").loot());
	}

	@Test
	void unknownSpecKeysAreRejected() {
		JsonObject json = JsonParser.parseString("{\"variant\":\"x\",\"colour\":1}").getAsJsonObject();
		assertThrows(IllegalArgumentException.class, () -> ShapeSpec.parse(json));
	}

	@Test
	void templatesEscapeValues() {
		Template t = Template.compile(JsonParser.parseString("{\"a\":\"@v\",\"b\":[1,true,\"lit\"]}"));
		String out = new String(t.render(k -> "q\"\\\n"), StandardCharsets.UTF_8);
		assertEquals("{\"a\":\"q\\\"\\\\\\n\",\"b\":[1,true,\"lit\"]}", out);
		JsonParser.parseString(out);
	}

	@Test
	void missingValuesAreErrors() {
		Template t = Template.compile(JsonParser.parseString("{\"a\":\"@v\"}"));
		assertThrows(IllegalArgumentException.class, () -> t.render(k -> null));
	}

	@Test
	void displayNamesAreTitleCase() {
		assertEquals("Stripped Oak", Material.titleCase("stripped_oak"));
		assertEquals("Oak", Material.titleCase("oak"));
		assertFalse(Material.titleCase("a__b").contains("  "));
	}

	private static String render(Template t) {
		return new String(t.render(k -> k.equals("base") ? "ns:block/base" : "ns:block/x" + k.substring(1)), StandardCharsets.UTF_8);
	}
}
