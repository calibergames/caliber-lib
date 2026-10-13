package com.calibermc.caliberlib.fabric;

import com.calibermc.caliberlib.platform.PlatformHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.CreativeModeTab;

import java.nio.file.Path;

/** {@link PlatformHelper} on Fabric and Quilt. */
public class FabricPlatformHelper implements PlatformHelper {

	@Override
	public String loaderName() {
		return FabricLoader.getInstance().isModLoaded("quilt_loader") ? "quilt" : "fabric";
	}

	@Override
	public boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Override
	public boolean isDevelopment() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	@Override
	public boolean isClient() {
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
	}

	@Override
	public Path configDir() {
		return FabricLoader.getInstance().getConfigDir();
	}

	@Override
	public String commonTagNamespace() {
		return "c";
	}

	@Override
	public CreativeModeTab.Builder creativeTab() {
		return FabricItemGroup.builder();
	}
}
