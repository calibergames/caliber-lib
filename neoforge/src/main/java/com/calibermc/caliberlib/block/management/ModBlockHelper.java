package com.calibermc.caliberlib.block.management;

import com.calibermc.caliberlib.CaliberLib;
import com.calibermc.caliberlib.block.custom.*;
import com.calibermc.caliberlib.block.properties.RecipeStoneTypes;
import com.calibermc.caliberlib.compat.ChippedPath;
import com.calibermc.caliberlib.data.datagen.ModBlockStateProvider;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraftforge.client.model.generators.ModelBuilder;
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
    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> ARCH = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<ArchBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) -> provider.archBlock(block, side, bottom, top), provider::archBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> ARCH_HALF = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<HalfArchBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.archHalfBlock(block, side, bottom, top), provider::archHalfBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> ARCH_LARGE = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<LargeArchBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.archLargeBlock(block, side, bottom, top), provider::archLargeBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> ARCH_LARGE_HALF = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<LargeHalfArchBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.archLargeHalfBlock(block, side, bottom, top), provider::archLargeHalfBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> ARROWSLIT = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<ArrowSlitBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.arrowslitBlock(block, side, bottom, top), provider::arrowslitBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> BALUSTRADE = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<BalustradeBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.balustradeBlock(block, side, bottom, top), provider::balustradeBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> BEAM_DIAGONAL = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<DiagonalBeamBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.beamDiagonalBlock(block, tex), provider::beamDiagonalBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> BEAM_HORIZONTAL = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<HorizontalBeamBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.beamHorizontalBlock(block, tex), provider::beamHorizontalBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> BEAM_LINTEL = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<BeamLintelBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.beamLintelBlock(block, side, bottom, top), provider::beamLintelBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> BEAM_POSTS = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<BeamPostsBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.beamPostsBlock(block, tex), provider::beamPostsBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> BEAM_VERTICAL = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<VerticalBeamBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.beamVerticalBlock(block, tex), provider::beamVerticalBlock);

//    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> BUTTON = (textureFrom) -> (b, provider) ->
//            provider.buttonBlock((StoneButtonBlock) b.get(), provider.blockTexture(BlockManager.getMainBy(b, textureFrom)));

//    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> WOOD_BUTTON = (textureFrom) -> (b, provider) ->
//            provider.buttonBlock((WoodButtonBlock) b.get(), provider.blockTexture(BlockManager.getMainBy(b, textureFrom)));

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> BUTTON = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<ButtonBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) -> {
                provider.buttonBlock(block, top);
                provider.models().buttonInventory(provider.name(block) + "_inventory", top);
            }, (block, texture) -> {
                provider.buttonBlock(block, texture);
                provider.models().buttonInventory(provider.name(block) + "_inventory", texture);
            });

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> CAPITAL = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<CapitalBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.capitalBlock(block, side, bottom, top), provider::capitalBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> CORNER = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<CornerLayerBlock>fixBlockTex(textureFrom, b, provider, provider::cornerLayerBlock, provider::cornerLayerBlock);

