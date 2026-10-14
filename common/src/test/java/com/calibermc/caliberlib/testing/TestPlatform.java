package com.calibermc.caliberlib.testing;

import com.calibermc.caliberlib.platform.ModFiles;
import com.calibermc.caliberlib.platform.PlatformHelper;
import net.minecraft.world.item.CreativeModeTab;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** The platform in unit tests: no loader, and the mods a test says are installed. */
public final class TestPlatform implements PlatformHelper {

	/** The mods tests treat as installed. */
	public static final Set<String> MODS = new HashSet<>();
	/** The mods' files tests give. */
	public static final List<ModFiles> FILES = new ArrayList<>();

	@Override
	public String loaderName() {
		return "test";
	}

	@Override
	public boolean isModLoaded(String modId) {
		return MODS.contains(modId);
	}

	@Override
	public boolean isDevelopment() {
		return true;
	}

	@Override
	public boolean isClient() {
		return false;
	}

	@Override
	public Path configDir() {
		return Path.of("build", "test-config");
	}

	@Override
	public String commonTagNamespace() {
		return "c";
	}

	@Override
	public List<ModFiles> modFiles() {
		return List.copyOf(FILES);
	}

	@Override
	public CreativeModeTab.Builder creativeTab() {
		throw new UnsupportedOperationException("No creative tabs in unit tests");
	}
}
