package com.calibermc.caliberlib.event;

import com.calibermc.caliberlib.CaliberLib;
import com.calibermc.caliberlib.block.properties.ModWoodType;
import net.minecraft.client.renderer.Sheets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


@Mod.EventBusSubscriber(modid = CaliberLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModClientEventBus {

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {

        // Set Wood Types
        Sheets.addWoodType(ModWoodType.CEDAR);
        Sheets.addWoodType(ModWoodType.FIR);
        Sheets.addWoodType(ModWoodType.PINE);
        Sheets.addWoodType(ModWoodType.MAPLE);

    }

    @Mod.EventBusSubscriber(modid = CaliberLib.MOD_ID, value = Dist.CLIENT)
    public static class Client {

    }
}