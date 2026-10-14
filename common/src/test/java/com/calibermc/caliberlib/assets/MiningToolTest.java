package com.calibermc.caliberlib.assets;

import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MiningToolTest {

	private static Material family(String prefix, String source, MaterialKind kind, MiningTool tool) {
		ResourceLocation wall = new ResourceLocation("caliber", prefix + "_wall");
		ResourceLocation src = new ResourceLocation(source);
		return new Material(new ResourceLocation("caliber", prefix), "caliber", source.split(":")[1], kind, prefix, src, src,
				null, null, Map.of(), Map.of("wall", wall), List.of(new Material.Shape("wall", wall)), false, null, tool);
	}

	@Test
	void familiesAreMinedByTheirKindUnlessTheyNameATool() {
		Fixtures.bootstrap();
		List<Material> families = List.of(
				family("stone", "minecraft:stone", MaterialKind.STONE, null),
				family("oak", "minecraft:oak_planks", MaterialKind.WOOD, null),
				family("white_wool", "minecraft:white_wool", MaterialKind.STONE, MiningTool.NONE),
				family("dirt", "minecraft:dirt", MaterialKind.STONE, MiningTool.SHOVEL));
		Map<String, String> tagged = new TreeMap<>();
		AssetEngine.dataIndex(families, Fixtures.game(families)).forEach((location, content) -> {
			String path = location.toString();
			if (path.startsWith("minecraft:tags/blocks/mineable/")) {
				JsonParser.parseString(new String(content.get(), StandardCharsets.UTF_8)).getAsJsonObject().getAsJsonArray("values")
						.forEach(v -> tagged.put(v.isJsonObject() ? v.getAsJsonObject().get("id").getAsString() : v.getAsString(),
								path.substring(path.lastIndexOf('/') + 1, path.length() - 5)));
			}
		});
		assertEquals(Map.of("caliber:stone_wall", "pickaxe", "caliber:oak_wall", "axe", "caliber:dirt_wall", "shovel"), tagged);
	}
}
