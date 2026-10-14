package com.calibermc.caliberlib.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.List;

import static com.calibermc.caliberlib.gametest.TestSupport.EMPTY_STRUCTURE;
import static com.calibermc.caliberlib.gametest.TestSupport.block;
import static com.calibermc.caliberlib.gametest.TestSupport.check;
import static com.calibermc.caliberlib.gametest.TestSupport.item;

public class LootGameTests {

	/** How many of the shape's item the state drops, mined with the right tool. */
	private static int drops(GameTestHelper helper, BlockState state, String shape) {
		BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
		ItemStack tool = new ItemStack(state.is(net.minecraft.tags.BlockTags.MINEABLE_WITH_AXE) ? Items.DIAMOND_AXE : Items.DIAMOND_PICKAXE);
		List<ItemStack> stacks = Block.getDrops(state, helper.getLevel(), pos, null, null, tool);
		return stacks.stream().filter(s -> s.is(item(shape))).mapToInt(ItemStack::getCount).sum();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static BlockState with(BlockState state, String property, String value) {
		Property p = state.getBlock().getStateDefinition().getProperty(property);
		check(p != null, state.getBlock() + " has no " + property);
		return state.setValue(p, (Comparable) p.getValue(value).orElseThrow());
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void aShapeDropsItself(GameTestHelper helper) {
		check(drops(helper, block("gt_stone_arch").defaultBlockState(), "gt_stone_arch") == 1, "arch");
		check(drops(helper, block("gt_oak_beam_vertical").defaultBlockState(), "gt_oak_beam_vertical") == 1, "beam");
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void aDoubleSlabDropsTwo(GameTestHelper helper) {
		BlockState slab = block("gt_stone_slab").defaultBlockState();
		check(drops(helper, with(slab, "type", "bottom"), "gt_stone_slab") == 1, "single slab");
		check(drops(helper, with(slab, "type", "double"), "gt_stone_slab") == 2, "double slab");
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void layeredShapesDropWhatWasPlaced(GameTestHelper helper) {
		BlockState corner = block("gt_stone_corner").defaultBlockState();
		check(drops(helper, with(corner, "layers", "3"), "gt_stone_corner") == 3, "3-layer corner");
		BlockState layer = block("gt_stone_layer").defaultBlockState();
		check(drops(helper, with(layer, "layers", "8"), "gt_stone_layer") == 8, "8 layers");
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void aTallDoorDropsOnce(GameTestHelper helper) {
		BlockState door = block("gt_oak_tall_door").defaultBlockState();
		int total = 0;
		for (Object value : door.getBlock().getStateDefinition().getProperty("part").getPossibleValues()) {
			total += drops(helper, with(door, "part", value.toString()), "gt_oak_tall_door");
		}
		check(total == 1, "a tall door drops " + total + " over its parts");
		helper.succeed();
	}
}
