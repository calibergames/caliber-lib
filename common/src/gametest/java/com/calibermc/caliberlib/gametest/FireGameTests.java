package com.calibermc.caliberlib.gametest;

import com.calibermc.caliberlib.gametest.support.GameTestPlatform;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Arrays;

import static com.calibermc.caliberlib.gametest.TestSupport.EMPTY_STRUCTURE;
import static com.calibermc.caliberlib.gametest.TestSupport.block;
import static com.calibermc.caliberlib.gametest.TestSupport.check;

public class FireGameTests {

	private static void same(Block shape, Block source) {
		int[] a = GameTestPlatform.INSTANCE.fireOdds(shape);
		int[] b = GameTestPlatform.INSTANCE.fireOdds(source);
		check(Arrays.equals(a, b), shape + " burns " + Arrays.toString(a) + ", its source " + Arrays.toString(b));
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void shapesBurnLikeTheirSource(GameTestHelper helper) {
		same(block("gt_oak_beam_vertical"), Blocks.OAK_PLANKS);
		same(block("gt_oak_tall_door"), Blocks.OAK_PLANKS);
		same(block("gt_wool_arch"), Blocks.WHITE_WOOL);
		same(block("gt_crimson_beam_vertical"), Blocks.CRIMSON_PLANKS);
		same(block("gt_stone_arch"), Blocks.STONE_BRICKS);
		check(GameTestPlatform.INSTANCE.fireOdds(block("gt_oak_beam_vertical"))[1] > 0, "oak beam does not burn");
		helper.succeed();
	}
}
