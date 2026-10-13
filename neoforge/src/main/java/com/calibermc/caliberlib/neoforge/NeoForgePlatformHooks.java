package com.calibermc.caliberlib.neoforge;

import com.calibermc.caliberlib.platform.PlatformHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;

/** {@link PlatformHooks} on NeoForge and Forge: the loader's own farmland events and plant checks. */
public class NeoForgePlatformHooks implements PlatformHooks {

	@Override
	public boolean onFarmlandTrample(Level level, BlockPos pos, BlockState toState, float fallDistance, Entity entity) {
		return ForgeHooks.onFarmlandTrample(level, pos, toState, fallDistance, entity);
	}

	@Override
	public boolean isUnderCrops(BlockGetter level, BlockPos pos) {
		BlockState plant = level.getBlockState(pos.above());
		return plant.getBlock() instanceof IPlantable plantable
				&& level.getBlockState(pos).canSustainPlant(level, pos, Direction.UP, plantable);
	}

	@Override
	public boolean hasWaterTicket(LevelReader level, BlockPos pos) {
		return FarmlandWaterManager.hasBlockWaterTicket(level, pos);
	}
}
