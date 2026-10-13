package com.calibermc.caliberlib.data.datagen;

import com.calibermc.caliberlib.CaliberLib;
import com.calibermc.caliberlib.block.custom.*;
import com.calibermc.caliberlib.block.properties.RecipeStoneTypes;
import com.calibermc.caliberlib.compat.ChippedPath;
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
import com.calibermc.caliberlib.block.management.BlockManager;
import com.calibermc.caliberlib.data.ModBlockFamily;
import com.calibermc.caliberlib.block.management.ModBlockHelper;

/**
 * The block-state and model generators for each shape, used by the data
 * generators. {@link #forVariant} picks the generator a variant uses.
 */
public class ModBlockModels {


    /** The model of a full block (or a shape with no texture source): cube, or bottom/side/top. */
    public static final BiConsumer<Supplier<Block>, Pair<BlockManager, ModBlockStateProvider>> DEFAULT = (b, pair) -> {
        ModBlockStateProvider provider = pair.getSecond();
        fixBlockTex(b, b, provider, (block, side, bottom, top, tex) ->
                provider.simpleBlock(b.get(), provider.models().cubeBottomTop(provider.name(b.get()), side, bottom, top)), (block, tex) ->
                provider.simpleBlock(b.get(), provider.models().cubeAll(provider.name(b.get()), tex)));
    };

