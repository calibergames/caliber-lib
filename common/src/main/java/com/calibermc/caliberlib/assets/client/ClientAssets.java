package com.calibermc.caliberlib.assets.client;

import com.calibermc.caliberlib.assets.AssetEngine;
import com.calibermc.caliberlib.assets.GameView;
import com.calibermc.caliberlib.assets.Material;
import com.calibermc.caliberlib.assets.PackIndex;
import com.calibermc.caliberlib.assets.ShapeSpecs;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.List;

/** Client-only setup of the asset engine. Each loader's client start calls {@link #install()}. */
public final class ClientAssets {

	private ClientAssets() {
	}

	public static void install() {
		AssetEngine.setClientPacks(ClientGenerator::open);
	}

	/**
	 * Generates the client files of these families without opening a pack,
	 * reading the source blocks' models and textures from {@code others}
	 * (e.g. for tests and tools).
	 */
	public static PackIndex index(List<Material> materials, ResourceManager others, GameView game) {
		return new ClientGenerator(() -> others, game).index(materials, ShapeSpecs.get());
	}
}
