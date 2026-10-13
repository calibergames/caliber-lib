package com.calibermc.caliberlib.assets.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.calibermc.caliberlib.testing.GameBootstrap;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ObjModelTest {

	private static final String ROOT = "assets/caliberlib/models/block/templates/obj/";

	@Test
	void everyRoofTemplateParsesWithSideAndTopTextures() throws IOException {
		for (String name : List.of("roof_67", "roof_67_inner", "roof_67_outer", "roof_67_top", "roof_67_top_inner",
				"roof_67_top_outer")) {
			ObjModel model = ObjModel.parse(read(name + ".obj"), read(name + ".mtl"));
			assertEquals(Set.of("#side", "#top"), Set.copyOf(model.textureVariables()), name);
		}
	}

	@Test
	void facesNeedThreeOrFourCorners() {
		String obj = "v 0 0 0\nv 1 0 0\nv 1 1 0\nv 0 1 0\nv 0 0 1\nf 1 2 3 4 5\n";
		assertThrows(IllegalArgumentException.class, () -> ObjModel.parse(obj, ""));
	}

	/** One quad on the block's east side (material "lit", with an ambient colour), one inside it ("plain"). */
	private static final String OBJ = String.join("\n",
			"mtllib test.mtl",
			"v 1 0 0", "v 1 0 1", "v 1 1 1", "v 1 1 0",
			"v 0.5 0 0", "v 0.5 0 1", "v 0.5 1 1", "v 0.5 1 0",
			"vt 0 0", "vt 1 0", "vt 1 1", "vt 0 1",
			"vn 1 0 0",
			"usemtl lit",
			"f 1/1/1 4/4/1 3/3/1 2/2/1",
			"usemtl plain",
			"f 5/1/1 8/4/1 7/3/1 6/2/1", "");
	private static final String MTL = String.join("\n",
			"newmtl lit", "Ka 0.15 0.15 0.15", "map_Kd #top",
			"newmtl plain", "Ka 0 0 0", "map_Kd #side", "");

	private record Baked(BakedQuad quad, Direction cull) {
	}

	private static List<Baked> bake(BlockModelRotation rotation, ObjModel.Options options) {
		GameBootstrap.boot();
		SpriteContents contents = new SpriteContents(new ResourceLocation("test", "sprite"), new FrameSize(16, 16),
				new NativeImage(16, 16, false), AnimationMetadataSection.EMPTY);
		TextureAtlasSprite sprite = new TextureAtlasSprite(new ResourceLocation("test", "atlas"), contents, 16, 16, 0, 0) {
		};
		List<Baked> out = new ArrayList<>();
		ObjModel.parse(OBJ, MTL).bake(name -> sprite, rotation.getRotation(), options, (quad, cull) -> out.add(new Baked(quad, cull)));
		return out;
	}

	@Test
	void facesPointAndCullAsForgeDraws() {
		List<Baked> quads = bake(BlockModelRotation.X0_Y0, new ObjModel.Options(true, true, true));
		assertEquals(2, quads.size());
		assertEquals(Direction.EAST, quads.get(0).quad().getDirection());
		assertEquals(Direction.EAST, quads.get(0).cull(), "a face on the block's side culls against it");
		assertNull(quads.get(1).cull(), "a face inside the block never culls");
		// A material with an ambient colour turns shading off; one without keeps it.
		assertFalse(quads.get(0).quad().isShade());
		assertTrue(quads.get(1).quad().isShade());
	}

	@Test
	void facesTurnWithTheModel() {
		List<Baked> quads = bake(BlockModelRotation.X0_Y90, new ObjModel.Options(true, true, true));
		assertEquals(Direction.SOUTH, quads.get(0).quad().getDirection());
		assertEquals(Direction.SOUTH, quads.get(0).cull());
		// The corner at (1, 0, 0) moves to (1, 0, 1).
		int[] v = quads.get(0).quad().getVertices();
		assertEquals(1f, Float.intBitsToFloat(v[0]), 1.0E-5f);
		assertEquals(1f, Float.intBitsToFloat(v[2]), 1.0E-5f);
	}

	@Test
	void optionsTurnOffCullingShadingRulesAndFlipping() {
		List<Baked> quads = bake(BlockModelRotation.X0_Y0, new ObjModel.Options(false, false, false));
		assertNull(quads.get(0).cull());
		assertTrue(quads.get(0).quad().isShade(), "without emissive ambient every face is shaded");
		// The first corner has v = 0: unflipped it maps to the sprite's top, flipped to its bottom.
		float unflipped = Float.intBitsToFloat(quads.get(0).quad().getVertices()[5]);
		float flipped = Float.intBitsToFloat(bake(BlockModelRotation.X0_Y0, new ObjModel.Options(true, false, false))
				.get(0).quad().getVertices()[5]);
		assertEquals(0f, unflipped, 1.0E-5f);
		assertEquals(1f, flipped, 1.0E-5f);
	}

	private static String read(String file) throws IOException {
		try (InputStream in = ObjModelTest.class.getClassLoader().getResourceAsStream(ROOT + file)) {
			assertNotNull(in, file);
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
	}
}
