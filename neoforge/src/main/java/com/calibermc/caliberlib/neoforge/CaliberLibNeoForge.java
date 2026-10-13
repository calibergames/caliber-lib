package com.calibermc.caliberlib.neoforge;

import com.calibermc.caliberlib.CaliberLib;
import net.minecraftforge.fml.common.Mod;

/** The NeoForge and Forge entry point. */
@Mod(CaliberLib.MOD_ID)
public class CaliberLibNeoForge {

	// No constructor arguments: Forge passes the loading context to mod
	// constructors only from 47.3 on, and NeoForge 1.20.1 never does.
	public CaliberLibNeoForge() {
		CaliberLib.init();
	}
}