//    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> CORNER_SLAB = (textureFrom) -> (b, provider) ->
//            ModBlockHelper.<CornerSlabBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
//                    provider.cornerSlabBlock(block, side, bottom, top), provider::cornerSlabBlock);
//
//    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> CORNER_SLAB_VERTICAL = (textureFrom) -> (b, provider) ->
//            ModBlockHelper.<VerticalCornerSlabBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
//                    provider.cornerSlabVerticalBlock(block, side, bottom, top), provider::cornerSlabVerticalBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> CORNER_SLAB = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<CornerSlabLayerBlock>fixBlockTex(textureFrom, b, provider, provider::cornerSlabLayerBlock, provider::cornerSlabLayerBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> CORNER_SLAB_VERTICAL = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<VerticalCornerSlabLayerBlock>fixBlockTex(textureFrom, b, provider, provider::cornerSlabLayerVerticalBlock, provider::cornerSlabLayerVerticalBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> DOOR = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<DoorBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.doorBlock(block, bottom, top), (block, texture) -> {
                ResourceLocation loc = provider.blockTexture(block);
                provider.doorBlock(block, loc.withPath(loc.getPath() + "_bottom"), loc.withPath(loc.getPath() + "_top"));
            });

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> TALL_DOOR = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<TallDoorBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.tallDoorBlock(block, bottom, side, top), (block, texture) -> {
                ResourceLocation loc = provider.blockTexture(block);
                provider.tallDoorBlock(block, loc.withPath(loc.getPath() + "_bottom"), loc.withPath(loc.getPath() + "_middle"), loc.withPath(loc.getPath() + "_top"));
            });

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> DOOR_FRAME = (textureFrom) -> (b, provider) ->

            ModBlockHelper.<DoorFrameBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                            provider.doorFrameBlock(block, tex), provider::doorFrameBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> DOOR_FRAME_LINTEL = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<DoorFrameLintelBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.doorFrameLintelBlock(block, tex), provider::doorFrameLintelBlock);

//    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> EIGHTH = (textureFrom) -> (b, provider) ->
//            ModBlockHelper.<EighthBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
//                    provider.eighthBlock(block, side, bottom, top), provider::eighthBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> EIGHTH = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<EighthLayerBlock>fixBlockTex(textureFrom, b, provider, provider::eighthLayerBlock, provider::eighthLayerBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> FENCE = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<FenceBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) -> {
                provider.fenceBlock(block, top);
                provider.models().fenceInventory(provider.name(block) + "_inventory", top);
            }, (block, texture) -> {
                provider.fenceBlock(block, texture);
                provider.models().fenceInventory(provider.name(block) + "_inventory", texture);
            });

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> FENCE_GATE = (textureFrom) -> (b, provider) ->
            provider.fenceGateBlock((FenceGateBlock) b.get(), provider.blockTexture(BlockManager.getMainBy(b, textureFrom)));

