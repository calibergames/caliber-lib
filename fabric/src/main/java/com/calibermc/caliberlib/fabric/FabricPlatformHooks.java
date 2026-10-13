package com.calibermc.caliberlib.fabric;

import com.calibermc.caliberlib.platform.PlatformHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

/** {@link PlatformHooks} on Fabric and Quilt: vanilla's farmland rules. */
public class FabricPlatformHooks implements PlatformHooks {

	@Override
	public boolean onFarmlandTrample(Level level, BlockPos pos, BlockState toState, float fallDistance, Entity entity) {
		// Vanilla FarmBlock.fallOn.
		return level.random.nextFloat() < fallDistance - 0.5F
				&& entity instanceof LivingEntity
				&& (entity instanceof Player || level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING))
				&& entity.getBbWidth() * entity.getBbWidth() * entity.getBbHeight() > 0.512F;
	}

	@Override
	public boolean isUnderCrops(BlockGetter level, BlockPos pos) {
		return level.getBlockState(pos.above()).is(BlockTags.MAINTAINS_FARMLAND);
	}

	@Override
	public boolean hasWaterTicket(LevelReader level, BlockPos pos) {
		return false;
	}
}
