package com.calibermc.caliberlib.neoforge;

import com.calibermc.caliberlib.platform.ModFiles;
import com.calibermc.caliberlib.platform.PlatformHelper;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.VersionInfo;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** {@link PlatformHelper} on NeoForge and Forge 1.20.1. */
public class NeoForgePlatformHelper implements PlatformHelper {

	@Override
	public String loaderName() {
		return NEOFORGE ? "neoforge" : "forge";
	}

	// NeoForge 1.20.1 keeps Forge's mod id and packages. Its loader's VersionInfo
	// has fmlVersion(), which Forge's does not.
	private static final boolean NEOFORGE = hasMethod(VersionInfo.class, "fmlVersion");

	private static boolean hasMethod(Class<?> type, String name) {
		try {
			type.getMethod(name);
			return true;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

	@Override
	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Override
	public boolean isDevelopment() {
		return !FMLLoader.isProduction();
	}

	@Override
	public boolean isClient() {
		return FMLEnvironment.dist.isClient();
	}

	@Override
	public Path configDir() {
		return FMLPaths.CONFIGDIR.get();
	}

	@Override
	public String commonTagNamespace() {
		return "forge";
	}

	@Override
	public CreativeModeTab.Builder creativeTab() {
		return CreativeModeTab.builder();
	}

	@Override
	public List<ModFiles> modFiles() {
		List<ModFiles> out = new ArrayList<>();
		ModList.get().getMods().forEach(mod -> {
			if (mod.getModId().equals("minecraft") || mod.getModId().equals("forge")) {
				return;
			}
			// One union file system per mod file: its roots are merged, and a missing path comes back non-existent.
			var file = mod.getOwningFile().getFile();
			out.add(new ModFiles(mod.getModId(), relative -> {
				Path p = file.findResource(relative.split("/"));
				return Files.exists(p) ? List.of(p) : List.of();
			}));
		});
		out.sort(Comparator.comparing(ModFiles::modId));
		return out;
	}
}
