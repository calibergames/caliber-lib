package com.calibermc.caliberlib.block.management;

import com.calibermc.caliberlib.assets.MaterialKind;
import com.calibermc.caliberlib.assets.MiningTool;
import com.calibermc.caliberlib.block.custom.*;
import com.calibermc.caliberlib.data.ModBlockFamily;
import com.calibermc.caliberlib.data.datagen.loot.ModBlockLootTables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.calibermc.caliberlib.registry.Registrar;
import com.calibermc.caliberlib.registry.RegistryEntry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created to reduce the code in ModBlocks, for optimization and easier register of complex blocks
 */
public class BlockManager {

    public static final List<Supplier<Block>> ALL_BLOCKS = new ArrayList<>();
    public static final Map<String, List<BlockManager>> BLOCK_MANAGERS = new HashMap<>();

    private final String name;
    private final String modId;
    private final String idPrefix;
    private final BlockSetType blockSetType;
    private final Assets assets;
    private final ImmutableMap<BlockAdditional, Pair<ResourceLocation, Supplier<Block>>> blocks;

    BlockManager(String name, List<BlockAdditional> directions, BlockSetType blockSetType, String modid, Assets assets) {
        this.blockSetType = blockSetType;
        this.modId = modid;
        this.assets = assets;
        String modifiedName = idPrefix(name);
        this.idPrefix = modifiedName;
        ImmutableMap.Builder<BlockAdditional, Pair<ResourceLocation, Supplier<Block>>> builder = ImmutableMap.builder();
        for (BlockAdditional e : directions) {
            if (e.registerBlockFunc == null) {
                builder.put(e, Pair.of(new ResourceLocation("minecraft", name), e.blockSupplier));
            } else {
                if (e.variant != ModBlockFamily.Variant.BASE) {
                    String name1 = "%s_%s".formatted(modifiedName, e.variant.name().toLowerCase());
                    builder.put(e, Pair.of(new ResourceLocation(modid, name1), e.registerBlockFunc.apply(name1, e.blockSupplier)));
                } else {
                    if (!e.skipRegister) {
                        builder.put(e, Pair.of(new ResourceLocation(modid, name), e.registerBlockFunc.apply(name, e.blockSupplier)));
                    } else {
                        builder.put(e, Pair.of(new ResourceLocation("minecraft", name), e.blockSupplier));
                    }
                }
            }
        }

        this.blocks = builder.build();
        for (Map.Entry<BlockAdditional, Pair<ResourceLocation, Supplier<Block>>> e : this.blocks.entrySet()) {
            e.getKey().blockConsumer.accept(this, e.getValue());
        }
        this.name = name;
        ALL_BLOCKS.addAll(this.blocks.values().stream().map(Pair::getSecond).toList());
        if (!BLOCK_MANAGERS.containsKey(modid)) {
            BLOCK_MANAGERS.put(modid, Lists.newArrayList(this));
        } else {
            List<BlockManager> managers = BLOCK_MANAGERS.get(modid);
            managers.add(this);
            BLOCK_MANAGERS.put(modid, managers);
        }
    }

    /** The shapes' id prefix: the family name without its block word ("oak_planks" gives "oak"). */
    static String idPrefix(String name) {
        if (name.contains("bricks")) {
            return name.replace("_bricks", "_brick");
        } else if (name.contains("tiles")) {
            return name.replace("_tiles", "_tile");
        } else if (name.contains("planks")) {
            return name.replace("_planks", "");
        } else if (name.contains("wood")) {
            return name.replace("_wood", "");
        } else if (name.contains("boards")) {
            return name.replace("boards", "board");
        } else if (name.contains("bamboo_block")) {
            return name.replace("_block", "");
        } else if (name.contains("shingles")) {
            return name.replace("_shingles", "_shingle");
        }
        return name;
    }

    public String getName() {
        return name;
    }

    /** The namespace the shapes are registered in. */
    public String modId() {
        return modId;
    }

    /** The shapes' id prefix, e.g. "oak" for the "oak_planks" family. */
    public String idPrefix() {
        return idPrefix;
    }

    /** What the asset engine needs to know about this family beyond its blocks. */
    public Assets assets() {
        return assets;
    }

    // if created using a block manager
    public Block baseBlock() {
        return this.get(ModBlockFamily.Variant.BASE);
    }

    public BlockSetType blockType() {
        return blockSetType;
    }

