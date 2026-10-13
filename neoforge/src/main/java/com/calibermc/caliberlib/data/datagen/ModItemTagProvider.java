package com.calibermc.caliberlib.data.datagen;

import com.calibermc.caliberlib.block.management.BlockManager;
import com.calibermc.caliberlib.block.properties.RecipeWoodTypes;
import com.calibermc.caliberlib.data.ModBlockFamily;
import com.calibermc.caliberlib.util.ModTags;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ModItemTagProvider extends ItemTagsProvider {

    protected String modid;

    public ModItemTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider,
                              CompletableFuture<TagLookup<Block>> pItemTags, String modid, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pItemTags, modid, existingFileHelper);
        this.modid = modid;
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        // Sort BlockManagers by name
        List<BlockManager> sortedBlockManagers = BlockManager.BLOCK_MANAGERS.get(this.modid).stream()
                .sorted(Comparator.comparing(BlockManager::getName))
                .toList();

        for (BlockManager blockManager : sortedBlockManagers) {
            // Sort blocks by name
            List<Map.Entry<BlockManager.BlockAdditional, Pair<ResourceLocation, Supplier<Block>>>> sortedBlocks = blockManager.getBlocks().entrySet().stream()
                    .sorted(Comparator.comparing(e -> e.getValue().getFirst().getPath()))
                    .toList();

            for (Map.Entry<BlockManager.BlockAdditional, Pair<ResourceLocation, Supplier<Block>>> e : sortedBlocks) {
//        for (BlockManager blockManager : BlockManager.BLOCK_MANAGERS.get(this.modid)) {
//            for (Map.Entry<BlockManager.BlockAdditional, Pair<ResourceLocation, Supplier<Block>>> e : blockManager.getBlocks().entrySet()) {
                ModBlockFamily.Variant variant = e.getKey().variant;
                String blockName = e.getValue().getFirst().getPath();
                BlockSetType blockSetType = blockManager.blockType();
                ResourceLocation namespace = e.getValue().getFirst();

                if (blockName.contains("crimson") || blockName.contains("warped")) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ItemTags.NON_FLAMMABLE_WOOD).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ItemTags.NON_FLAMMABLE_WOOD).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (blockName.contains("thatch")) {
                    this.tag(ItemTags.DAMPENS_VIBRATIONS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                }

                if (variant.equals(ModBlockFamily.Variant.BASE)) {
                    if (blockName.contains("cobbled")) {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(ItemTags.STONE_CRAFTING_MATERIALS).add(e.getValue().getSecond().get().asItem());
                        } else {
                            this.tag(ItemTags.STONE_CRAFTING_MATERIALS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    } else if (blockName.contains("limestone_bricks")) {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(ItemTags.STONE_BRICKS).add(e.getValue().getSecond().get().asItem());
                        } else {
                            this.tag(ItemTags.STONE_BRICKS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    } else if (blockName.contains("log") || ((blockName.contains("wood") && !blockName.equals("woodcutter")))) {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(ItemTags.LOGS_THAT_BURN).add(e.getValue().getSecond().get().asItem());
                        } else {
                            this.tag(ItemTags.LOGS_THAT_BURN).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    } else if (blockName.contains("planks")) {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(ItemTags.PLANKS).add(e.getValue().getSecond().get().asItem());
                        } else {
                            this.tag(ItemTags.PLANKS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.ARCH)) {
                    this.tag(ModTags.Items.ARCHES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.ARCHES).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.ARCHES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.ARCH_HALF)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.HALF_ARCHES).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.HALF_ARCHES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.ARCH_LARGE)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.LARGE_ARCHES).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.LARGE_ARCHES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.ARCH_LARGE_HALF)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.HALF_LARGE_ARCHES).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.HALF_LARGE_ARCHES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.ARROWSLIT)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.ARROWSLITS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.ARROWSLITS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.BALUSTRADE)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.BALUSTRADES).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.BALUSTRADES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.BEAM_DIAGONAL)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.DIAGONAL_BEAMS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.DIAGONAL_BEAMS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.BEAM_HORIZONTAL)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.HORIZONTAL_BEAMS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.HORIZONTAL_BEAMS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.BEAM_LINTEL)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.BEAM_LINTELS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.BEAM_LINTELS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.BEAM_POSTS)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.BEAM_POSTS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.BEAM_POSTS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.BEAM_VERTICAL)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.VERTICAL_BEAMS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.VERTICAL_BEAMS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.CAPITAL)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.CAPTIALS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.CAPTIALS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

