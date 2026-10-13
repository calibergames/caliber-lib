package com.calibermc.caliberlib.platform;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

/** Places where a loader changes vanilla block behaviour; the library follows the loader it runs on. */
public interface PlatformHooks {

	/** Whether an entity falling this far onto farmland tramples it into {@code toState}. */
	boolean onFarmlandTrample(Level level, BlockPos pos, BlockState toState, float fallDistance, Entity entity);

	/** Whether the block above {@code pos} is a plant that keeps farmland from drying into dirt. */
	boolean isUnderCrops(BlockGetter level, BlockPos pos);

	/** Whether something other than nearby water keeps the farmland at {@code pos} wet. */
	boolean hasWaterTicket(LevelReader level, BlockPos pos);
}
