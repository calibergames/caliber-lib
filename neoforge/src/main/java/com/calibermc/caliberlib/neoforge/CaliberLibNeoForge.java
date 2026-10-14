package com.calibermc.caliberlib.neoforge;

import com.calibermc.caliberlib.CaliberLib;
import com.calibermc.caliberlib.assets.AssetEngine;
import com.calibermc.caliberlib.assets.client.ClientAssets;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

/** The NeoForge and Forge entry point. */
@Mod(CaliberLib.MOD_ID)
public class CaliberLibNeoForge {

	// No constructor arguments: Forge passes the loading context to mod
	// constructors only from 47.3 on, and NeoForge 1.20.1 never does.
	public CaliberLibNeoForge() {
		CaliberLib.init();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(CaliberLibNeoForge::addPacks);
		// Every block is registered by setup, so the compat list's hash is final.
		bus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(NeoForgeCompatLogin::register));
		// After every mod's setup, so source blocks have their own fire values.
		bus.addListener((FMLLoadCompleteEvent event) -> event.enqueueWork(() -> {
			AssetEngine.copyFlammability();
			AssetEngine.ferriteCoreHint();
		}));
		if (FMLEnvironment.dist == Dist.CLIENT) {
			ClientAssets.install();
			NeoForgeClientHooks.init(bus);
		}
	}

	/** Adds the generated client or data pack to every pack repository. */
	private static void addPacks(AddPackFindersEvent event) {
		PackType type = event.getPackType();
		event.addRepositorySource(packs -> {
			Pack pack = AssetEngine.pack(type);
			if (pack != null) {
				packs.accept(pack);
			}
		});
	}
}
