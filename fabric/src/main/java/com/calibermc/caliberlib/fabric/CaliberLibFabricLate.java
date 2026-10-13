package com.calibermc.caliberlib.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;

/** The {@code server} and {@code client} entrypoints: they run after every mod's {@code main}. */
public class CaliberLibFabricLate implements DedicatedServerModInitializer, ClientModInitializer {

	@Override
	public void onInitializeServer() {
		FabricRegistryHelper.runLate();
	}

	@Override
	public void onInitializeClient() {
		FabricRegistryHelper.runLate();
	}
}
