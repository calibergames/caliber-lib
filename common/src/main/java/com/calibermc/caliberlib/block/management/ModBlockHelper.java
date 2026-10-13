package com.calibermc.caliberlib.block.management;

import com.calibermc.caliberlib.CaliberLib;
import com.calibermc.caliberlib.block.custom.*;
import com.calibermc.caliberlib.block.properties.RecipeStoneTypes;
import com.calibermc.caliberlib.compat.ChippedPath;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

import static com.calibermc.caliberlib.data.ModBlockFamily.Variant;

public class ModBlockHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaliberLib.class);

    private static final List<Variant> ALL_VARIANTS = List.of(Variant.values());

    // Filters
    private static final List<Variant> STONE_ONLY = List.of(Variant.ARCH, Variant.ARCH_HALF, Variant.ARCH_LARGE, Variant.ARCH_LARGE_HALF, Variant.ARROWSLIT);
    private static final List<Variant> STRIPPED = List.of(Variant.BEAM_DIAGONAL, Variant.BEAM_HORIZONTAL, Variant.BEAM_LINTEL, Variant.BEAM_POSTS, Variant.BEAM_VERTICAL, Variant.DOOR_FRAME, Variant.DOOR_FRAME_LINTEL);
    private static final List<Variant> BUTTONS = List.of(Variant.BUTTON, Variant.PRESSURE_PLATE);
    private static final List<Variant> DOORS = List.of(Variant.DOOR, Variant.TRAPDOOR);
    private static final List<Variant> FENCES = List.of(Variant.FENCE, Variant.FENCE_GATE);
    private static final List<Variant> ROOFS = List.of(Variant.ROOF_PEAK, Variant.ROOF_22, Variant.ROOF_45, Variant.ROOF_67);
    private static final List<Variant> SIGNS = List.of(Variant.HANGING_SIGN, Variant.SIGN, Variant.WALL_HANGING_SIGN, Variant.WALL_SIGN);
    private static final List<Variant> SLABS_STAIRS = List.of(Variant.SLAB, Variant.STAIRS);

    // Combined Filters
    private static final List<Variant> BUTTONS_DOORS_FENCES = List.of(Variant.BUTTON, Variant.PRESSURE_PLATE, Variant.DOOR, Variant.TRAPDOOR, Variant.FENCE, Variant.FENCE_GATE);
    private static final List<Variant> DOORS_FENCES_ROOFS_SIGNS = List.of(Variant.DOOR, Variant.TRAPDOOR, Variant.FENCE, Variant.FENCE_GATE, Variant.ROOF_PEAK, Variant.ROOF_22, Variant.ROOF_45, Variant.ROOF_67, Variant.HANGING_SIGN, Variant.SIGN, Variant.WALL_HANGING_SIGN, Variant.WALL_SIGN);
    private static final List<Variant> BUTTONS_DOORS_FENCES_ROOFS_SIGNS = List.of(Variant.BUTTON, Variant.PRESSURE_PLATE, Variant.DOOR, Variant.TRAPDOOR, Variant.FENCE, Variant.FENCE_GATE, Variant.ROOF_PEAK, Variant.ROOF_22, Variant.ROOF_45, Variant.ROOF_67, Variant.HANGING_SIGN, Variant.SIGN, Variant.WALL_HANGING_SIGN, Variant.WALL_SIGN);

    /* Final unmodifiable lists */
    public static final List<Variant> VANILLA_STONE_VARIANTS = createVanillaStoneVariants();
    public static final List<Variant> STONE_VARIANTS = createStoneVariants();
    public static final List<Variant> STONE_VARIANTS_WITHOUT_SLAB = createStoneVariantsWithoutSlab();
    public static final List<Variant> STONE_VARIANTS_WITHOUT_SLAB_STAIRS = createStoneVariantsWithoutSlabStairs();
    public static final List<Variant> STONE_VARIANTS_WITHOUT_SLAB_STAIRS_WALL = createStoneVariantsWithoutSlabStairsWall();
    public static final List<Variant> TERRACOTTA_VARIANTS = createTerracottaVariants();
    public static final List<Variant> VANILLA_PLANK_VARIANTS = createVanillaPlankVariants();
    public static final List<Variant> PLANK_VARIANTS = createPlankVariants(); // PLANK_VARIANTS_ETC
    public static final List<Variant> COMPAT_PLANK_VARIANTS = createCompatPlankVariants(); // COMPAT_PLANK_VARIANTS
    public static final List<Variant> BOARD_VARIANTS = createBoardVariants();
    public static final List<Variant> VANILLA_STRIPPED_WOOD_VARIANTS = createVanillaStrippedWoodVariants();
    public static final List<Variant> STRIPPED_WOOD_VARIANTS = createStrippedWoodVariants();
    public static final List<Variant> ROOF_VARIANTS = createRoofVariants();
    public static final List<Variant> THATCH_VARIANTS = createThatchVariants();
    public static final List<Variant> WOOL_VARIANTS = createWoolVariants();

    private static List<Variant> createVanillaStoneVariants() {
        List<Variant> VANILLA_STONE_VARIANTS = new ArrayList<>(ALL_VARIANTS);
        VANILLA_STONE_VARIANTS.removeAll(DOORS_FENCES_ROOFS_SIGNS);
        VANILLA_STONE_VARIANTS.removeAll(STRIPPED);
        VANILLA_STONE_VARIANTS.remove(Variant.TALL_DOOR);
        return List.copyOf(VANILLA_STONE_VARIANTS);
    }

    private static List<Variant> createStoneVariants() {
        List<Variant> STONE_VARIANTS = new ArrayList<>(VANILLA_STONE_VARIANTS);
        STONE_VARIANTS.removeAll(BUTTONS);
        STONE_VARIANTS.remove(Variant.BASE);
        return List.copyOf(STONE_VARIANTS);
    }

    private static List<Variant> createStoneVariantsWithoutSlab() {
        List<Variant> STONE_VARIANTS_WITHOUT_SLAB = new ArrayList<>(STONE_VARIANTS);
        STONE_VARIANTS_WITHOUT_SLAB.remove(Variant.SLAB);
        return List.copyOf(STONE_VARIANTS_WITHOUT_SLAB);
    }

    private static List<Variant> createStoneVariantsWithoutSlabStairs() {
        List<Variant> STONE_VARIANTS_WITHOUT_SLAB_STAIRS = new ArrayList<>(STONE_VARIANTS_WITHOUT_SLAB);
        STONE_VARIANTS_WITHOUT_SLAB_STAIRS.remove(Variant.STAIRS);
        return List.copyOf(STONE_VARIANTS_WITHOUT_SLAB_STAIRS);
    }

    private static List<Variant> createStoneVariantsWithoutSlabStairsWall() {
        List<Variant> STONE_VARIANTS_WITHOUT_SLAB_STAIRS_WALL = new ArrayList<>(STONE_VARIANTS_WITHOUT_SLAB_STAIRS);
        STONE_VARIANTS_WITHOUT_SLAB_STAIRS_WALL.remove(Variant.WALL);
        return List.copyOf(STONE_VARIANTS_WITHOUT_SLAB_STAIRS_WALL);
    }

    private static List<Variant> createTerracottaVariants() {
        List<Variant> TERRACOTTA_VARIANTS = new ArrayList<>(ALL_VARIANTS);
        TERRACOTTA_VARIANTS.removeAll(BUTTONS_DOORS_FENCES);
        TERRACOTTA_VARIANTS.removeAll(SIGNS);
        TERRACOTTA_VARIANTS.removeAll(STRIPPED);
        TERRACOTTA_VARIANTS.remove(Variant.TALL_DOOR);
        TERRACOTTA_VARIANTS.remove(Variant.BASE);
        return List.copyOf(TERRACOTTA_VARIANTS);
    }

    private static List<Variant> createVanillaPlankVariants() {
        List<Variant> VANILLA_PLANK_VARIANTS = new ArrayList<>(ALL_VARIANTS);
        VANILLA_PLANK_VARIANTS.removeAll(STONE_ONLY);
        VANILLA_PLANK_VARIANTS.removeAll(STRIPPED);
        VANILLA_PLANK_VARIANTS.removeAll(ROOFS);
        return List.copyOf(VANILLA_PLANK_VARIANTS);
    }

    private static List<Variant> createPlankVariants() {
        List<Variant> PLANK_VARIANTS = new ArrayList<>(VANILLA_PLANK_VARIANTS);
        PLANK_VARIANTS.removeAll(BUTTONS_DOORS_FENCES);
        PLANK_VARIANTS.removeAll(SLABS_STAIRS);
        PLANK_VARIANTS.removeAll(SIGNS);
        PLANK_VARIANTS.remove(Variant.BASE);
        return List.copyOf(PLANK_VARIANTS);
    }

    private static List<Variant> createCompatPlankVariants() {
        List<Variant> COMPAT_PLANK_VARIANTS = new ArrayList<>(PLANK_VARIANTS);
        COMPAT_PLANK_VARIANTS.remove(Variant.TALL_DOOR);
        return List.copyOf(COMPAT_PLANK_VARIANTS);
    }

    private static List<Variant> createBoardVariants() {
        List<Variant> BOARD_VARIANTS = new ArrayList<>(ALL_VARIANTS);
        BOARD_VARIANTS.removeAll(DOORS_FENCES_ROOFS_SIGNS);
        BOARD_VARIANTS.removeAll(STONE_ONLY);
        BOARD_VARIANTS.removeAll(STRIPPED);
        BOARD_VARIANTS.remove(Variant.TALL_DOOR);
        return List.copyOf(BOARD_VARIANTS);
    }

    private static List<Variant> createRoofVariants() {
        List<Variant> ROOF_VARIANTS = new ArrayList<>(ROOFS);
        return List.copyOf(ROOF_VARIANTS);
    }

    private static List<Variant> createVanillaStrippedWoodVariants() {
        List<Variant> VANILLA_STRIPPED_WOOD_VARIANTS = new ArrayList<>(ALL_VARIANTS);
        VANILLA_STRIPPED_WOOD_VARIANTS.removeAll(BUTTONS_DOORS_FENCES_ROOFS_SIGNS);
        VANILLA_STRIPPED_WOOD_VARIANTS.removeAll(STONE_ONLY);
        VANILLA_STRIPPED_WOOD_VARIANTS.remove(Variant.TALL_DOOR);
        return List.copyOf(VANILLA_STRIPPED_WOOD_VARIANTS);
    }

    private static List<Variant> createStrippedWoodVariants() {
        List<Variant> STRIPPED_WOOD_VARIANTS = new ArrayList<>(VANILLA_STRIPPED_WOOD_VARIANTS);
        STRIPPED_WOOD_VARIANTS.remove(Variant.BASE);
        return List.copyOf(STRIPPED_WOOD_VARIANTS);
    }

    private static List<Variant> createThatchVariants() {
        List<Variant> THATCH_VARIANTS = new ArrayList<>(ALL_VARIANTS);
        THATCH_VARIANTS.removeAll(BUTTONS);
        THATCH_VARIANTS.removeAll(DOORS);
        THATCH_VARIANTS.removeAll(FENCES);
        THATCH_VARIANTS.removeAll(SIGNS);
        THATCH_VARIANTS.removeAll(STONE_ONLY);
        THATCH_VARIANTS.removeAll(STRIPPED);
        THATCH_VARIANTS.remove(Variant.BALUSTRADE);
        THATCH_VARIANTS.remove(Variant.TALL_DOOR);
        return List.copyOf(THATCH_VARIANTS);
    }

    private static List<Variant> createWoolVariants() {
        List<Variant> WOOL_VARIANTS = new ArrayList<>(BOARD_VARIANTS);
        WOOL_VARIANTS.removeAll(BUTTONS);
        WOOL_VARIANTS.remove(Variant.BALUSTRADE);
        WOOL_VARIANTS.remove(Variant.BASE);
        return List.copyOf(WOOL_VARIANTS);
    }


    public static <T> List<T> modifyList(List<T> list, Consumer<List<T>> consumer) {
        List<T> newList = new ArrayList<>(list);
        consumer.accept(newList);
        return newList;
    }


    // Asset & Data Generation presets
}
