package com.calibermc.caliberlib.data.datagen;

import com.calibermc.caliberlib.block.custom.*;
import com.calibermc.caliberlib.block.management.BlockManager;
import com.calibermc.caliberlib.block.shapes.*;
import com.calibermc.caliberlib.block.shapes.doors.TallDoorPart;
import com.calibermc.caliberlib.block.shapes.misc.BeamConnection;
import com.calibermc.caliberlib.block.shapes.trim.ArchTrim;
import com.calibermc.caliberlib.block.shapes.trim.LargeArchTrim;
import com.calibermc.caliberlib.data.ModBlockFamily;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;
import java.util.function.Supplier;

public class ModBlockStateProvider extends BlockStateProvider {

    protected final String modid;

    public ModBlockStateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        this(gen, modid, modid, exFileHelper);
    }

    public ModBlockStateProvider(DataGenerator gen, String modid, String blockManagerModId, ExistingFileHelper exFileHelper) {
        super(gen.getPackOutput(), modid, exFileHelper);
        this.modid = blockManagerModId;
    }

    public String name(Block block) {
        return ForgeRegistries.BLOCKS.getKey(block).getPath();
    }

    @Override
    protected void registerStatesAndModels() {
        for (BlockManager blockManager : BlockManager.BLOCK_MANAGERS.get(this.modid)) {
            BlockManager.BlockAdditional base = blockManager.getByVariant(ModBlockFamily.Variant.BASE);
            if (base != null && !base.skipRegister) {
                base.stateGenerator.accept(blockManager::baseBlock, Pair.of(blockManager, this));
            }
            for (Map.Entry<BlockManager.BlockAdditional, Pair<ResourceLocation, Supplier<Block>>> e : blockManager.getBlocks().entrySet()) {
                if (e.getKey().variant != ModBlockFamily.Variant.BASE) {
                    try {
                        e.getKey().stateGenerator.accept(e.getValue().getSecond(), Pair.of(blockManager, this));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public ResourceLocation modLoc(String name) {
        String n = super.modLoc(name).getPath();
        if (n.startsWith("block/templates/")) {
            return new ResourceLocation("caliberlib", n);
        }
        return super.modLoc(name);
    }


    public void archBlock(ArchBlock block) {
        archBlock(block, blockTexture(block));
    }

    public void archBlock(ArchBlock block, ResourceLocation texture) {
        archBlock(block, texture, texture, texture);
    }

    public void archBlock(ArchBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile arch = models().withExistingParent(name(block), modLoc("block/templates/arch"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                arch_trim = models().withExistingParent(name(block) + "_trim", modLoc("block/templates/arch_trim"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                arch_trim_2 = models().withExistingParent(name(block) + "_trim_2", modLoc("block/templates/arch_trim_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                arch_cross = models().withExistingParent(name(block) + "_cross", modLoc("block/templates/arch_cross"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                arch_corner = models().withExistingParent(name(block) + "_corner", modLoc("block/templates/arch_corner"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                arch_t = models().withExistingParent(name(block) + "_t", modLoc("block/templates/arch_t"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);

        getVariantBuilder(block)
                .partialState().with(ArchBlock.FACING, Direction.NORTH).with(ArchBlock.TYPE, ArchShape.STRAIGHT).with(ArchBlock.TRIM, ArchTrim.NONE)
                .modelForState().modelFile(arch).addModel()
                .partialState().with(ArchBlock.FACING, Direction.NORTH).with(ArchBlock.TYPE, ArchShape.STRAIGHT).with(ArchBlock.TRIM, ArchTrim.FRONT)
                .modelForState().modelFile(arch_trim).addModel()
                .partialState().with(ArchBlock.FACING, Direction.NORTH).with(ArchBlock.TYPE, ArchShape.STRAIGHT).with(ArchBlock.TRIM, ArchTrim.BOTH)
                .modelForState().modelFile(arch_trim_2).addModel()
                .partialState().with(ArchBlock.FACING, Direction.NORTH).with(ArchBlock.TYPE, ArchShape.CROSS)
                .modelForState().modelFile(arch_cross).addModel()
                .partialState().with(ArchBlock.FACING, Direction.NORTH).with(ArchBlock.TYPE, ArchShape.CORNER_LEFT)
                .modelForState().modelFile(arch_corner).rotationY(270).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.NORTH).with(ArchBlock.TYPE, ArchShape.CORNER_RIGHT)
                .modelForState().modelFile(arch_corner).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.NORTH).with(ArchBlock.TYPE, ArchShape.LEFT_T)
                .modelForState().modelFile(arch_t).rotationY(90).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.NORTH).with(ArchBlock.TYPE, ArchShape.RIGHT_T)
                .modelForState().modelFile(arch_t).rotationY(270).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.NORTH).with(ArchBlock.TYPE, ArchShape.T)
                .modelForState().modelFile(arch_t).addModel()
                .partialState().with(ArchBlock.FACING, Direction.EAST).with(ArchBlock.TYPE, ArchShape.STRAIGHT).with(ArchBlock.TRIM, ArchTrim.NONE)
                .modelForState().modelFile(arch).rotationY(90).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.EAST).with(ArchBlock.TYPE, ArchShape.STRAIGHT).with(ArchBlock.TRIM, ArchTrim.FRONT)
                .modelForState().modelFile(arch_trim).rotationY(90).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.EAST).with(ArchBlock.TYPE, ArchShape.STRAIGHT).with(ArchBlock.TRIM, ArchTrim.BOTH)
                .modelForState().modelFile(arch_trim_2).rotationY(90).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.EAST).with(ArchBlock.TYPE, ArchShape.CROSS)
                .modelForState().modelFile(arch_cross).rotationY(90).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.EAST).with(ArchBlock.TYPE, ArchShape.CORNER_LEFT)
                .modelForState().modelFile(arch_corner).addModel()
                .partialState().with(ArchBlock.FACING, Direction.EAST).with(ArchBlock.TYPE, ArchShape.CORNER_RIGHT)
                .modelForState().modelFile(arch_corner).rotationY(90).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.EAST).with(ArchBlock.TYPE, ArchShape.LEFT_T)
                .modelForState().modelFile(arch_t).rotationY(180).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.EAST).with(ArchBlock.TYPE, ArchShape.RIGHT_T)
                .modelForState().modelFile(arch_t).addModel()
                .partialState().with(ArchBlock.FACING, Direction.EAST).with(ArchBlock.TYPE, ArchShape.T)
                .modelForState().modelFile(arch_t).rotationY(90).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.SOUTH).with(ArchBlock.TYPE, ArchShape.STRAIGHT).with(ArchBlock.TRIM, ArchTrim.NONE)
                .modelForState().modelFile(arch).rotationY(180).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.SOUTH).with(ArchBlock.TYPE, ArchShape.STRAIGHT).with(ArchBlock.TRIM, ArchTrim.FRONT)
                .modelForState().modelFile(arch_trim).rotationY(180).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.SOUTH).with(ArchBlock.TYPE, ArchShape.STRAIGHT).with(ArchBlock.TRIM, ArchTrim.BOTH)
                .modelForState().modelFile(arch_trim_2).rotationY(180).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.SOUTH).with(ArchBlock.TYPE, ArchShape.CROSS)
                .modelForState().modelFile(arch_cross).rotationY(180).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.SOUTH).with(ArchBlock.TYPE, ArchShape.CORNER_LEFT)
                .modelForState().modelFile(arch_corner).rotationY(90).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.SOUTH).with(ArchBlock.TYPE, ArchShape.CORNER_RIGHT)
                .modelForState().modelFile(arch_corner).rotationY(180).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.SOUTH).with(ArchBlock.TYPE, ArchShape.LEFT_T)
                .modelForState().modelFile(arch_t).rotationY(270).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.SOUTH).with(ArchBlock.TYPE, ArchShape.RIGHT_T)
                .modelForState().modelFile(arch_t).rotationY(90).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.SOUTH).with(ArchBlock.TYPE, ArchShape.T)
                .modelForState().modelFile(arch_t).rotationY(180).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.WEST).with(ArchBlock.TYPE, ArchShape.STRAIGHT).with(ArchBlock.TRIM, ArchTrim.NONE)
                .modelForState().modelFile(arch).rotationY(270).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.WEST).with(ArchBlock.TYPE, ArchShape.STRAIGHT).with(ArchBlock.TRIM, ArchTrim.FRONT)
                .modelForState().modelFile(arch_trim).rotationY(270).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.WEST).with(ArchBlock.TYPE, ArchShape.STRAIGHT).with(ArchBlock.TRIM, ArchTrim.BOTH)
                .modelForState().modelFile(arch_trim_2).rotationY(270).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.WEST).with(ArchBlock.TYPE, ArchShape.CROSS)
                .modelForState().modelFile(arch_cross).rotationY(270).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.WEST).with(ArchBlock.TYPE, ArchShape.CORNER_LEFT)
                .modelForState().modelFile(arch_corner).rotationY(180).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.WEST).with(ArchBlock.TYPE, ArchShape.CORNER_RIGHT)
                .modelForState().modelFile(arch_corner).rotationY(270).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.WEST).with(ArchBlock.TYPE, ArchShape.LEFT_T)
                .modelForState().modelFile(arch_t).addModel()
                .partialState().with(ArchBlock.FACING, Direction.WEST).with(ArchBlock.TYPE, ArchShape.RIGHT_T)
                .modelForState().modelFile(arch_t).rotationY(180).uvLock(true).addModel()
                .partialState().with(ArchBlock.FACING, Direction.WEST).with(ArchBlock.TYPE, ArchShape.T)
                .modelForState().modelFile(arch_t).rotationY(270).uvLock(true).addModel();
    }

    public void archHalfBlock(HalfArchBlock block) {
        archHalfBlock(block, blockTexture(block));
    }

    public void archHalfBlock(HalfArchBlock block, ResourceLocation texture) {
        archHalfBlock(block, texture, texture, texture);
    }

    public void archHalfBlock(HalfArchBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile arch_half = models().withExistingParent(name(block), modLoc("block/templates/arch_half"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom);

        getVariantBuilder(block)
                .partialState().with(HalfArchBlock.FACING, Direction.NORTH)
                .modelForState().modelFile(arch_half).addModel()
                .partialState().with(HalfArchBlock.FACING, Direction.EAST)
                .modelForState().modelFile(arch_half).rotationY(90).uvLock(true).addModel()
                .partialState().with(HalfArchBlock.FACING, Direction.SOUTH)
                .modelForState().modelFile(arch_half).rotationY(180).uvLock(true).addModel()
                .partialState().with(HalfArchBlock.FACING, Direction.WEST)
                .modelForState().modelFile(arch_half).rotationY(270).uvLock(true).addModel();
    }

    public void archLargeBlock(LargeArchBlock block) {
        archLargeBlock(block, blockTexture(block));
    }

    public void archLargeBlock(LargeArchBlock block, ResourceLocation texture) {
        archLargeBlock(block, texture, texture, texture);
    }

    public void archLargeBlock(LargeArchBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile arch_large = models().withExistingParent(name(block), modLoc("block/templates/arch_large"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                arch_large_trim = models().withExistingParent(name(block) + "_trim", modLoc("block/templates/arch_large_trim"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                arch_large_trim_left = models().withExistingParent(name(block) + "_trim_left", modLoc("block/templates/arch_large_trim_left"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                arch_large_trim_right = models().withExistingParent(name(block) + "_trim_right", modLoc("block/templates/arch_large_trim_right"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                arch_large_corner_inner = models().withExistingParent(name(block) + "_corner_inner", modLoc("block/templates/arch_large_corner_inner"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                arch_large_corner_outer = models().withExistingParent(name(block) + "_corner_outer", modLoc("block/templates/arch_large_corner_outer"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);

        getVariantBuilder(block)
                .partialState().with(LargeArchBlock.FACING, Direction.NORTH).with(LargeArchBlock.TYPE, LargeArchShape.STRAIGHT).with(LargeArchBlock.TRIM, LargeArchTrim.NONE)
                .modelForState().modelFile(arch_large).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.NORTH).with(LargeArchBlock.TYPE, LargeArchShape.STRAIGHT).with(LargeArchBlock.TRIM, LargeArchTrim.LEFT)
                .modelForState().modelFile(arch_large_trim_left).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.NORTH).with(LargeArchBlock.TYPE, LargeArchShape.STRAIGHT).with(LargeArchBlock.TRIM, LargeArchTrim.RIGHT)
                .modelForState().modelFile(arch_large_trim_right).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.NORTH).with(LargeArchBlock.TYPE, LargeArchShape.STRAIGHT).with(LargeArchBlock.TRIM, LargeArchTrim.BOTH)
                .modelForState().modelFile(arch_large_trim).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.NORTH).with(LargeArchBlock.TYPE, LargeArchShape.CORNER_LEFT)
                .modelForState().modelFile(arch_large_corner_inner).rotationY(270).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.NORTH).with(LargeArchBlock.TYPE, LargeArchShape.CORNER_RIGHT)
                .modelForState().modelFile(arch_large_corner_inner).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.NORTH).with(LargeArchBlock.TYPE, LargeArchShape.CORNER_OUTER_LEFT)
                .modelForState().modelFile(arch_large_corner_outer).rotationY(270).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.NORTH).with(LargeArchBlock.TYPE, LargeArchShape.CORNER_OUTER_RIGHT)
                .modelForState().modelFile(arch_large_corner_outer).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.EAST).with(LargeArchBlock.TYPE, LargeArchShape.STRAIGHT).with(LargeArchBlock.TRIM, LargeArchTrim.NONE)
                .modelForState().modelFile(arch_large).rotationY(90).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.EAST).with(LargeArchBlock.TYPE, LargeArchShape.STRAIGHT).with(LargeArchBlock.TRIM, LargeArchTrim.LEFT)
                .modelForState().modelFile(arch_large_trim_left).rotationY(90).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.EAST).with(LargeArchBlock.TYPE, LargeArchShape.STRAIGHT).with(LargeArchBlock.TRIM, LargeArchTrim.RIGHT)
                .modelForState().modelFile(arch_large_trim_right).rotationY(90).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.EAST).with(LargeArchBlock.TYPE, LargeArchShape.STRAIGHT).with(LargeArchBlock.TRIM, LargeArchTrim.BOTH)
                .modelForState().modelFile(arch_large_trim).rotationY(90).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.EAST).with(LargeArchBlock.TYPE, LargeArchShape.CORNER_LEFT)
                .modelForState().modelFile(arch_large_corner_inner).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.EAST).with(LargeArchBlock.TYPE, LargeArchShape.CORNER_RIGHT)
                .modelForState().modelFile(arch_large_corner_inner).rotationY(90).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.EAST).with(LargeArchBlock.TYPE, LargeArchShape.CORNER_OUTER_LEFT)
                .modelForState().modelFile(arch_large_corner_outer).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.EAST).with(LargeArchBlock.TYPE, LargeArchShape.CORNER_OUTER_RIGHT)
                .modelForState().modelFile(arch_large_corner_outer).rotationY(90).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.SOUTH).with(LargeArchBlock.TYPE, LargeArchShape.STRAIGHT).with(LargeArchBlock.TRIM, LargeArchTrim.NONE)
                .modelForState().modelFile(arch_large).rotationY(180).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.SOUTH).with(LargeArchBlock.TYPE, LargeArchShape.STRAIGHT).with(LargeArchBlock.TRIM, LargeArchTrim.LEFT)
                .modelForState().modelFile(arch_large_trim_left).rotationY(180).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.SOUTH).with(LargeArchBlock.TYPE, LargeArchShape.STRAIGHT).with(LargeArchBlock.TRIM, LargeArchTrim.RIGHT)
                .modelForState().modelFile(arch_large_trim_right).rotationY(180).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.SOUTH).with(LargeArchBlock.TYPE, LargeArchShape.STRAIGHT).with(LargeArchBlock.TRIM, LargeArchTrim.BOTH)
                .modelForState().modelFile(arch_large_trim).rotationY(180).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.SOUTH).with(LargeArchBlock.TYPE, LargeArchShape.CORNER_LEFT)
                .modelForState().modelFile(arch_large_corner_inner).rotationY(90).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.SOUTH).with(LargeArchBlock.TYPE, LargeArchShape.CORNER_RIGHT)
                .modelForState().modelFile(arch_large_corner_inner).rotationY(180).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.SOUTH).with(LargeArchBlock.TYPE, LargeArchShape.CORNER_OUTER_LEFT)
                .modelForState().modelFile(arch_large_corner_outer).rotationY(90).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.SOUTH).with(LargeArchBlock.TYPE, LargeArchShape.CORNER_OUTER_RIGHT)
                .modelForState().modelFile(arch_large_corner_outer).rotationY(180).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.WEST).with(LargeArchBlock.TYPE, LargeArchShape.STRAIGHT).with(LargeArchBlock.TRIM, LargeArchTrim.NONE)
                .modelForState().modelFile(arch_large).rotationY(270).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.WEST).with(LargeArchBlock.TYPE, LargeArchShape.STRAIGHT).with(LargeArchBlock.TRIM, LargeArchTrim.LEFT)
                .modelForState().modelFile(arch_large_trim_left).rotationY(270).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.WEST).with(LargeArchBlock.TYPE, LargeArchShape.STRAIGHT).with(LargeArchBlock.TRIM, LargeArchTrim.RIGHT)
                .modelForState().modelFile(arch_large_trim_right).rotationY(270).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.WEST).with(LargeArchBlock.TYPE, LargeArchShape.STRAIGHT).with(LargeArchBlock.TRIM, LargeArchTrim.BOTH)
                .modelForState().modelFile(arch_large_trim).rotationY(270).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.WEST).with(LargeArchBlock.TYPE, LargeArchShape.CORNER_LEFT)
                .modelForState().modelFile(arch_large_corner_inner).rotationY(180).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.WEST).with(LargeArchBlock.TYPE, LargeArchShape.CORNER_RIGHT)
                .modelForState().modelFile(arch_large_corner_inner).rotationY(270).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.WEST).with(LargeArchBlock.TYPE, LargeArchShape.CORNER_OUTER_LEFT)
                .modelForState().modelFile(arch_large_corner_outer).rotationY(180).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.WEST).with(LargeArchBlock.TYPE, LargeArchShape.CORNER_OUTER_RIGHT)
                .modelForState().modelFile(arch_large_corner_outer).rotationY(270).uvLock(true).addModel();
    }

    public void archLargeHalfBlock(LargeHalfArchBlock block) {
        archLargeHalfBlock(block, blockTexture(block));
    }

    public void archLargeHalfBlock(LargeHalfArchBlock block, ResourceLocation texture) {
        archLargeHalfBlock(block, texture, texture, texture);
    }

    public void archLargeHalfBlock(LargeHalfArchBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile arch_large_half = models().withExistingParent(name(block), modLoc("block/templates/arch_large_half"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                arch_large_half_left = models().withExistingParent(name(block) + "_left", modLoc("block/templates/arch_large_half_left"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);

        getVariantBuilder(block)
                .partialState().with(LargeArchBlock.FACING, Direction.NORTH).with(LargeHalfArchBlock.TYPE, LeftRightShape.RIGHT)
                .modelForState().modelFile(arch_large_half).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.NORTH).with(LargeHalfArchBlock.TYPE, LeftRightShape.LEFT)
                .modelForState().modelFile(arch_large_half_left).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.EAST).with(LargeHalfArchBlock.TYPE, LeftRightShape.RIGHT)
                .modelForState().modelFile(arch_large_half).rotationY(90).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.EAST).with(LargeHalfArchBlock.TYPE, LeftRightShape.LEFT)
                .modelForState().modelFile(arch_large_half_left).rotationY(90).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.SOUTH).with(LargeHalfArchBlock.TYPE, LeftRightShape.RIGHT)
                .modelForState().modelFile(arch_large_half).rotationY(180).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.SOUTH).with(LargeHalfArchBlock.TYPE, LeftRightShape.LEFT)
                .modelForState().modelFile(arch_large_half_left).rotationY(180).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.WEST).with(LargeHalfArchBlock.TYPE, LeftRightShape.RIGHT)
                .modelForState().modelFile(arch_large_half).rotationY(270).uvLock(true).addModel()
                .partialState().with(LargeArchBlock.FACING, Direction.WEST).with(LargeHalfArchBlock.TYPE, LeftRightShape.LEFT)
                .modelForState().modelFile(arch_large_half_left).rotationY(270).uvLock(true).addModel();
    }

    public void arrowslitBlock(ArrowSlitBlock block) {
        arrowslitBlock(block, blockTexture(block));
    }

    public void arrowslitBlock(ArrowSlitBlock block, ResourceLocation texture) {
        arrowslitBlock(block, texture, texture, texture);
    }

    public void arrowslitBlock(ArrowSlitBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile arrowslit = models().withExistingParent(name(block), modLoc("block/templates/arrowslit"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom);

        getVariantBuilder(block)
                .partialState().with(ArrowSlitBlock.FACING, Direction.NORTH)
                .modelForState().modelFile(arrowslit).addModel()
                .partialState().with(ArrowSlitBlock.FACING, Direction.EAST)
                .modelForState().modelFile(arrowslit).rotationY(90).addModel()
                .partialState().with(ArrowSlitBlock.FACING, Direction.SOUTH)
                .modelForState().modelFile(arrowslit).rotationY(180).addModel()
                .partialState().with(ArrowSlitBlock.FACING, Direction.WEST)
                .modelForState().modelFile(arrowslit).rotationY(270).addModel();
    }

    public void balustradeBlock(BalustradeBlock block) {
        balustradeBlock(block, blockTexture(block));
    }

    public void balustradeBlock(BalustradeBlock block, ResourceLocation texture) {
        balustradeBlock(block, texture, texture, texture);
    }

    public void balustradeBlock(BalustradeBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile balustrade = models().withExistingParent(name(block), modLoc("block/templates/balustrade"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                balustrade_connected = models().withExistingParent(name(block) + "_connected", modLoc("block/templates/balustrade_connected"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);

        getVariantBuilder(block)
                .partialState().with(BalustradeBlock.TYPE, BalustradeShape.SINGLE)
                .modelForState().modelFile(balustrade).addModel()
                .partialState().with(BalustradeBlock.FACING, Direction.NORTH).with(BalustradeBlock.TYPE, BalustradeShape.CONNECTED)
                .modelForState().modelFile(balustrade_connected).addModel()
                .partialState().with(BalustradeBlock.FACING, Direction.EAST).with(BalustradeBlock.TYPE, BalustradeShape.CONNECTED)
                .modelForState().modelFile(balustrade_connected).rotationY(90).uvLock(true).addModel()
                .partialState().with(BalustradeBlock.FACING, Direction.SOUTH).with(BalustradeBlock.TYPE, BalustradeShape.CONNECTED)
                .modelForState().modelFile(balustrade_connected).rotationY(180).uvLock(true).addModel()
                .partialState().with(BalustradeBlock.FACING, Direction.WEST).with(BalustradeBlock.TYPE, BalustradeShape.CONNECTED)
                .modelForState().modelFile(balustrade_connected).rotationY(270).uvLock(true).addModel();
    }

    public void beamLintelBlock(BeamLintelBlock block) {
        beamLintelBlock(block, blockTexture(block));
    }

    public void beamLintelBlock(BeamLintelBlock block, ResourceLocation texture) {
        beamLintelBlock(block, texture, texture, texture);
    }

    public void beamLintelBlock(BeamLintelBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile beam_lintel_1 = models().withExistingParent(name(block) + "_1", modLoc("block/templates/beam_lintel_1"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                beam_lintel_2 = models().withExistingParent(name(block) + "_2", modLoc("block/templates/beam_lintel_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_lintel_3 = models().withExistingParent(name(block) + "_3", modLoc("block/templates/beam_lintel_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_lintel_4 = models().withExistingParent(name(block) + "_4", modLoc("block/templates/beam_lintel_4"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);

        getVariantBuilder(block)

                .partialState().with(BeamLintelBlock.FACING, Direction.NORTH).with(BeamLintelBlock.TYPE, TopBottomShape.TOP).with(BeamLintelBlock.BEAM, 1)
                .modelForState().modelFile(beam_lintel_1).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.NORTH).with(BeamLintelBlock.TYPE, TopBottomShape.TOP).with(BeamLintelBlock.BEAM, 2)
                .modelForState().modelFile(beam_lintel_2).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.NORTH).with(BeamLintelBlock.TYPE, TopBottomShape.TOP).with(BeamLintelBlock.BEAM, 3)
                .modelForState().modelFile(beam_lintel_3).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.NORTH).with(BeamLintelBlock.TYPE, TopBottomShape.TOP).with(BeamLintelBlock.BEAM, 4)
                .modelForState().modelFile(beam_lintel_4).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.NORTH).with(BeamLintelBlock.TYPE, TopBottomShape.BOTTOM).with(BeamLintelBlock.BEAM, 1)
                .modelForState().modelFile(beam_lintel_1).rotationX(180).rotationY(180).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.NORTH).with(BeamLintelBlock.TYPE, TopBottomShape.BOTTOM).with(BeamLintelBlock.BEAM, 2)
                .modelForState().modelFile(beam_lintel_2).rotationX(180).rotationY(180).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.NORTH).with(BeamLintelBlock.TYPE, TopBottomShape.BOTTOM).with(BeamLintelBlock.BEAM, 3)
                .modelForState().modelFile(beam_lintel_3).rotationX(180).rotationY(180).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.NORTH).with(BeamLintelBlock.TYPE, TopBottomShape.BOTTOM).with(BeamLintelBlock.BEAM, 4)
                .modelForState().modelFile(beam_lintel_4).rotationX(180).rotationY(180).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.EAST).with(BeamLintelBlock.TYPE, TopBottomShape.TOP).with(BeamLintelBlock.BEAM, 1)
                .modelForState().modelFile(beam_lintel_1).rotationY(90).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.EAST).with(BeamLintelBlock.TYPE, TopBottomShape.TOP).with(BeamLintelBlock.BEAM, 2)
                .modelForState().modelFile(beam_lintel_2).rotationY(90).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.EAST).with(BeamLintelBlock.TYPE, TopBottomShape.TOP).with(BeamLintelBlock.BEAM, 3)
                .modelForState().modelFile(beam_lintel_3).rotationY(90).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.EAST).with(BeamLintelBlock.TYPE, TopBottomShape.TOP).with(BeamLintelBlock.BEAM, 4)
                .modelForState().modelFile(beam_lintel_4).rotationY(90).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.EAST).with(BeamLintelBlock.TYPE, TopBottomShape.BOTTOM).with(BeamLintelBlock.BEAM, 1)
                .modelForState().modelFile(beam_lintel_1).rotationX(180).rotationY(270).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.EAST).with(BeamLintelBlock.TYPE, TopBottomShape.BOTTOM).with(BeamLintelBlock.BEAM, 2)
                .modelForState().modelFile(beam_lintel_2).rotationX(180).rotationY(270).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.EAST).with(BeamLintelBlock.TYPE, TopBottomShape.BOTTOM).with(BeamLintelBlock.BEAM, 3)
                .modelForState().modelFile(beam_lintel_3).rotationX(180).rotationY(270).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.EAST).with(BeamLintelBlock.TYPE, TopBottomShape.BOTTOM).with(BeamLintelBlock.BEAM, 4)
                .modelForState().modelFile(beam_lintel_4).rotationX(180).rotationY(270).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.SOUTH).with(BeamLintelBlock.TYPE, TopBottomShape.TOP).with(BeamLintelBlock.BEAM, 1)
                .modelForState().modelFile(beam_lintel_1).rotationY(180).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.SOUTH).with(BeamLintelBlock.TYPE, TopBottomShape.TOP).with(BeamLintelBlock.BEAM, 2)
                .modelForState().modelFile(beam_lintel_2).rotationY(180).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.SOUTH).with(BeamLintelBlock.TYPE, TopBottomShape.TOP).with(BeamLintelBlock.BEAM, 3)
                .modelForState().modelFile(beam_lintel_3).rotationY(180).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.SOUTH).with(BeamLintelBlock.TYPE, TopBottomShape.TOP).with(BeamLintelBlock.BEAM, 4)
                .modelForState().modelFile(beam_lintel_4).rotationY(180).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.SOUTH).with(BeamLintelBlock.TYPE, TopBottomShape.BOTTOM).with(BeamLintelBlock.BEAM, 1)
                .modelForState().modelFile(beam_lintel_1).rotationX(180).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.SOUTH).with(BeamLintelBlock.TYPE, TopBottomShape.BOTTOM).with(BeamLintelBlock.BEAM, 2)
                .modelForState().modelFile(beam_lintel_2).rotationX(180).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.SOUTH).with(BeamLintelBlock.TYPE, TopBottomShape.BOTTOM).with(BeamLintelBlock.BEAM, 3)
                .modelForState().modelFile(beam_lintel_3).rotationX(180).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.SOUTH).with(BeamLintelBlock.TYPE, TopBottomShape.BOTTOM).with(BeamLintelBlock.BEAM, 4)
                .modelForState().modelFile(beam_lintel_4).rotationX(180).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.WEST).with(BeamLintelBlock.TYPE, TopBottomShape.TOP).with(BeamLintelBlock.BEAM, 1)
                .modelForState().modelFile(beam_lintel_1).rotationY(270).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.WEST).with(BeamLintelBlock.TYPE, TopBottomShape.TOP).with(BeamLintelBlock.BEAM, 2)
                .modelForState().modelFile(beam_lintel_2).rotationY(270).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.WEST).with(BeamLintelBlock.TYPE, TopBottomShape.TOP).with(BeamLintelBlock.BEAM, 3)
                .modelForState().modelFile(beam_lintel_3).rotationY(270).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.WEST).with(BeamLintelBlock.TYPE, TopBottomShape.TOP).with(BeamLintelBlock.BEAM, 4)
                .modelForState().modelFile(beam_lintel_4).rotationY(270).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.WEST).with(BeamLintelBlock.TYPE, TopBottomShape.BOTTOM).with(BeamLintelBlock.BEAM, 1)
                .modelForState().modelFile(beam_lintel_1).rotationX(180).rotationY(90).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.WEST).with(BeamLintelBlock.TYPE, TopBottomShape.BOTTOM).with(BeamLintelBlock.BEAM, 2)
                .modelForState().modelFile(beam_lintel_2).rotationX(180).rotationY(90).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.WEST).with(BeamLintelBlock.TYPE, TopBottomShape.BOTTOM).with(BeamLintelBlock.BEAM, 3)
                .modelForState().modelFile(beam_lintel_3).rotationX(180).rotationY(90).addModel()
                .partialState().with(BeamLintelBlock.FACING, Direction.WEST).with(BeamLintelBlock.TYPE, TopBottomShape.BOTTOM).with(BeamLintelBlock.BEAM, 4)
                .modelForState().modelFile(beam_lintel_4).rotationX(180).rotationY(90).addModel();
    }

    public void beamPostsBlock(BeamPostsBlock block) {
        beamPostsBlock(block, blockTexture(block));
    }

    public void beamPostsBlock(BeamPostsBlock block, ResourceLocation texture) {
        beamPostsBlock(block, texture, texture, texture);
    }

    public void beamPostsBlock(BeamPostsBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile beam_posts_1 = models().withExistingParent(name(block) + "_1", modLoc("block/templates/beam_posts_1"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                beam_posts_2 = models().withExistingParent(name(block) + "_2", modLoc("block/templates/beam_posts_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_posts_3 = models().withExistingParent(name(block) + "_3", modLoc("block/templates/beam_posts_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);

        getVariantBuilder(block)
                .partialState().with(BeamPostsBlock.FACING, Direction.NORTH).with(BeamPostsBlock.BEAM, 1)
                .modelForState().modelFile(beam_posts_1).addModel()
                .partialState().with(BeamPostsBlock.FACING, Direction.NORTH).with(BeamPostsBlock.BEAM, 2)
                .modelForState().modelFile(beam_posts_2).addModel()
                .partialState().with(BeamPostsBlock.FACING, Direction.NORTH).with(BeamPostsBlock.BEAM, 3)
                .modelForState().modelFile(beam_posts_3).addModel()
                .partialState().with(BeamPostsBlock.FACING, Direction.EAST).with(BeamPostsBlock.BEAM, 1)
                .modelForState().modelFile(beam_posts_1).rotationY(90).addModel()
                .partialState().with(BeamPostsBlock.FACING, Direction.EAST).with(BeamPostsBlock.BEAM, 2)
                .modelForState().modelFile(beam_posts_2).rotationY(90).addModel()
                .partialState().with(BeamPostsBlock.FACING, Direction.EAST).with(BeamPostsBlock.BEAM, 3)
                .modelForState().modelFile(beam_posts_3).rotationY(90).addModel()
                .partialState().with(BeamPostsBlock.FACING, Direction.SOUTH).with(BeamPostsBlock.BEAM, 1)
                .modelForState().modelFile(beam_posts_1).rotationY(180).addModel()
                .partialState().with(BeamPostsBlock.FACING, Direction.SOUTH).with(BeamPostsBlock.BEAM, 2)
                .modelForState().modelFile(beam_posts_2).rotationY(180).addModel()
                .partialState().with(BeamPostsBlock.FACING, Direction.SOUTH).with(BeamPostsBlock.BEAM, 3)
                .modelForState().modelFile(beam_posts_3).rotationY(180).addModel()
                .partialState().with(BeamPostsBlock.FACING, Direction.WEST).with(BeamPostsBlock.BEAM, 1)
                .modelForState().modelFile(beam_posts_1).rotationY(270).addModel()
                .partialState().with(BeamPostsBlock.FACING, Direction.WEST).with(BeamPostsBlock.BEAM, 2)
                .modelForState().modelFile(beam_posts_2).rotationY(270).addModel()
                .partialState().with(BeamPostsBlock.FACING, Direction.WEST).with(BeamPostsBlock.BEAM, 3)
                .modelForState().modelFile(beam_posts_3).rotationY(270).addModel();
    }

    public void beamHorizontalBlock(HorizontalBeamBlock block) {
        beamHorizontalBlock(block, blockTexture(block));
    }

    public void beamHorizontalBlock(HorizontalBeamBlock block, ResourceLocation texture) {
        beamHorizontalBlock(block, texture, texture, texture);
    }

    public void beamHorizontalBlock(HorizontalBeamBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile beam_horizontal_1 = models().withExistingParent(name(block) + "_1", modLoc("block/templates/beam_horizontal_1"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                beam_horizontal_2 = models().withExistingParent(name(block) + "_2", modLoc("block/templates/beam_horizontal_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_horizontal_3 = models().withExistingParent(name(block) + "_3", modLoc("block/templates/beam_horizontal_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_horizontal_4 = models().withExistingParent(name(block) + "_4", modLoc("block/templates/beam_horizontal_4"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_horizontal_5 = models().withExistingParent(name(block) + "_5", modLoc("block/templates/beam_horizontal_5"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_horizontal_6 = models().withExistingParent(name(block) + "_6", modLoc("block/templates/beam_horizontal_6"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);

        getVariantBuilder(block)
                .partialState().with(HorizontalBeamBlock.FACING, Direction.NORTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_horizontal_1).rotationX(180).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.NORTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_horizontal_2).rotationX(180).rotationY(90).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.NORTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 3)
                .modelForState().modelFile(beam_horizontal_3).rotationX(180).rotationY(270).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.NORTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 4)
                .modelForState().modelFile(beam_horizontal_4).rotationX(180).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.NORTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 5)
                .modelForState().modelFile(beam_horizontal_5).rotationX(180).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.NORTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 6)
                .modelForState().modelFile(beam_horizontal_6).rotationX(180).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.NORTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_horizontal_1).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.NORTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_horizontal_2).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.NORTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 3)
                .modelForState().modelFile(beam_horizontal_3).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.NORTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 4)
                .modelForState().modelFile(beam_horizontal_4).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.NORTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 5)
                .modelForState().modelFile(beam_horizontal_5).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.NORTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 6)
                .modelForState().modelFile(beam_horizontal_6).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.EAST).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_horizontal_1).rotationX(180).rotationY(90).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.EAST).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_horizontal_2).rotationX(180).rotationY(180).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.EAST).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 3)
                .modelForState().modelFile(beam_horizontal_3).rotationX(180).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.EAST).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 4)
                .modelForState().modelFile(beam_horizontal_4).rotationX(180).rotationY(90).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.EAST).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 5)
                .modelForState().modelFile(beam_horizontal_5).rotationX(180).rotationY(90).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.EAST).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 6)
                .modelForState().modelFile(beam_horizontal_6).rotationX(180).rotationY(90).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.EAST).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_horizontal_1).rotationY(90).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.EAST).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_horizontal_2).rotationY(90).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.EAST).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 3)
                .modelForState().modelFile(beam_horizontal_3).rotationY(90).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.EAST).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 4)
                .modelForState().modelFile(beam_horizontal_4).rotationY(90).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.EAST).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 5)
                .modelForState().modelFile(beam_horizontal_5).rotationY(90).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.EAST).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 6)
                .modelForState().modelFile(beam_horizontal_6).rotationY(90).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.SOUTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_horizontal_1).rotationX(180).rotationY(180).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.SOUTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_horizontal_2).rotationX(180).rotationY(270).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.SOUTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 3)
                .modelForState().modelFile(beam_horizontal_3).rotationX(180).rotationY(90).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.SOUTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 4)
                .modelForState().modelFile(beam_horizontal_4).rotationX(180).rotationY(180).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.SOUTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 5)
                .modelForState().modelFile(beam_horizontal_5).rotationX(180).rotationY(180).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.SOUTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 6)
                .modelForState().modelFile(beam_horizontal_6).rotationX(180).rotationY(180).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.SOUTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_horizontal_1).rotationY(180).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.SOUTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_horizontal_2).rotationY(180).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.SOUTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 3)
                .modelForState().modelFile(beam_horizontal_3).rotationY(180).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.SOUTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 4)
                .modelForState().modelFile(beam_horizontal_4).rotationY(180).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.SOUTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 5)
                .modelForState().modelFile(beam_horizontal_5).rotationY(180).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.SOUTH).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 6)
                .modelForState().modelFile(beam_horizontal_6).rotationY(180).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.WEST).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_horizontal_1).rotationX(180).rotationY(270).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.WEST).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_horizontal_2).rotationX(180).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.WEST).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 3)
                .modelForState().modelFile(beam_horizontal_3).rotationX(180).rotationY(180).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.WEST).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 4)
                .modelForState().modelFile(beam_horizontal_4).rotationX(180).rotationY(270).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.WEST).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 5)
                .modelForState().modelFile(beam_horizontal_5).rotationX(180).rotationY(270).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.WEST).with(HorizontalBeamBlock.TYPE, TopBottomShape.TOP).with(HorizontalBeamBlock.BEAM, 6)
                .modelForState().modelFile(beam_horizontal_6).rotationX(180).rotationY(270).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.WEST).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_horizontal_1).rotationY(270).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.WEST).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_horizontal_2).rotationY(270).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.WEST).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 3)
                .modelForState().modelFile(beam_horizontal_3).rotationY(270).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.WEST).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 4)
                .modelForState().modelFile(beam_horizontal_4).rotationY(270).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.WEST).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 5)
                .modelForState().modelFile(beam_horizontal_5).rotationY(270).addModel()
                .partialState().with(HorizontalBeamBlock.FACING, Direction.WEST).with(HorizontalBeamBlock.TYPE, TopBottomShape.BOTTOM).with(HorizontalBeamBlock.BEAM, 6)
                .modelForState().modelFile(beam_horizontal_6).rotationY(270).addModel();
    }

    public void beamVerticalBlock(VerticalBeamBlock block) {
        beamVerticalBlock(block, blockTexture(block));
    }

    public void beamVerticalBlock(VerticalBeamBlock block, ResourceLocation texture) {
        beamVerticalBlock(block, texture, texture, texture);
    }

    public void beamVerticalBlock(VerticalBeamBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile beam_vertical_1 = models().withExistingParent(name(block) + "_1", modLoc("block/templates/beam_vertical_1"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                beam_vertical_2 = models().withExistingParent(name(block) + "_2", modLoc("block/templates/beam_vertical_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_vertical_3 = models().withExistingParent(name(block) + "_3", modLoc("block/templates/beam_vertical_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_vertical_4 = models().withExistingParent(name(block) + "_4", modLoc("block/templates/beam_vertical_4"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_vertical_5 = models().withExistingParent(name(block) + "_5", modLoc("block/templates/beam_vertical_5"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_vertical_6 = models().withExistingParent(name(block) + "_6", modLoc("block/templates/beam_vertical_6"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_vertical_7 = models().withExistingParent(name(block) + "_7", modLoc("block/templates/beam_vertical_7"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);

        getVariantBuilder(block)
                .partialState().with(VerticalBeamBlock.FACING, Direction.NORTH).with(VerticalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_vertical_1).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.NORTH).with(VerticalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_vertical_2).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.NORTH).with(VerticalBeamBlock.BEAM, 3)
                .modelForState().modelFile(beam_vertical_3).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.NORTH).with(VerticalBeamBlock.BEAM, 4)
                .modelForState().modelFile(beam_vertical_4).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.NORTH).with(VerticalBeamBlock.BEAM, 5)
                .modelForState().modelFile(beam_vertical_5).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.NORTH).with(VerticalBeamBlock.BEAM, 6)
                .modelForState().modelFile(beam_vertical_6).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.NORTH).with(VerticalBeamBlock.BEAM, 7)
                .modelForState().modelFile(beam_vertical_7).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.EAST).with(VerticalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_vertical_1).rotationY(90).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.EAST).with(VerticalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_vertical_2).rotationY(90).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.EAST).with(VerticalBeamBlock.BEAM, 3)
                .modelForState().modelFile(beam_vertical_3).rotationY(90).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.EAST).with(VerticalBeamBlock.BEAM, 4)
                .modelForState().modelFile(beam_vertical_4).rotationY(90).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.EAST).with(VerticalBeamBlock.BEAM, 5)
                .modelForState().modelFile(beam_vertical_5).rotationY(90).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.EAST).with(VerticalBeamBlock.BEAM, 6)
                .modelForState().modelFile(beam_vertical_6).rotationY(90).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.EAST).with(VerticalBeamBlock.BEAM, 7)
                .modelForState().modelFile(beam_vertical_7).rotationY(90).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.SOUTH).with(VerticalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_vertical_1).rotationY(180).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.SOUTH).with(VerticalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_vertical_2).rotationY(180).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.SOUTH).with(VerticalBeamBlock.BEAM, 3)
                .modelForState().modelFile(beam_vertical_3).rotationY(180).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.SOUTH).with(VerticalBeamBlock.BEAM, 4)
                .modelForState().modelFile(beam_vertical_4).rotationY(180).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.SOUTH).with(VerticalBeamBlock.BEAM, 5)
                .modelForState().modelFile(beam_vertical_5).rotationY(180).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.SOUTH).with(VerticalBeamBlock.BEAM, 6)
                .modelForState().modelFile(beam_vertical_6).rotationY(180).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.SOUTH).with(VerticalBeamBlock.BEAM, 7)
                .modelForState().modelFile(beam_vertical_7).rotationY(180).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.WEST).with(VerticalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_vertical_1).rotationY(270).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.WEST).with(VerticalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_vertical_2).rotationY(270).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.WEST).with(VerticalBeamBlock.BEAM, 3)
                .modelForState().modelFile(beam_vertical_3).rotationY(270).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.WEST).with(VerticalBeamBlock.BEAM, 4)
                .modelForState().modelFile(beam_vertical_4).rotationY(270).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.WEST).with(VerticalBeamBlock.BEAM, 5)
                .modelForState().modelFile(beam_vertical_5).rotationY(270).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.WEST).with(VerticalBeamBlock.BEAM, 6)
                .modelForState().modelFile(beam_vertical_6).rotationY(270).addModel()
                .partialState().with(VerticalBeamBlock.FACING, Direction.WEST).with(VerticalBeamBlock.BEAM, 7)
                .modelForState().modelFile(beam_vertical_7).rotationY(270).addModel();
    }

    public void beamDiagonalBlock(DiagonalBeamBlock block) {
        beamDiagonalBlock(block, blockTexture(block));
    }

    public void beamDiagonalBlock(DiagonalBeamBlock block, ResourceLocation texture) {
        beamDiagonalBlock(block, texture, texture, texture);
    }

    public void beamDiagonalBlock(DiagonalBeamBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        createDiagonalBeam1Models(block, side, bottom, top);
        createDiagonalBeam2Models(block, side, bottom, top);
    }

    private void createDiagonalBeam1Models(DiagonalBeamBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile beam_diagonal_1 = models().withExistingParent(name(block) + "_1", modLoc("block/templates/beam_diagonal_1"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                beam_diagonal_1_n = models().withExistingParent(name(block) + "_1_n", modLoc("block/templates/beam_diagonal_1_n"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_1_m = models().withExistingParent(name(block) + "_1_m", modLoc("block/templates/beam_diagonal_1_m"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_1_ah = models().withExistingParent(name(block) + "_1_ah", modLoc("block/templates/beam_diagonal_1_ah"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_1_ahn = models().withExistingParent(name(block) + "_1_ahn", modLoc("block/templates/beam_diagonal_1_ahn"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_1_ahav = models().withExistingParent(name(block) + "_1_ahav", modLoc("block/templates/beam_diagonal_1_ahav"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_1_ahavn = models().withExistingParent(name(block) + "_1_ahavn", modLoc("block/templates/beam_diagonal_1_ahavn"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_1_ahavch = models().withExistingParent(name(block) + "_1_ahavch", modLoc("block/templates/beam_diagonal_1_ahavch"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_1_ahavchcv = models().withExistingParent(name(block) + "_1_ahavchcv", modLoc("block/templates/beam_diagonal_1_ahavchcv"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_1_ahavcv = models().withExistingParent(name(block) + "_1_ahavcv", modLoc("block/templates/beam_diagonal_1_ahavcv"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_1_ahch = models().withExistingParent(name(block) + "_1_ahch", modLoc("block/templates/beam_diagonal_1_ahch"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_1_ahchcv = models().withExistingParent(name(block) + "_1_ahchcv", modLoc("block/templates/beam_diagonal_1_ahchcv"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_1_ahcv = models().withExistingParent(name(block) + "_1_ahcv", modLoc("block/templates/beam_diagonal_1_ahcv"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_1_av = models().withExistingParent(name(block) + "_1_av", modLoc("block/templates/beam_diagonal_1_av"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_1_avn = models().withExistingParent(name(block) + "_1_avn", modLoc("block/templates/beam_diagonal_1_avn"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_1_avch = models().withExistingParent(name(block) + "_1_avch", modLoc("block/templates/beam_diagonal_1_avch"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_1_avchcv = models().withExistingParent(name(block) + "_1_avchcv", modLoc("block/templates/beam_diagonal_1_avchcv"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_1_avcv = models().withExistingParent(name(block) + "_1_avcv", modLoc("block/templates/beam_diagonal_1_avcv"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_1_ch = models().withExistingParent(name(block) + "_1_ch", modLoc("block/templates/beam_diagonal_1_ch"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_1_chcv = models().withExistingParent(name(block) + "_1_chcv", modLoc("block/templates/beam_diagonal_1_chcv"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_1_cv = models().withExistingParent(name(block) + "_1_cv", modLoc("block/templates/beam_diagonal_1_cv"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);

        getVariantBuilder(block)
                // NORTH
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.NONE).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ah).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahav).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahavch).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CH_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahavchcv).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahavcv).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CH_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahchcv).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahch).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahcv).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_DH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ah).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_DV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ah).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_av).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_avch).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CH_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_avchcv).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_avcv).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_DH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_av).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.CH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ch).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.CH_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_chcv).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_cv).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.DV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_N).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahavn).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_N).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahn).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_N).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_avn).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.N).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_n).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.M).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_m).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH_DV_M).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH_M).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.DV_M).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).addModel()

                // EAST
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.NONE).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ah).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahav).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahavch).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CH_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahavchcv).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahavcv).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CH_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahchcv).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahch).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahcv).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_DH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ah).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_DV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ah).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_av).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_avch).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CH_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_avchcv).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_avcv).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_DH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_av).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.CH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ch).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.CH_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_chcv).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_cv).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.DV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_N).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahavn).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_N).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahn).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_N).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_avn).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.N).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_n).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.M).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_m).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH_DV_M).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH_M).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.DV_M).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).rotationY(90).addModel()

                // SOUTH
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.NONE).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ah).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahav).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahavch).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CH_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahavchcv).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahavcv).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CH_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahchcv).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahch).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahcv).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_DH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ah).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_DV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ah).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_av).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_avch).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CH_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_avchcv).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_avcv).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_DH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_av).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.CH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ch).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.CH_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_chcv).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_cv).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.DV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_N).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahavn).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_N).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahn).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_N).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_avn).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.N).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_n).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.M).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_m).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH_DV_M).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH_M).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.DV_M).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).rotationY(180).addModel()

                // WEST
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.NONE).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ah).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahav).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahavch).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CH_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahavchcv).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahavcv).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CH_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahchcv).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahch).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahcv).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_DH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ah).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_DV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ah).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_av).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_avch).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CH_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_avchcv).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_avcv).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_DH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_av).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.CH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ch).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.CH_CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_chcv).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.CV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_cv).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.DV).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_N).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahavn).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_N).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_ahn).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_N).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_avn).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.N).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_n).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.M).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1_m).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH_DV_M).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH_M).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.DV_M).with(DiagonalBeamBlock.BEAM, 1)
                .modelForState().modelFile(beam_diagonal_1).rotationY(270).addModel();
    }

    private void createDiagonalBeam2Models(DiagonalBeamBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile beam_diagonal_2 = models().withExistingParent(name(block) + "_2", modLoc("block/templates/beam_diagonal_2"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                beam_diagonal_2_m = models().withExistingParent(name(block) + "_2_m", modLoc("block/templates/beam_diagonal_2_m"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_2_n = models().withExistingParent(name(block) + "_2_n", modLoc("block/templates/beam_diagonal_2_n"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_2_ah = models().withExistingParent(name(block) + "_2_ah", modLoc("block/templates/beam_diagonal_2_ah"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_2_ahn = models().withExistingParent(name(block) + "_2_ahn", modLoc("block/templates/beam_diagonal_2_ahn"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_2_ahav = models().withExistingParent(name(block) + "_2_ahav", modLoc("block/templates/beam_diagonal_2_ahav"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_2_ahavn = models().withExistingParent(name(block) + "_2_ahavn", modLoc("block/templates/beam_diagonal_2_ahavn"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_2_ahdh = models().withExistingParent(name(block) + "_2_ahdh", modLoc("block/templates/beam_diagonal_2_ahdh"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_2_ahdv = models().withExistingParent(name(block) + "_2_ahdv", modLoc("block/templates/beam_diagonal_2_ahdv"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_2_av = models().withExistingParent(name(block) + "_2_av", modLoc("block/templates/beam_diagonal_2_av"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_2_avn = models().withExistingParent(name(block) + "_2_avn", modLoc("block/templates/beam_diagonal_2_avn"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_2_avdh = models().withExistingParent(name(block) + "_2_avdh", modLoc("block/templates/beam_diagonal_2_avdh"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_2_dh = models().withExistingParent(name(block) + "_2_dh", modLoc("block/templates/beam_diagonal_2_dh"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_2_dv = models().withExistingParent(name(block) + "_2_dv", modLoc("block/templates/beam_diagonal_2_dv"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_2_dhdvm = models().withExistingParent(name(block) + "_2_dhdvm", modLoc("block/templates/beam_diagonal_2_dhdvm"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_2_dhm = models().withExistingParent(name(block) + "_2_dhm", modLoc("block/templates/beam_diagonal_2_dhm"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                beam_diagonal_2_dvm = models().withExistingParent(name(block) + "_2_dvm", modLoc("block/templates/beam_diagonal_2_dvm"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);

        getVariantBuilder(block)
                // NORTH
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.NONE).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ah).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahav).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahav).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CH_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahav).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahav).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CH_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ah).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ah).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ah).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_DH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahdh).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_DV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahdv).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_av).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_av).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CH_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_av).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_av).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_DH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_avdh).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.CH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.CH_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_dh).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.DV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_dv).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_N).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahavn).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_N).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahn).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_N).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_avn).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.N).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_n).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.M).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_m).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH_DV_M).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_dhdvm).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH_M).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_dhm).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.NORTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.DV_M).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_dvm).addModel()

                // EAST
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.NONE).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ah).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahav).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahav).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CH_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahav).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahav).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CH_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ah).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ah).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ah).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_DH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahdh).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_DV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahdv).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_av).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_av).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CH_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_av).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_av).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_DH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_avdh).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.CH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.CH_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_dh).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.DV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_dv).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_N).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahavn).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_N).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahn).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_N).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_avn).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.N).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_n).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.M).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_m).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH_DV_M).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_dhdvm).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH_M).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_dhm).rotationY(90).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.EAST).with(DiagonalBeamBlock.CONNECT, BeamConnection.DV_M).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_dvm).rotationY(90).addModel()

                // SOUTH
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.NONE).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ah).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahav).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahav).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CH_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahav).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahav).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CH_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ah).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ah).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ah).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_DH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahdh).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_DV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahdv).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_av).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_av).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CH_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_av).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_av).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_DH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_avdh).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.CH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.CH_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_dh).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.DV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_dv).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_N).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahavn).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_N).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahn).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_N).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_avn).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.N).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_n).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.M).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_m).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH_DV_M).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_dhdvm).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH_M).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_dhm).rotationY(180).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.SOUTH).with(DiagonalBeamBlock.CONNECT, BeamConnection.DV_M).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_dvm).rotationY(180).addModel()

                // WEST
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.NONE).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ah).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahav).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahav).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CH_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahav).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahav).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CH_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ah).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ah).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ah).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_DH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahdh).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_DV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahdv).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_av).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_av).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CH_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_av).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_av).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_DH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_avdh).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.CH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.CH_CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.CV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_dh).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.DV).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_dv).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_AV_N).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahavn).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AH_N).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_ahn).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.AV_N).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_avn).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.N).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_n).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.M).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_m).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH_DV_M).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_dhdvm).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.DH_M).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_dhm).rotationY(270).addModel()
                .partialState().with(DiagonalBeamBlock.FACING, Direction.WEST).with(DiagonalBeamBlock.CONNECT, BeamConnection.DV_M).with(DiagonalBeamBlock.BEAM, 2)
                .modelForState().modelFile(beam_diagonal_2_dvm).rotationY(270).addModel();
    }


    public void capitalBlock(CapitalBlock block) {
        capitalBlock(block, blockTexture(block));
    }

    public void capitalBlock(CapitalBlock block, ResourceLocation texture) {
        capitalBlock(block, texture, texture, texture);
    }

    public void capitalBlock(CapitalBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile capital = models().withExistingParent(name(block), modLoc("block/templates/capital"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                capital_end = models().withExistingParent(name(block) + "_end", modLoc("block/templates/capital_end"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                capital_connected = models().withExistingParent(name(block) + "_connected", modLoc("block/templates/capital_connected"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                capital_corner = models().withExistingParent(name(block) + "_corner", modLoc("block/templates/capital_corner"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                capital_cross = models().withExistingParent(name(block) + "_cross", modLoc("block/templates/capital_cross"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                capital_t = models().withExistingParent(name(block) + "_t", modLoc("block/templates/capital_t"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                capital_top = models().withExistingParent(name(block) + "_top", modLoc("block/templates/capital_top"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                capital_top_end = models().withExistingParent(name(block) + "_top_end", modLoc("block/templates/capital_top_end"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                capital_top_connected = models().withExistingParent(name(block) + "_top_connected", modLoc("block/templates/capital_top_connected"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                capital_top_corner = models().withExistingParent(name(block) + "_top_corner", modLoc("block/templates/capital_top_corner"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                capital_top_cross = models().withExistingParent(name(block) + "_top_cross", modLoc("block/templates/capital_top_cross"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                capital_top_t = models().withExistingParent(name(block) + "_top_t", modLoc("block/templates/capital_top_t"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);


        getVariantBuilder(block)
                // Bottom
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.SINGLE)
                .modelForState().modelFile(capital).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.END)
                .modelForState().modelFile(capital_end).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.CONNECTED)
                .modelForState().modelFile(capital_connected).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.INNER_LEFT)
                .modelForState().modelFile(capital_corner).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.INNER_RIGHT)
                .modelForState().modelFile(capital_corner).rotationY(270).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.OUTER_LEFT)
                .modelForState().modelFile(capital_corner).rotationY(90).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.OUTER_RIGHT)
                .modelForState().modelFile(capital_corner).rotationY(180).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.CROSS)
                .modelForState().modelFile(capital_cross).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.T)
                .modelForState().modelFile(capital_t).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.T_LEFT)
                .modelForState().modelFile(capital_t).rotationY(90).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.T_RIGHT)
                .modelForState().modelFile(capital_t).rotationY(270).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.T_OPPOSITE)
                .modelForState().modelFile(capital_t).rotationY(180).uvLock(true).addModel()
                // Top
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.SINGLE)
                .modelForState().modelFile(capital_top).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.END)
                .modelForState().modelFile(capital_top_end).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.CONNECTED)
                .modelForState().modelFile(capital_top_connected).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.INNER_LEFT)
                .modelForState().modelFile(capital_top_corner).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.INNER_RIGHT)
                .modelForState().modelFile(capital_top_corner).rotationY(270).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.OUTER_LEFT)
                .modelForState().modelFile(capital_top_corner).rotationY(90).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.OUTER_RIGHT)
                .modelForState().modelFile(capital_top_corner).rotationY(180).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.CROSS)
                .modelForState().modelFile(capital_top_cross).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.T)
                .modelForState().modelFile(capital_top_t).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.T_LEFT)
                .modelForState().modelFile(capital_top_t).rotationY(90).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.T_RIGHT)
                .modelForState().modelFile(capital_top_t).rotationY(270).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.NORTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.T_OPPOSITE)
                .modelForState().modelFile(capital_t).rotationY(180).uvLock(true).addModel()

                // Bottom
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.SINGLE)
                .modelForState().modelFile(capital).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.END)
                .modelForState().modelFile(capital_end).rotationY(90).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.CONNECTED)
                .modelForState().modelFile(capital_connected).rotationY(90).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.INNER_LEFT)
                .modelForState().modelFile(capital_corner).rotationY(90).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.INNER_RIGHT)
                .modelForState().modelFile(capital_corner).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.OUTER_LEFT)
                .modelForState().modelFile(capital_corner).rotationY(180).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.OUTER_RIGHT)
                .modelForState().modelFile(capital_corner).rotationY(270).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.CROSS)
                .modelForState().modelFile(capital_cross).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.T)
                .modelForState().modelFile(capital_t).rotationY(90).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.T_LEFT)
                .modelForState().modelFile(capital_t).rotationY(180).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.T_RIGHT)
                .modelForState().modelFile(capital_t).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.T_OPPOSITE)
                .modelForState().modelFile(capital_t).rotationY(270).uvLock(true).addModel()
                // Top
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.SINGLE)
                .modelForState().modelFile(capital_top).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.END)
                .modelForState().modelFile(capital_top_end).rotationY(90).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.CONNECTED)
                .modelForState().modelFile(capital_top_connected).rotationY(90).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.INNER_LEFT)
                .modelForState().modelFile(capital_top_corner).rotationY(90).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.INNER_RIGHT)
                .modelForState().modelFile(capital_top_corner).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.OUTER_LEFT)
                .modelForState().modelFile(capital_top_corner).rotationY(180).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.OUTER_RIGHT)
                .modelForState().modelFile(capital_top_corner).rotationY(270).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.CROSS)
                .modelForState().modelFile(capital_top_cross).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.T)
                .modelForState().modelFile(capital_top_t).rotationY(90).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.T_LEFT)
                .modelForState().modelFile(capital_top_t).rotationY(180).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.T_RIGHT)
                .modelForState().modelFile(capital_top_t).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.EAST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.T_OPPOSITE)
                .modelForState().modelFile(capital_t).rotationY(270).uvLock(true).addModel()

                // Bottom
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.SINGLE)
                .modelForState().modelFile(capital).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.END)
                .modelForState().modelFile(capital_end).rotationY(180).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.CONNECTED)
                .modelForState().modelFile(capital_connected).rotationY(180).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.INNER_LEFT)
                .modelForState().modelFile(capital_corner).rotationY(180).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.INNER_RIGHT)
                .modelForState().modelFile(capital_corner).rotationY(90).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.OUTER_LEFT)
                .modelForState().modelFile(capital_corner).rotationY(270).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.OUTER_RIGHT)
                .modelForState().modelFile(capital_corner).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.CROSS)
                .modelForState().modelFile(capital_cross).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.T)
                .modelForState().modelFile(capital_t).rotationY(180).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.T_LEFT)
                .modelForState().modelFile(capital_t).rotationY(270).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.T_RIGHT)
                .modelForState().modelFile(capital_t).rotationY(90).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.T_OPPOSITE)
                .modelForState().modelFile(capital_t).addModel()
                // Top
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.SINGLE)
                .modelForState().modelFile(capital_top).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.END)
                .modelForState().modelFile(capital_top_end).rotationY(180).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.CONNECTED)
                .modelForState().modelFile(capital_top_connected).rotationY(180).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.INNER_LEFT)
                .modelForState().modelFile(capital_top_corner).rotationY(180).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.INNER_RIGHT)
                .modelForState().modelFile(capital_top_corner).rotationY(90).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.OUTER_LEFT)
                .modelForState().modelFile(capital_top_corner).rotationY(270).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.OUTER_RIGHT)
                .modelForState().modelFile(capital_top_corner).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.CROSS)
                .modelForState().modelFile(capital_top_cross).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.T)
                .modelForState().modelFile(capital_top_t).rotationY(180).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.T_LEFT)
                .modelForState().modelFile(capital_top_t).rotationY(270).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.T_RIGHT)
                .modelForState().modelFile(capital_top_t).rotationY(90).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.SOUTH).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.T_OPPOSITE)
                .modelForState().modelFile(capital_t).addModel()

                // Bottom
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.SINGLE)
                .modelForState().modelFile(capital).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.END)
                .modelForState().modelFile(capital_end).rotationY(270).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.CONNECTED)
                .modelForState().modelFile(capital_connected).rotationY(270).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.INNER_LEFT)
                .modelForState().modelFile(capital_corner).rotationY(270).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.INNER_RIGHT)
                .modelForState().modelFile(capital_corner).rotationY(180).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.OUTER_LEFT)
                .modelForState().modelFile(capital_corner).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.OUTER_RIGHT)
                .modelForState().modelFile(capital_corner).rotationY(90).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.CROSS)
                .modelForState().modelFile(capital_cross).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.T)
                .modelForState().modelFile(capital_t).rotationY(270).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.T_LEFT)
                .modelForState().modelFile(capital_t).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.T_RIGHT)
                .modelForState().modelFile(capital_t).rotationY(180).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.BOTTOM).with(CapitalBlock.CONNECT, IntersectionShape.T_OPPOSITE)
                .modelForState().modelFile(capital_t).rotationY(90).uvLock(true).addModel()
                // Top
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.SINGLE)
                .modelForState().modelFile(capital_top).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.END)
                .modelForState().modelFile(capital_top_end).rotationY(270).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.CONNECTED)
                .modelForState().modelFile(capital_top_connected).rotationY(270).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.INNER_LEFT)
                .modelForState().modelFile(capital_top_corner).rotationY(270).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.INNER_RIGHT)
                .modelForState().modelFile(capital_top_corner).rotationY(180).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.OUTER_LEFT)
                .modelForState().modelFile(capital_top_corner).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.OUTER_RIGHT)
                .modelForState().modelFile(capital_top_corner).rotationY(90).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.CROSS)
                .modelForState().modelFile(capital_top_cross).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.T)
                .modelForState().modelFile(capital_top_t).rotationY(270).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.T_LEFT)
                .modelForState().modelFile(capital_top_t).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.T_RIGHT)
                .modelForState().modelFile(capital_top_t).rotationY(180).uvLock(true).addModel()
                .partialState().with(CapitalBlock.FACING, Direction.WEST).with(CapitalBlock.TYPE, TopBottomShape.TOP).with(CapitalBlock.CONNECT, IntersectionShape.T_OPPOSITE)
                .modelForState().modelFile(capital_t).rotationY(90).uvLock(true).addModel();
    }

    public void cornerLayerBlock(CornerLayerBlock block) {
        cornerLayerBlock(block, blockTexture(block));
    }

    public void cornerLayerBlock(CornerLayerBlock block, ResourceLocation texture) {
        cornerLayerBlock(block, texture, texture, texture, texture);
    }

    public void cornerLayerBlock(CornerLayerBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation baseBlock) {
        ModelFile corner_layer_1 = models().withExistingParent(name(block) + "_layer_1", modLoc("block/templates/corner_layer_1"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                corner_layer_2 = models().withExistingParent(name(block) + "_layer_2", modLoc("block/templates/corner_layer_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_layer_3 = models().withExistingParent(name(block) + "_layer_3", modLoc("block/templates/corner_layer_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_layer_4 = models().withExistingParent(name(block) + "_layer_4", modLoc("block/templates/corner_layer_4"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                full_block = models().getExistingFile(baseBlock);

        getVariantBuilder(block)
                .partialState().with(CornerLayerBlock.FACING, Direction.NORTH).with(CornerLayerBlock.TYPE, LeftRightShape.RIGHT).with(CornerLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_layer_1).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.NORTH).with(CornerLayerBlock.TYPE, LeftRightShape.RIGHT).with(CornerLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_layer_2).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.NORTH).with(CornerLayerBlock.TYPE, LeftRightShape.RIGHT).with(CornerLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_layer_3).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.NORTH).with(CornerLayerBlock.TYPE, LeftRightShape.RIGHT).with(CornerLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_layer_4).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.NORTH).with(CornerLayerBlock.TYPE, LeftRightShape.RIGHT).with(CornerLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.NORTH).with(CornerLayerBlock.TYPE, LeftRightShape.LEFT).with(CornerLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_layer_1).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.NORTH).with(CornerLayerBlock.TYPE, LeftRightShape.LEFT).with(CornerLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_layer_2).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.NORTH).with(CornerLayerBlock.TYPE, LeftRightShape.LEFT).with(CornerLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_layer_3).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.NORTH).with(CornerLayerBlock.TYPE, LeftRightShape.LEFT).with(CornerLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_layer_4).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.NORTH).with(CornerLayerBlock.TYPE, LeftRightShape.LEFT).with(CornerLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.EAST).with(CornerLayerBlock.TYPE, LeftRightShape.RIGHT).with(CornerLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_layer_1).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.EAST).with(CornerLayerBlock.TYPE, LeftRightShape.RIGHT).with(CornerLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_layer_2).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.EAST).with(CornerLayerBlock.TYPE, LeftRightShape.RIGHT).with(CornerLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_layer_3).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.EAST).with(CornerLayerBlock.TYPE, LeftRightShape.RIGHT).with(CornerLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_layer_4).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.EAST).with(CornerLayerBlock.TYPE, LeftRightShape.RIGHT).with(CornerLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.EAST).with(CornerLayerBlock.TYPE, LeftRightShape.LEFT).with(CornerLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_layer_1).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.EAST).with(CornerLayerBlock.TYPE, LeftRightShape.LEFT).with(CornerLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_layer_2).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.EAST).with(CornerLayerBlock.TYPE, LeftRightShape.LEFT).with(CornerLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_layer_3).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.EAST).with(CornerLayerBlock.TYPE, LeftRightShape.LEFT).with(CornerLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_layer_4).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.EAST).with(CornerLayerBlock.TYPE, LeftRightShape.LEFT).with(CornerLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.SOUTH).with(CornerLayerBlock.TYPE, LeftRightShape.RIGHT).with(CornerLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_layer_1).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.SOUTH).with(CornerLayerBlock.TYPE, LeftRightShape.RIGHT).with(CornerLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_layer_2).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.SOUTH).with(CornerLayerBlock.TYPE, LeftRightShape.RIGHT).with(CornerLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_layer_3).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.SOUTH).with(CornerLayerBlock.TYPE, LeftRightShape.RIGHT).with(CornerLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_layer_4).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.SOUTH).with(CornerLayerBlock.TYPE, LeftRightShape.RIGHT).with(CornerLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.SOUTH).with(CornerLayerBlock.TYPE, LeftRightShape.LEFT).with(CornerLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_layer_1).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.SOUTH).with(CornerLayerBlock.TYPE, LeftRightShape.LEFT).with(CornerLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_layer_2).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.SOUTH).with(CornerLayerBlock.TYPE, LeftRightShape.LEFT).with(CornerLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_layer_3).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.SOUTH).with(CornerLayerBlock.TYPE, LeftRightShape.LEFT).with(CornerLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_layer_4).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.SOUTH).with(CornerLayerBlock.TYPE, LeftRightShape.LEFT).with(CornerLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.WEST).with(CornerLayerBlock.TYPE, LeftRightShape.RIGHT).with(CornerLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_layer_1).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.WEST).with(CornerLayerBlock.TYPE, LeftRightShape.RIGHT).with(CornerLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_layer_2).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.WEST).with(CornerLayerBlock.TYPE, LeftRightShape.RIGHT).with(CornerLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_layer_3).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.WEST).with(CornerLayerBlock.TYPE, LeftRightShape.RIGHT).with(CornerLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_layer_4).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.WEST).with(CornerLayerBlock.TYPE, LeftRightShape.RIGHT).with(CornerLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.WEST).with(CornerLayerBlock.TYPE, LeftRightShape.LEFT).with(CornerLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_layer_1).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.WEST).with(CornerLayerBlock.TYPE, LeftRightShape.LEFT).with(CornerLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_layer_2).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.WEST).with(CornerLayerBlock.TYPE, LeftRightShape.LEFT).with(CornerLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_layer_3).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.WEST).with(CornerLayerBlock.TYPE, LeftRightShape.LEFT).with(CornerLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_layer_4).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerLayerBlock.FACING, Direction.WEST).with(CornerLayerBlock.TYPE, LeftRightShape.LEFT).with(CornerLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(180).uvLock(true).addModel();
    }


    public void cornerSlabBlock(CornerSlabBlock block) {
        cornerSlabBlock(block, blockTexture(block));
    }

    public void cornerSlabBlock(CornerSlabBlock block, ResourceLocation texture) {
        cornerSlabBlock(block, texture, texture, texture);
    }

    public void cornerSlabBlock(CornerSlabBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile corner_slab = models().withExistingParent(name(block), modLoc("block/templates/corner_slab"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                corner_slab_top = models().withExistingParent(name(block) + "_top", modLoc("block/templates/corner_slab_top"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);

        getVariantBuilder(block)
                .partialState().with(CornerSlabBlock.FACING, Direction.NORTH).with(CornerSlabBlock.TYPE, QuadShape.RIGHT)
                .modelForState().modelFile(corner_slab).addModel()
                .partialState().with(CornerSlabBlock.FACING, Direction.NORTH).with(CornerSlabBlock.TYPE, QuadShape.LEFT)
                .modelForState().modelFile(corner_slab).rotationY(270).addModel()
                .partialState().with(CornerSlabBlock.FACING, Direction.NORTH).with(CornerSlabBlock.TYPE, QuadShape.TOP_RIGHT)
                .modelForState().modelFile(corner_slab_top).addModel()
                .partialState().with(CornerSlabBlock.FACING, Direction.NORTH).with(CornerSlabBlock.TYPE, QuadShape.TOP_LEFT)
                .modelForState().modelFile(corner_slab_top).rotationY(270).addModel()
                .partialState().with(CornerSlabBlock.FACING, Direction.EAST).with(CornerSlabBlock.TYPE, QuadShape.RIGHT)
                .modelForState().modelFile(corner_slab).rotationY(90).addModel()
                .partialState().with(CornerSlabBlock.FACING, Direction.EAST).with(CornerSlabBlock.TYPE, QuadShape.LEFT)
                .modelForState().modelFile(corner_slab).addModel()
                .partialState().with(CornerSlabBlock.FACING, Direction.EAST).with(CornerSlabBlock.TYPE, QuadShape.TOP_RIGHT)
                .modelForState().modelFile(corner_slab_top).rotationY(90).addModel()
                .partialState().with(CornerSlabBlock.FACING, Direction.EAST).with(CornerSlabBlock.TYPE, QuadShape.TOP_LEFT)
                .modelForState().modelFile(corner_slab_top).addModel()
                .partialState().with(CornerSlabBlock.FACING, Direction.SOUTH).with(CornerSlabBlock.TYPE, QuadShape.RIGHT)
                .modelForState().modelFile(corner_slab).rotationY(180).addModel()
                .partialState().with(CornerSlabBlock.FACING, Direction.SOUTH).with(CornerSlabBlock.TYPE, QuadShape.LEFT)
                .modelForState().modelFile(corner_slab).rotationY(90).addModel()
                .partialState().with(CornerSlabBlock.FACING, Direction.SOUTH).with(CornerSlabBlock.TYPE, QuadShape.TOP_RIGHT)
                .modelForState().modelFile(corner_slab_top).rotationY(180).addModel()
                .partialState().with(CornerSlabBlock.FACING, Direction.SOUTH).with(CornerSlabBlock.TYPE, QuadShape.TOP_LEFT)
                .modelForState().modelFile(corner_slab_top).rotationY(90).addModel()
                .partialState().with(CornerSlabBlock.FACING, Direction.WEST).with(CornerSlabBlock.TYPE, QuadShape.RIGHT)
                .modelForState().modelFile(corner_slab).rotationY(270).addModel()
                .partialState().with(CornerSlabBlock.FACING, Direction.WEST).with(CornerSlabBlock.TYPE, QuadShape.LEFT)
                .modelForState().modelFile(corner_slab).rotationY(180).addModel()
                .partialState().with(CornerSlabBlock.FACING, Direction.WEST).with(CornerSlabBlock.TYPE, QuadShape.TOP_RIGHT)
                .modelForState().modelFile(corner_slab_top).rotationY(270).addModel()
                .partialState().with(CornerSlabBlock.FACING, Direction.WEST).with(CornerSlabBlock.TYPE, QuadShape.TOP_LEFT)
                .modelForState().modelFile(corner_slab_top).rotationY(180).addModel();
    }

    public void cornerSlabLayerBlock(CornerSlabLayerBlock block) {
        cornerSlabLayerBlock(block, blockTexture(block));
    }

    public void cornerSlabLayerBlock(CornerSlabLayerBlock block, ResourceLocation texture) {
        cornerSlabLayerBlock(block, texture, texture, texture, texture);
    }

    public void cornerSlabLayerBlock(CornerSlabLayerBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation baseBlock) {
        ModelFile corner_slab_layer_1 = models().withExistingParent(name(block) + "_layer_1", modLoc("block/templates/corner_slab_layer_1"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                corner_slab_layer_2 = models().withExistingParent(name(block) + "_layer_2", modLoc("block/templates/corner_slab_layer_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_layer_3 = models().withExistingParent(name(block) + "_layer_3", modLoc("block/templates/corner_slab_layer_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_layer_4 = models().withExistingParent(name(block) + "_layer_4", modLoc("block/templates/corner_slab_layer_4"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_top_layer_1 = models().withExistingParent(name(block) + "_top_layer_1", modLoc("block/templates/corner_slab_top_layer_1"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_top_layer_2 = models().withExistingParent(name(block) + "_top_layer_2", modLoc("block/templates/corner_slab_top_layer_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_top_layer_3 = models().withExistingParent(name(block) + "_top_layer_3", modLoc("block/templates/corner_slab_top_layer_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_top_layer_4 = models().withExistingParent(name(block) + "_top_layer_4", modLoc("block/templates/corner_slab_top_layer_4"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                full_block = models().getExistingFile(baseBlock);

        getVariantBuilder(block)
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.NORTH).with(CornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(CornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_layer_1).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.NORTH).with(CornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(CornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_layer_2).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.NORTH).with(CornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(CornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_layer_3).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.NORTH).with(CornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(CornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_layer_4).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.NORTH).with(CornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(CornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.NORTH).with(CornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(CornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_layer_1).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.NORTH).with(CornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(CornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_layer_2).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.NORTH).with(CornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(CornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_layer_3).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.NORTH).with(CornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(CornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_layer_4).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.NORTH).with(CornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(CornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.NORTH).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(CornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_top_layer_1).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.NORTH).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(CornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_top_layer_2).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.NORTH).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(CornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_top_layer_3).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.NORTH).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(CornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_top_layer_4).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.NORTH).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(CornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.NORTH).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(CornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_top_layer_1).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.NORTH).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(CornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_top_layer_2).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.NORTH).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(CornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_top_layer_3).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.NORTH).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(CornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_top_layer_4).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.NORTH).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(CornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()

                .partialState().with(CornerSlabLayerBlock.FACING, Direction.EAST).with(CornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(CornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_layer_1).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.EAST).with(CornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(CornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_layer_2).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.EAST).with(CornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(CornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_layer_3).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.EAST).with(CornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(CornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_layer_4).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.EAST).with(CornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(CornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.EAST).with(CornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(CornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_layer_1).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.EAST).with(CornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(CornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_layer_2).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.EAST).with(CornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(CornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_layer_3).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.EAST).with(CornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(CornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_layer_4).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.EAST).with(CornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(CornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.EAST).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(CornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_top_layer_1).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.EAST).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(CornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_top_layer_2).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.EAST).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(CornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_top_layer_3).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.EAST).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(CornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_top_layer_4).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.EAST).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(CornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.EAST).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(CornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_top_layer_1).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.EAST).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(CornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_top_layer_2).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.EAST).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(CornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_top_layer_3).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.EAST).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(CornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_top_layer_4).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.EAST).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(CornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()

                .partialState().with(CornerSlabLayerBlock.FACING, Direction.SOUTH).with(CornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(CornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_layer_1).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.SOUTH).with(CornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(CornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_layer_2).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.SOUTH).with(CornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(CornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_layer_3).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.SOUTH).with(CornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(CornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_layer_4).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.SOUTH).with(CornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(CornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.SOUTH).with(CornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(CornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_layer_1).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.SOUTH).with(CornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(CornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_layer_2).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.SOUTH).with(CornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(CornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_layer_3).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.SOUTH).with(CornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(CornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_layer_4).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.SOUTH).with(CornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(CornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.SOUTH).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(CornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_top_layer_1).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.SOUTH).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(CornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_top_layer_2).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.SOUTH).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(CornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_top_layer_3).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.SOUTH).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(CornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_top_layer_4).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.SOUTH).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(CornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.SOUTH).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(CornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_top_layer_1).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.SOUTH).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(CornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_top_layer_2).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.SOUTH).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(CornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_top_layer_3).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.SOUTH).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(CornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_top_layer_4).rotationY(90).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.SOUTH).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(CornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()

                .partialState().with(CornerSlabLayerBlock.FACING, Direction.WEST).with(CornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(CornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_layer_1).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.WEST).with(CornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(CornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_layer_2).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.WEST).with(CornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(CornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_layer_3).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.WEST).with(CornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(CornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_layer_4).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.WEST).with(CornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(CornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.WEST).with(CornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(CornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_layer_1).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.WEST).with(CornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(CornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_layer_2).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.WEST).with(CornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(CornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_layer_3).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.WEST).with(CornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(CornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_layer_4).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.WEST).with(CornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(CornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.WEST).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(CornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_top_layer_1).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.WEST).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(CornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_top_layer_2).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.WEST).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(CornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_top_layer_3).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.WEST).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(CornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_top_layer_4).rotationY(270).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.WEST).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(CornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.WEST).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(CornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_top_layer_1).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.WEST).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(CornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_top_layer_2).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.WEST).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(CornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_top_layer_3).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.WEST).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(CornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_top_layer_4).rotationY(180).uvLock(true).addModel()
                .partialState().with(CornerSlabLayerBlock.FACING, Direction.WEST).with(CornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(CornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel();
    }

    public void cornerSlabVerticalBlock(VerticalCornerSlabBlock block) {
        cornerSlabVerticalBlock(block, blockTexture(block));
    }

    public void cornerSlabVerticalBlock(VerticalCornerSlabBlock block, ResourceLocation texture) {
        cornerSlabVerticalBlock(block, texture, texture, texture);
    }

    public void cornerSlabVerticalBlock(VerticalCornerSlabBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile corner_slab_vertical = models().withExistingParent(name(block), modLoc("block/templates/corner_slab_vertical"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                corner_slab_vertical_left = models().withExistingParent(name(block) + "_left", modLoc("block/templates/corner_slab_vertical_left"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_vertical_top = models().withExistingParent(name(block) + "_top", modLoc("block/templates/corner_slab_vertical_top"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_vertical_top_left = models().withExistingParent(name(block) + "_top_left", modLoc("block/templates/corner_slab_vertical_top_left"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);

        getVariantBuilder(block)
                .partialState().with(VerticalCornerSlabBlock.FACING, Direction.NORTH).with(VerticalCornerSlabBlock.TYPE, QuadShape.RIGHT)
                .modelForState().modelFile(corner_slab_vertical).addModel()
                .partialState().with(VerticalCornerSlabBlock.FACING, Direction.NORTH).with(VerticalCornerSlabBlock.TYPE, QuadShape.LEFT)
                .modelForState().modelFile(corner_slab_vertical_left).addModel()
                .partialState().with(VerticalCornerSlabBlock.FACING, Direction.NORTH).with(VerticalCornerSlabBlock.TYPE, QuadShape.TOP_RIGHT)
                .modelForState().modelFile(corner_slab_vertical_top).addModel()
                .partialState().with(VerticalCornerSlabBlock.FACING, Direction.NORTH).with(VerticalCornerSlabBlock.TYPE, QuadShape.TOP_LEFT)
                .modelForState().modelFile(corner_slab_vertical_top_left).addModel()
                .partialState().with(VerticalCornerSlabBlock.FACING, Direction.EAST).with(VerticalCornerSlabBlock.TYPE, QuadShape.RIGHT)
                .modelForState().modelFile(corner_slab_vertical).rotationY(90).addModel()
                .partialState().with(VerticalCornerSlabBlock.FACING, Direction.EAST).with(VerticalCornerSlabBlock.TYPE, QuadShape.LEFT)
                .modelForState().modelFile(corner_slab_vertical_left).rotationY(90).addModel()
                .partialState().with(VerticalCornerSlabBlock.FACING, Direction.EAST).with(VerticalCornerSlabBlock.TYPE, QuadShape.TOP_RIGHT)
                .modelForState().modelFile(corner_slab_vertical_top).rotationY(90).addModel()
                .partialState().with(VerticalCornerSlabBlock.FACING, Direction.EAST).with(VerticalCornerSlabBlock.TYPE, QuadShape.TOP_LEFT)
                .modelForState().modelFile(corner_slab_vertical_top_left).rotationY(90).addModel()
                .partialState().with(VerticalCornerSlabBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabBlock.TYPE, QuadShape.RIGHT)
                .modelForState().modelFile(corner_slab_vertical).rotationY(180).addModel()
                .partialState().with(VerticalCornerSlabBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabBlock.TYPE, QuadShape.LEFT)
                .modelForState().modelFile(corner_slab_vertical_left).rotationY(180).addModel()
                .partialState().with(VerticalCornerSlabBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabBlock.TYPE, QuadShape.TOP_RIGHT)
                .modelForState().modelFile(corner_slab_vertical_top).rotationY(180).addModel()
                .partialState().with(VerticalCornerSlabBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabBlock.TYPE, QuadShape.TOP_LEFT)
                .modelForState().modelFile(corner_slab_vertical_top_left).rotationY(180).addModel()
                .partialState().with(VerticalCornerSlabBlock.FACING, Direction.WEST).with(VerticalCornerSlabBlock.TYPE, QuadShape.RIGHT)
                .modelForState().modelFile(corner_slab_vertical).rotationY(270).addModel()
                .partialState().with(VerticalCornerSlabBlock.FACING, Direction.WEST).with(VerticalCornerSlabBlock.TYPE, QuadShape.LEFT)
                .modelForState().modelFile(corner_slab_vertical_left).rotationY(270).addModel()
                .partialState().with(VerticalCornerSlabBlock.FACING, Direction.WEST).with(VerticalCornerSlabBlock.TYPE, QuadShape.TOP_RIGHT)
                .modelForState().modelFile(corner_slab_vertical_top).rotationY(270).addModel()
                .partialState().with(VerticalCornerSlabBlock.FACING, Direction.WEST).with(VerticalCornerSlabBlock.TYPE, QuadShape.TOP_LEFT)
                .modelForState().modelFile(corner_slab_vertical_top_left).rotationY(270).addModel();
    }

    public void cornerSlabLayerVerticalBlock(VerticalCornerSlabLayerBlock block) {
        cornerSlabLayerVerticalBlock(block, blockTexture(block));
    }

    public void cornerSlabLayerVerticalBlock(VerticalCornerSlabLayerBlock block, ResourceLocation texture) {
        cornerSlabLayerVerticalBlock(block, texture, texture, texture, texture);
    }

    public void cornerSlabLayerVerticalBlock(VerticalCornerSlabLayerBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation baseBlock) {
        ModelFile corner_slab_vertical_layer_1 = models().withExistingParent(name(block) + "_layer_1", modLoc("block/templates/corner_slab_vertical_layer_1"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                corner_slab_vertical_layer_2 = models().withExistingParent(name(block) + "_layer_2", modLoc("block/templates/corner_slab_vertical_layer_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_vertical_layer_3 = models().withExistingParent(name(block) + "_layer_3", modLoc("block/templates/corner_slab_vertical_layer_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_vertical_layer_4 = models().withExistingParent(name(block) + "_layer_4", modLoc("block/templates/corner_slab_vertical_layer_4"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_vertical_left_layer_1 = models().withExistingParent(name(block) + "_left_layer_1", modLoc("block/templates/corner_slab_vertical_left_layer_1"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_vertical_left_layer_2 = models().withExistingParent(name(block) + "_left_layer_2", modLoc("block/templates/corner_slab_vertical_left_layer_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_vertical_left_layer_3 = models().withExistingParent(name(block) + "_left_layer_3", modLoc("block/templates/corner_slab_vertical_left_layer_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_vertical_left_layer_4 = models().withExistingParent(name(block) + "_left_layer_4", modLoc("block/templates/corner_slab_vertical_left_layer_4"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_vertical_top_layer_1 = models().withExistingParent(name(block) + "_top_layer_1", modLoc("block/templates/corner_slab_vertical_top_layer_1"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_vertical_top_layer_2 = models().withExistingParent(name(block) + "_top_layer_2", modLoc("block/templates/corner_slab_vertical_top_layer_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_vertical_top_layer_3 = models().withExistingParent(name(block) + "_top_layer_3", modLoc("block/templates/corner_slab_vertical_top_layer_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_vertical_top_layer_4 = models().withExistingParent(name(block) + "_top_layer_4", modLoc("block/templates/corner_slab_vertical_top_layer_4"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_vertical_top_left_layer_1 = models().withExistingParent(name(block) + "_top_left_layer_1", modLoc("block/templates/corner_slab_vertical_top_left_layer_1"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_vertical_top_left_layer_2 = models().withExistingParent(name(block) + "_top_left_layer_2", modLoc("block/templates/corner_slab_vertical_top_left_layer_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_vertical_top_left_layer_3 = models().withExistingParent(name(block) + "_top_left_layer_3", modLoc("block/templates/corner_slab_vertical_top_left_layer_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                corner_slab_vertical_top_left_layer_4 = models().withExistingParent(name(block) + "_top_left_layer_4", modLoc("block/templates/corner_slab_vertical_top_left_layer_4"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                full_block = models().getExistingFile(baseBlock);

        getVariantBuilder(block)
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.NORTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_vertical_layer_1).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.NORTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_vertical_layer_2).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.NORTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_vertical_layer_3).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.NORTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_vertical_layer_4).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.NORTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.NORTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_vertical_left_layer_1).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.NORTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_vertical_left_layer_2).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.NORTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_vertical_left_layer_3).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.NORTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_vertical_left_layer_4).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.NORTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.NORTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_vertical_top_layer_1).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.NORTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_vertical_top_layer_2).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.NORTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_vertical_top_layer_3).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.NORTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_vertical_top_layer_4).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.NORTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.NORTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_vertical_top_left_layer_1).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.NORTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_vertical_top_left_layer_2).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.NORTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_vertical_top_left_layer_3).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.NORTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_vertical_top_left_layer_4).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.NORTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()

                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.EAST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_vertical_layer_1).rotationY(90).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.EAST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_vertical_layer_2).rotationY(90).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.EAST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_vertical_layer_3).rotationY(90).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.EAST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_vertical_layer_4).rotationY(90).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.EAST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.EAST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_vertical_left_layer_1).rotationY(90).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.EAST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_vertical_left_layer_2).rotationY(90).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.EAST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_vertical_left_layer_3).rotationY(90).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.EAST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_vertical_left_layer_4).rotationY(90).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.EAST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.EAST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_vertical_top_layer_1).rotationY(90).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.EAST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_vertical_top_layer_2).rotationY(90).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.EAST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_vertical_top_layer_3).rotationY(90).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.EAST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_vertical_top_layer_4).rotationY(90).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.EAST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.EAST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_vertical_top_left_layer_1).rotationY(90).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.EAST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_vertical_top_left_layer_2).rotationY(90).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.EAST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_vertical_top_left_layer_3).rotationY(90).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.EAST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_vertical_top_left_layer_4).rotationY(90).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.EAST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()

                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_vertical_layer_1).rotationY(180).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_vertical_layer_2).rotationY(180).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_vertical_layer_3).rotationY(180).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_vertical_layer_4).rotationY(180).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_vertical_left_layer_1).rotationY(180).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_vertical_left_layer_2).rotationY(180).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_vertical_left_layer_3).rotationY(180).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_vertical_left_layer_4).rotationY(180).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_vertical_top_layer_1).rotationY(180).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_vertical_top_layer_2).rotationY(180).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_vertical_top_layer_3).rotationY(180).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_vertical_top_layer_4).rotationY(180).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_vertical_top_left_layer_1).rotationY(180).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_vertical_top_left_layer_2).rotationY(180).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_vertical_top_left_layer_3).rotationY(180).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_vertical_top_left_layer_4).rotationY(180).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()

                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.WEST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_vertical_layer_1).rotationY(270).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.WEST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_vertical_layer_2).rotationY(270).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.WEST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_vertical_layer_3).rotationY(270).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.WEST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_vertical_layer_4).rotationY(270).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.WEST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.WEST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_vertical_left_layer_1).rotationY(270).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.WEST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_vertical_left_layer_2).rotationY(270).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.WEST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_vertical_left_layer_3).rotationY(270).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.WEST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_vertical_left_layer_4).rotationY(270).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.WEST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.WEST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_vertical_top_layer_1).rotationY(270).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.WEST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_vertical_top_layer_2).rotationY(270).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.WEST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_vertical_top_layer_3).rotationY(270).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.WEST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_vertical_top_layer_4).rotationY(270).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.WEST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_RIGHT).with(VerticalCornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.WEST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(corner_slab_vertical_top_left_layer_1).rotationY(270).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.WEST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(corner_slab_vertical_top_left_layer_2).rotationY(270).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.WEST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(corner_slab_vertical_top_left_layer_3).rotationY(270).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.WEST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(corner_slab_vertical_top_left_layer_4).rotationY(270).addModel()
                .partialState().with(VerticalCornerSlabLayerBlock.FACING, Direction.WEST).with(VerticalCornerSlabLayerBlock.TYPE, QuadShape.TOP_LEFT).with(VerticalCornerSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel();
    }

    public void doorFrameBlock(DoorFrameBlock block) {
        doorFrameBlock(block, blockTexture(block));
    }

    public void doorFrameBlock(DoorFrameBlock block, ResourceLocation texture) {
        doorFrameBlock(block, texture, texture, texture);
    }

    public void doorFrameBlock(DoorFrameBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile door_frame_1 = models().withExistingParent(name(block) + "_1", modLoc("block/templates/door_frame_1"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                door_frame_2 = models().withExistingParent(name(block) + "_2", modLoc("block/templates/door_frame_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                door_frame_3 = models().withExistingParent(name(block) + "_3", modLoc("block/templates/door_frame_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);

        getVariantBuilder(block)
                .partialState().with(DoorFrameBlock.FACING, Direction.NORTH).with(DoorFrameBlock.BEAM, 1)
                .modelForState().modelFile(door_frame_1).addModel()
                .partialState().with(DoorFrameBlock.FACING, Direction.NORTH).with(DoorFrameBlock.BEAM, 2)
                .modelForState().modelFile(door_frame_2).addModel()
                .partialState().with(DoorFrameBlock.FACING, Direction.NORTH).with(DoorFrameBlock.BEAM, 3)
                .modelForState().modelFile(door_frame_3).addModel()
                .partialState().with(DoorFrameBlock.FACING, Direction.EAST).with(DoorFrameBlock.BEAM, 1)
                .modelForState().modelFile(door_frame_1).rotationY(90).addModel()
                .partialState().with(DoorFrameBlock.FACING, Direction.EAST).with(DoorFrameBlock.BEAM, 2)
                .modelForState().modelFile(door_frame_2).rotationY(90).addModel()
                .partialState().with(DoorFrameBlock.FACING, Direction.EAST).with(DoorFrameBlock.BEAM, 3)
                .modelForState().modelFile(door_frame_3).rotationY(90).addModel()
                .partialState().with(DoorFrameBlock.FACING, Direction.SOUTH).with(DoorFrameBlock.BEAM, 1)
                .modelForState().modelFile(door_frame_1).rotationY(180).addModel()
                .partialState().with(DoorFrameBlock.FACING, Direction.SOUTH).with(DoorFrameBlock.BEAM, 2)
                .modelForState().modelFile(door_frame_2).rotationY(180).addModel()
                .partialState().with(DoorFrameBlock.FACING, Direction.SOUTH).with(DoorFrameBlock.BEAM, 3)
                .modelForState().modelFile(door_frame_3).rotationY(180).addModel()
                .partialState().with(DoorFrameBlock.FACING, Direction.WEST).with(DoorFrameBlock.BEAM, 1)
                .modelForState().modelFile(door_frame_1).rotationY(270).addModel()
                .partialState().with(DoorFrameBlock.FACING, Direction.WEST).with(DoorFrameBlock.BEAM, 2)
                .modelForState().modelFile(door_frame_2).rotationY(270).addModel()
                .partialState().with(DoorFrameBlock.FACING, Direction.WEST).with(DoorFrameBlock.BEAM, 3)
                .modelForState().modelFile(door_frame_3).rotationY(270).addModel();
    }

    public void doorFrameLintelBlock(DoorFrameLintelBlock block) {
        doorFrameLintelBlock(block, blockTexture(block));
    }

    public void doorFrameLintelBlock(DoorFrameLintelBlock block, ResourceLocation texture) {
        doorFrameLintelBlock(block, texture, texture, texture);
    }

    public void doorFrameLintelBlock(DoorFrameLintelBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile door_frame_lintel_1 = models().withExistingParent(name(block) + "_1", modLoc("block/templates/door_frame_lintel_1"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                door_frame_lintel_2 = models().withExistingParent(name(block) + "_2", modLoc("block/templates/door_frame_lintel_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                door_frame_lintel_3 = models().withExistingParent(name(block) + "_3", modLoc("block/templates/door_frame_lintel_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                door_frame_lintel_4 = models().withExistingParent(name(block) + "_4", modLoc("block/templates/door_frame_lintel_4"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);

        getVariantBuilder(block)

                .partialState().with(DoorFrameLintelBlock.FACING, Direction.NORTH).with(DoorFrameLintelBlock.TYPE, TopBottomShape.TOP).with(DoorFrameLintelBlock.BEAM, 1)
                .modelForState().modelFile(door_frame_lintel_1).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.NORTH).with(DoorFrameLintelBlock.TYPE, TopBottomShape.TOP).with(DoorFrameLintelBlock.BEAM, 2)
                .modelForState().modelFile(door_frame_lintel_2).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.NORTH).with(DoorFrameLintelBlock.TYPE, TopBottomShape.TOP).with(DoorFrameLintelBlock.BEAM, 3)
                .modelForState().modelFile(door_frame_lintel_3).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.NORTH).with(DoorFrameLintelBlock.TYPE, TopBottomShape.TOP).with(DoorFrameLintelBlock.BEAM, 4)
                .modelForState().modelFile(door_frame_lintel_4).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.NORTH).with(DoorFrameLintelBlock.TYPE, TopBottomShape.BOTTOM).with(DoorFrameLintelBlock.BEAM, 1)
                .modelForState().modelFile(door_frame_lintel_1).rotationX(180).rotationY(180).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.NORTH).with(DoorFrameLintelBlock.TYPE, TopBottomShape.BOTTOM).with(DoorFrameLintelBlock.BEAM, 2)
                .modelForState().modelFile(door_frame_lintel_2).rotationX(180).rotationY(180).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.NORTH).with(DoorFrameLintelBlock.TYPE, TopBottomShape.BOTTOM).with(DoorFrameLintelBlock.BEAM, 3)
                .modelForState().modelFile(door_frame_lintel_3).rotationX(180).rotationY(180).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.NORTH).with(DoorFrameLintelBlock.TYPE, TopBottomShape.BOTTOM).with(DoorFrameLintelBlock.BEAM, 4)
                .modelForState().modelFile(door_frame_lintel_4).rotationX(180).rotationY(180).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.EAST).with(DoorFrameLintelBlock.TYPE, TopBottomShape.TOP).with(DoorFrameLintelBlock.BEAM, 1)
                .modelForState().modelFile(door_frame_lintel_1).rotationY(90).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.EAST).with(DoorFrameLintelBlock.TYPE, TopBottomShape.TOP).with(DoorFrameLintelBlock.BEAM, 2)
                .modelForState().modelFile(door_frame_lintel_2).rotationY(90).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.EAST).with(DoorFrameLintelBlock.TYPE, TopBottomShape.TOP).with(DoorFrameLintelBlock.BEAM, 3)
                .modelForState().modelFile(door_frame_lintel_3).rotationY(90).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.EAST).with(DoorFrameLintelBlock.TYPE, TopBottomShape.TOP).with(DoorFrameLintelBlock.BEAM, 4)
                .modelForState().modelFile(door_frame_lintel_4).rotationY(90).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.EAST).with(DoorFrameLintelBlock.TYPE, TopBottomShape.BOTTOM).with(DoorFrameLintelBlock.BEAM, 1)
                .modelForState().modelFile(door_frame_lintel_1).rotationX(180).rotationY(270).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.EAST).with(DoorFrameLintelBlock.TYPE, TopBottomShape.BOTTOM).with(DoorFrameLintelBlock.BEAM, 2)
                .modelForState().modelFile(door_frame_lintel_2).rotationX(180).rotationY(270).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.EAST).with(DoorFrameLintelBlock.TYPE, TopBottomShape.BOTTOM).with(DoorFrameLintelBlock.BEAM, 3)
                .modelForState().modelFile(door_frame_lintel_3).rotationX(180).rotationY(270).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.EAST).with(DoorFrameLintelBlock.TYPE, TopBottomShape.BOTTOM).with(DoorFrameLintelBlock.BEAM, 4)
                .modelForState().modelFile(door_frame_lintel_4).rotationX(180).rotationY(270).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.SOUTH).with(DoorFrameLintelBlock.TYPE, TopBottomShape.TOP).with(DoorFrameLintelBlock.BEAM, 1)
                .modelForState().modelFile(door_frame_lintel_1).rotationY(180).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.SOUTH).with(DoorFrameLintelBlock.TYPE, TopBottomShape.TOP).with(DoorFrameLintelBlock.BEAM, 2)
                .modelForState().modelFile(door_frame_lintel_2).rotationY(180).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.SOUTH).with(DoorFrameLintelBlock.TYPE, TopBottomShape.TOP).with(DoorFrameLintelBlock.BEAM, 3)
                .modelForState().modelFile(door_frame_lintel_3).rotationY(180).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.SOUTH).with(DoorFrameLintelBlock.TYPE, TopBottomShape.TOP).with(DoorFrameLintelBlock.BEAM, 4)
                .modelForState().modelFile(door_frame_lintel_4).rotationY(180).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.SOUTH).with(DoorFrameLintelBlock.TYPE, TopBottomShape.BOTTOM).with(DoorFrameLintelBlock.BEAM, 1)
                .modelForState().modelFile(door_frame_lintel_1).rotationX(180).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.SOUTH).with(DoorFrameLintelBlock.TYPE, TopBottomShape.BOTTOM).with(DoorFrameLintelBlock.BEAM, 2)
                .modelForState().modelFile(door_frame_lintel_2).rotationX(180).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.SOUTH).with(DoorFrameLintelBlock.TYPE, TopBottomShape.BOTTOM).with(DoorFrameLintelBlock.BEAM, 3)
                .modelForState().modelFile(door_frame_lintel_3).rotationX(180).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.SOUTH).with(DoorFrameLintelBlock.TYPE, TopBottomShape.BOTTOM).with(DoorFrameLintelBlock.BEAM, 4)
                .modelForState().modelFile(door_frame_lintel_4).rotationX(180).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.WEST).with(DoorFrameLintelBlock.TYPE, TopBottomShape.TOP).with(DoorFrameLintelBlock.BEAM, 1)
                .modelForState().modelFile(door_frame_lintel_1).rotationY(270).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.WEST).with(DoorFrameLintelBlock.TYPE, TopBottomShape.TOP).with(DoorFrameLintelBlock.BEAM, 2)
                .modelForState().modelFile(door_frame_lintel_2).rotationY(270).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.WEST).with(DoorFrameLintelBlock.TYPE, TopBottomShape.TOP).with(DoorFrameLintelBlock.BEAM, 3)
                .modelForState().modelFile(door_frame_lintel_3).rotationY(270).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.WEST).with(DoorFrameLintelBlock.TYPE, TopBottomShape.TOP).with(DoorFrameLintelBlock.BEAM, 4)
                .modelForState().modelFile(door_frame_lintel_4).rotationY(270).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.WEST).with(DoorFrameLintelBlock.TYPE, TopBottomShape.BOTTOM).with(DoorFrameLintelBlock.BEAM, 1)
                .modelForState().modelFile(door_frame_lintel_1).rotationX(180).rotationY(90).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.WEST).with(DoorFrameLintelBlock.TYPE, TopBottomShape.BOTTOM).with(DoorFrameLintelBlock.BEAM, 2)
                .modelForState().modelFile(door_frame_lintel_2).rotationX(180).rotationY(90).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.WEST).with(DoorFrameLintelBlock.TYPE, TopBottomShape.BOTTOM).with(DoorFrameLintelBlock.BEAM, 3)
                .modelForState().modelFile(door_frame_lintel_3).rotationX(180).rotationY(90).addModel()
                .partialState().with(DoorFrameLintelBlock.FACING, Direction.WEST).with(DoorFrameLintelBlock.TYPE, TopBottomShape.BOTTOM).with(DoorFrameLintelBlock.BEAM, 4)
                .modelForState().modelFile(door_frame_lintel_4).rotationX(180).rotationY(90).addModel();
    }

    public void eighthBlock(EighthBlock block) {
        eighthBlock(block, blockTexture(block));
    }

    public void eighthBlock(EighthBlock block, ResourceLocation texture) {
        eighthBlock(block, texture, texture, texture);
    }

    public void eighthBlock(EighthBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile eighth = models().withExistingParent(name(block), modLoc("block/templates/eighth"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                eighth_left = models().withExistingParent(name(block) + "_left", modLoc("block/templates/eighth_left"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                eighth_top = models().withExistingParent(name(block) + "_top", modLoc("block/templates/eighth_top"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                eighth_top_left = models().withExistingParent(name(block) + "_top_left", modLoc("block/templates/eighth_top_left"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);

        getVariantBuilder(block)
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.RIGHT)
                .modelForState().modelFile(eighth).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.LEFT)
                .modelForState().modelFile(eighth_left).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT)
                .modelForState().modelFile(eighth_top).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.TOP_LEFT)
                .modelForState().modelFile(eighth_top_left).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.RIGHT)
                .modelForState().modelFile(eighth).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.LEFT)
                .modelForState().modelFile(eighth_left).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT)
                .modelForState().modelFile(eighth_top).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.TOP_LEFT)
                .modelForState().modelFile(eighth_top_left).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.RIGHT)
                .modelForState().modelFile(eighth).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.LEFT)
                .modelForState().modelFile(eighth_left).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT)
                .modelForState().modelFile(eighth_top).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.TOP_LEFT)
                .modelForState().modelFile(eighth_top_left).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.RIGHT)
                .modelForState().modelFile(eighth).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.LEFT)
                .modelForState().modelFile(eighth_left).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT)
                .modelForState().modelFile(eighth_top).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.TOP_LEFT)
                .modelForState().modelFile(eighth_top_left).rotationY(270).addModel();

    }

    public void eighthLayerBlock(EighthLayerBlock block) {
        eighthLayerBlock(block, blockTexture(block));
    }

    public void eighthLayerBlock(EighthLayerBlock block, ResourceLocation texture) {
        eighthLayerBlock(block, texture, texture, texture, texture);
    }

    public void eighthLayerBlock(EighthLayerBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation baseBlock) {

        ModelFile eighth_layer_1 = models().withExistingParent(name(block) + "_layer_1", modLoc("block/templates/eighth_layer_1"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                eighth_layer_2 = models().withExistingParent(name(block) + "_layer_2", modLoc("block/templates/eighth_layer_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                eighth_layer_3 = models().withExistingParent(name(block) + "_layer_3", modLoc("block/templates/eighth_layer_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                eighth_layer_4 = models().withExistingParent(name(block) + "_layer_4", modLoc("block/templates/eighth_layer_4"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                eighth_left_layer_1 = models().withExistingParent(name(block) + "_left_layer_1", modLoc("block/templates/eighth_left_layer_1"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                eighth_left_layer_2 = models().withExistingParent(name(block) + "_left_layer_2", modLoc("block/templates/eighth_left_layer_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                eighth_left_layer_3 = models().withExistingParent(name(block) + "_left_layer_3", modLoc("block/templates/eighth_left_layer_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                eighth_left_layer_4 = models().withExistingParent(name(block) + "_left_layer_4", modLoc("block/templates/eighth_left_layer_4"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                eighth_top_layer_1 = models().withExistingParent(name(block) + "_top_layer_1", modLoc("block/templates/eighth_top_layer_1"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                eighth_top_layer_2 = models().withExistingParent(name(block) + "_top_layer_2", modLoc("block/templates/eighth_top_layer_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                eighth_top_layer_3 = models().withExistingParent(name(block) + "_top_layer_3", modLoc("block/templates/eighth_top_layer_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                eighth_top_layer_4 = models().withExistingParent(name(block) + "_top_layer_4", modLoc("block/templates/eighth_top_layer_4"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                eighth_top_left_layer_1 = models().withExistingParent(name(block) + "_top_left_layer_1", modLoc("block/templates/eighth_top_left_layer_1"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                eighth_top_left_layer_2 = models().withExistingParent(name(block) + "_top_left_layer_2", modLoc("block/templates/eighth_top_left_layer_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                eighth_top_left_layer_3 = models().withExistingParent(name(block) + "_top_left_layer_3", modLoc("block/templates/eighth_top_left_layer_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                eighth_top_left_layer_4 = models().withExistingParent(name(block) + "_top_left_layer_4", modLoc("block/templates/eighth_top_left_layer_4"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                full_block = models().getExistingFile(baseBlock);

        getVariantBuilder(block)
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.RIGHT).with(EighthLayerBlock.LAYERS, 1)
                .modelForState().modelFile(eighth_layer_1).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.RIGHT).with(EighthLayerBlock.LAYERS, 2)
                .modelForState().modelFile(eighth_layer_2).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.RIGHT).with(EighthLayerBlock.LAYERS, 3)
                .modelForState().modelFile(eighth_layer_3).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.RIGHT).with(EighthLayerBlock.LAYERS, 4)
                .modelForState().modelFile(eighth_layer_4).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.RIGHT).with(EighthLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.LEFT).with(EighthLayerBlock.LAYERS, 1)
                .modelForState().modelFile(eighth_left_layer_1).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.LEFT).with(EighthLayerBlock.LAYERS, 2)
                .modelForState().modelFile(eighth_left_layer_2).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.LEFT).with(EighthLayerBlock.LAYERS, 3)
                .modelForState().modelFile(eighth_left_layer_3).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.LEFT).with(EighthLayerBlock.LAYERS, 4)
                .modelForState().modelFile(eighth_left_layer_4).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.LEFT).with(EighthLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT).with(EighthLayerBlock.LAYERS, 1)
                .modelForState().modelFile(eighth_top_layer_1).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT).with(EighthLayerBlock.LAYERS, 2)
                .modelForState().modelFile(eighth_top_layer_2).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT).with(EighthLayerBlock.LAYERS, 3)
                .modelForState().modelFile(eighth_top_layer_3).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT).with(EighthLayerBlock.LAYERS, 4)
                .modelForState().modelFile(eighth_top_layer_4).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT).with(EighthLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.TOP_LEFT).with(EighthLayerBlock.LAYERS, 1)
                .modelForState().modelFile(eighth_top_left_layer_1).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.TOP_LEFT).with(EighthLayerBlock.LAYERS, 2)
                .modelForState().modelFile(eighth_top_left_layer_2).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.TOP_LEFT).with(EighthLayerBlock.LAYERS, 3)
                .modelForState().modelFile(eighth_top_left_layer_3).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.TOP_LEFT).with(EighthLayerBlock.LAYERS, 4)
                .modelForState().modelFile(eighth_top_left_layer_4).addModel()
                .partialState().with(EighthBlock.FACING, Direction.NORTH).with(EighthBlock.TYPE, QuadShape.TOP_LEFT).with(EighthLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()

                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.RIGHT).with(EighthLayerBlock.LAYERS, 1)
                .modelForState().modelFile(eighth_layer_1).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.RIGHT).with(EighthLayerBlock.LAYERS, 2)
                .modelForState().modelFile(eighth_layer_2).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.RIGHT).with(EighthLayerBlock.LAYERS, 3)
                .modelForState().modelFile(eighth_layer_3).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.RIGHT).with(EighthLayerBlock.LAYERS, 4)
                .modelForState().modelFile(eighth_layer_4).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.RIGHT).with(EighthLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.LEFT).with(EighthLayerBlock.LAYERS, 1)
                .modelForState().modelFile(eighth_left_layer_1).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.LEFT).with(EighthLayerBlock.LAYERS, 2)
                .modelForState().modelFile(eighth_left_layer_2).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.LEFT).with(EighthLayerBlock.LAYERS, 3)
                .modelForState().modelFile(eighth_left_layer_3).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.LEFT).with(EighthLayerBlock.LAYERS, 4)
                .modelForState().modelFile(eighth_left_layer_4).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.LEFT).with(EighthLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT).with(EighthLayerBlock.LAYERS, 1)
                .modelForState().modelFile(eighth_top_layer_1).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT).with(EighthLayerBlock.LAYERS, 2)
                .modelForState().modelFile(eighth_top_layer_2).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT).with(EighthLayerBlock.LAYERS, 3)
                .modelForState().modelFile(eighth_top_layer_3).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT).with(EighthLayerBlock.LAYERS, 4)
                .modelForState().modelFile(eighth_top_layer_4).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT).with(EighthLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.TOP_LEFT).with(EighthLayerBlock.LAYERS, 1)
                .modelForState().modelFile(eighth_top_left_layer_1).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.TOP_LEFT).with(EighthLayerBlock.LAYERS, 2)
                .modelForState().modelFile(eighth_top_left_layer_2).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.TOP_LEFT).with(EighthLayerBlock.LAYERS, 3)
                .modelForState().modelFile(eighth_top_left_layer_3).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.TOP_LEFT).with(EighthLayerBlock.LAYERS, 4)
                .modelForState().modelFile(eighth_top_left_layer_4).rotationY(90).addModel()
                .partialState().with(EighthBlock.FACING, Direction.EAST).with(EighthBlock.TYPE, QuadShape.TOP_LEFT).with(EighthLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(90).addModel()

                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.RIGHT).with(EighthLayerBlock.LAYERS, 1)
                .modelForState().modelFile(eighth_layer_1).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.RIGHT).with(EighthLayerBlock.LAYERS, 2)
                .modelForState().modelFile(eighth_layer_2).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.RIGHT).with(EighthLayerBlock.LAYERS, 3)
                .modelForState().modelFile(eighth_layer_3).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.RIGHT).with(EighthLayerBlock.LAYERS, 4)
                .modelForState().modelFile(eighth_layer_4).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.RIGHT).with(EighthLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.LEFT).with(EighthLayerBlock.LAYERS, 1)
                .modelForState().modelFile(eighth_left_layer_1).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.LEFT).with(EighthLayerBlock.LAYERS, 2)
                .modelForState().modelFile(eighth_left_layer_2).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.LEFT).with(EighthLayerBlock.LAYERS, 3)
                .modelForState().modelFile(eighth_left_layer_3).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.LEFT).with(EighthLayerBlock.LAYERS, 4)
                .modelForState().modelFile(eighth_left_layer_4).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.LEFT).with(EighthLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT).with(EighthLayerBlock.LAYERS, 1)
                .modelForState().modelFile(eighth_top_layer_1).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT).with(EighthLayerBlock.LAYERS, 2)
                .modelForState().modelFile(eighth_top_layer_2).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT).with(EighthLayerBlock.LAYERS, 3)
                .modelForState().modelFile(eighth_top_layer_3).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT).with(EighthLayerBlock.LAYERS, 4)
                .modelForState().modelFile(eighth_top_layer_4).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT).with(EighthLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.TOP_LEFT).with(EighthLayerBlock.LAYERS, 1)
                .modelForState().modelFile(eighth_top_left_layer_1).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.TOP_LEFT).with(EighthLayerBlock.LAYERS, 2)
                .modelForState().modelFile(eighth_top_left_layer_2).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.TOP_LEFT).with(EighthLayerBlock.LAYERS, 3)
                .modelForState().modelFile(eighth_top_left_layer_3).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.TOP_LEFT).with(EighthLayerBlock.LAYERS, 4)
                .modelForState().modelFile(eighth_top_left_layer_4).rotationY(180).addModel()
                .partialState().with(EighthBlock.FACING, Direction.SOUTH).with(EighthBlock.TYPE, QuadShape.TOP_LEFT).with(EighthLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(180).addModel()

                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.RIGHT).with(EighthLayerBlock.LAYERS, 1)
                .modelForState().modelFile(eighth_layer_1).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.RIGHT).with(EighthLayerBlock.LAYERS, 2)
                .modelForState().modelFile(eighth_layer_2).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.RIGHT).with(EighthLayerBlock.LAYERS, 3)
                .modelForState().modelFile(eighth_layer_3).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.RIGHT).with(EighthLayerBlock.LAYERS, 4)
                .modelForState().modelFile(eighth_layer_4).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.RIGHT).with(EighthLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.LEFT).with(EighthLayerBlock.LAYERS, 1)
                .modelForState().modelFile(eighth_left_layer_1).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.LEFT).with(EighthLayerBlock.LAYERS, 2)
                .modelForState().modelFile(eighth_left_layer_2).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.LEFT).with(EighthLayerBlock.LAYERS, 3)
                .modelForState().modelFile(eighth_left_layer_3).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.LEFT).with(EighthLayerBlock.LAYERS, 4)
                .modelForState().modelFile(eighth_left_layer_4).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.LEFT).with(EighthLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT).with(EighthLayerBlock.LAYERS, 1)
                .modelForState().modelFile(eighth_top_layer_1).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT).with(EighthLayerBlock.LAYERS, 2)
                .modelForState().modelFile(eighth_top_layer_2).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT).with(EighthLayerBlock.LAYERS, 3)
                .modelForState().modelFile(eighth_top_layer_3).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT).with(EighthLayerBlock.LAYERS, 4)
                .modelForState().modelFile(eighth_top_layer_4).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.TOP_RIGHT).with(EighthLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.TOP_LEFT).with(EighthLayerBlock.LAYERS, 1)
                .modelForState().modelFile(eighth_top_left_layer_1).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.TOP_LEFT).with(EighthLayerBlock.LAYERS, 2)
                .modelForState().modelFile(eighth_top_left_layer_2).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.TOP_LEFT).with(EighthLayerBlock.LAYERS, 3)
                .modelForState().modelFile(eighth_top_left_layer_3).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.TOP_LEFT).with(EighthLayerBlock.LAYERS, 4)
                .modelForState().modelFile(eighth_top_left_layer_4).rotationY(270).addModel()
                .partialState().with(EighthBlock.FACING, Direction.WEST).with(EighthBlock.TYPE, QuadShape.TOP_LEFT).with(EighthLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(270).addModel();
    }

    public void hangingSignBlock(Block signBlock, Block wallSignBlock, ResourceLocation texture) {
        ModelFile sign = models().sign(name(signBlock), texture);
        hangingSignBlock(signBlock, wallSignBlock, sign);
    }

    public void hangingSignBlock(Block signBlock, Block wallSignBlock, ModelFile sign) {
        simpleBlock(signBlock, sign);
        simpleBlock(wallSignBlock, sign);
    }

    public void leavesBlock(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(),
                models().singleTexture(ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath(), new ResourceLocation("minecraft:block/leaves"),
                        "all", blockTexture(blockRegistryObject.get())).renderType("cutout"));
    }

    public void pillarLayerBlock(PillarLayerBlock block) {
        pillarLayerBlock(block, blockTexture(block));
    }

    public void pillarLayerBlock(PillarLayerBlock block, ResourceLocation texture) {
        pillarLayerBlock(block, texture, texture, texture, texture);
    }

    public void pillarLayerBlock(PillarLayerBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation baseBlock) {
        ModelFile pillar_layer_1 = models().withExistingParent(name(block) + "_layer_1", modLoc("block/templates/pillar_layer_1"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                pillar_layer_2 = models().withExistingParent(name(block) + "_layer_2", modLoc("block/templates/pillar_layer_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                pillar_layer_3 = models().withExistingParent(name(block) + "_layer_3", modLoc("block/templates/pillar_layer_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                pillar_layer_4 = models().withExistingParent(name(block) + "_layer_4", modLoc("block/templates/pillar_layer_4"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                full_block = this.models().getExistingFile(baseBlock);

        getVariantBuilder(block)
                .partialState().with(PillarLayerBlock.LAYERS, 1)
                .modelForState().modelFile(pillar_layer_1).addModel()
                .partialState().with(PillarLayerBlock.LAYERS, 2)
                .modelForState().modelFile(pillar_layer_2).addModel()
                .partialState().with(PillarLayerBlock.LAYERS, 3)
                .modelForState().modelFile(pillar_layer_3).addModel()
                .partialState().with(PillarLayerBlock.LAYERS, 4)
                .modelForState().modelFile(pillar_layer_4).addModel()
                .partialState().with(PillarLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel();
    }

    public void quarterLayerBlock(QuarterLayerBlock block) {
        quarterLayerBlock(block, blockTexture(block));
    }

    public void quarterLayerBlock(QuarterLayerBlock block, ResourceLocation texture) {
        quarterLayerBlock(block, texture, texture, texture, texture);
    }

    public void quarterLayerBlock(QuarterLayerBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation baseBlock) {
        ModelFile quarter_layer_1 = models().withExistingParent(name(block) + "_layer_1", modLoc("block/templates/quarter_layer_1"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                quarter_layer_2 = models().withExistingParent(name(block) + "_layer_2", modLoc("block/templates/quarter_layer_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                quarter_layer_3 = models().withExistingParent(name(block) + "_layer_3", modLoc("block/templates/quarter_layer_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                quarter_layer_4 = models().withExistingParent(name(block) + "_layer_4", modLoc("block/templates/quarter_layer_4"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                quarter_top_layer_1 = models().withExistingParent(name(block) + "_top_layer_1", modLoc("block/templates/quarter_top_layer_1"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                quarter_top_layer_2 = models().withExistingParent(name(block) + "_top_layer_2", modLoc("block/templates/quarter_top_layer_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                quarter_top_layer_3 = models().withExistingParent(name(block) + "_top_layer_3", modLoc("block/templates/quarter_top_layer_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                quarter_top_layer_4 = models().withExistingParent(name(block) + "_top_layer_4", modLoc("block/templates/quarter_top_layer_4"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                full_block = this.models().getExistingFile(baseBlock);

        getVariantBuilder(block)
                .partialState().with(QuarterLayerBlock.FACING, Direction.NORTH).with(QuarterLayerBlock.TYPE, TopBottomShape.BOTTOM).with(QuarterLayerBlock.LAYERS, 1)
                .modelForState().modelFile(quarter_layer_1).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.NORTH).with(QuarterLayerBlock.TYPE, TopBottomShape.BOTTOM).with(QuarterLayerBlock.LAYERS, 2)
                .modelForState().modelFile(quarter_layer_2).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.NORTH).with(QuarterLayerBlock.TYPE, TopBottomShape.BOTTOM).with(QuarterLayerBlock.LAYERS, 3)
                .modelForState().modelFile(quarter_layer_3).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.NORTH).with(QuarterLayerBlock.TYPE, TopBottomShape.BOTTOM).with(QuarterLayerBlock.LAYERS, 4)
                .modelForState().modelFile(quarter_layer_4).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.NORTH).with(QuarterLayerBlock.TYPE, TopBottomShape.BOTTOM).with(QuarterLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.NORTH).with(QuarterLayerBlock.TYPE, TopBottomShape.TOP).with(QuarterLayerBlock.LAYERS, 1)
                .modelForState().modelFile(quarter_top_layer_1).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.NORTH).with(QuarterLayerBlock.TYPE, TopBottomShape.TOP).with(QuarterLayerBlock.LAYERS, 2)
                .modelForState().modelFile(quarter_top_layer_2).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.NORTH).with(QuarterLayerBlock.TYPE, TopBottomShape.TOP).with(QuarterLayerBlock.LAYERS, 3)
                .modelForState().modelFile(quarter_top_layer_3).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.NORTH).with(QuarterLayerBlock.TYPE, TopBottomShape.TOP).with(QuarterLayerBlock.LAYERS, 4)
                .modelForState().modelFile(quarter_top_layer_4).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.NORTH).with(QuarterLayerBlock.TYPE, TopBottomShape.TOP).with(QuarterLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.EAST).with(QuarterLayerBlock.TYPE, TopBottomShape.BOTTOM).with(QuarterLayerBlock.LAYERS, 1)
                .modelForState().modelFile(quarter_layer_1).rotationY(90).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.EAST).with(QuarterLayerBlock.TYPE, TopBottomShape.BOTTOM).with(QuarterLayerBlock.LAYERS, 2)
                .modelForState().modelFile(quarter_layer_2).rotationY(90).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.EAST).with(QuarterLayerBlock.TYPE, TopBottomShape.BOTTOM).with(QuarterLayerBlock.LAYERS, 3)
                .modelForState().modelFile(quarter_layer_3).rotationY(90).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.EAST).with(QuarterLayerBlock.TYPE, TopBottomShape.BOTTOM).with(QuarterLayerBlock.LAYERS, 4)
                .modelForState().modelFile(quarter_layer_4).rotationY(90).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.EAST).with(QuarterLayerBlock.TYPE, TopBottomShape.BOTTOM).with(QuarterLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(90).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.EAST).with(QuarterLayerBlock.TYPE, TopBottomShape.TOP).with(QuarterLayerBlock.LAYERS, 1)
                .modelForState().modelFile(quarter_top_layer_1).rotationY(90).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.EAST).with(QuarterLayerBlock.TYPE, TopBottomShape.TOP).with(QuarterLayerBlock.LAYERS, 2)
                .modelForState().modelFile(quarter_top_layer_2).rotationY(90).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.EAST).with(QuarterLayerBlock.TYPE, TopBottomShape.TOP).with(QuarterLayerBlock.LAYERS, 3)
                .modelForState().modelFile(quarter_top_layer_3).rotationY(90).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.EAST).with(QuarterLayerBlock.TYPE, TopBottomShape.TOP).with(QuarterLayerBlock.LAYERS, 4)
                .modelForState().modelFile(quarter_top_layer_4).rotationY(90).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.EAST).with(QuarterLayerBlock.TYPE, TopBottomShape.TOP).with(QuarterLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(90).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.SOUTH).with(QuarterLayerBlock.TYPE, TopBottomShape.BOTTOM).with(QuarterLayerBlock.LAYERS, 1)
                .modelForState().modelFile(quarter_layer_1).rotationY(180).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.SOUTH).with(QuarterLayerBlock.TYPE, TopBottomShape.BOTTOM).with(QuarterLayerBlock.LAYERS, 2)
                .modelForState().modelFile(quarter_layer_2).rotationY(180).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.SOUTH).with(QuarterLayerBlock.TYPE, TopBottomShape.BOTTOM).with(QuarterLayerBlock.LAYERS, 3)
                .modelForState().modelFile(quarter_layer_3).rotationY(180).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.SOUTH).with(QuarterLayerBlock.TYPE, TopBottomShape.BOTTOM).with(QuarterLayerBlock.LAYERS, 4)
                .modelForState().modelFile(quarter_layer_4).rotationY(180).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.SOUTH).with(QuarterLayerBlock.TYPE, TopBottomShape.BOTTOM).with(QuarterLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(180).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.SOUTH).with(QuarterLayerBlock.TYPE, TopBottomShape.TOP).with(QuarterLayerBlock.LAYERS, 1)
                .modelForState().modelFile(quarter_top_layer_1).rotationY(180).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.SOUTH).with(QuarterLayerBlock.TYPE, TopBottomShape.TOP).with(QuarterLayerBlock.LAYERS, 2)
                .modelForState().modelFile(quarter_top_layer_2).rotationY(180).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.SOUTH).with(QuarterLayerBlock.TYPE, TopBottomShape.TOP).with(QuarterLayerBlock.LAYERS, 3)
                .modelForState().modelFile(quarter_top_layer_3).rotationY(180).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.SOUTH).with(QuarterLayerBlock.TYPE, TopBottomShape.TOP).with(QuarterLayerBlock.LAYERS, 4)
                .modelForState().modelFile(quarter_top_layer_4).rotationY(180).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.SOUTH).with(QuarterLayerBlock.TYPE, TopBottomShape.TOP).with(QuarterLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(180).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.WEST).with(QuarterLayerBlock.TYPE, TopBottomShape.BOTTOM).with(QuarterLayerBlock.LAYERS, 1)
                .modelForState().modelFile(quarter_layer_1).rotationY(270).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.WEST).with(QuarterLayerBlock.TYPE, TopBottomShape.BOTTOM).with(QuarterLayerBlock.LAYERS, 2)
                .modelForState().modelFile(quarter_layer_2).rotationY(270).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.WEST).with(QuarterLayerBlock.TYPE, TopBottomShape.BOTTOM).with(QuarterLayerBlock.LAYERS, 3)
                .modelForState().modelFile(quarter_layer_3).rotationY(270).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.WEST).with(QuarterLayerBlock.TYPE, TopBottomShape.BOTTOM).with(QuarterLayerBlock.LAYERS, 4)
                .modelForState().modelFile(quarter_layer_4).rotationY(270).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.WEST).with(QuarterLayerBlock.TYPE, TopBottomShape.BOTTOM).with(QuarterLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(270).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.WEST).with(QuarterLayerBlock.TYPE, TopBottomShape.TOP).with(QuarterLayerBlock.LAYERS, 1)
                .modelForState().modelFile(quarter_top_layer_1).rotationY(270).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.WEST).with(QuarterLayerBlock.TYPE, TopBottomShape.TOP).with(QuarterLayerBlock.LAYERS, 2)
                .modelForState().modelFile(quarter_top_layer_2).rotationY(270).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.WEST).with(QuarterLayerBlock.TYPE, TopBottomShape.TOP).with(QuarterLayerBlock.LAYERS, 3)
                .modelForState().modelFile(quarter_top_layer_3).rotationY(270).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.WEST).with(QuarterLayerBlock.TYPE, TopBottomShape.TOP).with(QuarterLayerBlock.LAYERS, 4)
                .modelForState().modelFile(quarter_top_layer_4).rotationY(270).addModel()
                .partialState().with(QuarterLayerBlock.FACING, Direction.WEST).with(QuarterLayerBlock.TYPE, TopBottomShape.TOP).with(QuarterLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(270).addModel();
    }

    public void quarterLayerVerticalBlock(VerticalQuarterLayerBlock block) {
        quarterLayerVerticalBlock(block, blockTexture(block));
    }

    public void quarterLayerVerticalBlock(VerticalQuarterLayerBlock block, ResourceLocation texture) {
        quarterLayerVerticalBlock(block, texture, texture, texture, texture);
    }

    public void quarterLayerVerticalBlock(VerticalQuarterLayerBlock block, ResourceLocation side, ResourceLocation left, ResourceLocation bottom, ResourceLocation baseBlock) {
        ModelFile quarter_vertical_layer_1 = models().withExistingParent(name(block) + "_layer_1", modLoc("block/templates/quarter_vertical_layer_1"))
                .texture("side", side)
                .texture("top", left)
                .texture("bottom", bottom),
                quarter_vertical_layer_2 = models().withExistingParent(name(block) + "_layer_2", modLoc("block/templates/quarter_vertical_layer_2"))
                        .texture("side", side)
                        .texture("top", left)
                        .texture("bottom", bottom),
                quarter_vertical_layer_3 = models().withExistingParent(name(block) + "_layer_3", modLoc("block/templates/quarter_vertical_layer_3"))
                        .texture("side", side)
                        .texture("top", left)
                        .texture("bottom", bottom),
                quarter_vertical_layer_4 = models().withExistingParent(name(block) + "_layer_4", modLoc("block/templates/quarter_vertical_layer_4"))
                        .texture("side", side)
                        .texture("top", left)
                        .texture("bottom", bottom),
                full_block = this.models().getExistingFile(baseBlock);

        getVariantBuilder(block)
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.NORTH).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.RIGHT).with(VerticalQuarterLayerBlock.LAYERS, 1)
                .modelForState().modelFile(quarter_vertical_layer_1).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.NORTH).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.RIGHT).with(VerticalQuarterLayerBlock.LAYERS, 2)
                .modelForState().modelFile(quarter_vertical_layer_2).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.NORTH).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.RIGHT).with(VerticalQuarterLayerBlock.LAYERS, 3)
                .modelForState().modelFile(quarter_vertical_layer_3).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.NORTH).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.RIGHT).with(VerticalQuarterLayerBlock.LAYERS, 4)
                .modelForState().modelFile(quarter_vertical_layer_4).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.NORTH).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.RIGHT).with(VerticalQuarterLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.NORTH).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.LEFT).with(VerticalQuarterLayerBlock.LAYERS, 1)
                .modelForState().modelFile(quarter_vertical_layer_1).rotationY(270).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.NORTH).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.LEFT).with(VerticalQuarterLayerBlock.LAYERS, 2)
                .modelForState().modelFile(quarter_vertical_layer_2).rotationY(270).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.NORTH).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.LEFT).with(VerticalQuarterLayerBlock.LAYERS, 3)
                .modelForState().modelFile(quarter_vertical_layer_3).rotationY(270).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.NORTH).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.LEFT).with(VerticalQuarterLayerBlock.LAYERS, 4)
                .modelForState().modelFile(quarter_vertical_layer_4).rotationY(270).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.NORTH).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.LEFT).with(VerticalQuarterLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(270).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.EAST).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.RIGHT).with(VerticalQuarterLayerBlock.LAYERS, 1)
                .modelForState().modelFile(quarter_vertical_layer_1).rotationY(90).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.EAST).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.RIGHT).with(VerticalQuarterLayerBlock.LAYERS, 2)
                .modelForState().modelFile(quarter_vertical_layer_2).rotationY(90).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.EAST).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.RIGHT).with(VerticalQuarterLayerBlock.LAYERS, 3)
                .modelForState().modelFile(quarter_vertical_layer_3).rotationY(90).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.EAST).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.RIGHT).with(VerticalQuarterLayerBlock.LAYERS, 4)
                .modelForState().modelFile(quarter_vertical_layer_4).rotationY(90).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.EAST).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.RIGHT).with(VerticalQuarterLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).rotationY(90).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.EAST).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.LEFT).with(VerticalQuarterLayerBlock.LAYERS, 1)
                .modelForState().modelFile(quarter_vertical_layer_1).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.EAST).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.LEFT).with(VerticalQuarterLayerBlock.LAYERS, 2)
                .modelForState().modelFile(quarter_vertical_layer_2).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.EAST).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.LEFT).with(VerticalQuarterLayerBlock.LAYERS, 3)
                .modelForState().modelFile(quarter_vertical_layer_3).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.EAST).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.LEFT).with(VerticalQuarterLayerBlock.LAYERS, 4)
                .modelForState().modelFile(quarter_vertical_layer_4).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.EAST).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.LEFT).with(VerticalQuarterLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.SOUTH).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.RIGHT).with(VerticalQuarterLayerBlock.LAYERS, 1)
                .modelForState().modelFile(quarter_vertical_layer_1).rotationY(180).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.SOUTH).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.RIGHT).with(VerticalQuarterLayerBlock.LAYERS, 2)
                .modelForState().modelFile(quarter_vertical_layer_2).rotationY(180).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.SOUTH).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.RIGHT).with(VerticalQuarterLayerBlock.LAYERS, 3)
                .modelForState().modelFile(quarter_vertical_layer_3).rotationY(180).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.SOUTH).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.RIGHT).with(VerticalQuarterLayerBlock.LAYERS, 4)
                .modelForState().modelFile(quarter_vertical_layer_4).rotationY(180).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.SOUTH).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.RIGHT).with(VerticalQuarterLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.SOUTH).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.LEFT).with(VerticalQuarterLayerBlock.LAYERS, 1)
                .modelForState().modelFile(quarter_vertical_layer_1).rotationY(90).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.SOUTH).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.LEFT).with(VerticalQuarterLayerBlock.LAYERS, 2)
                .modelForState().modelFile(quarter_vertical_layer_2).rotationY(90).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.SOUTH).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.LEFT).with(VerticalQuarterLayerBlock.LAYERS, 3)
                .modelForState().modelFile(quarter_vertical_layer_3).rotationY(90).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.SOUTH).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.LEFT).with(VerticalQuarterLayerBlock.LAYERS, 4)
                .modelForState().modelFile(quarter_vertical_layer_4).rotationY(90).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.SOUTH).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.LEFT).with(VerticalQuarterLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.WEST).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.RIGHT).with(VerticalQuarterLayerBlock.LAYERS, 1)
                .modelForState().modelFile(quarter_vertical_layer_1).rotationY(270).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.WEST).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.RIGHT).with(VerticalQuarterLayerBlock.LAYERS, 2)
                .modelForState().modelFile(quarter_vertical_layer_2).rotationY(270).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.WEST).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.RIGHT).with(VerticalQuarterLayerBlock.LAYERS, 3)
                .modelForState().modelFile(quarter_vertical_layer_3).rotationY(270).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.WEST).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.RIGHT).with(VerticalQuarterLayerBlock.LAYERS, 4)
                .modelForState().modelFile(quarter_vertical_layer_4).rotationY(270).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.WEST).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.RIGHT).with(VerticalQuarterLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.WEST).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.LEFT).with(VerticalQuarterLayerBlock.LAYERS, 1)
                .modelForState().modelFile(quarter_vertical_layer_1).rotationY(180).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.WEST).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.LEFT).with(VerticalQuarterLayerBlock.LAYERS, 2)
                .modelForState().modelFile(quarter_vertical_layer_2).rotationY(180).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.WEST).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.LEFT).with(VerticalQuarterLayerBlock.LAYERS, 3)
                .modelForState().modelFile(quarter_vertical_layer_3).rotationY(180).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.WEST).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.LEFT).with(VerticalQuarterLayerBlock.LAYERS, 4)
                .modelForState().modelFile(quarter_vertical_layer_4).rotationY(180).addModel()
                .partialState().with(VerticalQuarterLayerBlock.FACING, Direction.WEST).with(VerticalQuarterLayerBlock.TYPE, LeftRightShape.LEFT).with(VerticalQuarterLayerBlock.LAYERS, 5)
                .modelForState().modelFile(full_block).addModel();
    }

    public void saplingBlock(RegistryObject<Block> blockRegistryObject) {
        simpleBlock(blockRegistryObject.get(),
                models().cross(ForgeRegistries.BLOCKS.getKey(blockRegistryObject.get()).getPath(), blockTexture(blockRegistryObject.get())).renderType("cutout"));
    }

    public void slabLayerBlock(SlabLayerBlock block) {
        slabLayerBlock(block, blockTexture(block));
    }

    public void slabLayerBlock(SlabLayerBlock block, ResourceLocation texture) {
        slabLayerBlock(block, texture, texture, texture, texture);
    }

    public void slabLayerBlock(SlabLayerBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation baseBlock) {
        ModelFile slab_layer_1 = models().withExistingParent(name(block) + "_1", modLoc("block/templates/slab_layer_1"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                slab_layer_2 = models().withExistingParent(name(block) + "_2", modLoc("block/templates/slab_layer_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                slab_layer_3 = models().withExistingParent(name(block) + "_3", modLoc("block/templates/slab_layer_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                slab_layer_4 = models().withExistingParent(name(block) + "_4", modLoc("block/templates/slab_layer_4"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                slab_layer_5 = models().withExistingParent(name(block) + "_5", modLoc("block/templates/slab_layer_5"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                slab_layer_6 = models().withExistingParent(name(block) + "_6", modLoc("block/templates/slab_layer_6"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                slab_layer_7 = models().withExistingParent(name(block) + "_7", modLoc("block/templates/slab_layer_7"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                full_block = this.models().getExistingFile(baseBlock);


        getVariantBuilder(block)
                .partialState().with(SlabLayerBlock.TYPE, TopBottomShape.BOTTOM).with(SlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(slab_layer_1).addModel()
                .partialState().with(SlabLayerBlock.TYPE, TopBottomShape.BOTTOM).with(SlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(slab_layer_2).addModel()
                .partialState().with(SlabLayerBlock.TYPE, TopBottomShape.BOTTOM).with(SlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(slab_layer_3).addModel()
                .partialState().with(SlabLayerBlock.TYPE, TopBottomShape.BOTTOM).with(SlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(slab_layer_4).addModel()
                .partialState().with(SlabLayerBlock.TYPE, TopBottomShape.BOTTOM).with(SlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(slab_layer_5).addModel()
                .partialState().with(SlabLayerBlock.TYPE, TopBottomShape.BOTTOM).with(SlabLayerBlock.LAYERS, 6)
                .modelForState().modelFile(slab_layer_6).addModel()
                .partialState().with(SlabLayerBlock.TYPE, TopBottomShape.BOTTOM).with(SlabLayerBlock.LAYERS, 7)
                .modelForState().modelFile(slab_layer_7).addModel()
                .partialState().with(SlabLayerBlock.TYPE, TopBottomShape.BOTTOM).with(SlabLayerBlock.LAYERS, 8)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(SlabLayerBlock.TYPE, TopBottomShape.TOP).with(SlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(slab_layer_1).rotationX(180).uvLock(true).addModel()
                .partialState().with(SlabLayerBlock.TYPE, TopBottomShape.TOP).with(SlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(slab_layer_2).rotationX(180).uvLock(true).addModel()
                .partialState().with(SlabLayerBlock.TYPE, TopBottomShape.TOP).with(SlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(slab_layer_3).rotationX(180).uvLock(true).addModel()
                .partialState().with(SlabLayerBlock.TYPE, TopBottomShape.TOP).with(SlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(slab_layer_4).rotationX(180).uvLock(true).addModel()
                .partialState().with(SlabLayerBlock.TYPE, TopBottomShape.TOP).with(SlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(slab_layer_5).rotationX(180).uvLock(true).addModel()
                .partialState().with(SlabLayerBlock.TYPE, TopBottomShape.TOP).with(SlabLayerBlock.LAYERS, 6)
                .modelForState().modelFile(slab_layer_6).rotationX(180).uvLock(true).addModel()
                .partialState().with(SlabLayerBlock.TYPE, TopBottomShape.TOP).with(SlabLayerBlock.LAYERS, 7)
                .modelForState().modelFile(slab_layer_7).rotationX(180).uvLock(true).addModel()
                .partialState().with(SlabLayerBlock.TYPE, TopBottomShape.TOP).with(SlabLayerBlock.LAYERS, 8)
                .modelForState().modelFile(full_block).addModel();
    }

    public void slabVerticalLayerBlock(VerticalSlabLayerBlock block) {
        slabVerticalLayerBlock(block, blockTexture(block));
    }

    public void slabVerticalLayerBlock(VerticalSlabLayerBlock block, ResourceLocation texture) {
        slabVerticalLayerBlock(block, texture, texture, texture, texture);
    }

    public void slabVerticalLayerBlock(VerticalSlabLayerBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation baseBlock) {
        ModelFile slab_vertical_layer_1 = models().withExistingParent(name(block) + "_1", modLoc("block/templates/slab_vertical_layer_1"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                slab_vertical_layer_2 = models().withExistingParent(name(block) + "_2", modLoc("block/templates/slab_vertical_layer_2"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                slab_vertical_layer_3 = models().withExistingParent(name(block) + "_3", modLoc("block/templates/slab_vertical_layer_3"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                slab_vertical_layer_4 = models().withExistingParent(name(block) + "_4", modLoc("block/templates/slab_vertical_layer_4"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                slab_vertical_layer_5 = models().withExistingParent(name(block) + "_5", modLoc("block/templates/slab_vertical_layer_5"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                slab_vertical_layer_6 = models().withExistingParent(name(block) + "_6", modLoc("block/templates/slab_vertical_layer_6"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                slab_vertical_layer_7 = models().withExistingParent(name(block) + "_7", modLoc("block/templates/slab_vertical_layer_7"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                full_block = this.models().getExistingFile(baseBlock);

        getVariantBuilder(block)
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.NORTH).with(VerticalSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(slab_vertical_layer_1).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.NORTH).with(VerticalSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(slab_vertical_layer_2).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.NORTH).with(VerticalSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(slab_vertical_layer_3).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.NORTH).with(VerticalSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(slab_vertical_layer_4).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.NORTH).with(VerticalSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(slab_vertical_layer_5).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.NORTH).with(VerticalSlabLayerBlock.LAYERS, 6)
                .modelForState().modelFile(slab_vertical_layer_6).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.NORTH).with(VerticalSlabLayerBlock.LAYERS, 7)
                .modelForState().modelFile(slab_vertical_layer_7).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.NORTH).with(VerticalSlabLayerBlock.LAYERS, 8)
                .modelForState().modelFile(full_block).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.EAST).with(VerticalSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(slab_vertical_layer_1).rotationY(90).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.EAST).with(VerticalSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(slab_vertical_layer_2).rotationY(90).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.EAST).with(VerticalSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(slab_vertical_layer_3).rotationY(90).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.EAST).with(VerticalSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(slab_vertical_layer_4).rotationY(90).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.EAST).with(VerticalSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(slab_vertical_layer_5).rotationY(90).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.EAST).with(VerticalSlabLayerBlock.LAYERS, 6)
                .modelForState().modelFile(slab_vertical_layer_6).rotationY(90).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.EAST).with(VerticalSlabLayerBlock.LAYERS, 7)
                .modelForState().modelFile(slab_vertical_layer_7).rotationY(90).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.EAST).with(VerticalSlabLayerBlock.LAYERS, 8)
                .modelForState().modelFile(full_block).rotationY(90).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(slab_vertical_layer_1).rotationY(180).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(slab_vertical_layer_2).rotationY(180).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(slab_vertical_layer_3).rotationY(180).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(slab_vertical_layer_4).rotationY(180).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(slab_vertical_layer_5).rotationY(180).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalSlabLayerBlock.LAYERS, 6)
                .modelForState().modelFile(slab_vertical_layer_6).rotationY(180).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalSlabLayerBlock.LAYERS, 7)
                .modelForState().modelFile(slab_vertical_layer_7).rotationY(180).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.SOUTH).with(VerticalSlabLayerBlock.LAYERS, 8)
                .modelForState().modelFile(full_block).rotationY(180).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.WEST).with(VerticalSlabLayerBlock.LAYERS, 1)
                .modelForState().modelFile(slab_vertical_layer_1).rotationY(270).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.WEST).with(VerticalSlabLayerBlock.LAYERS, 2)
                .modelForState().modelFile(slab_vertical_layer_2).rotationY(270).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.WEST).with(VerticalSlabLayerBlock.LAYERS, 3)
                .modelForState().modelFile(slab_vertical_layer_3).rotationY(270).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.WEST).with(VerticalSlabLayerBlock.LAYERS, 4)
                .modelForState().modelFile(slab_vertical_layer_4).rotationY(270).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.WEST).with(VerticalSlabLayerBlock.LAYERS, 5)
                .modelForState().modelFile(slab_vertical_layer_5).rotationY(270).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.WEST).with(VerticalSlabLayerBlock.LAYERS, 6)
                .modelForState().modelFile(slab_vertical_layer_6).rotationY(270).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.WEST).with(VerticalSlabLayerBlock.LAYERS, 7)
                .modelForState().modelFile(slab_vertical_layer_7).rotationY(270).uvLock(true).addModel()
                .partialState().with(VerticalSlabLayerBlock.FACING, Direction.WEST).with(VerticalSlabLayerBlock.LAYERS, 8)
                .modelForState().modelFile(full_block).addModel();
    }

    public void slabVerticalBlock(VerticalSlabBlock block) {
        slabVerticalBlock(block, blockTexture(block));
    }

    public void slabVerticalBlock(VerticalSlabBlock block, ResourceLocation texture) {
        slabVerticalBlock(block, texture, texture, texture, texture);
    }

    public void slabVerticalBlock(VerticalSlabBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation baseBlock) {
        ModelFile slab_vertical = models().withExistingParent(name(block) + "_1", modLoc("block/templates/slab_vertical"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                full_block = this.models().getExistingFile(baseBlock);
        ;

        getVariantBuilder(block)
                .partialState().with(VerticalSlabBlock.FACING, Direction.NORTH).with(VerticalSlabBlock.WATERLOGGED, false)
                .modelForState().modelFile(slab_vertical).addModel()
                .partialState().with(VerticalSlabBlock.FACING, Direction.EAST).with(VerticalSlabBlock.WATERLOGGED, false)
                .modelForState().modelFile(slab_vertical).addModel()
                .partialState().with(VerticalSlabBlock.FACING, Direction.SOUTH).with(VerticalSlabBlock.WATERLOGGED, false)
                .modelForState().modelFile(slab_vertical).addModel()
                .partialState().with(VerticalSlabBlock.FACING, Direction.WEST).with(VerticalSlabBlock.WATERLOGGED, false)
                .modelForState().modelFile(slab_vertical).addModel()
                .partialState().with(VerticalSlabBlock.FACING, Direction.NORTH).with(VerticalSlabBlock.WATERLOGGED, true)
                .modelForState().modelFile(slab_vertical).addModel()
                .partialState().with(VerticalSlabBlock.FACING, Direction.EAST).with(VerticalSlabBlock.WATERLOGGED, true)
                .modelForState().modelFile(slab_vertical).addModel()
                .partialState().with(VerticalSlabBlock.FACING, Direction.SOUTH).with(VerticalSlabBlock.WATERLOGGED, true)
                .modelForState().modelFile(slab_vertical).addModel()
                .partialState().with(VerticalSlabBlock.FACING, Direction.WEST).with(VerticalSlabBlock.WATERLOGGED, true)
                .modelForState().modelFile(slab_vertical).addModel();
    }

    public void tallDoorBlock(TallDoorBlock block) {
        tallDoorBlock(block, blockTexture(block));
    }

    public void tallDoorBlock(TallDoorBlock block, ResourceLocation texture) {
        tallDoorBlock(block, texture, texture, texture);
    }

    public void tallDoorBlock(TallDoorBlock block, ResourceLocation bottom, ResourceLocation middle, ResourceLocation top) {
        ModelFile tall_door_bottom = models().withExistingParent(name(block) + "_bottom", modLoc("block/templates/door_bottom"))
                .texture("bottom", bottom),
                tall_door_bottom_hinge = models().withExistingParent(name(block) + "_bottom_hinge", modLoc("block/templates/door_bottom_rh"))
                        .texture("bottom", bottom),
                tall_door_middle = models().withExistingParent(name(block) + "_middle", modLoc("block/templates/door_middle"))
                        .texture("middle", middle),
                tall_door_middle_hinge = models().withExistingParent(name(block) + "_middle_hinge", modLoc("block/templates/door_middle_rh"))
                        .texture("middle", middle),
                tall_door_top = models().withExistingParent(name(block) + "_top", modLoc("block/templates/door_top"))
                        .texture("top", top),
                tall_door_top_hinge = models().withExistingParent(name(block) + "_top_hinge", modLoc("block/templates/door_top_rh"))
                        .texture("top", top);

        getVariantBuilder(block)
                .partialState().with(TallDoorBlock.FACING, Direction.NORTH).with(TallDoorBlock.THIRD, TallDoorPart.BOTTOM).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_bottom).rotationY(270).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.NORTH).with(TallDoorBlock.THIRD, TallDoorPart.BOTTOM).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_bottom_hinge).rotationY(270).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.NORTH).with(TallDoorBlock.THIRD, TallDoorPart.BOTTOM).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_bottom_hinge).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.NORTH).with(TallDoorBlock.THIRD, TallDoorPart.BOTTOM).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_bottom).rotationY(180).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.NORTH).with(TallDoorBlock.THIRD, TallDoorPart.MIDDLE).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_middle).rotationY(270).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.NORTH).with(TallDoorBlock.THIRD, TallDoorPart.MIDDLE).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_middle_hinge).rotationY(270).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.NORTH).with(TallDoorBlock.THIRD, TallDoorPart.MIDDLE).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_middle_hinge).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.NORTH).with(TallDoorBlock.THIRD, TallDoorPart.MIDDLE).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_middle).rotationY(180).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.NORTH).with(TallDoorBlock.THIRD, TallDoorPart.TOP).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_top).rotationY(270).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.NORTH).with(TallDoorBlock.THIRD, TallDoorPart.TOP).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_top_hinge).rotationY(270).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.NORTH).with(TallDoorBlock.THIRD, TallDoorPart.TOP).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_top_hinge).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.NORTH).with(TallDoorBlock.THIRD, TallDoorPart.TOP).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_top).rotationY(180).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.EAST).with(TallDoorBlock.THIRD, TallDoorPart.BOTTOM).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_bottom).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.EAST).with(TallDoorBlock.THIRD, TallDoorPart.BOTTOM).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_bottom_hinge).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.EAST).with(TallDoorBlock.THIRD, TallDoorPart.BOTTOM).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_bottom_hinge).rotationY(90).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.EAST).with(TallDoorBlock.THIRD, TallDoorPart.BOTTOM).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_bottom).rotationY(270).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.EAST).with(TallDoorBlock.THIRD, TallDoorPart.MIDDLE).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_middle).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.EAST).with(TallDoorBlock.THIRD, TallDoorPart.MIDDLE).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_middle_hinge).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.EAST).with(TallDoorBlock.THIRD, TallDoorPart.MIDDLE).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_middle_hinge).rotationY(90).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.EAST).with(TallDoorBlock.THIRD, TallDoorPart.MIDDLE).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_middle).rotationY(270).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.EAST).with(TallDoorBlock.THIRD, TallDoorPart.TOP).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_top).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.EAST).with(TallDoorBlock.THIRD, TallDoorPart.TOP).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_top_hinge).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.EAST).with(TallDoorBlock.THIRD, TallDoorPart.TOP).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_top_hinge).rotationY(90).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.EAST).with(TallDoorBlock.THIRD, TallDoorPart.TOP).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_top).rotationY(270).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.SOUTH).with(TallDoorBlock.THIRD, TallDoorPart.BOTTOM).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_bottom).rotationY(90).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.SOUTH).with(TallDoorBlock.THIRD, TallDoorPart.BOTTOM).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_bottom_hinge).rotationY(90).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.SOUTH).with(TallDoorBlock.THIRD, TallDoorPart.BOTTOM).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_bottom_hinge).rotationY(180).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.SOUTH).with(TallDoorBlock.THIRD, TallDoorPart.BOTTOM).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_bottom).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.SOUTH).with(TallDoorBlock.THIRD, TallDoorPart.MIDDLE).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_middle).rotationY(90).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.SOUTH).with(TallDoorBlock.THIRD, TallDoorPart.MIDDLE).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_middle_hinge).rotationY(90).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.SOUTH).with(TallDoorBlock.THIRD, TallDoorPart.MIDDLE).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_middle_hinge).rotationY(180).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.SOUTH).with(TallDoorBlock.THIRD, TallDoorPart.MIDDLE).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_middle).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.SOUTH).with(TallDoorBlock.THIRD, TallDoorPart.TOP).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_top).rotationY(90).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.SOUTH).with(TallDoorBlock.THIRD, TallDoorPart.TOP).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_top_hinge).rotationY(90).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.SOUTH).with(TallDoorBlock.THIRD, TallDoorPart.TOP).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_top_hinge).rotationY(180).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.SOUTH).with(TallDoorBlock.THIRD, TallDoorPart.TOP).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_top).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.WEST).with(TallDoorBlock.THIRD, TallDoorPart.BOTTOM).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_bottom).rotationY(180).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.WEST).with(TallDoorBlock.THIRD, TallDoorPart.BOTTOM).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_bottom_hinge).rotationY(180).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.WEST).with(TallDoorBlock.THIRD, TallDoorPart.BOTTOM).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_bottom_hinge).rotationY(270).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.WEST).with(TallDoorBlock.THIRD, TallDoorPart.BOTTOM).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_bottom).rotationY(90).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.WEST).with(TallDoorBlock.THIRD, TallDoorPart.MIDDLE).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_middle).rotationY(180).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.WEST).with(TallDoorBlock.THIRD, TallDoorPart.MIDDLE).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_middle_hinge).rotationY(180).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.WEST).with(TallDoorBlock.THIRD, TallDoorPart.MIDDLE).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_middle_hinge).rotationY(270).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.WEST).with(TallDoorBlock.THIRD, TallDoorPart.MIDDLE).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_middle).rotationY(90).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.WEST).with(TallDoorBlock.THIRD, TallDoorPart.TOP).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_top).rotationY(180).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.WEST).with(TallDoorBlock.THIRD, TallDoorPart.TOP).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, false)
                .modelForState().modelFile(tall_door_top_hinge).rotationY(180).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.WEST).with(TallDoorBlock.THIRD, TallDoorPart.TOP).with(TallDoorBlock.HINGE, DoorHingeSide.LEFT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_top_hinge).rotationY(270).addModel()
                .partialState().with(TallDoorBlock.FACING, Direction.WEST).with(TallDoorBlock.THIRD, TallDoorPart.TOP).with(TallDoorBlock.HINGE, DoorHingeSide.RIGHT).with(TallDoorBlock.OPEN, true)
                .modelForState().modelFile(tall_door_top).rotationY(90).addModel();
    }

    public void windowBlock(WindowBlock block) {
        windowBlock(block, blockTexture(block));
    }

    public void windowBlock(WindowBlock block, ResourceLocation texture) {
        windowBlock(block, texture, texture, texture);
    }

    public void windowBlock(WindowBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile window = models().withExistingParent(name(block), modLoc("block/templates/window"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                window_arch = models().withExistingParent(name(block) + "_arch", modLoc("block/templates/window_arch"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                window_top = models().withExistingParent(name(block) + "_top", modLoc("block/templates/window_top"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                window_arch_top = models().withExistingParent(name(block) + "_arch_top", modLoc("block/templates/window_arch_top"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                window_middle = models().withExistingParent(name(block) + "_middle", modLoc("block/templates/window_middle"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                window_bottom = models().withExistingParent(name(block) + "_bottom", modLoc("block/templates/window_bottom"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);

        getVariantBuilder(block)
                .partialState().with(WindowBlock.FACING, Direction.NORTH).with(WindowBlock.TYPE, WindowShape.TOP).with(WindowBlock.ARCH, false)
                .modelForState().modelFile(window_top).addModel()
                .partialState().with(WindowBlock.FACING, Direction.NORTH).with(WindowBlock.TYPE, WindowShape.TOP).with(WindowBlock.ARCH, true)
                .modelForState().modelFile(window_arch_top).addModel()
                .partialState().with(WindowBlock.FACING, Direction.NORTH).with(WindowBlock.TYPE, WindowShape.MIDDLE).with(WindowBlock.ARCH, false)
                .modelForState().modelFile(window_middle).addModel()
                .partialState().with(WindowBlock.FACING, Direction.NORTH).with(WindowBlock.TYPE, WindowShape.MIDDLE).with(WindowBlock.ARCH, true)
                .modelForState().modelFile(window_middle).addModel()
                .partialState().with(WindowBlock.FACING, Direction.NORTH).with(WindowBlock.TYPE, WindowShape.BOTTOM).with(WindowBlock.ARCH, false)
                .modelForState().modelFile(window_bottom).addModel()
                .partialState().with(WindowBlock.FACING, Direction.NORTH).with(WindowBlock.TYPE, WindowShape.BOTTOM).with(WindowBlock.ARCH, true)
                .modelForState().modelFile(window_bottom).addModel()
                .partialState().with(WindowBlock.FACING, Direction.NORTH).with(WindowBlock.TYPE, WindowShape.FULL_BLOCK).with(WindowBlock.ARCH, false)
                .modelForState().modelFile(window).addModel()
                .partialState().with(WindowBlock.FACING, Direction.NORTH).with(WindowBlock.TYPE, WindowShape.FULL_BLOCK).with(WindowBlock.ARCH, true)
                .modelForState().modelFile(window_arch).addModel()
                .partialState().with(WindowBlock.FACING, Direction.EAST).with(WindowBlock.TYPE, WindowShape.TOP).with(WindowBlock.ARCH, false)
                .modelForState().modelFile(window_top).rotationY(90).addModel()
                .partialState().with(WindowBlock.FACING, Direction.EAST).with(WindowBlock.TYPE, WindowShape.TOP).with(WindowBlock.ARCH, true)
                .modelForState().modelFile(window_arch_top).rotationY(90).addModel()
                .partialState().with(WindowBlock.FACING, Direction.EAST).with(WindowBlock.TYPE, WindowShape.MIDDLE).with(WindowBlock.ARCH, false)
                .modelForState().modelFile(window_middle).rotationY(90).addModel()
                .partialState().with(WindowBlock.FACING, Direction.EAST).with(WindowBlock.TYPE, WindowShape.MIDDLE).with(WindowBlock.ARCH, true)
                .modelForState().modelFile(window_middle).rotationY(90).addModel()
                .partialState().with(WindowBlock.FACING, Direction.EAST).with(WindowBlock.TYPE, WindowShape.BOTTOM).with(WindowBlock.ARCH, false)
                .modelForState().modelFile(window_bottom).rotationY(90).addModel()
                .partialState().with(WindowBlock.FACING, Direction.EAST).with(WindowBlock.TYPE, WindowShape.BOTTOM).with(WindowBlock.ARCH, true)
                .modelForState().modelFile(window_bottom).rotationY(90).addModel()
                .partialState().with(WindowBlock.FACING, Direction.EAST).with(WindowBlock.TYPE, WindowShape.FULL_BLOCK).with(WindowBlock.ARCH, false)
                .modelForState().modelFile(window).rotationY(90).addModel()
                .partialState().with(WindowBlock.FACING, Direction.EAST).with(WindowBlock.TYPE, WindowShape.FULL_BLOCK).with(WindowBlock.ARCH, true)
                .modelForState().modelFile(window_arch).rotationY(90).addModel()
                .partialState().with(WindowBlock.FACING, Direction.SOUTH).with(WindowBlock.TYPE, WindowShape.TOP).with(WindowBlock.ARCH, false)
                .modelForState().modelFile(window_top).rotationY(180).addModel()
                .partialState().with(WindowBlock.FACING, Direction.SOUTH).with(WindowBlock.TYPE, WindowShape.TOP).with(WindowBlock.ARCH, true)
                .modelForState().modelFile(window_arch_top).rotationY(180).addModel()
                .partialState().with(WindowBlock.FACING, Direction.SOUTH).with(WindowBlock.TYPE, WindowShape.MIDDLE).with(WindowBlock.ARCH, false)
                .modelForState().modelFile(window_middle).rotationY(180).addModel()
                .partialState().with(WindowBlock.FACING, Direction.SOUTH).with(WindowBlock.TYPE, WindowShape.MIDDLE).with(WindowBlock.ARCH, true)
                .modelForState().modelFile(window_middle).rotationY(180).addModel()
                .partialState().with(WindowBlock.FACING, Direction.SOUTH).with(WindowBlock.TYPE, WindowShape.BOTTOM).with(WindowBlock.ARCH, false)
                .modelForState().modelFile(window_bottom).rotationY(180).addModel()
                .partialState().with(WindowBlock.FACING, Direction.SOUTH).with(WindowBlock.TYPE, WindowShape.BOTTOM).with(WindowBlock.ARCH, true)
                .modelForState().modelFile(window_bottom).rotationY(180).addModel()
                .partialState().with(WindowBlock.FACING, Direction.SOUTH).with(WindowBlock.TYPE, WindowShape.FULL_BLOCK).with(WindowBlock.ARCH, false)
                .modelForState().modelFile(window).rotationY(180).addModel()
                .partialState().with(WindowBlock.FACING, Direction.SOUTH).with(WindowBlock.TYPE, WindowShape.FULL_BLOCK).with(WindowBlock.ARCH, true)
                .modelForState().modelFile(window_arch).rotationY(180).addModel()
                .partialState().with(WindowBlock.FACING, Direction.WEST).with(WindowBlock.TYPE, WindowShape.TOP).with(WindowBlock.ARCH, false)
                .modelForState().modelFile(window_top).rotationY(270).addModel()
                .partialState().with(WindowBlock.FACING, Direction.WEST).with(WindowBlock.TYPE, WindowShape.TOP).with(WindowBlock.ARCH, true)
                .modelForState().modelFile(window_arch_top).rotationY(270).addModel()
                .partialState().with(WindowBlock.FACING, Direction.WEST).with(WindowBlock.TYPE, WindowShape.MIDDLE).with(WindowBlock.ARCH, false)
                .modelForState().modelFile(window_middle).rotationY(270).addModel()
                .partialState().with(WindowBlock.FACING, Direction.WEST).with(WindowBlock.TYPE, WindowShape.MIDDLE).with(WindowBlock.ARCH, true)
                .modelForState().modelFile(window_middle).rotationY(270).addModel()
                .partialState().with(WindowBlock.FACING, Direction.WEST).with(WindowBlock.TYPE, WindowShape.BOTTOM).with(WindowBlock.ARCH, false)
                .modelForState().modelFile(window_bottom).rotationY(270).addModel()
                .partialState().with(WindowBlock.FACING, Direction.WEST).with(WindowBlock.TYPE, WindowShape.BOTTOM).with(WindowBlock.ARCH, true)
                .modelForState().modelFile(window_bottom).rotationY(270).addModel()
                .partialState().with(WindowBlock.FACING, Direction.WEST).with(WindowBlock.TYPE, WindowShape.FULL_BLOCK).with(WindowBlock.ARCH, false)
                .modelForState().modelFile(window).rotationY(270).addModel()
                .partialState().with(WindowBlock.FACING, Direction.WEST).with(WindowBlock.TYPE, WindowShape.FULL_BLOCK).with(WindowBlock.ARCH, true)
                .modelForState().modelFile(window_arch).rotationY(270).addModel();
    }

    public void windowHalfBlock(HalfWindowBlock block) {
        windowHalfBlock(block, blockTexture(block));
    }

    public void windowHalfBlock(HalfWindowBlock block, ResourceLocation texture) {
        windowHalfBlock(block, texture, texture, texture);
    }

    public void windowHalfBlock(HalfWindowBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile window_half = models().withExistingParent(name(block), modLoc("block/templates/window_half"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                window_half_arch = models().withExistingParent(name(block) + "_arch", modLoc("block/templates/window_half_arch"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                window_half_top = models().withExistingParent(name(block) + "_trim", modLoc("block/templates/window_half_top"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                window_half_arch_top = models().withExistingParent(name(block) + "_arch_top", modLoc("block/templates/window_half_arch_top"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                window_half_middle = models().withExistingParent(name(block) + "_trim2", modLoc("block/templates/window_half_middle"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                window_half_bottom = models().withExistingParent(name(block) + "_cross", modLoc("block/templates/window_half_bottom"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);

        getVariantBuilder(block)
                .partialState().with(HalfWindowBlock.FACING, Direction.NORTH).with(HalfWindowBlock.TYPE, WindowShape.TOP).with(HalfWindowBlock.ARCH, false)
                .modelForState().modelFile(window_half_top).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.NORTH).with(HalfWindowBlock.TYPE, WindowShape.TOP).with(HalfWindowBlock.ARCH, true)
                .modelForState().modelFile(window_half_arch_top).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.NORTH).with(HalfWindowBlock.TYPE, WindowShape.MIDDLE).with(HalfWindowBlock.ARCH, false)
                .modelForState().modelFile(window_half_middle).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.NORTH).with(HalfWindowBlock.TYPE, WindowShape.MIDDLE).with(HalfWindowBlock.ARCH, true)
                .modelForState().modelFile(window_half_middle).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.NORTH).with(HalfWindowBlock.TYPE, WindowShape.BOTTOM).with(HalfWindowBlock.ARCH, false)
                .modelForState().modelFile(window_half_bottom).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.NORTH).with(HalfWindowBlock.TYPE, WindowShape.BOTTOM).with(HalfWindowBlock.ARCH, true)
                .modelForState().modelFile(window_half_bottom).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.NORTH).with(HalfWindowBlock.TYPE, WindowShape.FULL_BLOCK).with(HalfWindowBlock.ARCH, false)
                .modelForState().modelFile(window_half).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.NORTH).with(HalfWindowBlock.TYPE, WindowShape.FULL_BLOCK).with(HalfWindowBlock.ARCH, true)
                .modelForState().modelFile(window_half_arch).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.EAST).with(HalfWindowBlock.TYPE, WindowShape.TOP).with(HalfWindowBlock.ARCH, false)
                .modelForState().modelFile(window_half_top).rotationY(90).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.EAST).with(HalfWindowBlock.TYPE, WindowShape.TOP).with(HalfWindowBlock.ARCH, true)
                .modelForState().modelFile(window_half_arch_top).rotationY(90).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.EAST).with(HalfWindowBlock.TYPE, WindowShape.MIDDLE).with(HalfWindowBlock.ARCH, false)
                .modelForState().modelFile(window_half_middle).rotationY(90).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.EAST).with(HalfWindowBlock.TYPE, WindowShape.MIDDLE).with(HalfWindowBlock.ARCH, true)
                .modelForState().modelFile(window_half_middle).rotationY(90).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.EAST).with(HalfWindowBlock.TYPE, WindowShape.BOTTOM).with(HalfWindowBlock.ARCH, false)
                .modelForState().modelFile(window_half_bottom).rotationY(90).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.EAST).with(HalfWindowBlock.TYPE, WindowShape.BOTTOM).with(HalfWindowBlock.ARCH, true)
                .modelForState().modelFile(window_half_bottom).rotationY(90).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.EAST).with(HalfWindowBlock.TYPE, WindowShape.FULL_BLOCK).with(HalfWindowBlock.ARCH, false)
                .modelForState().modelFile(window_half).rotationY(90).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.EAST).with(HalfWindowBlock.TYPE, WindowShape.FULL_BLOCK).with(HalfWindowBlock.ARCH, true)
                .modelForState().modelFile(window_half_arch).rotationY(90).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.SOUTH).with(HalfWindowBlock.TYPE, WindowShape.TOP).with(HalfWindowBlock.ARCH, false)
                .modelForState().modelFile(window_half_top).rotationY(180).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.SOUTH).with(HalfWindowBlock.TYPE, WindowShape.TOP).with(HalfWindowBlock.ARCH, true)
                .modelForState().modelFile(window_half_arch_top).rotationY(180).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.SOUTH).with(HalfWindowBlock.TYPE, WindowShape.MIDDLE).with(HalfWindowBlock.ARCH, false)
                .modelForState().modelFile(window_half_middle).rotationY(180).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.SOUTH).with(HalfWindowBlock.TYPE, WindowShape.MIDDLE).with(HalfWindowBlock.ARCH, true)
                .modelForState().modelFile(window_half_middle).rotationY(180).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.SOUTH).with(HalfWindowBlock.TYPE, WindowShape.BOTTOM).with(HalfWindowBlock.ARCH, false)
                .modelForState().modelFile(window_half_bottom).rotationY(180).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.SOUTH).with(HalfWindowBlock.TYPE, WindowShape.BOTTOM).with(HalfWindowBlock.ARCH, true)
                .modelForState().modelFile(window_half_bottom).rotationY(180).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.SOUTH).with(HalfWindowBlock.TYPE, WindowShape.FULL_BLOCK).with(HalfWindowBlock.ARCH, false)
                .modelForState().modelFile(window_half).rotationY(180).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.SOUTH).with(HalfWindowBlock.TYPE, WindowShape.FULL_BLOCK).with(HalfWindowBlock.ARCH, true)
                .modelForState().modelFile(window_half_arch).rotationY(180).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.WEST).with(HalfWindowBlock.TYPE, WindowShape.TOP).with(HalfWindowBlock.ARCH, false)
                .modelForState().modelFile(window_half_top).rotationY(270).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.WEST).with(HalfWindowBlock.TYPE, WindowShape.TOP).with(HalfWindowBlock.ARCH, true)
                .modelForState().modelFile(window_half_arch_top).rotationY(270).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.WEST).with(HalfWindowBlock.TYPE, WindowShape.MIDDLE).with(HalfWindowBlock.ARCH, false)
                .modelForState().modelFile(window_half_middle).rotationY(270).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.WEST).with(HalfWindowBlock.TYPE, WindowShape.MIDDLE).with(HalfWindowBlock.ARCH, true)
                .modelForState().modelFile(window_half_middle).rotationY(270).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.WEST).with(HalfWindowBlock.TYPE, WindowShape.BOTTOM).with(HalfWindowBlock.ARCH, false)
                .modelForState().modelFile(window_half_bottom).rotationY(270).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.WEST).with(HalfWindowBlock.TYPE, WindowShape.BOTTOM).with(HalfWindowBlock.ARCH, true)
                .modelForState().modelFile(window_half_bottom).rotationY(270).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.WEST).with(HalfWindowBlock.TYPE, WindowShape.FULL_BLOCK).with(HalfWindowBlock.ARCH, false)
                .modelForState().modelFile(window_half).rotationY(270).addModel()
                .partialState().with(HalfWindowBlock.FACING, Direction.WEST).with(HalfWindowBlock.TYPE, WindowShape.FULL_BLOCK).with(HalfWindowBlock.ARCH, true)
                .modelForState().modelFile(window_half_arch).rotationY(270).addModel();
    }

    public void roof22Block(Roof22Block block) {
        roof22Block(block, blockTexture(block));
    }

    public void roof22Block(Roof22Block block, ResourceLocation texture) {
        roof22Block(block, texture, texture, texture);
    }

    public void roof22Block(Roof22Block block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile roof_22 = models().withExistingParent(name(block), modLoc("block/templates/roof_22"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                roof_22_outer = models().withExistingParent(name(block) + "_outer", modLoc("block/templates/roof_22_outer"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                roof_22_inner = models().withExistingParent(name(block) + "_inner", modLoc("block/templates/roof_22_inner"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                roof_22_top = models().withExistingParent(name(block) + "_top", modLoc("block/templates/roof_22_top"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                roof_22_top_outer = models().withExistingParent(name(block) + "_top_outer", modLoc("block/templates/roof_22_top_outer"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                roof_22_top_inner = models().withExistingParent(name(block) + "_top_inner", modLoc("block/templates/roof_22_top_inner"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);

        getVariantBuilder(block)
                .partialState().with(Roof22Block.FACING, Direction.NORTH).with(Roof22Block.TYPE, RoofShape.STRAIGHT).with(Roof22Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_22_top).rotationY(270).addModel()
                .partialState().with(Roof22Block.FACING, Direction.NORTH).with(Roof22Block.TYPE, RoofShape.OUTER_LEFT).with(Roof22Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_22_top_outer).rotationY(180).addModel()
                .partialState().with(Roof22Block.FACING, Direction.NORTH).with(Roof22Block.TYPE, RoofShape.OUTER_RIGHT).with(Roof22Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_22_top_outer).rotationY(270).addModel()
                .partialState().with(Roof22Block.FACING, Direction.NORTH).with(Roof22Block.TYPE, RoofShape.INNER_LEFT).with(Roof22Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_22_top_inner).rotationY(180).addModel()
                .partialState().with(Roof22Block.FACING, Direction.NORTH).with(Roof22Block.TYPE, RoofShape.INNER_RIGHT).with(Roof22Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_22_top_inner).rotationY(270).addModel()
                .partialState().with(Roof22Block.FACING, Direction.NORTH).with(Roof22Block.TYPE, RoofShape.STRAIGHT).with(Roof22Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_22).rotationY(270).addModel()
                .partialState().with(Roof22Block.FACING, Direction.NORTH).with(Roof22Block.TYPE, RoofShape.OUTER_LEFT).with(Roof22Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_22_outer).rotationY(180).addModel()
                .partialState().with(Roof22Block.FACING, Direction.NORTH).with(Roof22Block.TYPE, RoofShape.OUTER_RIGHT).with(Roof22Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_22_outer).rotationY(270).addModel()
                .partialState().with(Roof22Block.FACING, Direction.NORTH).with(Roof22Block.TYPE, RoofShape.INNER_LEFT).with(Roof22Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_22_inner).rotationY(180).addModel()
                .partialState().with(Roof22Block.FACING, Direction.NORTH).with(Roof22Block.TYPE, RoofShape.INNER_RIGHT).with(Roof22Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_22_inner).rotationY(270).addModel()
                .partialState().with(Roof22Block.FACING, Direction.EAST).with(Roof22Block.TYPE, RoofShape.STRAIGHT).with(Roof22Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_22_top).addModel()
                .partialState().with(Roof22Block.FACING, Direction.EAST).with(Roof22Block.TYPE, RoofShape.OUTER_LEFT).with(Roof22Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_22_top_outer).rotationY(270).addModel()
                .partialState().with(Roof22Block.FACING, Direction.EAST).with(Roof22Block.TYPE, RoofShape.OUTER_RIGHT).with(Roof22Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_22_top_outer).addModel()
                .partialState().with(Roof22Block.FACING, Direction.EAST).with(Roof22Block.TYPE, RoofShape.INNER_LEFT).with(Roof22Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_22_top_inner).rotationY(270).addModel()
                .partialState().with(Roof22Block.FACING, Direction.EAST).with(Roof22Block.TYPE, RoofShape.INNER_RIGHT).with(Roof22Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_22_top_inner).addModel()
                .partialState().with(Roof22Block.FACING, Direction.EAST).with(Roof22Block.TYPE, RoofShape.STRAIGHT).with(Roof22Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_22).addModel()
                .partialState().with(Roof22Block.FACING, Direction.EAST).with(Roof22Block.TYPE, RoofShape.OUTER_LEFT).with(Roof22Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_22_outer).rotationY(270).addModel()
                .partialState().with(Roof22Block.FACING, Direction.EAST).with(Roof22Block.TYPE, RoofShape.OUTER_RIGHT).with(Roof22Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_22_outer).addModel()
                .partialState().with(Roof22Block.FACING, Direction.EAST).with(Roof22Block.TYPE, RoofShape.INNER_LEFT).with(Roof22Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_22_inner).rotationY(270).addModel()
                .partialState().with(Roof22Block.FACING, Direction.EAST).with(Roof22Block.TYPE, RoofShape.INNER_RIGHT).with(Roof22Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_22_inner).addModel()
                .partialState().with(Roof22Block.FACING, Direction.SOUTH).with(Roof22Block.TYPE, RoofShape.STRAIGHT).with(Roof22Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_22_top).rotationY(90).addModel()
                .partialState().with(Roof22Block.FACING, Direction.SOUTH).with(Roof22Block.TYPE, RoofShape.OUTER_LEFT).with(Roof22Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_22_top_outer).addModel()
                .partialState().with(Roof22Block.FACING, Direction.SOUTH).with(Roof22Block.TYPE, RoofShape.OUTER_RIGHT).with(Roof22Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_22_top_outer).rotationY(90).addModel()
                .partialState().with(Roof22Block.FACING, Direction.SOUTH).with(Roof22Block.TYPE, RoofShape.INNER_LEFT).with(Roof22Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_22_top_inner).addModel()
                .partialState().with(Roof22Block.FACING, Direction.SOUTH).with(Roof22Block.TYPE, RoofShape.INNER_RIGHT).with(Roof22Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_22_top_inner).rotationY(90).addModel()
                .partialState().with(Roof22Block.FACING, Direction.SOUTH).with(Roof22Block.TYPE, RoofShape.STRAIGHT).with(Roof22Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_22).rotationY(90).addModel()
                .partialState().with(Roof22Block.FACING, Direction.SOUTH).with(Roof22Block.TYPE, RoofShape.OUTER_LEFT).with(Roof22Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_22_outer).addModel()
                .partialState().with(Roof22Block.FACING, Direction.SOUTH).with(Roof22Block.TYPE, RoofShape.OUTER_RIGHT).with(Roof22Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_22_outer).rotationY(90).addModel()
                .partialState().with(Roof22Block.FACING, Direction.SOUTH).with(Roof22Block.TYPE, RoofShape.INNER_LEFT).with(Roof22Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_22_inner).addModel()
                .partialState().with(Roof22Block.FACING, Direction.SOUTH).with(Roof22Block.TYPE, RoofShape.INNER_RIGHT).with(Roof22Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_22_inner).rotationY(90).addModel()
                .partialState().with(Roof22Block.FACING, Direction.WEST).with(Roof22Block.TYPE, RoofShape.STRAIGHT).with(Roof22Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_22_top).rotationY(180).addModel()
                .partialState().with(Roof22Block.FACING, Direction.WEST).with(Roof22Block.TYPE, RoofShape.OUTER_LEFT).with(Roof22Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_22_top_outer).rotationY(90).addModel()
                .partialState().with(Roof22Block.FACING, Direction.WEST).with(Roof22Block.TYPE, RoofShape.OUTER_RIGHT).with(Roof22Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_22_top_outer).rotationY(180).addModel()
                .partialState().with(Roof22Block.FACING, Direction.WEST).with(Roof22Block.TYPE, RoofShape.INNER_LEFT).with(Roof22Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_22_top_inner).rotationY(90).addModel()
                .partialState().with(Roof22Block.FACING, Direction.WEST).with(Roof22Block.TYPE, RoofShape.INNER_RIGHT).with(Roof22Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_22_top_inner).rotationY(180).addModel()
                .partialState().with(Roof22Block.FACING, Direction.WEST).with(Roof22Block.TYPE, RoofShape.STRAIGHT).with(Roof22Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_22).rotationY(180).addModel()
                .partialState().with(Roof22Block.FACING, Direction.WEST).with(Roof22Block.TYPE, RoofShape.OUTER_LEFT).with(Roof22Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_22_outer).rotationY(90).addModel()
                .partialState().with(Roof22Block.FACING, Direction.WEST).with(Roof22Block.TYPE, RoofShape.OUTER_RIGHT).with(Roof22Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_22_outer).rotationY(180).addModel()
                .partialState().with(Roof22Block.FACING, Direction.WEST).with(Roof22Block.TYPE, RoofShape.INNER_LEFT).with(Roof22Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_22_inner).rotationY(90).addModel()
                .partialState().with(Roof22Block.FACING, Direction.WEST).with(Roof22Block.TYPE, RoofShape.INNER_RIGHT).with(Roof22Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_22_inner).rotationY(180).addModel();
    }


    public void roof45Block(Roof45Block block) {
        roof45Block(block, blockTexture(block));
    }

    public void roof45Block(Roof45Block block, ResourceLocation texture) {
        roof45Block(block, texture, texture, texture);
    }

    public void roof45Block(Roof45Block block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile roof_45 = models().withExistingParent(name(block), modLoc("block/templates/roof_45"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                roof_45_outer = models().withExistingParent(name(block) + "_outer", modLoc("block/templates/roof_45_outer"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                roof_45_inner = models().withExistingParent(name(block) + "_inner", modLoc("block/templates/roof_45_inner"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);

        getVariantBuilder(block)
                .partialState().with(Roof45Block.FACING, Direction.NORTH).with(Roof45Block.TYPE, RoofShape.STRAIGHT)
                .modelForState().modelFile(roof_45).rotationY(270).addModel()
                .partialState().with(Roof45Block.FACING, Direction.NORTH).with(Roof45Block.TYPE, RoofShape.OUTER_LEFT)
                .modelForState().modelFile(roof_45_outer).rotationY(180).addModel()
                .partialState().with(Roof45Block.FACING, Direction.NORTH).with(Roof45Block.TYPE, RoofShape.OUTER_RIGHT)
                .modelForState().modelFile(roof_45_outer).rotationY(270).addModel()
                .partialState().with(Roof45Block.FACING, Direction.NORTH).with(Roof45Block.TYPE, RoofShape.INNER_LEFT)
                .modelForState().modelFile(roof_45_inner).rotationY(180).addModel()
                .partialState().with(Roof45Block.FACING, Direction.NORTH).with(Roof45Block.TYPE, RoofShape.INNER_RIGHT)
                .modelForState().modelFile(roof_45_inner).rotationY(270).addModel()
                .partialState().with(Roof45Block.FACING, Direction.EAST).with(Roof45Block.TYPE, RoofShape.STRAIGHT)
                .modelForState().modelFile(roof_45).addModel()
                .partialState().with(Roof45Block.FACING, Direction.EAST).with(Roof45Block.TYPE, RoofShape.OUTER_LEFT)
                .modelForState().modelFile(roof_45_outer).rotationY(270).addModel()
                .partialState().with(Roof45Block.FACING, Direction.EAST).with(Roof45Block.TYPE, RoofShape.OUTER_RIGHT)
                .modelForState().modelFile(roof_45_outer).addModel()
                .partialState().with(Roof45Block.FACING, Direction.EAST).with(Roof45Block.TYPE, RoofShape.INNER_LEFT)
                .modelForState().modelFile(roof_45_inner).rotationY(270).addModel()
                .partialState().with(Roof45Block.FACING, Direction.EAST).with(Roof45Block.TYPE, RoofShape.INNER_RIGHT)
                .modelForState().modelFile(roof_45_inner).addModel()
                .partialState().with(Roof45Block.FACING, Direction.SOUTH).with(Roof45Block.TYPE, RoofShape.STRAIGHT)
                .modelForState().modelFile(roof_45).rotationY(90).addModel()
                .partialState().with(Roof45Block.FACING, Direction.SOUTH).with(Roof45Block.TYPE, RoofShape.OUTER_LEFT)
                .modelForState().modelFile(roof_45_outer).addModel()
                .partialState().with(Roof45Block.FACING, Direction.SOUTH).with(Roof45Block.TYPE, RoofShape.OUTER_RIGHT)
                .modelForState().modelFile(roof_45_outer).rotationY(90).addModel()
                .partialState().with(Roof45Block.FACING, Direction.SOUTH).with(Roof45Block.TYPE, RoofShape.INNER_LEFT)
                .modelForState().modelFile(roof_45_inner).addModel()
                .partialState().with(Roof45Block.FACING, Direction.SOUTH).with(Roof45Block.TYPE, RoofShape.INNER_RIGHT)
                .modelForState().modelFile(roof_45_inner).rotationY(90).addModel()
                .partialState().with(Roof45Block.FACING, Direction.WEST).with(Roof45Block.TYPE, RoofShape.STRAIGHT)
                .modelForState().modelFile(roof_45).rotationY(180).addModel()
                .partialState().with(Roof45Block.FACING, Direction.WEST).with(Roof45Block.TYPE, RoofShape.OUTER_LEFT)
                .modelForState().modelFile(roof_45_outer).rotationY(90).addModel()
                .partialState().with(Roof45Block.FACING, Direction.WEST).with(Roof45Block.TYPE, RoofShape.OUTER_RIGHT)
                .modelForState().modelFile(roof_45_outer).rotationY(180).addModel()
                .partialState().with(Roof45Block.FACING, Direction.WEST).with(Roof45Block.TYPE, RoofShape.INNER_LEFT)
                .modelForState().modelFile(roof_45_inner).rotationY(90).addModel()
                .partialState().with(Roof45Block.FACING, Direction.WEST).with(Roof45Block.TYPE, RoofShape.INNER_RIGHT)
                .modelForState().modelFile(roof_45_inner).rotationY(180).addModel();
    }

    public void roof67Block(Roof67Block block) {
        roof67Block(block, blockTexture(block));
    }

    public void roof67Block(Roof67Block block, ResourceLocation texture) {
        roof67Block(block, texture, texture, texture);
    }

    public void roof67Block(Roof67Block block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile roof_67 = models().withExistingParent(name(block), modLoc("block/templates/roof_67"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                roof_67_outer = models().withExistingParent(name(block) + "_outer", modLoc("block/templates/roof_67_outer"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                roof_67_inner = models().withExistingParent(name(block) + "_inner", modLoc("block/templates/roof_67_inner"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                roof_67_top = models().withExistingParent(name(block) + "_top", modLoc("block/templates/roof_67_top"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                roof_67_top_outer = models().withExistingParent(name(block) + "_top_outer", modLoc("block/templates/roof_67_top_outer"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                roof_67_top_inner = models().withExistingParent(name(block) + "_top_inner", modLoc("block/templates/roof_67_top_inner"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                roof_67_inventory = models().withExistingParent(name(block) + "_inventory", modLoc("block/templates/inv/roof_67_top_inventory"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);

        getVariantBuilder(block)
                .partialState().with(Roof67Block.FACING, Direction.NORTH).with(Roof67Block.TYPE, RoofShape.STRAIGHT).with(Roof67Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_67_top).rotationY(270).addModel()
                .partialState().with(Roof67Block.FACING, Direction.NORTH).with(Roof67Block.TYPE, RoofShape.OUTER_LEFT).with(Roof67Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_67_top_outer).rotationY(180).addModel()
                .partialState().with(Roof67Block.FACING, Direction.NORTH).with(Roof67Block.TYPE, RoofShape.OUTER_RIGHT).with(Roof67Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_67_top_outer).rotationY(270).addModel()
                .partialState().with(Roof67Block.FACING, Direction.NORTH).with(Roof67Block.TYPE, RoofShape.INNER_LEFT).with(Roof67Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_67_top_inner).rotationY(180).addModel()
                .partialState().with(Roof67Block.FACING, Direction.NORTH).with(Roof67Block.TYPE, RoofShape.INNER_RIGHT).with(Roof67Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_67_top_inner).rotationY(270).addModel()
                .partialState().with(Roof67Block.FACING, Direction.NORTH).with(Roof67Block.TYPE, RoofShape.STRAIGHT).with(Roof67Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_67).rotationY(270).addModel()
                .partialState().with(Roof67Block.FACING, Direction.NORTH).with(Roof67Block.TYPE, RoofShape.OUTER_LEFT).with(Roof67Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_67_outer).rotationY(180).addModel()
                .partialState().with(Roof67Block.FACING, Direction.NORTH).with(Roof67Block.TYPE, RoofShape.OUTER_RIGHT).with(Roof67Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_67_outer).rotationY(270).addModel()
                .partialState().with(Roof67Block.FACING, Direction.NORTH).with(Roof67Block.TYPE, RoofShape.INNER_LEFT).with(Roof67Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_67_inner).rotationY(180).addModel()
                .partialState().with(Roof67Block.FACING, Direction.NORTH).with(Roof67Block.TYPE, RoofShape.INNER_RIGHT).with(Roof67Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_67_inner).rotationY(270).addModel()
                .partialState().with(Roof67Block.FACING, Direction.EAST).with(Roof67Block.TYPE, RoofShape.STRAIGHT).with(Roof67Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_67_top).addModel()
                .partialState().with(Roof67Block.FACING, Direction.EAST).with(Roof67Block.TYPE, RoofShape.OUTER_LEFT).with(Roof67Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_67_top_outer).rotationY(270).addModel()
                .partialState().with(Roof67Block.FACING, Direction.EAST).with(Roof67Block.TYPE, RoofShape.OUTER_RIGHT).with(Roof67Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_67_top_outer).addModel()
                .partialState().with(Roof67Block.FACING, Direction.EAST).with(Roof67Block.TYPE, RoofShape.INNER_LEFT).with(Roof67Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_67_top_inner).rotationY(270).addModel()
                .partialState().with(Roof67Block.FACING, Direction.EAST).with(Roof67Block.TYPE, RoofShape.INNER_RIGHT).with(Roof67Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_67_top_inner).addModel()
                .partialState().with(Roof67Block.FACING, Direction.EAST).with(Roof67Block.TYPE, RoofShape.STRAIGHT).with(Roof67Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_67).addModel()
                .partialState().with(Roof67Block.FACING, Direction.EAST).with(Roof67Block.TYPE, RoofShape.OUTER_LEFT).with(Roof67Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_67_outer).rotationY(270).addModel()
                .partialState().with(Roof67Block.FACING, Direction.EAST).with(Roof67Block.TYPE, RoofShape.OUTER_RIGHT).with(Roof67Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_67_outer).addModel()
                .partialState().with(Roof67Block.FACING, Direction.EAST).with(Roof67Block.TYPE, RoofShape.INNER_LEFT).with(Roof67Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_67_inner).rotationY(270).addModel()
                .partialState().with(Roof67Block.FACING, Direction.EAST).with(Roof67Block.TYPE, RoofShape.INNER_RIGHT).with(Roof67Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_67_inner).addModel()
                .partialState().with(Roof67Block.FACING, Direction.SOUTH).with(Roof67Block.TYPE, RoofShape.STRAIGHT).with(Roof67Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_67_top).rotationY(90).addModel()
                .partialState().with(Roof67Block.FACING, Direction.SOUTH).with(Roof67Block.TYPE, RoofShape.OUTER_LEFT).with(Roof67Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_67_top_outer).addModel()
                .partialState().with(Roof67Block.FACING, Direction.SOUTH).with(Roof67Block.TYPE, RoofShape.OUTER_RIGHT).with(Roof67Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_67_top_outer).rotationY(90).addModel()
                .partialState().with(Roof67Block.FACING, Direction.SOUTH).with(Roof67Block.TYPE, RoofShape.INNER_LEFT).with(Roof67Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_67_top_inner).addModel()
                .partialState().with(Roof67Block.FACING, Direction.SOUTH).with(Roof67Block.TYPE, RoofShape.INNER_RIGHT).with(Roof67Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_67_top_inner).rotationY(90).addModel()
                .partialState().with(Roof67Block.FACING, Direction.SOUTH).with(Roof67Block.TYPE, RoofShape.STRAIGHT).with(Roof67Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_67).rotationY(90).addModel()
                .partialState().with(Roof67Block.FACING, Direction.SOUTH).with(Roof67Block.TYPE, RoofShape.OUTER_LEFT).with(Roof67Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_67_outer).addModel()
                .partialState().with(Roof67Block.FACING, Direction.SOUTH).with(Roof67Block.TYPE, RoofShape.OUTER_RIGHT).with(Roof67Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_67_outer).rotationY(90).addModel()
                .partialState().with(Roof67Block.FACING, Direction.SOUTH).with(Roof67Block.TYPE, RoofShape.INNER_LEFT).with(Roof67Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_67_inner).addModel()
                .partialState().with(Roof67Block.FACING, Direction.SOUTH).with(Roof67Block.TYPE, RoofShape.INNER_RIGHT).with(Roof67Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_67_inner).rotationY(90).addModel()
                .partialState().with(Roof67Block.FACING, Direction.WEST).with(Roof67Block.TYPE, RoofShape.STRAIGHT).with(Roof67Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_67_top).rotationY(180).addModel()
                .partialState().with(Roof67Block.FACING, Direction.WEST).with(Roof67Block.TYPE, RoofShape.OUTER_LEFT).with(Roof67Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_67_top_outer).rotationY(90).addModel()
                .partialState().with(Roof67Block.FACING, Direction.WEST).with(Roof67Block.TYPE, RoofShape.OUTER_RIGHT).with(Roof67Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_67_top_outer).rotationY(180).addModel()
                .partialState().with(Roof67Block.FACING, Direction.WEST).with(Roof67Block.TYPE, RoofShape.INNER_LEFT).with(Roof67Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_67_top_inner).rotationY(90).addModel()
                .partialState().with(Roof67Block.FACING, Direction.WEST).with(Roof67Block.TYPE, RoofShape.INNER_RIGHT).with(Roof67Block.HALF, Half.TOP)
                .modelForState().modelFile(roof_67_top_inner).rotationY(180).addModel()
                .partialState().with(Roof67Block.FACING, Direction.WEST).with(Roof67Block.TYPE, RoofShape.STRAIGHT).with(Roof67Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_67).rotationY(180).addModel()
                .partialState().with(Roof67Block.FACING, Direction.WEST).with(Roof67Block.TYPE, RoofShape.OUTER_LEFT).with(Roof67Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_67_outer).rotationY(90).addModel()
                .partialState().with(Roof67Block.FACING, Direction.WEST).with(Roof67Block.TYPE, RoofShape.OUTER_RIGHT).with(Roof67Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_67_outer).rotationY(180).addModel()
                .partialState().with(Roof67Block.FACING, Direction.WEST).with(Roof67Block.TYPE, RoofShape.INNER_LEFT).with(Roof67Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_67_inner).rotationY(90).addModel()
                .partialState().with(Roof67Block.FACING, Direction.WEST).with(Roof67Block.TYPE, RoofShape.INNER_RIGHT).with(Roof67Block.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_67_inner).rotationY(180).addModel();
    }

    public void roofPeakBlock(RoofPeakBlock block) {
        roofPeakBlock(block, blockTexture(block));
    }

    public void roofPeakBlock(RoofPeakBlock block, ResourceLocation texture) {
        roofPeakBlock(block, texture, texture, texture);
    }

    public void roofPeakBlock(RoofPeakBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
        ModelFile roof_peak = models().withExistingParent(name(block), modLoc("block/templates/roof_peak"))
                .texture("side", side)
                .texture("top", top)
                .texture("bottom", bottom),
                roof_peak_cap = models().withExistingParent(name(block) + "_cap", modLoc("block/templates/roof_peak_cap"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                roof_peak_cross = models().withExistingParent(name(block) + "_cross", modLoc("block/templates/roof_peak_cross"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                roof_peak_corner = models().withExistingParent(name(block) + "_corner", modLoc("block/templates/roof_peak_corner"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                roof_peak_t = models().withExistingParent(name(block) + "_t", modLoc("block/templates/roof_peak_t"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                roof_peak_end = models().withExistingParent(name(block) + "_end", modLoc("block/templates/roof_peak_end"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                roof_peak_top = models().withExistingParent(name(block) + "_top", modLoc("block/templates/roof_peak_top"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                roof_peak_top_cap = models().withExistingParent(name(block) + "_cap_top", modLoc("block/templates/roof_peak_cap_top"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                roof_peak_top_cross = models().withExistingParent(name(block) + "_cross_top", modLoc("block/templates/roof_peak_cross_top"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                roof_peak_top_corner = models().withExistingParent(name(block) + "_corner_top", modLoc("block/templates/roof_peak_corner_top"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                roof_peak_top_t = models().withExistingParent(name(block) + "_top_t", modLoc("block/templates/roof_peak_t_top"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom),
                roof_peak_end_top = models().withExistingParent(name(block) + "_end_top", modLoc("block/templates/roof_peak_end_top"))
                        .texture("side", side)
                        .texture("top", top)
                        .texture("bottom", bottom);

        getVariantBuilder(block)
                .partialState().with(RoofPeakBlock.TYPE, RoofPeakShape.CAP).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_cap).addModel()
                .partialState().with(RoofPeakBlock.TYPE, RoofPeakShape.CAP).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_cap).addModel()
                .partialState().with(RoofPeakBlock.TYPE, RoofPeakShape.CROSS).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_cross).addModel()
                .partialState().with(RoofPeakBlock.TYPE, RoofPeakShape.CROSS).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_cross).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.NORTH).with(RoofPeakBlock.TYPE, RoofPeakShape.STRAIGHT).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.NORTH).with(RoofPeakBlock.TYPE, RoofPeakShape.STRAIGHT).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.NORTH).with(RoofPeakBlock.TYPE, RoofPeakShape.CORNER_LEFT).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_corner).rotationY(270).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.NORTH).with(RoofPeakBlock.TYPE, RoofPeakShape.CORNER_LEFT).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_corner).rotationY(270).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.NORTH).with(RoofPeakBlock.TYPE, RoofPeakShape.CORNER_RIGHT).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_corner).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.NORTH).with(RoofPeakBlock.TYPE, RoofPeakShape.CORNER_RIGHT).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_corner).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.NORTH).with(RoofPeakBlock.TYPE, RoofPeakShape.LEFT_T).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_t).rotationY(90).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.NORTH).with(RoofPeakBlock.TYPE, RoofPeakShape.LEFT_T).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_t).rotationY(90).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.NORTH).with(RoofPeakBlock.TYPE, RoofPeakShape.RIGHT_T).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_t).rotationY(270).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.NORTH).with(RoofPeakBlock.TYPE, RoofPeakShape.RIGHT_T).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_t).rotationY(270).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.NORTH).with(RoofPeakBlock.TYPE, RoofPeakShape.FACING_T).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_top_t).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.NORTH).with(RoofPeakBlock.TYPE, RoofPeakShape.FACING_T).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_t).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.NORTH).with(RoofPeakBlock.TYPE, RoofPeakShape.OPPOSITE_T).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_top_t).rotationY(180).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.NORTH).with(RoofPeakBlock.TYPE, RoofPeakShape.OPPOSITE_T).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_t).rotationY(180).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.NORTH).with(RoofPeakBlock.TYPE, RoofPeakShape.END_FACING).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_end).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.NORTH).with(RoofPeakBlock.TYPE, RoofPeakShape.END_FACING).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_end_top).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.NORTH).with(RoofPeakBlock.TYPE, RoofPeakShape.END_OPPOSITE).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_end).rotationY(180).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.NORTH).with(RoofPeakBlock.TYPE, RoofPeakShape.END_OPPOSITE).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_end_top).rotationY(180).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.EAST).with(RoofPeakBlock.TYPE, RoofPeakShape.STRAIGHT).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak).rotationY(90).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.EAST).with(RoofPeakBlock.TYPE, RoofPeakShape.STRAIGHT).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top).rotationY(90).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.EAST).with(RoofPeakBlock.TYPE, RoofPeakShape.CORNER_LEFT).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_corner).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.EAST).with(RoofPeakBlock.TYPE, RoofPeakShape.CORNER_LEFT).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_corner).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.EAST).with(RoofPeakBlock.TYPE, RoofPeakShape.CORNER_RIGHT).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_corner).rotationY(90).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.EAST).with(RoofPeakBlock.TYPE, RoofPeakShape.CORNER_RIGHT).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_corner).rotationY(90).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.EAST).with(RoofPeakBlock.TYPE, RoofPeakShape.LEFT_T).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_t).rotationY(180).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.EAST).with(RoofPeakBlock.TYPE, RoofPeakShape.LEFT_T).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_t).rotationY(180).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.EAST).with(RoofPeakBlock.TYPE, RoofPeakShape.RIGHT_T).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_t).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.EAST).with(RoofPeakBlock.TYPE, RoofPeakShape.RIGHT_T).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_t).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.EAST).with(RoofPeakBlock.TYPE, RoofPeakShape.FACING_T).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_t).rotationY(90).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.EAST).with(RoofPeakBlock.TYPE, RoofPeakShape.FACING_T).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_t).rotationY(90).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.EAST).with(RoofPeakBlock.TYPE, RoofPeakShape.OPPOSITE_T).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_t).rotationY(270).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.EAST).with(RoofPeakBlock.TYPE, RoofPeakShape.OPPOSITE_T).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_t).rotationY(270).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.EAST).with(RoofPeakBlock.TYPE, RoofPeakShape.END_FACING).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_end).rotationY(90).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.EAST).with(RoofPeakBlock.TYPE, RoofPeakShape.END_FACING).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_end_top).rotationY(90).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.EAST).with(RoofPeakBlock.TYPE, RoofPeakShape.END_OPPOSITE).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_end).rotationY(270).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.EAST).with(RoofPeakBlock.TYPE, RoofPeakShape.END_OPPOSITE).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_end_top).rotationY(270).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.SOUTH).with(RoofPeakBlock.TYPE, RoofPeakShape.STRAIGHT).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak).rotationY(180).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.SOUTH).with(RoofPeakBlock.TYPE, RoofPeakShape.STRAIGHT).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top).rotationY(180).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.SOUTH).with(RoofPeakBlock.TYPE, RoofPeakShape.CORNER_LEFT).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_corner).rotationY(90).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.SOUTH).with(RoofPeakBlock.TYPE, RoofPeakShape.CORNER_LEFT).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_corner).rotationY(90).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.SOUTH).with(RoofPeakBlock.TYPE, RoofPeakShape.CORNER_RIGHT).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_corner).rotationY(180).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.SOUTH).with(RoofPeakBlock.TYPE, RoofPeakShape.CORNER_RIGHT).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_corner).rotationY(180).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.SOUTH).with(RoofPeakBlock.TYPE, RoofPeakShape.LEFT_T).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_t).rotationY(270).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.SOUTH).with(RoofPeakBlock.TYPE, RoofPeakShape.LEFT_T).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_t).rotationY(270).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.SOUTH).with(RoofPeakBlock.TYPE, RoofPeakShape.RIGHT_T).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_t).rotationY(90).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.SOUTH).with(RoofPeakBlock.TYPE, RoofPeakShape.RIGHT_T).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_t).rotationY(90).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.SOUTH).with(RoofPeakBlock.TYPE, RoofPeakShape.FACING_T).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_t).rotationY(180).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.SOUTH).with(RoofPeakBlock.TYPE, RoofPeakShape.FACING_T).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_t).rotationY(180).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.SOUTH).with(RoofPeakBlock.TYPE, RoofPeakShape.OPPOSITE_T).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_t).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.SOUTH).with(RoofPeakBlock.TYPE, RoofPeakShape.OPPOSITE_T).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_t).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.SOUTH).with(RoofPeakBlock.TYPE, RoofPeakShape.END_FACING).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_end).rotationY(180).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.SOUTH).with(RoofPeakBlock.TYPE, RoofPeakShape.END_FACING).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_end_top).rotationY(180).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.SOUTH).with(RoofPeakBlock.TYPE, RoofPeakShape.END_OPPOSITE).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_end).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.SOUTH).with(RoofPeakBlock.TYPE, RoofPeakShape.END_OPPOSITE).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_end_top).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.WEST).with(RoofPeakBlock.TYPE, RoofPeakShape.STRAIGHT).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak).rotationY(270).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.WEST).with(RoofPeakBlock.TYPE, RoofPeakShape.STRAIGHT).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top).rotationY(270).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.WEST).with(RoofPeakBlock.TYPE, RoofPeakShape.CORNER_LEFT).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_corner).rotationY(180).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.WEST).with(RoofPeakBlock.TYPE, RoofPeakShape.CORNER_LEFT).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_corner).rotationY(180).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.WEST).with(RoofPeakBlock.TYPE, RoofPeakShape.CORNER_RIGHT).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_corner).rotationY(270).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.WEST).with(RoofPeakBlock.TYPE, RoofPeakShape.CORNER_RIGHT).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_corner).rotationY(270).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.WEST).with(RoofPeakBlock.TYPE, RoofPeakShape.LEFT_T).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_t).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.WEST).with(RoofPeakBlock.TYPE, RoofPeakShape.LEFT_T).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_t).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.WEST).with(RoofPeakBlock.TYPE, RoofPeakShape.RIGHT_T).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_t).rotationY(180).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.WEST).with(RoofPeakBlock.TYPE, RoofPeakShape.RIGHT_T).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_t).rotationY(180).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.WEST).with(RoofPeakBlock.TYPE, RoofPeakShape.FACING_T).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_t).rotationY(270).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.WEST).with(RoofPeakBlock.TYPE, RoofPeakShape.FACING_T).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_t).rotationY(270).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.WEST).with(RoofPeakBlock.TYPE, RoofPeakShape.OPPOSITE_T).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_t).rotationY(90).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.WEST).with(RoofPeakBlock.TYPE, RoofPeakShape.OPPOSITE_T).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_top_t).rotationY(90).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.WEST).with(RoofPeakBlock.TYPE, RoofPeakShape.END_FACING).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_end).rotationY(270).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.WEST).with(RoofPeakBlock.TYPE, RoofPeakShape.END_FACING).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_end_top).rotationY(270).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.WEST).with(RoofPeakBlock.TYPE, RoofPeakShape.END_OPPOSITE).with(RoofPeakBlock.HALF, Half.BOTTOM)
                .modelForState().modelFile(roof_peak_end).rotationY(90).addModel()
                .partialState().with(RoofPeakBlock.FACING, Direction.WEST).with(RoofPeakBlock.TYPE, RoofPeakShape.END_OPPOSITE).with(RoofPeakBlock.HALF, Half.TOP)
                .modelForState().modelFile(roof_peak_end_top).rotationY(90).addModel();
    }


    //    public void slabBlock(SlabBlock block, ResourceLocation texture) {
