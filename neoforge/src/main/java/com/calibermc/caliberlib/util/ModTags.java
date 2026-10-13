package com.calibermc.caliberlib.util;

import com.calibermc.caliberlib.CaliberLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static class Blocks {

        /* Shapes */
        public static final TagKey<Block> ARCHES = tag("shapes/arches");
        public static final TagKey<Block> ARROWSLITS = tag("shapes/arrowslits");
        public static final TagKey<Block> BALUSTRADES = tag("shapes/balustrades");
        public static final TagKey<Block> DIAGONAL_BEAMS = tag("shapes/diagonal_beams");
        public static final TagKey<Block> HORIZONTAL_BEAMS = tag("shapes/horizontal_beams");
        public static final TagKey<Block> BEAM_LINTELS = tag("shapes/beam_lintels");
        public static final TagKey<Block> BEAM_POSTS = tag("shapes/beam_posts");
        public static final TagKey<Block> VERTICAL_BEAMS = tag("shapes/vertical_beams");
        public static final TagKey<Block> CAPTIALS = tag("shapes/capitals");
        public static final TagKey<Block> COLUMNS = tag("shapes/columns");
        public static final TagKey<Block> CORNERS = tag("shapes/corners");
        public static final TagKey<Block> CORNER_SLABS = tag("shapes/corner_slabs");
        public static final TagKey<Block> DOOR_FRAMES = tag("shapes/door_frames");
        public static final TagKey<Block> DOOR_FRAME_LINTELS = tag("shapes/door_frame_lintels");
        public static final TagKey<Block> EIGHTHS = tag("shapes/eighths");
        public static final TagKey<Block> HALF_ARCHES = tag("shapes/half_arches");
        public static final TagKey<Block> HALF_LARGE_ARCHES= tag("shapes/half_large_arches");
        public static final TagKey<Block> HALF_WINDOWS = tag("shapes/half_windows");
        public static final TagKey<Block> LARGE_ARCHES = tag("shapes/large_arches");
        public static final TagKey<Block> LAYERS = tag("shapes/layers");
        public static final TagKey<Block> PILLARS = tag("shapes/pillars");
        public static final TagKey<Block> QUARTERS = tag("shapes/quarters");
        public static final TagKey<Block> ROOF_22S = tag("shapes/roof_22s");
        public static final TagKey<Block> ROOF_45S = tag("shapes/roof_45s");
        public static final TagKey<Block> ROOF_67S = tag("shapes/roof_67s");
        public static final TagKey<Block> ROOF_PEAKS = tag("shapes/roof_peaks");
        public static final TagKey<Block> TALL_IRON_DOORS = tag("shapes/tall_iron_doors");
        public static final TagKey<Block> TALL_WOODEN_DOORS = tag("shapes/tall_wooden_doors");
        public static final TagKey<Block> VERTICAL_CORNER_SLABS = tag("shapes/vertical_corner_slabs");
        public static final TagKey<Block> VERTICAL_LAYERS = tag("shapes/vertical_layers");
        public static final TagKey<Block> VERTICAL_QUARTERS = tag("shapes/vertical_quarters");
        public static final TagKey<Block> VERTICAL_SLABS = tag("shapes/vertical_slabs");
        public static final TagKey<Block> WINDOWS = tag("shapes/windows");


        /* Forge Block Tags */
        public static final TagKey<Block> GRANITE = forgeTag("granite");
        public static final TagKey<Block> MARBLE = forgeTag("marble");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation("caliber", name));
        }
        private static TagKey<Block> forgeTag(String name) {
            return BlockTags.create(new ResourceLocation("forge", name));
        }

    }

    public static class Items {
        /* Shapes */
        public static final TagKey<Item> ARCHES = tag("shapes/arches");
        public static final TagKey<Item> ARROWSLITS = tag("shapes/arrowslits");
        public static final TagKey<Item> BALUSTRADES = tag("shapes/balustrades");
        public static final TagKey<Item> DIAGONAL_BEAMS = tag("shapes/diagonal_beams");
        public static final TagKey<Item> HORIZONTAL_BEAMS = tag("shapes/horizontal_beams");
        public static final TagKey<Item> BEAM_LINTELS = tag("shapes/beam_lintels");
        public static final TagKey<Item> BEAM_POSTS = tag("shapes/beam_posts");
        public static final TagKey<Item> VERTICAL_BEAMS = tag("shapes/vertical_beams");
        public static final TagKey<Item> CAPTIALS = tag("shapes/capitals");
        public static final TagKey<Item> COLUMNS = tag("shapes/columns");
        public static final TagKey<Item> CORNERS = tag("shapes/corners");
        public static final TagKey<Item> CORNER_SLABS = tag("shapes/corner_slabs");
        public static final TagKey<Item> DOOR_FRAMES = tag("shapes/door_frames");
        public static final TagKey<Item> DOOR_FRAME_LINTELS = tag("shapes/door_frame_lintels");
        public static final TagKey<Item> EIGHTHS = tag("shapes/eighths");
        public static final TagKey<Item> HALF_ARCHES = tag("shapes/half_arches");
        public static final TagKey<Item> HALF_LARGE_ARCHES= tag("shapes/half_large_arches");
        public static final TagKey<Item> HALF_WINDOWS = tag("shapes/half_windows");
        public static final TagKey<Item> LARGE_ARCHES = tag("shapes/large_arches");
        public static final TagKey<Item> LAYERS = tag("shapes/layers");
        public static final TagKey<Item> PILLARS = tag("shapes/pillars");
        public static final TagKey<Item> QUARTERS = tag("shapes/quarters");
        public static final TagKey<Item> ROOF_22S = tag("shapes/roof_22s");
        public static final TagKey<Item> ROOF_45S = tag("shapes/roof_45s");
        public static final TagKey<Item> ROOF_67S = tag("shapes/roof_67s");
        public static final TagKey<Item> ROOF_PEAKS = tag("shapes/roof_peaks");
        public static final TagKey<Item> TALL_IRON_DOORS = tag("shapes/tall_iron_doors");
        public static final TagKey<Item> TALL_WOODEN_DOORS = tag("shapes/tall_wooden_doors");
        public static final TagKey<Item> VERTICAL_CORNER_SLABS = tag("shapes/vertical_corner_slabs");
        public static final TagKey<Item> VERTICAL_LAYERS = tag("shapes/vertical_layers");
        public static final TagKey<Item> VERTICAL_QUARTERS = tag("shapes/vertical_quarters");
        public static final TagKey<Item> VERTICAL_SLABS = tag("shapes/vertical_slabs");
        public static final TagKey<Item> WINDOWS = tag("shapes/windows");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(new ResourceLocation("caliber", name));
        }

        private static TagKey<Item> forgeTag(String name) {
            return ItemTags.create(new ResourceLocation("forge", name));
        }
    }

}

