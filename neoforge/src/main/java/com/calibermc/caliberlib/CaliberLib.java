package com.calibermc.caliberlib;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("caliberlib")
public class CaliberLib {
    public static final String MOD_ID = "caliberlib";

    private static final Logger LOGGER = LogManager.getLogger();

    // No constructor arguments: Forge passes the loading context to mod
    // constructors only from 47.3 on, and NeoForge 1.20.1 never does.
    public CaliberLib() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Loading Caliber Lib");
    }
}