//        slabBlock(block,  texture);
//    }
//
//    public void slabBlock(SlabBlock block, ResourceLocation doubleslab, ResourceLocation texture) {
//        slabBlock(block, doubleslab, texture, texture, texture);
//    }
//
//    public void slabBlock(SlabBlock block, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, ResourceLocation doubleslab) {
//        slabBlock(block, models().slab(name(block), side, bottom, top), models().slabTop(name(block) + "_top", side, bottom, top), models().getExistingFile(doubleslab));
//    }
//
//    public void slabBlock(SlabBlock block, ModelFile bottom, ModelFile top, ModelFile doubleslab) {
//        getVariantBuilder(block)
//                .partialState().with(SlabBlock.TYPE, SlabType.BOTTOM).addModels(new ConfiguredModel(bottom))
//                .partialState().with(SlabBlock.TYPE, SlabType.TOP).addModels(new ConfiguredModel(top))
//                .partialState().with(SlabBlock.TYPE, SlabType.DOUBLE).addModels(new ConfiguredModel(doubleslab));
//    }

    /* no need anymore, because BlockManager right now stateGenerator field
    private void tudorBlocks() {
        ImmutableMap.Builder<List<BlockManager>, Block> map = ImmutableMap.builder();
        // Beige, Brown, Ochre, Tan, White Plasters - Half-Timbered
        map.put(Lists.newArrayList(ModBlocks.TUDOR_ACACIA_BEIGE_PLASTER, ModBlocks.TUDOR_ACACIA_BROWN_PLASTER, ModBlocks.TUDOR_ACACIA_OCHRE_PLASTER, ModBlocks.TUDOR_ACACIA_TAN_PLASTER, ModBlocks.TUDOR_ACACIA_WHITE_PLASTER), Blocks.ACACIA_PLANKS);
        map.put(Lists.newArrayList(ModBlocks.TUDOR_BIRCH_BEIGE_PLASTER, ModBlocks.TUDOR_BIRCH_BROWN_PLASTER, ModBlocks.TUDOR_BIRCH_OCHRE_PLASTER, ModBlocks.TUDOR_BIRCH_TAN_PLASTER, ModBlocks.TUDOR_BIRCH_WHITE_PLASTER), Blocks.BIRCH_PLANKS);
        map.put(Lists.newArrayList(ModBlocks.TUDOR_DARK_OAK_BEIGE_PLASTER, ModBlocks.TUDOR_DARK_OAK_BROWN_PLASTER, ModBlocks.TUDOR_DARK_OAK_OCHRE_PLASTER, ModBlocks.TUDOR_DARK_OAK_TAN_PLASTER, ModBlocks.TUDOR_DARK_OAK_WHITE_PLASTER), Blocks.DARK_OAK_PLANKS);
        map.put(Lists.newArrayList(ModBlocks.TUDOR_JUNGLE_BEIGE_PLASTER, ModBlocks.TUDOR_JUNGLE_BROWN_PLASTER, ModBlocks.TUDOR_JUNGLE_OCHRE_PLASTER, ModBlocks.TUDOR_JUNGLE_TAN_PLASTER, ModBlocks.TUDOR_JUNGLE_WHITE_PLASTER), Blocks.JUNGLE_PLANKS);
        map.put(Lists.newArrayList(ModBlocks.TUDOR_OAK_BEIGE_PLASTER, ModBlocks.TUDOR_OAK_BROWN_PLASTER, ModBlocks.TUDOR_OAK_OCHRE_PLASTER, ModBlocks.TUDOR_OAK_TAN_PLASTER, ModBlocks.TUDOR_OAK_WHITE_PLASTER), Blocks.OAK_PLANKS);
        map.put(Lists.newArrayList(ModBlocks.TUDOR_SPRUCE_BEIGE_PLASTER, ModBlocks.TUDOR_SPRUCE_BROWN_PLASTER, ModBlocks.TUDOR_SPRUCE_OCHRE_PLASTER, ModBlocks.TUDOR_SPRUCE_TAN_PLASTER, ModBlocks.TUDOR_SPRUCE_WHITE_PLASTER), Blocks.SPRUCE_PLANKS);


        //Creates a simple block using the above data, can be replaced with a similar option like in ModItemModelProvider
        map.build().forEach((l, b) -> {
            for (BlockManager blockManager : l) {
                for (RegistryObject<Block> e : blockManager.getBlocks().values()) {
                    simpleBlock(e.get(), models().cubeBottomTop(e.getId().getPath(), modLoc("block/%s".formatted(e.getId().getPath())), blockTexture(b), blockTexture(b)));
                }
            }
        });
    }*/
}