    public Block get(ModBlockFamily.Variant direction) {
        BlockAdditional a = this.getByVariant(direction);
        assert a != null;
        if (a.skipRegister) {
            return a.blockSupplier.get();
        }
        return this.blocks.get(this.getByVariant(direction)).getSecond().get();
    }

//    public Block get(ModBlockFamily.Variant direction) {
//        BlockAdditional a = this.getByVariant(direction);
//        if (a != null && a.skipRegister) {
//            return a.blockSupplier.get();
//        }
//        return this.blocks.get(this.getByVariant(direction)).getSecond().get();
//    }

    public BlockAdditional getByVariant(ModBlockFamily.Variant variant) {
        for (BlockAdditional blockAdditional : this.blocks.keySet()) {
            if (blockAdditional.variant == variant) {
                return blockAdditional;
            }
        }
        return null;
    }

    public static Block getMainBy(Supplier<Block> block, Supplier<Block> textureFrom) {
        for (BlockManager blockManager : BLOCK_MANAGERS.get(BuiltInRegistries.BLOCK.getKey(block.get()).getNamespace())) {
            for (Pair<ResourceLocation, Supplier<Block>> e : blockManager.getBlocks().values()) {
                if (e.getSecond() == block && blockManager.getByVariant(ModBlockFamily.Variant.BASE) != null) {
                    return blockManager.baseBlock();
                }
            }
        }
        return textureFrom.get();
    }

    public HashMap<BlockAdditional, Pair<ResourceLocation, Supplier<Block>>> getBlocks() {
        // In the order the variants were added, so every run sees the same order.
        HashMap<BlockAdditional, Pair<ResourceLocation, Supplier<Block>>> map = new LinkedHashMap<>();
        this.blocks.forEach((ba, pair) -> {
            if (!ba.skipRegister)
                map.put(ba, pair);
        });
        return map;
    }

    public static Builder register(String name, Registrar<Block> register, BlockBehaviour.Properties properties, Collection<ModBlockFamily.Variant> variants) {
        return BlockManager.register(name, register, properties, () -> Blocks.AIR, variants);
    }

    public static Builder registerOneBlock(String name, Registrar<Block> register, Supplier<Block> block) {
        return new Builder(name, register).addVariant(ModBlockFamily.Variant.BASE, block);
    }

    public static Builder register(String name, Registrar<Block> register, BlockBehaviour.Properties properties, Supplier<Block> blockSupplier, Collection<ModBlockFamily.Variant> variants) {
        return register(name, (b) -> BlockSetType.values().forEach(type -> {
            if (name.contains(type.name())) {
                b.type(type);
            }
        }), register, properties, blockSupplier, variants);
    }

    // example of registering with own type of block
    /*public static Builder registerCedarOrSmth(String name, Registrar<Block> register, BlockBehaviour.Properties properties, Supplier<Block> blockSupplier, Collection<ModBlockFamily.Variant> variants) {
        return register(name, (b) -> b.type(ModBlockSetType.CEDAR), register, properties, blockSupplier, variants);
    }*/

    public static Builder register(String name, Consumer<Builder> consumer, Registrar<Block> register, BlockBehaviour.Properties properties, Supplier<Block> blockSupplier, Collection<ModBlockFamily.Variant> variants) {
        return BlockManager.register(name, consumer, register, () -> properties, blockSupplier, variants);
    }

    public static Builder register(String name, Consumer<Builder> consumer, Registrar<Block> register, Supplier<BlockBehaviour.Properties> properties, Supplier<Block> blockSupplier, Collection<ModBlockFamily.Variant> variants) {
        Builder builder = new Builder(name, register);
        consumer.accept(builder);
//        ModBlockSetType.values().forEach(mb -> {
//            if (name.contains(mb.name())) {
//                builder.type(mb);
//            }
//        });

        if (variants.contains(ModBlockFamily.Variant.BASE)) {
            Supplier<Block> baseBlock = () -> new Block(properties.get());
            builder.addVariant(ModBlockFamily.Variant.BASE, baseBlock);
            BlockManager.addDefaultVariants(builder, properties, baseBlock, variants);
        } else {
            builder.addVariant(ModBlockFamily.Variant.BASE, blockSupplier, BlockAdditional::skipRegister);
            BlockManager.addDefaultVariants(builder, properties, blockSupplier, variants);
        }
        return builder;
    }

    public static void addDefaultVariants(Builder builder, BlockBehaviour.Properties properties, Supplier<Block> blockSupplier, Collection<ModBlockFamily.Variant> variants) {
        BlockManager.addDefaultVariants(builder, () -> properties, blockSupplier, variants);
    }