//                if (variant.equals(ModBlockFamily.Variant.COLUMN)) {
//                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
//                        this.tag(ModTags.Items.COLUMNS).add(e.getValue().getSecond().get().asItem());
//                    } else {
//                        this.tag(ModTags.Items.COLUMNS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
//                    }
//                }

                if (variant.equals(ModBlockFamily.Variant.CORNER)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.CORNERS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.CORNERS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.CORNER_SLAB)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.CORNER_SLABS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.CORNER_SLABS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.CORNER_SLAB_VERTICAL)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.VERTICAL_CORNER_SLABS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.VERTICAL_CORNER_SLABS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.DOOR)) {
                    if (blockName.contains("iron")) {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(ItemTags.DOORS).add(e.getValue().getSecond().get().asItem());
                        } else {
                            this.tag(ItemTags.DOORS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    } else {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(ItemTags.WOODEN_DOORS).add(e.getValue().getSecond().get().asItem());
                        } else {
                            this.tag(ItemTags.WOODEN_DOORS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.DOOR_FRAME)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.DOOR_FRAMES).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.DOOR_FRAMES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.DOOR_FRAME_LINTEL)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.DOOR_FRAME_LINTELS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.DOOR_FRAME_LINTELS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.EIGHTH)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.EIGHTHS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.EIGHTHS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.FENCE)) {
                    if (Arrays.stream(RecipeWoodTypes.values()).anyMatch(p -> blockName.contains(p.getName()))) {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(ItemTags.WOODEN_FENCES).add(e.getValue().getSecond().get().asItem());
                        } else {
                            this.tag(ItemTags.WOODEN_FENCES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    } else {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(ItemTags.FENCES).add(e.getValue().getSecond().get().asItem());
                        } else {
                            this.tag(ItemTags.FENCES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.FENCE_GATE)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ItemTags.FENCE_GATES).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ItemTags.FENCE_GATES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.HANGING_SIGN)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ItemTags.HANGING_SIGNS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ItemTags.HANGING_SIGNS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.LAYER)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.LAYERS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.LAYERS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.LAYER_VERTICAL)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.VERTICAL_LAYERS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.VERTICAL_LAYERS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.PILLAR)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.PILLARS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.PILLARS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.PRESSURE_PLATE)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ItemTags.WOODEN_PRESSURE_PLATES).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ItemTags.WOODEN_PRESSURE_PLATES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.QUARTER)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.QUARTERS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.QUARTERS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.QUARTER_VERTICAL)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.VERTICAL_QUARTERS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.VERTICAL_QUARTERS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.ROOF_22)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.ROOF_22S).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.ROOF_22S).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.ROOF_45)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.ROOF_45S).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.ROOF_45S).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.ROOF_67)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.ROOF_67S).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.ROOF_67S).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.ROOF_PEAK)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.ROOF_PEAKS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.ROOF_PEAKS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.SIGN)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ItemTags.SIGNS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ItemTags.SIGNS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.SLAB_VERTICAL)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.VERTICAL_SLABS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.VERTICAL_SLABS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }



                if (variant.equals(ModBlockFamily.Variant.SLAB)) {
                    if (Arrays.stream(RecipeWoodTypes.values()).anyMatch(p -> blockName.contains(p.getName()))) {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(ItemTags.WOODEN_SLABS).add(e.getValue().getSecond().get().asItem());
                        } else {
                            this.tag(ItemTags.WOODEN_SLABS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    } else {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(ItemTags.SLABS).add(e.getValue().getSecond().get().asItem());
                        } else {
                            this.tag(ItemTags.SLABS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.STAIRS)) {
                    if (Arrays.stream(RecipeWoodTypes.values()).anyMatch(p -> blockName.contains(p.getName()))) {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(ItemTags.WOODEN_STAIRS).add(e.getValue().getSecond().get().asItem());
                        } else {
                            this.tag(ItemTags.WOODEN_STAIRS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    } else {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(ItemTags.STAIRS).add(e.getValue().getSecond().get().asItem());
                        } else {
                            this.tag(ItemTags.STAIRS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.TALL_DOOR)) {
                    if ((Arrays.stream(RecipeWoodTypes.values()).anyMatch(p -> blockManager.getName().contains(p.getName())))){
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(ModTags.Items.TALL_WOODEN_DOORS).add(e.getValue().getSecond().get().asItem());
                        } else {
                            this.tag(ModTags.Items.TALL_WOODEN_DOORS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    } else {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(ModTags.Items.TALL_IRON_DOORS).add(e.getValue().getSecond().get().asItem());
                        } else {
                            this.tag(ModTags.Items.TALL_IRON_DOORS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    }

                }

                if (variant.equals(ModBlockFamily.Variant.TRAPDOOR)) {
                    if (Arrays.stream(RecipeWoodTypes.values()).anyMatch(p -> blockName.contains(p.getName()))) {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(ItemTags.WOODEN_TRAPDOORS).add(e.getValue().getSecond().get().asItem());
                        } else {
                            this.tag(ItemTags.WOODEN_TRAPDOORS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    } else {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(ItemTags.TRAPDOORS).add(e.getValue().getSecond().get().asItem());
                        } else {
                            this.tag(ItemTags.TRAPDOORS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.WINDOW)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.WINDOWS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.WINDOWS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.WINDOW_HALF)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Items.HALF_WINDOWS).add(e.getValue().getSecond().get().asItem());
                    } else {
                        this.tag(ModTags.Items.HALF_WINDOWS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }
            }
        }

        ForgeRegistries.BLOCKS.getValues().stream()
                .filter(block -> {
                    ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(block);
                    return BlockManager.ALL_BLOCKS.stream().map(Supplier::get).noneMatch(b -> b.equals(block))
                            && registryName != null && registryName.getNamespace().equals(this.modId);
                })
                .forEach(block -> {
                    String itemName = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getPath();

                    if (itemName.contains("crimson") || itemName.contains("warped")) {
                        this.tag(ItemTags.NON_FLAMMABLE_WOOD).add(block.asItem());
                    }

                    if (itemName.contains("leaves")) {
                        this.tag(ItemTags.LEAVES).add(block.asItem());
                    }

                    if (itemName.contains("sand") && !itemName.contains("stone")) {
                        this.tag(ItemTags.SAND).add(block.asItem());
                        this.tag(ItemTags.SMELTS_TO_GLASS).add(block.asItem());
                        this.tag(Tags.Items.SAND).add(block.asItem());
                    }

                    if (itemName.contains("ingot")) {
                        this.tag(ItemTags.TRIM_MATERIALS).add(block.asItem());
                    }

                    if (itemName.contains("ore")) {
                        this.tag(Tags.Items.ORES).add(block.asItem());
                        if (itemName.contains("deepslate")) {
                            this.tag(Tags.Items.ORES_IN_GROUND_DEEPSLATE).add(block.asItem());
                        } else if (itemName.contains("nether")) {
                            this.tag(Tags.Items.ORES_IN_GROUND_NETHERRACK).add(block.asItem());
                        } else {
                            this.tag(Tags.Items.ORES_IN_GROUND_STONE).add(block.asItem());
                        }
                    }
                });

        // Item Tags
        for (Map.Entry<ResourceKey<Item>, Item> resourceKeyItemEntry : ForgeRegistries.ITEMS.getEntries()) {
            // Check if the item's registry name is in the MOD_ID namespace
            ResourceLocation registryName = resourceKeyItemEntry.getKey().location();
            String itemName = registryKey.toString();
            if (registryName != null && this.modId.equals(registryName.getNamespace())) {
                Item item = resourceKeyItemEntry.getValue();
                Block block = Block.byItem(item);

                if (item instanceof SwordItem) {
                    this.tag(ItemTags.SWORDS).add(item);
                }

                if (item instanceof PickaxeItem) {
                    this.tag(ItemTags.PICKAXES).add(item);
                }

                if (item instanceof ShovelItem) {
                    this.tag(ItemTags.SHOVELS).add(item);
                }

                if (item instanceof HoeItem) {
                    this.tag(ItemTags.HOES).add(item);

                }
            }
        }
    }
}