//    public static final BiFunction<Supplier<Block>, Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> HANGING_SIGN = (sign, wall) -> (b, provider) ->
//            provider.hangingSignBlock(sign.get(), wall.get(), provider.blockTexture(BlockManager.getMainBy(b, sign)));

    public static final BiConsumer<Supplier<Block>, Pair<BlockManager, ModBlockStateProvider>> HANGING_SIGN = (s, pair) -> {
        ModBlockStateProvider provider = pair.getSecond();
        Block sign = pair.getFirst().get(Variant.HANGING_SIGN);
        Block wall = pair.getFirst().get(Variant.WALL_HANGING_SIGN);
        provider.hangingSignBlock(sign, wall, provider.blockTexture(BlockManager.getMainBy(s, () -> sign)));
    };

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> LAYER = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<SlabLayerBlock>fixBlockTex(textureFrom, b, provider, provider::slabLayerBlock, provider::slabLayerBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> LAYER_VERTICAL = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<VerticalSlabLayerBlock>fixBlockTex(textureFrom, b, provider, provider::slabVerticalLayerBlock, provider::slabVerticalLayerBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> PILLAR = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<PillarLayerBlock>fixBlockTex(textureFrom, b, provider, provider::pillarLayerBlock, provider::pillarLayerBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> PRESSURE_PLATE = (textureFrom) -> (b, provider) ->
            provider.pressurePlateBlock((PressurePlateBlock) b.get(), provider.blockTexture(BlockManager.getMainBy(b, textureFrom)));

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> TRAP_DOOR = (textureFrom) -> (b, provider) ->
            provider.trapdoorBlock((TrapDoorBlock) b.get(), provider.blockTexture(BlockManager.getMainBy(b, textureFrom)), true);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> QUARTER = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<QuarterLayerBlock>fixBlockTex(textureFrom, b, provider, provider::quarterLayerBlock, provider::quarterLayerBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> VERTICAL_QUARTER = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<VerticalQuarterLayerBlock>fixBlockTex(textureFrom, b, provider, provider::quarterLayerVerticalBlock, provider::quarterLayerVerticalBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> ROOF_22 = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<Roof22Block>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.roof22Block(block, tex), provider::roof22Block);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> ROOF_45 = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<Roof45Block>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.roof45Block(block, tex), provider::roof45Block);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> ROOF_67 = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<Roof67Block>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.roof67Block(block, tex), provider::roof67Block);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> ROOF_PEAK = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<RoofPeakBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.roofPeakBlock(block, tex), provider::roofPeakBlock);

    public static final BiConsumer<Supplier<Block>, Pair<BlockManager, ModBlockStateProvider>> SIGN = (s, pair) -> {
        ModBlockStateProvider provider = pair.getSecond();
        StandingSignBlock sign = (StandingSignBlock) pair.getFirst().get(Variant.SIGN);
        WallSignBlock wall = (WallSignBlock) pair.getFirst().get(Variant.WALL_SIGN);
        provider.signBlock(sign, wall, provider.blockTexture(BlockManager.getMainBy(s, () -> sign)));
    };

//    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> SLAB = (textureFrom) -> (b, provider) ->
//            ModBlockHelper.<SlabBlock>fixBlockTex(textureFrom, b, provider, provider::slabBlock, provider::slabBlock);

//    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> SLAB = (textureFrom) -> (b, provider) ->
//            ModBlockHelper.<SlabBlock>fixBlockTex(textureFrom, b, provider, (block, tex, side, bottom, top) ->
//                    provider.slabBlock(block, side, bottom, top), provider::slabBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> SLAB = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<SlabBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) -> {
                provider.slabBlock(block, tex, side, bottom, top);
                provider.models().slab(provider.name(block), side, bottom, top);
                provider.models().slab(provider.name(block) + "_top", side, bottom, top);
            }, (block, texture) -> {
                provider.slabBlock(block, texture, texture, texture, texture);
            });

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> SLAB_VERTICAL = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<VerticalSlabBlock>fixBlockTex(textureFrom, b, provider, provider::slabVerticalBlock, provider::slabVerticalBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> STAIRS = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<StairBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.stairsBlock(block, side, bottom, top), provider::stairsBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> WALL = (textureFrom) -> (b, provider) ->
            ModBlockHelper.<WallBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) -> {
                provider.wallBlock(block, top);
                provider.models().wallInventory(provider.name(block) + "_inventory", top);
            }, (block, texture) -> {
                provider.wallBlock(block, texture);
                provider.models().wallInventory(provider.name(block) + "_inventory", texture);
            });

    public static final BiFunction<String, Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> WINDOW = (s, textureFrom) -> (b, provider) ->
            ModBlockHelper.<WindowBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.windowBlock(block, side, bottom, top), provider::windowBlock);

    public static final BiFunction<String, Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> WINDOW_HALF = (s, textureFrom) -> (b, provider) ->
            ModBlockHelper.<HalfWindowBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) -> provider.windowHalfBlock(block, side, bottom, top), provider::windowHalfBlock);


    public static <T extends ModelBuilder<T>> T textures(T builder, ResourceLocation location) {
        ResourceLocation sideLoc = location;
        ResourceLocation bottomLoc = location;
        ResourceLocation topLoc = location;
        if (location.getPath().equals("block/basalt") || location.getPath().equals("block/polished_basalt")) {
            sideLoc = new ResourceLocation(location.getNamespace(), location.getPath() + "_side");
            bottomLoc = topLoc = new ResourceLocation(location.getNamespace(), location.getPath() + "_top");
        }

        return builder.texture("side", sideLoc)
                .texture("bottom", bottomLoc)
                .texture("top", topLoc);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Block> void fixBlockTex(Supplier<Block> textureFrom, Supplier<Block> b, ModBlockStateProvider provider, BlockGenLayerWithSides<T> genWithSides, BlockGenWithBaseBlock<T> gen) {
        ResourceLocation tex = provider.blockTexture(BlockManager.getMainBy(b, textureFrom));
        ResourceLocation top = tex;
        ResourceLocation side = tex;
        ResourceLocation bottom = tex;
        String originalPath = tex.getPath();
        String modifiedPath = tex.getPath();
        String topBottomPath = tex.getPath();
        boolean isMinecraftNamespace = tex.getNamespace().equals("minecraft");
        boolean isCreateStone = tex.getNamespace().equals("create"); //&& (tex.getPath().contains("asurine") || tex.getPath().contains("crimsite") || tex.getPath().contains("limestone") || tex.getPath().contains("ochrum") || tex.getPath().contains("scoria") || tex.getPath().contains("scorchia") || tex.getPath().contains("veridium"));
        boolean isChipped = tex.getNamespace().equals("chipped");

        if (tex.getPath().contains("_wood") && !tex.getPath().contains("stained") || tex.getPath().contains("_hyphae")) {
            String replacement = tex.getPath().contains("_wood") ? "_wood" : "_hyphae";
            String newTexture = tex.getPath().contains("_wood") ? "_log" : "_stem";
            tex = new ResourceLocation(tex.getNamespace(), tex.getPath().replace(replacement, newTexture));
            genWithSides.data((T) b.get(), tex, tex, tex, tex);

        } else if (tex.getPath().contains("bamboo_block") && !tex.getPath().contains("stained")) {
            tex = new ResourceLocation(tex.getNamespace(), "block/stripped_bamboo_block");
            genWithSides.data((T) b.get(), tex, tex, tex, tex);

        } else if (tex.getPath().contains("tudor")) {
            String woodType = extractWoodType(tex);
            String prefix = tex.getPath().contains("stained") ? "stained_" : "";
            top = bottom = new ResourceLocation(tex.getNamespace(), "block/" + prefix + woodType + "_boards");
            genWithSides.data((T) b.get(), side, bottom, top, tex);

        } else if (tex.getPath().equals("block/basalt") || tex.getPath().equals("block/polished_basalt") || (tex.getPath().contains("quartz") && !tex.getPath().contains("brick"))) {
            side = new ResourceLocation(tex.getNamespace(), modifiedPath + "_side");
            top = bottom = new ResourceLocation(tex.getNamespace(), modifiedPath + "_top");
            if (tex.getPath().equals("block/smooth_quartz")) {
                side = top = bottom = new ResourceLocation(tex.getNamespace(), "block/quartz_block_bottom");
            }
            genWithSides.data((T) b.get(), side, bottom, top, tex);

        } else if (tex.getPath().contains("chalk_pillar") || tex.getPath().contains("purpur_pillar")) {
            side = tex;
            top = bottom = new ResourceLocation(tex.getNamespace(), modifiedPath + "_top");
            genWithSides.data((T) b.get(), side, bottom, top, tex);

        } else if (tex.getPath().contains("sandstone")) {

            if (tex.getPath().contains("cut")) {
                modifiedPath = modifiedPath.replace("cut_", "");
                side = tex;
                top = bottom = new ResourceLocation(tex.getNamespace(), modifiedPath + "_top");

            } else if (tex.getPath().contains("chiseled")) {
                modifiedPath = modifiedPath.replace("chiseled_", "");
                side = tex;
                top = bottom = new ResourceLocation(tex.getNamespace(), modifiedPath + "_top");

            } else if (tex.getPath().contains("smooth")) {
                modifiedPath = modifiedPath.replace("smooth_", "");
                side = new ResourceLocation(tex.getNamespace(), modifiedPath + "_top");
                top = bottom = new ResourceLocation(tex.getNamespace(), modifiedPath + "_top");

            } else {
                side = tex;
                top = new ResourceLocation(tex.getNamespace(), modifiedPath + "_top");
                bottom = new ResourceLocation(tex.getNamespace(), modifiedPath + "_bottom");
            }

            if (isMinecraftNamespace) {
                side = tex.getPath().contains("smooth") ? top : new ResourceLocation("minecraft", originalPath);
                top = new ResourceLocation("minecraft", modifiedPath + "_top");
                bottom = !tex.getPath().contains("smooth") && !tex.getPath().contains("cut") && !tex.getPath().contains("chiseled") ?
                        new ResourceLocation("minecraft", modifiedPath + "_bottom") : top;
            }
            genWithSides.data((T) b.get(), side, bottom, top, tex);

        } else if (isCreateStone) {
            String brickPath = originalPath.replace("block/", "block/palettes/stone_types/brick/");
            String capPath = originalPath.replace("block/", "block/palettes/stone_types/cap/");
            String cutPath = originalPath.replace("block/", "block/palettes/stone_types/cut/");
            String layeredPath = originalPath.replace("block/", "block/palettes/stone_types/layered/");
            String naturalPath = originalPath.replace("block/", "block/palettes/stone_types/natural/");
            String pillarPath = originalPath.replace("block/", "block/palettes/stone_types/pillar/");
            String polishedPath = originalPath.replace("block/", "block/palettes/stone_types/polished/");
            String stonePath = originalPath.replace("block/", "block/palettes/stone_types/");

            if (tex.getPath().contains("brick")) {
                if (tex.getPath().contains("small")) {
                    modifiedPath = originalPath.replace("small_", "").replace(tex.getPath().contains("_bricks") ? "_bricks" : "_brick", "");
                    modifiedPath = modifiedPath.replace("block/", "block/palettes/stone_types/small_brick/");
                    top = bottom = side = new ResourceLocation(tex.getNamespace(), modifiedPath + "_cut_small_brick");
                } else if (tex.getPath().contains("cut")) {
                    modifiedPath = brickPath.replace("cut_", "").replace(tex.getPath().contains("_bricks") ? "_bricks" : "_brick", "");
                    top = bottom = side = new ResourceLocation(tex.getNamespace(), modifiedPath + "_cut_brick");
                }
            } else if (tex.getPath().contains("cut") && !tex.getPath().contains("polished") && !tex.getPath().contains("brick")) {
                modifiedPath = cutPath.replace("cut_", "");
                top = bottom = side = new ResourceLocation(tex.getNamespace(), modifiedPath + "_cut");

            } else if (tex.getPath().contains("layered")) {
                topBottomPath = capPath.replace("layered_", "");
                modifiedPath = layeredPath.replace("layered_", "");
                side = new ResourceLocation(tex.getNamespace(), modifiedPath + "_cut_layered");
                top = bottom = new ResourceLocation(tex.getNamespace(), topBottomPath + "_cut_cap");

            } else if (tex.getPath().contains("pillar")) {
                topBottomPath = capPath.replace("_pillar", "");
                modifiedPath = pillarPath.replace("_pillar", "_cut_pillar");
                side = new ResourceLocation(tex.getNamespace(), modifiedPath);
                top = bottom = new ResourceLocation(tex.getNamespace(), topBottomPath + "_cut_cap");

            } else if (tex.getPath().contains("polished_cut")) {
                modifiedPath = polishedPath.replace("polished_cut_", "");
                top = bottom = side = new ResourceLocation(tex.getNamespace(), modifiedPath + "_cut_polished");

            } else {
                if (tex.getPath().contains("asurine") || tex.getPath().contains("crimsite") || tex.getPath().contains("ochrum") || tex.getPath().contains("veridium")) {
                    top = bottom = side = new ResourceLocation(tex.getNamespace(), naturalPath + "_0");
                    tex = new ResourceLocation(tex.getNamespace(), originalPath + "_natural_0");
                } else if (tex.getPath().contains("limestone") || tex.getPath().contains("scoria") || tex.getPath().contains("scorchia")) {
                    top = bottom = side = new ResourceLocation(tex.getNamespace(), stonePath);
                }
            }
            genWithSides.data((T) b.get(), side, bottom, top, tex);

        } else if (isChipped) {
            String name = "";
            name = extractChippedPath(tex.getPath(), name);
            modifiedPath = originalPath.replace("block/", "block/" + name + "/");
            top = bottom = side = new ResourceLocation(tex.getNamespace(), modifiedPath);

            genWithSides.data((T) b.get(), side, bottom, top, tex);

        } else {
            gen.data((T) b.get(), tex);
        }
    }

    private static final Set<String> lowerPriorityMaterials = Set.of("bricks", "sandstone", "obsidian", "stone");

    private static String extractChippedPath(String texPath, String defaultValue) {
        List<String> matches = Arrays.stream(ChippedPath.values())
                .map(ChippedPath::getName)
                .filter(texPath::contains)
                .collect(Collectors.toList());

        if (matches.isEmpty()) {
            return defaultValue;
        }

        // Filter to find the best match that isn't in the lower priority materials
        return matches.stream()
                .filter(name -> !lowerPriorityMaterials.contains(name))
                .findFirst()
                .orElseGet(() -> findFirstLowerPriorityMaterial(matches));
    }

    private static String findFirstLowerPriorityMaterial(List<String> matches) {
        // Ordered lower priority materials, "stone" being the lowest priority
        String[] orderedLowerPriorityMaterials = {"bricks", "obsidian", "sandstone", "terracotta", "stone"};

        // Return the first match from the ordered lower priority materials that is present in matches
        for (String material : orderedLowerPriorityMaterials) {
            if (matches.contains(material)) {
                return material;
            }
        }
        return "";
    }

    // Filter brick
//    private static String extractChippedPath(String texPath, String defaultValue) {
//        List<String> matches = Arrays.stream(ChippedPath.values())
//                .map(ChippedPath::getName)
//                .filter(texPath::contains)
//                .collect(Collectors.toList());  // Collect all matches
//
//        if (matches.isEmpty()) {
//            return defaultValue;  // Return default if no matches
//        }
//
//        // Prioritize non-'bricks' matches, use 'bricks' only if it's the only match
//        return matches.stream()
//                .filter(name -> !name.equals("bricks"))  // Filter out 'bricks'
//                .findFirst()                             // Try to find any match that isn't 'bricks'
//                .orElse("bricks");                       // If none found, default to 'bricks' if it was in the original matches
//    }


    private static String extractWoodType(ResourceLocation resourceLocation) {
        if (resourceLocation.getPath().contains("acacia")) {
            return "acacia";
        } else if (resourceLocation.getPath().contains("bamboo")) {
            return "bamboo";
        } else if (resourceLocation.getPath().contains("birch")) {
            return "birch";
        } else if (resourceLocation.getPath().contains("cherry")) {
            return "cherry";
        } else if (resourceLocation.getPath().contains("dark_oak")) {
            return "dark_oak";
        } else if (resourceLocation.getPath().contains("jungle")) {
            return "jungle";
        } else if (resourceLocation.getPath().contains("mangrove")) {
            return "mangrove";
        } else if (resourceLocation.getPath().contains("oak") && !resourceLocation.getPath().contains("dark")) {
            return "oak";
        } else if (resourceLocation.getPath().contains("spruce")) {
            return "spruce";
        } else {
            return null;
        }
    }


    @FunctionalInterface
    interface BlockGenLayerWithSides<T extends Block> {
        void data(T block, ResourceLocation side, ResourceLocation top, ResourceLocation bottom, ResourceLocation baseBlock);
    }

    @FunctionalInterface
    interface BlockGenWithBaseBlock<T extends Block> {
        void data(T block, ResourceLocation baseBlock);
    }
}