package com.calibermc.caliberlib.gametest.neoforge;

import com.calibermc.caliberlib.gametest.support.GameTestPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** {@link GameTestPlatform} on NeoForge and Forge: the block state's own fire odds. */
public class NeoForgeGameTestPlatform implements GameTestPlatform {

	@Override
	public int[] fireOdds(Block block) {
		BlockState state = block.defaultBlockState();
		return new int[] {state.getFireSpreadSpeed(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, Direction.UP),
				state.getFlammability(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, Direction.UP)};
	}
}
