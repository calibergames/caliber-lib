package com.calibermc.caliberlib.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;

import static com.calibermc.caliberlib.gametest.TestSupport.EMPTY_STRUCTURE;
import static com.calibermc.caliberlib.gametest.TestSupport.block;
import static com.calibermc.caliberlib.gametest.TestSupport.check;
import static com.calibermc.caliberlib.gametest.TestSupport.item;

public class TagGameTests {

	@GameTest(template = EMPTY_STRUCTURE)
	public void shapesAreMinedLikeTheirKind(GameTestHelper helper) {
		check(block("gt_stone_arch").defaultBlockState().is(BlockTags.MINEABLE_WITH_PICKAXE), "stone arch: pickaxe");
		check(block("gt_oak_beam_vertical").defaultBlockState().is(BlockTags.MINEABLE_WITH_AXE), "oak beam: axe");
		check(block("gt_mud_arch").defaultBlockState().is(BlockTags.MINEABLE_WITH_PICKAXE), "late mud arch: pickaxe");
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void aFamilyWithoutAToolHasNoMiningTag(GameTestHelper helper) {
		var wool = block("gt_wool_arch").defaultBlockState();
		check(!wool.is(BlockTags.MINEABLE_WITH_PICKAXE) && !wool.is(BlockTags.MINEABLE_WITH_AXE)
				&& !wool.is(BlockTags.MINEABLE_WITH_SHOVEL) && !wool.is(BlockTags.MINEABLE_WITH_HOE), "wool arch has a mining tag");
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void slabsAndNetherWoodJoinVanillasTags(GameTestHelper helper) {
		check(block("gt_stone_slab").defaultBlockState().is(BlockTags.SLABS), "stone slab in minecraft:slabs");
		check(item("gt_crimson_beam_vertical").getDefaultInstance().is(ItemTags.NON_FLAMMABLE_WOOD), "crimson beam is not fuel");
		check(!item("gt_oak_beam_vertical").getDefaultInstance().is(ItemTags.NON_FLAMMABLE_WOOD), "oak beam stays fuel");
		helper.succeed();
	}
}