    /** Writes the block state and models of one shape of a family. */
    public static void generate(BlockManager.BlockAdditional shape, Supplier<Block> block, BlockManager manager, ModBlockStateProvider provider) {
        Supplier<Block> src = shape.textureSource;
        if (src == null) {
            DEFAULT.accept(block, Pair.of(manager, provider));
            return;
        }
        BiConsumer<Supplier<Block>, ModBlockStateProvider> generator = switch (shape.variant) {
            case ARCH -> ARCH.apply(src);
            case ARCH_HALF -> ARCH_HALF.apply(src);
            case ARCH_LARGE -> ARCH_LARGE.apply(src);
            case ARCH_LARGE_HALF -> ARCH_LARGE_HALF.apply(src);
            case ARROWSLIT -> ARROWSLIT.apply(src);
            case BALUSTRADE -> BALUSTRADE.apply(src);
            case BUTTON -> BUTTON.apply(src);
            case BEAM_DIAGONAL -> BEAM_DIAGONAL.apply(src);
            case BEAM_HORIZONTAL -> BEAM_HORIZONTAL.apply(src);
            case BEAM_LINTEL -> BEAM_LINTEL.apply(src);
            case BEAM_POSTS -> BEAM_POSTS.apply(src);
            case BEAM_VERTICAL -> BEAM_VERTICAL.apply(src);
            case CAPITAL -> CAPITAL.apply(src);
            case CORNER -> CORNER.apply(src);
            case CORNER_SLAB -> CORNER_SLAB.apply(src);
            case CORNER_SLAB_VERTICAL -> CORNER_SLAB_VERTICAL.apply(src);
            case DOOR -> DOOR.apply(src);
            case DOOR_FRAME -> DOOR_FRAME.apply(src);
            case DOOR_FRAME_LINTEL -> DOOR_FRAME_LINTEL.apply(src);
            case EIGHTH -> EIGHTH.apply(src);
            case FENCE -> FENCE.apply(src);
            case FENCE_GATE -> FENCE_GATE.apply(src);
            case LAYER -> LAYER.apply(src);
            case LAYER_VERTICAL -> LAYER_VERTICAL.apply(src);
            case PILLAR -> PILLAR.apply(src);
            case PRESSURE_PLATE -> PRESSURE_PLATE.apply(src);
            case QUARTER -> QUARTER.apply(src);
            case QUARTER_VERTICAL -> VERTICAL_QUARTER.apply(src);
            case ROOF_22 -> ROOF_22.apply(src);
            case ROOF_45 -> ROOF_45.apply(src);
            case ROOF_67 -> ROOF_67.apply(src);
            case ROOF_PEAK -> ROOF_PEAK.apply(src);
            case SLAB -> SLAB.apply(src);
            case SLAB_VERTICAL -> SLAB_VERTICAL.apply(src);
            case STAIRS -> STAIRS.apply(src);
            case TALL_DOOR -> TALL_DOOR.apply(src);
            case TRAPDOOR -> TRAP_DOOR.apply(src);
            case WALL -> WALL.apply(src);
            case WINDOW -> WINDOW.apply("window", src);
            case WINDOW_HALF -> WINDOW_HALF.apply("window_half", src);
            default -> null;
        };
        if (generator != null) {
            generator.accept(block, provider);
        } else if (shape.variant == ModBlockFamily.Variant.SIGN) {
            SIGN.accept(block, Pair.of(manager, provider));
        } else if (shape.variant == ModBlockFamily.Variant.HANGING_SIGN) {
            HANGING_SIGN.accept(block, Pair.of(manager, provider));
        } else {
            DEFAULT.accept(block, Pair.of(manager, provider));
        }
    }

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> ARCH = (textureFrom) -> (b, provider) ->
            ModBlockModels.<ArchBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) -> provider.archBlock(block, side, bottom, top), provider::archBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> ARCH_HALF = (textureFrom) -> (b, provider) ->
            ModBlockModels.<HalfArchBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.archHalfBlock(block, side, bottom, top), provider::archHalfBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> ARCH_LARGE = (textureFrom) -> (b, provider) ->
            ModBlockModels.<LargeArchBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.archLargeBlock(block, side, bottom, top), provider::archLargeBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> ARCH_LARGE_HALF = (textureFrom) -> (b, provider) ->
            ModBlockModels.<LargeHalfArchBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.archLargeHalfBlock(block, side, bottom, top), provider::archLargeHalfBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> ARROWSLIT = (textureFrom) -> (b, provider) ->
            ModBlockModels.<ArrowSlitBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.arrowslitBlock(block, side, bottom, top), provider::arrowslitBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> BALUSTRADE = (textureFrom) -> (b, provider) ->
            ModBlockModels.<BalustradeBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.balustradeBlock(block, side, bottom, top), provider::balustradeBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> BEAM_DIAGONAL = (textureFrom) -> (b, provider) ->
            ModBlockModels.<DiagonalBeamBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.beamDiagonalBlock(block, tex), provider::beamDiagonalBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> BEAM_HORIZONTAL = (textureFrom) -> (b, provider) ->
            ModBlockModels.<HorizontalBeamBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.beamHorizontalBlock(block, tex), provider::beamHorizontalBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> BEAM_LINTEL = (textureFrom) -> (b, provider) ->
            ModBlockModels.<BeamLintelBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.beamLintelBlock(block, side, bottom, top), provider::beamLintelBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> BEAM_POSTS = (textureFrom) -> (b, provider) ->
            ModBlockModels.<BeamPostsBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.beamPostsBlock(block, tex), provider::beamPostsBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> BEAM_VERTICAL = (textureFrom) -> (b, provider) ->
            ModBlockModels.<VerticalBeamBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.beamVerticalBlock(block, tex), provider::beamVerticalBlock);

//    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> BUTTON = (textureFrom) -> (b, provider) ->
//            provider.buttonBlock((StoneButtonBlock) b.get(), provider.blockTexture(BlockManager.getMainBy(b, textureFrom)));

//    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> WOOD_BUTTON = (textureFrom) -> (b, provider) ->
//            provider.buttonBlock((WoodButtonBlock) b.get(), provider.blockTexture(BlockManager.getMainBy(b, textureFrom)));

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> BUTTON = (textureFrom) -> (b, provider) ->
            ModBlockModels.<ButtonBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) -> {
                provider.buttonBlock(block, top);
                provider.models().buttonInventory(provider.name(block) + "_inventory", top);
            }, (block, texture) -> {
                provider.buttonBlock(block, texture);
                provider.models().buttonInventory(provider.name(block) + "_inventory", texture);
            });

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> CAPITAL = (textureFrom) -> (b, provider) ->
            ModBlockModels.<CapitalBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.capitalBlock(block, side, bottom, top), provider::capitalBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> CORNER = (textureFrom) -> (b, provider) ->
            ModBlockModels.<CornerLayerBlock>fixBlockTex(textureFrom, b, provider, provider::cornerLayerBlock, provider::cornerLayerBlock);

