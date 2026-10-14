package com.calibermc.caliberlib.fabric;

import com.calibermc.caliberlib.platform.ModFiles;
import com.calibermc.caliberlib.platform.PlatformHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.world.item.CreativeModeTab;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
	public List<ModFiles> modFiles() {
		List<ModFiles> out = new ArrayList<>();
		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			if (mod.getMetadata().getType().equals("builtin")) {
				continue;
			}
			// A jar's root is a zip file system; a development run can have several roots (classes, resources).
			List<Path> roots = List.copyOf(mod.getRootPaths());
			out.add(new ModFiles(mod.getMetadata().getId(), relative -> {
				List<Path> found = new ArrayList<>();
				for (Path root : roots) {
					Path p = root.resolve(relative.replace("/", root.getFileSystem().getSeparator()));
					if (Files.exists(p)) {
						found.add(p);
					}
				}
				return found;
			}));
		}
		out.sort(Comparator.comparing(ModFiles::modId));
		return out;
	}

	@Override
	public CreativeModeTab.Builder creativeTab() {
		return FabricItemGroup.builder();
	}
}
