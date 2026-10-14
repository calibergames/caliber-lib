package com.calibermc.caliberlib.gametest.fabric;

import com.calibermc.caliberlib.gametest.support.GameTestPlatform;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.world.level.block.Block;

/** {@link GameTestPlatform} on Fabric and Quilt. */
public class FabricGameTestPlatform implements GameTestPlatform {

	@Override
	public int[] fireOdds(Block block) {
		FlammableBlockRegistry.Entry entry = FlammableBlockRegistry.getDefaultInstance().get(block);
		return entry == null ? new int[] {0, 0} : new int[] {entry.getSpreadChance(), entry.getBurnChance()};
	}
}
