package com.calibermc.caliberlib.neoforge;

import com.calibermc.caliberlib.CaliberLib;
import com.calibermc.caliberlib.platform.PlatformHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.Method;
import java.util.Collection;

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

	/** {@code FireBlock.setFlammable}, which is private; looked up on first use, so the other hooks never depend on it. */
	private static volatile boolean flammabilityFailed;

	private static final class SetFlammable {
		static final Method METHOD = ObfuscationReflectionHelper.findMethod(FireBlock.class, "m_53444_",
				Block.class, int.class, int.class);
	}

	@Override
	public boolean burns(Block block) {
		// Forge blocks may answer for themselves, so ask the block state, not vanilla's table.
		BlockState state = block.defaultBlockState();
		return state.getFlammability(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, Direction.UP) > 0
				|| state.getFireSpreadSpeed(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, Direction.UP) > 0;
	}

	@Override
	public void copyFlammability(Block source, Collection<Block> targets) {
		// Forge blocks may answer for themselves, so ask the block state, not vanilla's table.
		BlockState state = source.defaultBlockState();
		int encouragement = state.getFireSpreadSpeed(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, Direction.UP);
		int flammability = state.getFlammability(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, Direction.UP);
		if (encouragement == 0 && flammability == 0) {
			return;
		}
		FireBlock fire = (FireBlock) Blocks.FIRE;
		try {
			Method setFlammable = SetFlammable.METHOD;
			for (Block target : targets) {
				// setFlammable(block, encouragement, flammability)
				setFlammable.invoke(fire, target, encouragement, flammability);
			}
		} catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
			if (!flammabilityFailed) {
				flammabilityFailed = true;
				CaliberLib.LOGGER.warn("Could not copy flammability (first from {}); shapes do not burn", source, e);
			}
		}
	}
}
