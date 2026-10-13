package com.calibermc.caliberlib.assets.client;

import com.calibermc.caliberlib.assets.AssetEngine;

/** Client-only setup of the asset engine. Each loader's client start calls {@link #install()}. */
public final class ClientAssets {

	private ClientAssets() {
	}

	public static void install() {
		AssetEngine.setClientPacks(ClientGenerator::open);
	}
}
