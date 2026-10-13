package com.calibermc.caliberlib.block.shapes;

import com.calibermc.caliberlib.testing.GameBootstrap;
import com.calibermc.caliberlib.block.custom.Roof22Block;
import com.calibermc.caliberlib.block.custom.Roof67Block;
import com.calibermc.caliberlib.block.custom.RoofPeakBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelOcclusionTest {

	@BeforeAll
	static void boot() {
		GameBootstrap.boot();
	}

	@Test
	void rotationTurnsEastToSouth() {
		// A box against the east side ends up against the south side.
		assertArrayEquals(new double[] {0, 0, 12, 16, 8, 16}, ModelOcclusion.rotate(new double[] {12, 0, 0, 16, 8, 16}, 90));
		assertArrayEquals(new double[] {0, 0, 0, 4, 8, 16}, ModelOcclusion.rotate(new double[] {12, 0, 0, 16, 8, 16}, 180));
		assertArrayEquals(new double[] {0, 0, 0, 16, 8, 4}, ModelOcclusion.rotate(new double[] {12, 0, 0, 16, 8, 16}, 270));
	}

	@Test
	void roofPiecesHaveASolidBottomInEveryState() {
		for (String variant : List.of("roof_22", "roof_67", "roof_peak")) {
			List<String> keys = ModelOcclusion.keys(variant);
			assertEquals(variant.equals("roof_peak") ? 228 : 40, keys.size(), variant);
			for (String key : keys) {
				VoxelShape shape = ModelOcclusion.ofKey(variant, key);
				assertNotNull(shape, key);
				// A steep roof's top piece always sits on its bottom piece, which closes the floor.
				if (!(variant.equals("roof_67") && key.contains("half=top"))) {
					assertTrue(Block.isFaceFull(shape, Direction.DOWN), variant + " " + key);
				}
				assertFalse(Block.isShapeFullBlock(shape), variant + " " + key + " would count as a full block");
			}
		}
	}

	@Test
	void roofsReachingTheTopAreSolidOnTheirUphillSide() {
		for (String[] piece : new String[][] {{"roof_67", "bottom"}, {"roof_67", "top"}, {"roof_22", "top"}})
		for (Direction facing : Direction.Plane.HORIZONTAL) {
			String key = "facing=" + facing.getName() + ",half=" + piece[1] + ",type=straight";
			VoxelShape shape = ModelOcclusion.ofKey(piece[0], key);
			// Pieces face down-slope, so the uphill side is behind them.
			assertTrue(Block.isFaceFull(shape, facing.getOpposite()), piece[0] + " " + key);
			assertFalse(Block.isFaceFull(shape, facing), piece[0] + " " + key);
			assertFalse(Block.isFaceFull(shape, Direction.UP), piece[0] + " " + key);
		}
	}

	@Test
	void everyRealStateMatchesOneKeyAndGetsTheModelsShape() {
		Block[] blocks = {
				GameBootstrap.block(() -> new Roof22Block(BlockBehaviour.Properties.of())),
				GameBootstrap.block(() -> new Roof67Block(BlockBehaviour.Properties.of())),
				GameBootstrap.block(() -> new RoofPeakBlock(BlockBehaviour.Properties.of()))};
		String[] variants = {"roof_22", "roof_67", "roof_peak"};
		VoxelShape marker = Shapes.box(0, 0, 0, 0.5, 0.5, 0.5);
		for (int i = 0; i < blocks.length; i++) {
			for (BlockState state : blocks[i].getStateDefinition().getPossibleStates()) {
				List<String> keys = ModelOcclusion.matchingKeys(variants[i], state);
				assertEquals(1, keys.size(), variants[i] + " " + state + " matches " + keys);
				VoxelShape shape = ModelOcclusion.of(variants[i], state, () -> marker);
				assertSame(ModelOcclusion.ofKey(variants[i], keys.get(0)), shape, state.toString());
				// The indexed lookup agrees with the scan.
				assertSame(ModelOcclusion.ofKey(variants[i], keys.get(0)), ModelOcclusion.of(variants[i], state, () -> marker));
			}
		}
	}

	@Test
	void aShapeWithoutASpecKeepsTheBlocksOwn() {
		BlockState state = GameBootstrap.block(() -> new Roof22Block(BlockBehaviour.Properties.of())).defaultBlockState();
		VoxelShape own = Shapes.box(0, 0, 0, 1, 0.25, 1);
		assertSame(own, ModelOcclusion.of("no_such_shape", state, () -> own));
		assertEquals(List.of(), ModelOcclusion.keys("no_such_shape"));
	}

	@Test
	void solidPartsCloseToTheTopReachItAndBoundsSnap() {
		List<double[]> out = new ArrayList<>();
		ModelOcclusion.add(out, 0, 0, 0, 16, 14.2, 16);
		ModelOcclusion.add(out, 0, 0, 0, 16, 13.9, 16);
		ModelOcclusion.add(out, -2, 0, 3.99999, 15.984, 2, 18);
		ModelOcclusion.add(out, 17, 0, 0, 20, 2, 16);
		assertEquals(3, out.size(), "a box outside the block is dropped");
		assertArrayEquals(new double[] {0, 0, 0, 16, 16, 16}, out.get(0));
		assertArrayEquals(new double[] {0, 0, 0, 16, 13.875, 16}, out.get(1));
		assertArrayEquals(new double[] {0, 0, 4, 16, 2, 16}, out.get(2));
		assertEquals(16 - ModelOcclusion.SHINGLE_GAP, 14);
	}

	@Test
	void onlyCuboidObjectsOfAnObjCount() {
		String obj = String.join("\n",
				"o box",
				"v 0 0 0", "v 0.5 0 0", "v 0.5 0.25 0", "v 0 0.25 0",
				"v 0 0 1", "v 0.5 0 1", "v 0.5 0.25 1", "v 0 0.25 1",
				"f 1 2 3 4", "f 5 6 7 8", "f 1 2 6 5", "f 4 3 7 8", "f 1 4 8 5", "f 2 3 7 6",
				"o slant",
				"v 0 0.5 0", "v 1 1 0", "v 1 1 1", "v 0 0.5 1",
				"f 9 10 11 12");
		List<double[]> out = new ArrayList<>();
		ModelOcclusion.objSolidsOf(obj, out);
		assertEquals(1, out.size());
		assertArrayEquals(new double[] {0, 0, 0, 8, 4, 16}, out.get(0));
	}
}
