package com.calibermc.caliberlib.fabric;

import com.calibermc.caliberlib.assets.AssetEngine;
import com.calibermc.caliberlib.assets.client.ClientAssets;
import com.calibermc.caliberlib.fabric.client.FabricCompatLoginClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;

/** The {@code server} and {@code client} entrypoints: they run after every mod's {@code main}. */
public class CaliberLibFabricLate implements DedicatedServerModInitializer, ClientModInitializer {

	@Override
	public void onInitializeServer() {
		FabricRegistryHelper.runLate();
		AssetEngine.copyFlammability();
		AssetEngine.ferriteCoreHint();
	}

	@Override
	public void onInitializeClient() {
		FabricRegistryHelper.runLate();
		AssetEngine.copyFlammability();
		AssetEngine.ferriteCoreHint();
		ClientAssets.install();
		FabricClientHooks.init();
		FabricCompatLoginClient.init();
	}
}