    public static void addDefaultVariants(BlockManager.Builder builder, Supplier<BlockBehaviour.Properties> properties, Supplier<Block> blockSupplier, Collection<ModBlockFamily.Variant> variants) {
        String modId = builder.modid;
        WoodType woodType = WoodType.values().filter(p ->
                p.name().equals(builder.blockSetType.name())).findFirst().orElse(WoodType.OAK);
        Supplier<BlockState> baseBlockState = () -> BlockManager.BLOCK_MANAGERS.get(modId).stream().filter(blockManager ->
                blockManager.getName().equals(builder.name)).findFirst().orElseThrow().baseBlock().defaultBlockState();
        for (ModBlockFamily.Variant variant : variants) {
            if (variant != ModBlockFamily.Variant.BASE && variant.caliberIsLoaded()) {
                switch (variant) {
                    case ARCH -> builder.addVariant(variant, () -> new ArchBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case ARCH_HALF -> builder.addVariant(variant, () -> new HalfArchBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case ARCH_LARGE -> builder.addVariant(variant, () -> new LargeArchBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case ARCH_LARGE_HALF -> builder.addVariant(variant, () -> new LargeHalfArchBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case ARROWSLIT -> builder.addVariant(variant, () -> new ArrowSlitBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case BALUSTRADE -> builder.addVariant(variant, () -> new BalustradeBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case BUTTON -> builder.addVariant(variant, () -> new ButtonBlock(properties.get(), builder.blockSetType, 20, false), (b) -> b.textureFrom(blockSupplier));
                    case BEAM_DIAGONAL -> builder.addVariant(variant, () -> new DiagonalBeamBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case BEAM_HORIZONTAL -> builder.addVariant(variant, () -> new HorizontalBeamBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case BEAM_LINTEL -> builder.addVariant(variant, () -> new BeamLintelBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case BEAM_POSTS -> builder.addVariant(variant, () -> new BeamPostsBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case BEAM_VERTICAL -> builder.addVariant(variant, () -> new VerticalBeamBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case CAPITAL -> builder.addVariant(variant, () -> new CapitalBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case CORNER -> builder.addVariant(variant, () -> new CornerLayerBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case CORNER_SLAB -> builder.addVariant(variant, () -> new CornerSlabLayerBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case CORNER_SLAB_VERTICAL -> builder.addVariant(variant, () -> new VerticalCornerSlabLayerBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case DOOR -> builder.addVariant(variant, () -> new DoorBlock(properties.get(), builder.blockSetType), (b) -> b.lootGen(ModBlockLootTables::dropDoor).textureFrom(blockSupplier));
                    case DOOR_FRAME -> builder.addVariant(variant, () -> new DoorFrameBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case DOOR_FRAME_LINTEL -> builder.addVariant(variant, () -> new DoorFrameLintelBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case EIGHTH -> builder.addVariant(variant, () -> new EighthLayerBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case FENCE -> builder.addVariant(variant, () -> new FenceBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case FENCE_GATE -> builder.addVariant(variant, () -> new FenceGateBlock(properties.get(), woodType), (b) -> b.textureFrom(blockSupplier));
                    case LAYER -> builder.addVariant(variant, () -> new SlabLayerBlock(properties.get(), 1), (b) -> b.textureFrom(blockSupplier));
                    case LAYER_VERTICAL -> builder.addVariant(variant, () -> new VerticalSlabLayerBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case PILLAR -> builder.addVariant(variant, () -> new PillarLayerBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case PRESSURE_PLATE -> builder.addVariant(variant, () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, properties.get(), builder.blockSetType), (b) -> b.textureFrom(blockSupplier));
                    case QUARTER -> builder.addVariant(variant, () -> new QuarterLayerBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case QUARTER_VERTICAL -> builder.addVariant(variant, () -> new VerticalQuarterLayerBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case ROOF_22 -> builder.addVariant(variant, () -> new Roof22Block(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case ROOF_45 -> builder.addVariant(variant, () -> new Roof45Block(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case ROOF_67 -> builder.addVariant(variant, () -> new Roof67Block(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case ROOF_PEAK -> builder.addVariant(variant, () -> new RoofPeakBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case SIGN -> builder.addVariant(variant, () -> new StandingSignBlock(properties.get(), woodType), (b) -> b.textureFrom(blockSupplier));
                    case HANGING_SIGN -> builder.addVariant(variant, () -> new CeilingHangingSignBlock(properties.get(), woodType), (b) -> b.textureFrom(blockSupplier));
                    case SLAB -> builder.addVariant(variant, () -> new SlabBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case SLAB_VERTICAL -> builder.addVariant(variant, () -> new VerticalSlabBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case TALL_DOOR -> builder.addVariant(variant, () -> new TallDoorBlock(properties.get(), builder.blockSetType), (b) -> b.lootGen(ModBlockLootTables::dropTallDoor).textureFrom(blockSupplier));
                    case STAIRS -> builder.addVariant(variant, () -> new StairBlock(baseBlockState.get(), properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case TRAPDOOR -> builder.addVariant(variant, () -> new TrapDoorBlock(properties.get(), builder.blockSetType), (b) -> b.textureFrom(blockSupplier));
                    case WALL -> builder.addVariant(variant, () -> new WallBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case WALL_SIGN -> builder.addVariant(variant, () -> new WallSignBlock(properties.get(), woodType));
                    case WALL_HANGING_SIGN -> builder.addVariant(variant, () -> new WallHangingSignBlock(properties.get(), woodType));
                    case WINDOW -> builder.addVariant(variant, () -> new WindowBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                    case WINDOW_HALF -> builder.addVariant(variant, () -> new HalfWindowBlock(properties.get()), (b) -> b.textureFrom(blockSupplier));
                }
            }
        }
    }

    public static class Builder {

        public final String name;
        public final String modid;
        public BiFunction<String, Supplier<Block>, RegistryEntry<Block>> registerBlockFunc;
        public BlockSetType blockSetType = BlockSetType.STONE;
        public final List<BlockAdditional> blocks = new ArrayList<>();
        private final Assets.Builder assets = new Assets.Builder();

        public Builder(String name, Registrar<Block> registry) {
            this(name, registry.modId());
            this.registerBlockFunc = registry::register;
        }

        public Builder(String name, String modid) {
            this.name = name;
            this.modid = modid;
        }

        public Builder type(BlockSetType type) {
            this.blockSetType = type;
            return this;
        }

        /** Stone shapes use the stonecutter and a pickaxe; wood shapes the woodcutter and an axe, and burn. */
        public Builder kind(MaterialKind kind) {
            this.assets.kind = kind;
            return this;
        }

        /** The item the shapes are cut and crafted from; default: the family's full block. */
        public Builder ingredient(Supplier<? extends ItemLike> ingredient) {
            this.assets.ingredient = ingredient;
            return this;
        }

        /** The door a tall door is crafted from, and whose textures it takes. */
        public Builder door(Supplier<? extends ItemLike> door) {
            this.assets.door = door;
            return this;
        }

        /** The material word of the shapes' names; default: the id prefix in title case. */
        public Builder displayName(String displayName) {
            this.assets.displayName = displayName;
            return this;
        }

        /** The full-block model of whole layer stacks and double slabs; default: the full block's own model. */
        public Builder baseModel(ResourceLocation model) {
            this.assets.baseModel = model;
            return this;
        }

        /** Sets one texture slot (side, top, bottom, end, particle), replacing what the full block's model gives. */
        public Builder texture(String slot, ResourceLocation texture) {
            this.assets.textures.put(slot, texture);
            return this;
        }

        /** A shape of this family that another mod already registers, e.g. vanilla's oak slab. */
        public Builder sibling(ModBlockFamily.Variant variant, Supplier<Block> block) {
            this.assets.siblings.put(variant, block);
            return this;
        }

        /** The shapes render with cut-out transparency. */
        public Builder cutout() {
            this.assets.cutout = true;
            return this;
        }

        /** The tool that mines the shapes fastest; by default pickaxe for stone and axe for wood. */
        public Builder tool(MiningTool tool) {
            this.assets.tool = tool;
            return this;
        }

        /** The shapes are tinted like this block (grass, leaves). */
        public Builder tint(Supplier<Block> block) {
            this.assets.tint = block;
            return this;
        }

        public Builder registerBlockFunc(BiFunction<String, Supplier<Block>, RegistryEntry<Block>> registerBlockFunc) {
            this.registerBlockFunc = registerBlockFunc;
            return this;
        }

        public Builder addVariant(ModBlockFamily.Variant variant, Supplier<Block> blockSupplier) {
            this.addVariant(variant, blockSupplier, (b) -> {
            });
            return this;
        }

        public Builder addVariant(ModBlockFamily.Variant variant, Supplier<Block> block, Consumer<BlockAdditional> consumer) {
            this.addVariant(variant, block, null, consumer);
            return this;
        }

        public Builder addVariant(ModBlockFamily.Variant variant, Supplier<Block> block, BiFunction<String, Supplier<Block>, RegistryEntry<Block>> registerBlockFunc, Consumer<BlockAdditional> consumer) {
            BlockAdditional additional = new BlockAdditional(variant, block, registerBlockFunc);
            consumer.accept(additional);
            this.blocks.add(additional);
            return this;
        }

        public BlockManager build() {
            for (BlockAdditional block : this.blocks) {
                if (block.registerBlockFunc == null) {
                    block.registerBlockFunc = this.registerBlockFunc;
                }
            }

            return new BlockManager(this.name, this.blocks, this.blockSetType, this.modid, this.assets.build(this.blockSetType));
        }
    }

    /** A family's asset-engine inputs, fixed when the family is built. */
    public static final class Assets {
        public final MaterialKind kind;
        @Nullable public final Supplier<? extends ItemLike> ingredient;
        @Nullable public final Supplier<? extends ItemLike> door;
        @Nullable public final String displayName;
        @Nullable public final ResourceLocation baseModel;
        public final Map<String, ResourceLocation> textures;
        public final Map<ModBlockFamily.Variant, Supplier<Block>> siblings;
        public final boolean cutout;
        @Nullable public final Supplier<Block> tint;
        @Nullable public final MiningTool tool;

        private Assets(Builder b, MaterialKind kind) {
            this.kind = kind;
            this.ingredient = b.ingredient;
            this.door = b.door;
            this.displayName = b.displayName;
            this.baseModel = b.baseModel;
            this.textures = Map.copyOf(b.textures);
            this.siblings = Collections.unmodifiableMap(new EnumMap<>(b.siblings));
            this.cutout = b.cutout;
            this.tint = b.tint;
            this.tool = b.tool;
        }

        static final class Builder {
            MaterialKind kind;
            Supplier<? extends ItemLike> ingredient;
            Supplier<? extends ItemLike> door;
            String displayName;
            ResourceLocation baseModel;
            MiningTool tool;
            final Map<String, ResourceLocation> textures = new LinkedHashMap<>();
            final Map<ModBlockFamily.Variant, Supplier<Block>> siblings = new EnumMap<>(ModBlockFamily.Variant.class);
            boolean cutout;
            Supplier<Block> tint;

            Assets build(BlockSetType type) {
                // Without an explicit kind, a family whose block set is a wood type is wood.
                MaterialKind k = kind != null ? kind
                        : WoodType.values().anyMatch(w -> w.name().equals(type.name())) ? MaterialKind.WOOD : MaterialKind.STONE;
                return new Assets(this, k);
            }
        }
    }

    public static final class BlockAdditional {
        public final ModBlockFamily.Variant variant;
        public final Supplier<Block> blockSupplier;
        public boolean skipRegister; // used to register in manager, but don't register it in minecraft (basically when block already registered in mc)
        public BiConsumer<ModBlockLootTables, Block> lootGen = ModBlockLootTables::dropSelf;
        /** The block whose textures this shape's models use; null for a full block, which uses its own. */
        public Supplier<Block> textureSource;
        public BiConsumer<BlockManager, Pair<ResourceLocation, Supplier<Block>>> blockConsumer = (manager, b) -> {};
        public BiFunction<String, Supplier<Block>, RegistryEntry<Block>> registerBlockFunc;

        public BlockAdditional(ModBlockFamily.Variant variant, Supplier<Block> blockSupplier, BiFunction<String, Supplier<Block>, RegistryEntry<Block>> registerBlockFunc) {
            this.variant = variant;
            this.blockSupplier = blockSupplier;
            this.registerBlockFunc = registerBlockFunc;
        }

        public BlockAdditional afterRegister(BiConsumer<BlockManager, Pair<ResourceLocation, Supplier<Block>>> blockConsumer) {
            this.blockConsumer = blockConsumer;
            return this;
        }

        public BlockAdditional lootGen(BiConsumer<ModBlockLootTables, Block> lootGen) {
            this.lootGen = lootGen;
            return this;
        }

        public BlockAdditional textureFrom(Supplier<Block> textureSource) {
            this.textureSource = textureSource;
            return this;
        }

        public BlockAdditional skipRegister() {
            this.skipRegister = true;
            return this;
        }

    }
}
