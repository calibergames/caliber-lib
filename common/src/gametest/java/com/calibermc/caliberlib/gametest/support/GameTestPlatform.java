package com.calibermc.caliberlib.gametest.support;

import net.minecraft.world.level.block.Block;

import java.util.ServiceLoader;

/** What the game tests need from the loader they run on; each loader's test mod lists its implementation in META-INF/services. */
public interface GameTestPlatform {

	GameTestPlatform INSTANCE = ServiceLoader.load(GameTestPlatform.class, GameTestPlatform.class.getClassLoader()).findFirst()
			.orElseThrow(() -> new IllegalStateException("No GameTestPlatform for this loader"));

	/** The block's fire odds as the loader reports them: {encouragement, flammability}. */
	int[] fireOdds(Block block);
}