//    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> CORNER_SLAB = (textureFrom) -> (b, provider) ->
//            ModBlockModels.<CornerSlabBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
//                    provider.cornerSlabBlock(block, side, bottom, top), provider::cornerSlabBlock);
//
//    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> CORNER_SLAB_VERTICAL = (textureFrom) -> (b, provider) ->
//            ModBlockModels.<VerticalCornerSlabBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
//                    provider.cornerSlabVerticalBlock(block, side, bottom, top), provider::cornerSlabVerticalBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> CORNER_SLAB = (textureFrom) -> (b, provider) ->
            ModBlockModels.<CornerSlabLayerBlock>fixBlockTex(textureFrom, b, provider, provider::cornerSlabLayerBlock, provider::cornerSlabLayerBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> CORNER_SLAB_VERTICAL = (textureFrom) -> (b, provider) ->
            ModBlockModels.<VerticalCornerSlabLayerBlock>fixBlockTex(textureFrom, b, provider, provider::cornerSlabLayerVerticalBlock, provider::cornerSlabLayerVerticalBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> DOOR = (textureFrom) -> (b, provider) ->
            ModBlockModels.<DoorBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.doorBlock(block, bottom, top), (block, texture) -> {
                ResourceLocation loc = provider.blockTexture(block);
                provider.doorBlock(block, loc.withPath(loc.getPath() + "_bottom"), loc.withPath(loc.getPath() + "_top"));
            });

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> TALL_DOOR = (textureFrom) -> (b, provider) ->
            ModBlockModels.<TallDoorBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.tallDoorBlock(block, bottom, side, top), (block, texture) -> {
                ResourceLocation loc = provider.blockTexture(block);
                provider.tallDoorBlock(block, loc.withPath(loc.getPath() + "_bottom"), loc.withPath(loc.getPath() + "_middle"), loc.withPath(loc.getPath() + "_top"));
            });

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> DOOR_FRAME = (textureFrom) -> (b, provider) ->

            ModBlockModels.<DoorFrameBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                            provider.doorFrameBlock(block, tex), provider::doorFrameBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> DOOR_FRAME_LINTEL = (textureFrom) -> (b, provider) ->
            ModBlockModels.<DoorFrameLintelBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.doorFrameLintelBlock(block, tex), provider::doorFrameLintelBlock);

