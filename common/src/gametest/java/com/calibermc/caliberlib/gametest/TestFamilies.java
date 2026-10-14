package com.calibermc.caliberlib.gametest;

import com.calibermc.caliberlib.assets.AssetEngine;
import com.calibermc.caliberlib.assets.MaterialKind;
import com.calibermc.caliberlib.assets.MiningTool;
import com.calibermc.caliberlib.block.management.BlockManager;
import com.calibermc.caliberlib.compat.CompatLists;
import com.calibermc.caliberlib.data.ModBlockFamily.Variant;
import com.calibermc.caliberlib.platform.Services;
import com.calibermc.caliberlib.registry.Registrar;
import com.calibermc.caliberlib.registry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.List;

/**
 * The families the game tests check, in the test mod's namespace: stone with
 * its own slab and vanilla's stairs and wall, oak with a tall door, crimson
 * (fire does not take it), wool (no tool), and mud brick, registered late.
 *
 * <p>It is also a source mod for compat lists: it registers source blocks
 * ({@code gt_src_*}) and lists them in its bundled compat folder and its own
 * {@code calibermod-compat.json}, and owns the compat shapes, as Caliber Mod
 * does in a game.
 */
public final class TestFamilies {

	public static final String MOD_ID = "caliberlib_gametest";
	private static Registrar<Block> lateBlocks;
	/** The compat source with its own slab, which wins over the listed one. */
	static RegistryEntry<SlabBlock> marbleSlab;

	private TestFamilies() {
	}

	/** Registers the families; the test mod calls it once, at its start. */
	public static void init() {
		AssetEngine.generateFor(MOD_ID, MOD_ID);
		Registrar<Block> blocks = Registrar.create(Registries.BLOCK, MOD_ID);
		Registrar<Item> items = Registrar.create(Registries.ITEM, MOD_ID);
		BlockManager.register("gt_stone", b -> b.sibling(Variant.STAIRS, () -> Blocks.STONE_BRICK_STAIRS)
						.sibling(Variant.WALL, () -> Blocks.STONE_BRICK_WALL),
				blocks, BlockBehaviour.Properties.copy(Blocks.STONE_BRICKS), () -> Blocks.STONE_BRICKS,
				List.of(Variant.SLAB, Variant.SLAB_VERTICAL, Variant.LAYER, Variant.CORNER, Variant.ARCH, Variant.PILLAR)).build();
		BlockManager.register("gt_oak", b -> b.type(BlockSetType.OAK).kind(MaterialKind.WOOD).door(() -> Items.OAK_DOOR)
						.sibling(Variant.SLAB, () -> Blocks.OAK_SLAB),
				blocks, BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), () -> Blocks.OAK_PLANKS,
				List.of(Variant.TALL_DOOR, Variant.BEAM_VERTICAL, Variant.SLAB_VERTICAL, Variant.ROOF_45)).build();
		BlockManager.register("gt_crimson", b -> b.type(BlockSetType.CRIMSON).kind(MaterialKind.WOOD), blocks,
				BlockBehaviour.Properties.copy(Blocks.CRIMSON_PLANKS), () -> Blocks.CRIMSON_PLANKS, List.of(Variant.BEAM_VERTICAL)).build();
		BlockManager.register("gt_wool", b -> b.tool(MiningTool.NONE), blocks, BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL),
				() -> Blocks.WHITE_WOOL, List.of(Variant.ARCH)).build();
		// Compat sources: stone with its own slab, planks with a door, stripped wood, a lamp whose light
		// depends on its state.
		blocks.register("gt_src_marble", () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
		marbleSlab = blocks.register("gt_src_marble_slab", () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.STONE)));
		blocks.register("gt_src_fir_planks", () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
		blocks.register("gt_src_fir_door", () -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_DOOR), BlockSetType.OAK));
		blocks.register("gt_src_stripped_fir_wood", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_OAK_WOOD)));
		blocks.register("gt_src_lamp", () -> new RedstoneLampBlock(BlockBehaviour.Properties.copy(Blocks.REDSTONE_LAMP)));
		blocks.finish();
		blocks.entries().forEach(e -> items.register(e.id().getPath(), () -> new BlockItem(e.get(), new Item.Properties())));
		items.finish();
		Services.registry().lateBlocks(MOD_ID, r -> {
			BlockManager.register("gt_mud", r, BlockBehaviour.Properties.copy(Blocks.MUD_BRICKS), () -> Blocks.MUD_BRICKS,
					List.of(Variant.ARCH, Variant.LAYER)).build();
			lateBlocks = r;
		});
		Services.registry().lateItems(MOD_ID, r -> lateBlocks.entries().forEach(e ->
				r.register(e.id().getPath(), () -> new BlockItem(e.get(), new Item.Properties()))));
		CompatLists.enable(MOD_ID, "data/" + MOD_ID + "/calibermod-compat", MOD_ID + "-common.toml");
	}
}
