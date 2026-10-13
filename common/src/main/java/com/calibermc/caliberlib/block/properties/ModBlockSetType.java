package com.calibermc.caliberlib.block.properties;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.Set;
import java.util.stream.Stream;

public record ModBlockSetType(String name, boolean canOpenByHand, SoundType soundType, SoundEvent doorClose, SoundEvent doorOpen, SoundEvent trapdoorClose, SoundEvent trapdoorOpen, SoundEvent pressurePlateClickOff, SoundEvent pressurePlateClickOn, SoundEvent buttonClickOff, SoundEvent buttonClickOn) {
//    private static final Set<BlockSetType> VALUES = new ObjectArraySet<>();

    public static final BlockSetType TUDOR_1 = BlockSetType.register(new BlockSetType("tudor_1"));
    public static final BlockSetType TUDOR_2 = BlockSetType.register(new BlockSetType("tudor_2"));

    public static final BlockSetType CEDAR = BlockSetType.register(new BlockSetType("cedar", true, SoundType.WOOD, SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundEvents.WOODEN_BUTTON_CLICK_ON));
    public static final BlockSetType FIR = BlockSetType.register(new BlockSetType("fir", true, SoundType.WOOD, SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundEvents.WOODEN_BUTTON_CLICK_ON));
    public static final BlockSetType MAPLE = BlockSetType.register(new BlockSetType("maple", true, SoundType.WOOD, SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundEvents.WOODEN_BUTTON_CLICK_ON));
    public static final BlockSetType PINE = BlockSetType.register(new BlockSetType("pine", true, SoundType.WOOD, SoundEvents.WOODEN_DOOR_CLOSE, SoundEvents.WOODEN_DOOR_OPEN, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundEvents.WOODEN_TRAPDOOR_OPEN, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundEvents.WOODEN_BUTTON_CLICK_ON));
    public static final BlockSetType MARBLE = BlockSetType.register(new BlockSetType("marble", true, SoundType.STONE, SoundEvents.IRON_DOOR_CLOSE, SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundEvents.IRON_TRAPDOOR_OPEN, SoundEvents.STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundEvents.STONE_BUTTON_CLICK_OFF, SoundEvents.STONE_BUTTON_CLICK_ON));

    public static final BlockSetType ALPHA = BlockSetType.register(new BlockSetType("alpha"));
    public static final BlockSetType BAOBAB = BlockSetType.register(new BlockSetType("baobab"));
    public static final BlockSetType BLACKWOOD = BlockSetType.register(new BlockSetType("blackwood"));
    public static final BlockSetType BLUE_BIOSHROOM = BlockSetType.register(new BlockSetType("blue_bioshroom"));
    public static final BlockSetType BRIMWOOD = BlockSetType.register(new BlockSetType("brimwood"));
    public static final BlockSetType COBALT = BlockSetType.register(new BlockSetType("cobalt"));
    public static final BlockSetType CYPRESS = BlockSetType.register(new BlockSetType("cypress"));
    public static final BlockSetType DEAD = BlockSetType.register(new BlockSetType("dead"));
    public static final BlockSetType EUCALYPTUS = BlockSetType.register(new BlockSetType("eucalyptus"));
    public static final BlockSetType GREEN_BIOSHROOM = BlockSetType.register(new BlockSetType("green_bioshroom"));
    public static final BlockSetType HELLBARK = BlockSetType.register(new BlockSetType("hellbark"));
    public static final BlockSetType JACARANDA = BlockSetType.register(new BlockSetType("jacaranda"));
    public static final BlockSetType JOSHUA = BlockSetType.register(new BlockSetType("joshua"));
    public static final BlockSetType KAPOK = BlockSetType.register(new BlockSetType("kapok"));
    public static final BlockSetType LARCH = BlockSetType.register(new BlockSetType("larch"));
    public static final BlockSetType MAGIC = BlockSetType.register(new BlockSetType("magic"));
    public static final BlockSetType MAGNOLIA = BlockSetType.register(new BlockSetType("magnolia"));
    public static final BlockSetType MAHOGANY = BlockSetType.register(new BlockSetType("mahogany"));
    public static final BlockSetType MAUVE = BlockSetType.register(new BlockSetType("mauve"));
    public static final BlockSetType PALM = BlockSetType.register(new BlockSetType("palm"));
    public static final BlockSetType PINK_BIOSHROOM = BlockSetType.register(new BlockSetType("pink_bioshroom"));
    public static final BlockSetType REDWOOD = BlockSetType.register(new BlockSetType("redwood"));
    public static final BlockSetType SOCOTRA = BlockSetType.register(new BlockSetType("socotra"));
    public static final BlockSetType UMBRAN = BlockSetType.register(new BlockSetType("umbran"));
    public static final BlockSetType WILLOW = BlockSetType.register(new BlockSetType("willow"));
    public static final BlockSetType YELLOW_BIOSHROOM = BlockSetType.register(new BlockSetType("yellow_bioshroom"));
    
    
    
    
//    public static Stream<BlockSetType> values() {
//        return VALUES.stream();
//    }
}









