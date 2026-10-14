package com.calibermc.caliberlib.gametest.fabric;

import com.calibermc.caliberlib.gametest.TestFamilies;
import net.fabricmc.api.ModInitializer;

/** The Fabric test mod's start: registers the families the game tests check. */
public class FabricTestMod implements ModInitializer {

	@Override
	public void onInitialize() {
		TestFamilies.init();
	}
}
