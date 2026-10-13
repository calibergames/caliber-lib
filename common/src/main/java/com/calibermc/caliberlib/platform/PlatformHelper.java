package com.calibermc.caliberlib.platform;

import net.minecraft.world.item.CreativeModeTab;

import java.nio.file.Path;

/** What the library needs from the loader it runs on. */
public interface PlatformHelper {

	/** The loader's name, for logs: "neoforge", "forge", "fabric" or "quilt". */
	String loaderName();

	/** Whether a mod with this id is installed. */
	boolean isModLoaded(String modId);

	/** Whether the game runs from a development environment. */
	boolean isDevelopment();

	/** Whether this is a client (a dedicated server is not). */
	boolean isClient();

	/** The folder for config files. */
	Path configDir();

	/** The namespace of the tags loaders share between mods: "forge" or "c". */
	String commonTagNamespace();

	/** A creative tab builder placed by the loader after vanilla's tabs. */
	CreativeModeTab.Builder creativeTab();
}
