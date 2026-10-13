package com.calibermc.caliberlib.fabric;

import com.calibermc.caliberlib.CaliberLib;
import net.fabricmc.api.ModInitializer;

/** The Fabric and Quilt entry point. */
public class CaliberLibFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		CaliberLib.init();
	}
}
