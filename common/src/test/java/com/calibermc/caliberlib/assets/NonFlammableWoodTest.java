package com.calibermc.caliberlib.assets;

import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NonFlammableWoodTest {

	private static Material wood(String prefix, String source) {
		ResourceLocation wall = new ResourceLocation("caliber", prefix + "_wall");
		ResourceLocation src = new ResourceLocation(source);
		return new Material(new ResourceLocation("caliber", prefix), "caliber", source.split(":")[1], MaterialKind.WOOD,
				prefix, src, src, null, null, Map.of(), Map.of("wall", wall), List.of(new Material.Shape("wall", wall)),
				false, null, null);
	}

	@Test
	void woodThatFireDoesNotTakeIsNeverFuel() {
		Fixtures.bootstrap();
		List<Material> families = List.of(wood("crimson", "minecraft:crimson_planks"), wood("oak", "minecraft:oak_planks"));
		List<String> tagged = new ArrayList<>();
		AssetEngine.dataIndex(families, Fixtures.game(families)).forEach((location, content) -> {
			if (location.toString().equals("minecraft:tags/items/non_flammable_wood.json")) {
				JsonParser.parseString(new String(content.get(), StandardCharsets.UTF_8)).getAsJsonObject()
						.getAsJsonArray("values").forEach(v -> tagged.add(v.isJsonObject() ? v.getAsJsonObject().get("id").getAsString() : v.getAsString()));
			}
		});
		assertEquals(List.of("caliber:crimson_wall"), tagged);
	}
}
