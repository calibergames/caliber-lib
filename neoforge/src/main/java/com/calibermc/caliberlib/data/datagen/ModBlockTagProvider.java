package com.calibermc.caliberlib.data.datagen;

import com.calibermc.caliberlib.block.management.BlockManager;
import com.calibermc.caliberlib.block.properties.RecipeStoneTypes;
import com.calibermc.caliberlib.block.properties.RecipeWoodTypes;
import com.calibermc.caliberlib.data.ModBlockFamily;
import com.calibermc.caliberlib.util.ModTags;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;


public class ModBlockTagProvider extends BlockTagsProvider {

    protected String modid;
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modid, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modid, existingFileHelper);
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
                ModBlockFamily.Variant variant = e.getKey().variant;
                String blockName = e.getValue().getFirst().getPath();
                BlockSetType blockSetType = blockManager.blockType();
                ResourceLocation namespace = e.getValue().getFirst();

                if (Arrays.stream(RecipeStoneTypes.values()).anyMatch(p -> blockManager.getName().contains(p.getName()))) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(BlockTags.MINEABLE_WITH_PICKAXE).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (Arrays.stream(RecipeWoodTypes.values()).anyMatch(p -> blockManager.getName().contains(p.getName()))
                        || blockName.contains("wool")) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(BlockTags.MINEABLE_WITH_AXE).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(BlockTags.MINEABLE_WITH_AXE).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if ((!blockName.contains("stone") && !(blockName.contains("sanded")) && (blockName.contains("clay") || blockName.contains("dirt")
                        || blockName.contains("gravel") || blockName.contains("mycelium") || blockName.contains("nylium")
                        || blockName.contains("podzol") || blockName.contains("soil") || (blockName.contains("sand"))))) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(BlockTags.MINEABLE_WITH_SHOVEL).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (blockName.contains("leaves") || blockName.contains("thatch")) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(BlockTags.MINEABLE_WITH_HOE).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(BlockTags.MINEABLE_WITH_HOE).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.ARCH)) {
                    this.tag(ModTags.Blocks.ARCHES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.ARCHES).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.ARCHES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.ARCH_HALF)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.HALF_ARCHES).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.HALF_ARCHES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.ARCH_LARGE)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.LARGE_ARCHES).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.LARGE_ARCHES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.ARCH_LARGE_HALF)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.HALF_LARGE_ARCHES).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.HALF_LARGE_ARCHES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.ARROWSLIT)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.ARROWSLITS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.ARROWSLITS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.BALUSTRADE)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.BALUSTRADES).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.BALUSTRADES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.BEAM_DIAGONAL)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.DIAGONAL_BEAMS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.DIAGONAL_BEAMS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.BEAM_HORIZONTAL)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.HORIZONTAL_BEAMS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.HORIZONTAL_BEAMS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.BEAM_LINTEL)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.BEAM_LINTELS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.BEAM_LINTELS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.BEAM_POSTS)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.BEAM_POSTS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.BEAM_POSTS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.BEAM_VERTICAL)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.VERTICAL_BEAMS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.VERTICAL_BEAMS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.BUTTON)) {
                    if (blockName.contains("limestone")) {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(BlockTags.STONE_BUTTONS).add(e.getValue().getSecond().get());
                        } else {
                            this.tag(BlockTags.STONE_BUTTONS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    } else {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(BlockTags.WOODEN_BUTTONS).add(e.getValue().getSecond().get());
                        } else {
                            this.tag(BlockTags.WOODEN_BUTTONS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.CAPITAL)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.CAPTIALS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.CAPTIALS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

//                if (variant.equals(ModBlockFamily.Variant.COLUMN)) {
//                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
//                        this.tag(ModTags.Blocks.COLUMNS).add(e.getValue().getSecond().get());
//                    } else {
//                        this.tag(ModTags.Blocks.COLUMNS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
//                    }
//                }

                if (variant.equals(ModBlockFamily.Variant.CORNER)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.CORNERS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.CORNERS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.CORNER_SLAB)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.CORNER_SLABS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.CORNER_SLABS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.CORNER_SLAB_VERTICAL)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.VERTICAL_CORNER_SLABS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.VERTICAL_CORNER_SLABS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.DOOR)) {
                    if ((Arrays.stream(RecipeWoodTypes.values()).anyMatch(p -> blockManager.getName().contains(p.getName())))){
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(BlockTags.WOODEN_DOORS).add(e.getValue().getSecond().get());
                        } else {
                            this.tag(BlockTags.WOODEN_DOORS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    } else {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(BlockTags.DOORS).add(e.getValue().getSecond().get());
                        } else {
                            this.tag(BlockTags.DOORS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    }

                }

                if (variant.equals(ModBlockFamily.Variant.DOOR_FRAME)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.DOOR_FRAMES).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.DOOR_FRAMES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.DOOR_FRAME_LINTEL)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.DOOR_FRAME_LINTELS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.DOOR_FRAME_LINTELS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.EIGHTH)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.EIGHTHS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.EIGHTHS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.FENCE)) {
                    if (Arrays.stream(RecipeWoodTypes.values()).anyMatch(p -> blockName.contains(p.getName()))) {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(BlockTags.WOODEN_FENCES).add(e.getValue().getSecond().get());
                        } else {
                            this.tag(BlockTags.WOODEN_FENCES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    } else {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(BlockTags.FENCES).add(e.getValue().getSecond().get());
                        } else {
                            this.tag(BlockTags.FENCES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.FENCE_GATE)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(BlockTags.FENCE_GATES).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(BlockTags.FENCE_GATES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.HANGING_SIGN)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(BlockTags.CEILING_HANGING_SIGNS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(BlockTags.CEILING_HANGING_SIGNS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.LAYER)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.LAYERS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.LAYERS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.LAYER_VERTICAL)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.VERTICAL_LAYERS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.VERTICAL_LAYERS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.PILLAR)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.PILLARS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.PILLARS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.PRESSURE_PLATE)) {
                    if (blockName.contains("limestone")) {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(BlockTags.STONE_PRESSURE_PLATES).add(e.getValue().getSecond().get());
                        } else {
                            this.tag(BlockTags.STONE_PRESSURE_PLATES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    } else {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(BlockTags.WOODEN_PRESSURE_PLATES).add(e.getValue().getSecond().get());
                        } else {
                            this.tag(BlockTags.WOODEN_PRESSURE_PLATES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.QUARTER)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.QUARTERS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.QUARTERS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.QUARTER_VERTICAL)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.VERTICAL_QUARTERS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.VERTICAL_QUARTERS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.ROOF_22)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.ROOF_22S).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.ROOF_22S).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.ROOF_45)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.ROOF_45S).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.ROOF_45S).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.ROOF_67)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.ROOF_67S).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.ROOF_67S).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.ROOF_PEAK)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.ROOF_PEAKS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.ROOF_PEAKS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.SIGN)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(BlockTags.STANDING_SIGNS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(BlockTags.STANDING_SIGNS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.SLAB_VERTICAL)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.VERTICAL_SLABS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.VERTICAL_SLABS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.SLAB)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(BlockTags.SLABS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(BlockTags.SLABS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.STAIRS)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(BlockTags.STAIRS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(BlockTags.STAIRS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.TALL_DOOR)) {
                    if ((Arrays.stream(RecipeWoodTypes.values()).anyMatch(p -> blockManager.getName().contains(p.getName())))){
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(ModTags.Blocks.TALL_WOODEN_DOORS).add(e.getValue().getSecond().get());
                        } else {
                            this.tag(ModTags.Blocks.TALL_WOODEN_DOORS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    } else {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(ModTags.Blocks.TALL_IRON_DOORS).add(e.getValue().getSecond().get());
                        } else {
                            this.tag(ModTags.Blocks.TALL_IRON_DOORS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    }

                }

                if (variant.equals(ModBlockFamily.Variant.TRAPDOOR)) {
                    if ((Arrays.stream(RecipeWoodTypes.values()).anyMatch(p -> blockManager.getName().contains(p.getName())))){
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(BlockTags.WOODEN_TRAPDOORS).add(e.getValue().getSecond().get());
                        } else {
                            this.tag(BlockTags.WOODEN_TRAPDOORS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    } else {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(BlockTags.TRAPDOORS).add(e.getValue().getSecond().get());
                        } else {
                            this.tag(BlockTags.TRAPDOORS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.WALL_HANGING_SIGN)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(BlockTags.WALL_HANGING_SIGNS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(BlockTags.WALL_HANGING_SIGNS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.WALL_SIGN)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(BlockTags.WALL_SIGNS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(BlockTags.WALL_SIGNS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.WALL)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(BlockTags.WALLS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(BlockTags.WALLS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.WINDOW)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.WINDOWS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.WINDOWS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.WINDOW_HALF)) {
                    if (namespace != null && namespace.getNamespace().equals("caliber")) {
                        this.tag(ModTags.Blocks.HALF_WINDOWS).add(e.getValue().getSecond().get());
                    } else {
                        this.tag(ModTags.Blocks.HALF_WINDOWS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                    }
                }

                if (variant.equals(ModBlockFamily.Variant.BASE)) {
                    if ((blockName.contains("granite") || blockName.contains("limestone") || blockName.contains("marble"))
                            && (!blockName.contains("brick") && !blockName.contains("cobbled") && !blockName.contains("polished")
                            && !blockName.contains("smooth"))) {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(BlockTags.BASE_STONE_OVERWORLD).add(e.getValue().getSecond().get());
                            this.tag(BlockTags.STONE_ORE_REPLACEABLES).add(e.getValue().getSecond().get());
                            this.tag(Tags.Blocks.ORE_BEARING_GROUND_STONE).add(e.getValue().getSecond().get());
                        } else {
                            this.tag(BlockTags.BASE_STONE_OVERWORLD).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                            this.tag(BlockTags.STONE_ORE_REPLACEABLES).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                            this.tag(Tags.Blocks.ORE_BEARING_GROUND_STONE).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                        if (blockName.contains("granite")) {
                            if (namespace != null && namespace.getNamespace().equals("caliber")) {
                                this.tag(ModTags.Blocks.GRANITE).add(e.getValue().getSecond().get());
                            } else {
                                this.tag(ModTags.Blocks.GRANITE).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                            }
                        } else if (blockName.contains("marble")) {
                            if (namespace != null && namespace.getNamespace().equals("caliber")) {
                                this.tag(ModTags.Blocks.MARBLE).add(e.getValue().getSecond().get());
                            } else {
                                this.tag(ModTags.Blocks.MARBLE).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                            }
                        }
                    } else if (blockName.contains("planks")) {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(BlockTags.PLANKS).add(e.getValue().getSecond().get());
                        } else {
                            this.tag(BlockTags.PLANKS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    } else if (blockName.contains("bricks")) {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(BlockTags.STONE_BRICKS).add(e.getValue().getSecond().get());
                        } else {
                            this.tag(BlockTags.STONE_BRICKS).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    } else if (blockName.contains("wood") && !blockName.contains("woodcutter")) {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(BlockTags.LOGS_THAT_BURN).add(e.getValue().getSecond().get());
                        } else {
                            this.tag(BlockTags.LOGS_THAT_BURN).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    } else if (blockName.contains("sandstone")) {
                        if (namespace != null && namespace.getNamespace().equals("caliber")) {
                            this.tag(Tags.Blocks.SANDSTONE).add(e.getValue().getSecond().get());
                        } else {
                            this.tag(Tags.Blocks.SANDSTONE).addOptional(new ResourceLocation(modid, e.getValue().getFirst().getPath()));
                        }
                    }
                }




            }
        }

            // BLOCKS NOT REGISTERED WITH BLOCK MANAGER
         ForgeRegistries.BLOCKS.getValues().stream()
                    .filter(block -> {
                        ResourceLocation registryName = ForgeRegistries.BLOCKS.getKey(block);
                        return BlockManager.ALL_BLOCKS.stream().map(Supplier::get).noneMatch(b -> b.equals(block))
                                && registryName != null && registryName.getNamespace().equals(this.modId);
                    })
                    .forEach(block -> {
                        String blockName = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getPath();

                        if (blockName.contains("andesite") || blockName.contains("basalt") || blockName.contains("blackstone")
                                || blockName.contains("brick") || blockName.contains("calcite") || blockName.contains("diorite")
                                || blockName.contains("dripstone") || blockName.contains("end_stone") || blockName.contains("granite")
                                || blockName.contains("limestone") || blockName.contains("marble") || blockName.contains("netherite")
                                || blockName.contains("obsidian") || blockName.contains("prismarine") || blockName.contains("purpur")
                                || blockName.contains("quartz") || blockName.contains("sandstone") || blockName.contains("stone")
                                || blockName.contains("tuff") || blockName.contains("terracotta") || blockName.contains("warped")) {
                            this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
                        }

                        if (blockName.contains("acacia") || blockName.contains("bamboo") || blockName.contains("birch")
                                || blockName.contains("cedar") || blockName.contains("cherry") || blockName.contains("crimson")
                                || blockName.contains("dark_oak") || blockName.contains("fir") || blockName.contains("jungle")
                                || blockName.contains("mangrove") || blockName.contains("maple") || blockName.contains("oak")
                                || blockName.contains("pine") || blockName.contains("spruce") || blockName.contains("warped")
                                || blockName.contains("willow") || blockName.contains("wool")) {
                            this.tag(BlockTags.MINEABLE_WITH_AXE).add(block);
                        }

                        if (blockName.contains("clay") || blockName.contains("dirt") || blockName.contains("gravel")
                                || blockName.contains("mycelium") || blockName.contains("nylium") || blockName.contains("podzol")
                                || blockName.contains("sand") || blockName.contains("soil")) {
                            this.tag(BlockTags.MINEABLE_WITH_SHOVEL).add(block);
                        }

                        if (blockName.contains("leaves") || blockName.contains("thatch")) {
                            this.tag(BlockTags.MINEABLE_WITH_HOE).add(block);
                        }

                        if (blockName.contains("block")) {
                            if (blockName.contains("bronze") || blockName.contains("silver") || blockName.contains("tin")) {
                                this.tag(Tags.Blocks.STORAGE_BLOCKS).add(block);
                            }
                        }

                        if (blockName.contains("cobbled")) {
                            this.tag(Tags.Blocks.COBBLESTONE).add(block);
                            if (blockName.contains("mossy")) {
                                this.tag(Tags.Blocks.COBBLESTONE_MOSSY).add(block);
                            } else if (blockName.contains("deeplslate")) {
                                this.tag(Tags.Blocks.COBBLESTONE_DEEPSLATE).add(block);
                            } else if (blockName.contains("infested")) {
                                this.tag(Tags.Blocks.COBBLESTONE_INFESTED).add(block);
                            } else {
                                this.tag(Tags.Blocks.COBBLESTONE_NORMAL).add(block);
                            }
                        }

                        if (blockName.contains("leaves")) {
                            this.tag(BlockTags.LEAVES).add(block);
                        }

                        if (blockName.contains("log") || (blockName.contains("wood") && !blockName.contains("woodcutter"))) {
                            this.tag(BlockTags.LOGS_THAT_BURN).add(block);
                            if (!blockName.contains("stripped") && !blockName.contains("wood")) {
                                this.tag(BlockTags.OVERWORLD_NATURAL_LOGS).add(block);
                            }
                        }

                        if (blockName.contains("sapling")) {
                            this.tag(BlockTags.SAPLINGS)
                                    .add(block);
                        }

                        if (blockName.contains("sand") && !blockName.contains("stone")) {
                            this.tag(BlockTags.SAND).add(block);
                            this.tag(BlockTags.SMELTS_TO_GLASS).add(block);
                            this.tag(Tags.Blocks.SAND).add(block);
                        }

                        if (blockName.contains("ore")) {
                            this.tag(Tags.Blocks.ORES).add(block);
                            if (blockName.contains("deepslate")) {
                                this.tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE).add(block);
                            } else if (blockName.contains("nether")) {
                                this.tag(Tags.Blocks.ORES_IN_GROUND_NETHERRACK).add(block);
                            } else {
                                this.tag(Tags.Blocks.ORES_IN_GROUND_STONE).add(block);
                            }
                            this.tag(BlockTags.SNAPS_GOAT_HORN).add(block);
                        }
                    });

    }
}