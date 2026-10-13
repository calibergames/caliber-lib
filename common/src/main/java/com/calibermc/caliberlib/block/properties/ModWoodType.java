package com.calibermc.caliberlib.block.properties;

import com.calibermc.caliberlib.CaliberLib;
import com.calibermc.caliberlib.block.management.BlockManager;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.HashSet;
import java.util.Set;

public class ModWoodType {

    public static final WoodType ALPHA = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":alpha", ModBlockSetType.ALPHA));
    public static final WoodType BAOBAB = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":baobab", ModBlockSetType.BAOBAB));
    public static final WoodType BLACKWOOD = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":blackwood", ModBlockSetType.BLACKWOOD));
    public static final WoodType BLUE_BIOSHROOM = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":blue_bioshroom", ModBlockSetType.BLUE_BIOSHROOM));
    public static final WoodType BRIMWOOD = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":brimwood", ModBlockSetType.BRIMWOOD));
    public static final WoodType CEDAR = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":cedar", ModBlockSetType.CEDAR));
    public static final WoodType COBALT = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":cobalt", ModBlockSetType.COBALT));
    public static final WoodType CYPRESS = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":cypress", ModBlockSetType.CYPRESS));
    public static final WoodType DEAD = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":dead", ModBlockSetType.DEAD));
    public static final WoodType EUCALYPTUS = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":eucalyptus", ModBlockSetType.EUCALYPTUS));
    public static final WoodType FIR = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":fir", ModBlockSetType.FIR));
    public static final WoodType GREEN_BIOSHROOM = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":green_bioshroom", ModBlockSetType.GREEN_BIOSHROOM));
    public static final WoodType HELLBARK = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":hellbark", ModBlockSetType.HELLBARK));
    public static final WoodType JACARANDA = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":jacaranda", ModBlockSetType.JACARANDA));
    public static final WoodType JOSHUA = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":joshua", ModBlockSetType.JOSHUA));
    public static final WoodType KAPOK = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":kapok", ModBlockSetType.KAPOK));
    public static final WoodType LARCH = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":larch", ModBlockSetType.LARCH));
    public static final WoodType MAGIC = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":magic", ModBlockSetType.MAGIC));
    public static final WoodType MAGNOLIA = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":magnolia", ModBlockSetType.MAGNOLIA));
    public static final WoodType MAHOGANY = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":mahogany", ModBlockSetType.MAHOGANY));
    public static final WoodType MAPLE = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":maple", ModBlockSetType.MAPLE));
    public static final WoodType MAUVE = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":mauve", ModBlockSetType.MAUVE));
    public static final WoodType PALM = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":palm", ModBlockSetType.PALM));
    public static final WoodType PINE = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":pine", ModBlockSetType.PINE));
    public static final WoodType PINK_BIOSHROOM = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":pink_bioshroom", ModBlockSetType.PINK_BIOSHROOM));
    public static final WoodType REDWOOD = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":redwood", ModBlockSetType.REDWOOD));
    public static final WoodType SOCOTRA = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":socotra", ModBlockSetType.SOCOTRA));
    public static final WoodType UMBRAN = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":umbran", ModBlockSetType.UMBRAN));
    public static final WoodType WILLOW = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":willow", ModBlockSetType.WILLOW));
    public static final WoodType YELLOW_BIOSHROOM = WoodType.register(new WoodType(CaliberLib.MOD_ID + ":yellow_bioshroom", ModBlockSetType.YELLOW_BIOSHROOM));


    public static Set<WoodType> getWoodTypes() {
        Set<WoodType> woodTypes = new HashSet<>();
        woodTypes.add(ALPHA);
        woodTypes.add(BAOBAB);
        woodTypes.add(BLACKWOOD);
        woodTypes.add(BLUE_BIOSHROOM);
        woodTypes.add(BRIMWOOD);
        woodTypes.add(CEDAR);
        woodTypes.add(COBALT);
        woodTypes.add(CYPRESS);
        woodTypes.add(DEAD);
        woodTypes.add(EUCALYPTUS);
        woodTypes.add(FIR);
        woodTypes.add(GREEN_BIOSHROOM);
        woodTypes.add(HELLBARK);
        woodTypes.add(JACARANDA);
        woodTypes.add(JOSHUA);
        woodTypes.add(KAPOK);
        woodTypes.add(LARCH);
        woodTypes.add(MAGIC);
        woodTypes.add(MAGNOLIA);
        woodTypes.add(MAHOGANY);
        woodTypes.add(MAPLE);
        woodTypes.add(MAUVE);
        woodTypes.add(PALM);
        woodTypes.add(PINE);
        woodTypes.add(PINK_BIOSHROOM);
        woodTypes.add(REDWOOD);
        woodTypes.add(SOCOTRA);
        woodTypes.add(UMBRAN);
        woodTypes.add(WILLOW);
        woodTypes.add(YELLOW_BIOSHROOM);

        return woodTypes;
    }

    public static boolean checkWoodType(BlockManager blockManager) {
        Set<WoodType> woodTypes = getWoodTypes();
        return woodTypes.stream().anyMatch(p -> p.name().equals(blockManager.blockType().name()));
    }

}