//    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> EIGHTH = (textureFrom) -> (b, provider) ->
//            ModBlockModels.<EighthBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
//                    provider.eighthBlock(block, side, bottom, top), provider::eighthBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> EIGHTH = (textureFrom) -> (b, provider) ->
            ModBlockModels.<EighthLayerBlock>fixBlockTex(textureFrom, b, provider, provider::eighthLayerBlock, provider::eighthLayerBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> FENCE = (textureFrom) -> (b, provider) ->
            ModBlockModels.<FenceBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) -> {
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
            ModBlockModels.<SlabLayerBlock>fixBlockTex(textureFrom, b, provider, provider::slabLayerBlock, provider::slabLayerBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> LAYER_VERTICAL = (textureFrom) -> (b, provider) ->
            ModBlockModels.<VerticalSlabLayerBlock>fixBlockTex(textureFrom, b, provider, provider::slabVerticalLayerBlock, provider::slabVerticalLayerBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> PILLAR = (textureFrom) -> (b, provider) ->
            ModBlockModels.<PillarLayerBlock>fixBlockTex(textureFrom, b, provider, provider::pillarLayerBlock, provider::pillarLayerBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> PRESSURE_PLATE = (textureFrom) -> (b, provider) ->
            provider.pressurePlateBlock((PressurePlateBlock) b.get(), provider.blockTexture(BlockManager.getMainBy(b, textureFrom)));

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> TRAP_DOOR = (textureFrom) -> (b, provider) ->
            provider.trapdoorBlock((TrapDoorBlock) b.get(), provider.blockTexture(BlockManager.getMainBy(b, textureFrom)), true);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> QUARTER = (textureFrom) -> (b, provider) ->
            ModBlockModels.<QuarterLayerBlock>fixBlockTex(textureFrom, b, provider, provider::quarterLayerBlock, provider::quarterLayerBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> VERTICAL_QUARTER = (textureFrom) -> (b, provider) ->
            ModBlockModels.<VerticalQuarterLayerBlock>fixBlockTex(textureFrom, b, provider, provider::quarterLayerVerticalBlock, provider::quarterLayerVerticalBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> ROOF_22 = (textureFrom) -> (b, provider) ->
            ModBlockModels.<Roof22Block>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.roof22Block(block, tex), provider::roof22Block);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> ROOF_45 = (textureFrom) -> (b, provider) ->
            ModBlockModels.<Roof45Block>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.roof45Block(block, tex), provider::roof45Block);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> ROOF_67 = (textureFrom) -> (b, provider) ->
            ModBlockModels.<Roof67Block>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.roof67Block(block, tex), provider::roof67Block);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> ROOF_PEAK = (textureFrom) -> (b, provider) ->
            ModBlockModels.<RoofPeakBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.roofPeakBlock(block, tex), provider::roofPeakBlock);

    public static final BiConsumer<Supplier<Block>, Pair<BlockManager, ModBlockStateProvider>> SIGN = (s, pair) -> {
        ModBlockStateProvider provider = pair.getSecond();
        StandingSignBlock sign = (StandingSignBlock) pair.getFirst().get(Variant.SIGN);
        WallSignBlock wall = (WallSignBlock) pair.getFirst().get(Variant.WALL_SIGN);
        provider.signBlock(sign, wall, provider.blockTexture(BlockManager.getMainBy(s, () -> sign)));
    };

//    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> SLAB = (textureFrom) -> (b, provider) ->
//            ModBlockModels.<SlabBlock>fixBlockTex(textureFrom, b, provider, provider::slabBlock, provider::slabBlock);

//    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> SLAB = (textureFrom) -> (b, provider) ->
//            ModBlockModels.<SlabBlock>fixBlockTex(textureFrom, b, provider, (block, tex, side, bottom, top) ->
//                    provider.slabBlock(block, side, bottom, top), provider::slabBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> SLAB = (textureFrom) -> (b, provider) ->
            ModBlockModels.<SlabBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) -> {
                provider.slabBlock(block, tex, side, bottom, top);
                provider.models().slab(provider.name(block), side, bottom, top);
                provider.models().slab(provider.name(block) + "_top", side, bottom, top);
            }, (block, texture) -> {
                provider.slabBlock(block, texture, texture, texture, texture);
            });

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> SLAB_VERTICAL = (textureFrom) -> (b, provider) ->
            ModBlockModels.<VerticalSlabBlock>fixBlockTex(textureFrom, b, provider, provider::slabVerticalBlock, provider::slabVerticalBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> STAIRS = (textureFrom) -> (b, provider) ->
            ModBlockModels.<StairBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.stairsBlock(block, side, bottom, top), provider::stairsBlock);

    public static final Function<Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> WALL = (textureFrom) -> (b, provider) ->
            ModBlockModels.<WallBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) -> {
                provider.wallBlock(block, top);
                provider.models().wallInventory(provider.name(block) + "_inventory", top);
            }, (block, texture) -> {
                provider.wallBlock(block, texture);
                provider.models().wallInventory(provider.name(block) + "_inventory", texture);
            });

    public static final BiFunction<String, Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> WINDOW = (s, textureFrom) -> (b, provider) ->
            ModBlockModels.<WindowBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) ->
                    provider.windowBlock(block, side, bottom, top), provider::windowBlock);

    public static final BiFunction<String, Supplier<Block>, BiConsumer<Supplier<Block>, ModBlockStateProvider>> WINDOW_HALF = (s, textureFrom) -> (b, provider) ->
            ModBlockModels.<HalfWindowBlock>fixBlockTex(textureFrom, b, provider, (block, side, bottom, top, tex) -> provider.windowHalfBlock(block, side, bottom, top), provider::windowHalfBlock);


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
