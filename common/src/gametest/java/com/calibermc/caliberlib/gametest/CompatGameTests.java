package com.calibermc.caliberlib.gametest;

import com.calibermc.caliberlib.assets.AssetEngine;
import com.calibermc.caliberlib.assets.Material;
import com.calibermc.caliberlib.assets.MaterialKind;
import com.calibermc.caliberlib.compat.CompatLists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

import static com.calibermc.caliberlib.gametest.TestSupport.EMPTY_STRUCTURE;
import static com.calibermc.caliberlib.gametest.TestSupport.check;

/**
 * Compat lists, end to end: the test mod's bundled files (one of them
 * released), its own {@code calibermod-compat.json}, and the shapes they
 * register for its source blocks and for vanilla's calcite.
 */
public class CompatGameTests {

	static final List<String> REGISTERED = List.of(
			"caliberlib_gametest:gt_src_marble_arch", "caliberlib_gametest:gt_src_marble_corner",
			"caliberlib_gametest:gt_src_marble_slab_vertical", "caliberlib_gametest:gt_src_marble_wall",
			// Added by the mod's own file to the released entry.
			"caliberlib_gametest:gt_src_marble_pillar",
			"caliberlib_gametest:gt_src_fir_arch", "caliberlib_gametest:gt_src_fir_slab_vertical",
			"caliberlib_gametest:gt_src_fir_tall_door", "caliberlib_gametest:gt_src_fir_shingle_roof_45",
			"caliberlib_gametest:gt_src_stripped_fir_beam_vertical", "caliberlib_gametest:gt_src_lamp_arch",
			// In a namespace no mod owns, as Caliber Mod's shapes for other mods' blocks are.
			"caliberlib_gametest_compat:calcite_arch", "caliberlib_gametest_compat:calcite_pillar");

	private static Block block(String id) {
		Block b = BuiltInRegistries.BLOCK.get(new ResourceLocation(id));
		check(b != Blocks.AIR, "Not registered: " + id);
		return b;
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void listedShapesRegisterWithTheirItems(GameTestHelper helper) {
		for (String id : REGISTERED) {
			Block b = block(id);
			check(BuiltInRegistries.ITEM.get(new ResourceLocation(id)) instanceof BlockItem bi && bi.getBlock() == b, id + " has no item");
		}
		check(CompatLists.active() && CompatLists.hash().length() == 64, "the compat list is active with a hash");
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void absentTakenAndForeignEntriesRegisterNothing(GameTestHelper helper) {
		check(block("caliberlib_gametest:gt_src_marble_slab") == TestFamilies.marbleSlab.get(), "the source mod's own slab stays");
		for (String id : List.of("caliberlib_gametest:gt_src_missing_arch", "minecraft:stone_arch", "minecraft:calcite_arch")) {
			check(!BuiltInRegistries.BLOCK.containsKey(new ResourceLocation(id)), id + " should not register");
		}
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void shapesTakeTheSourcesPlainProperties(GameTestHelper helper) {
		// The lamp's light depends on its own "lit" property; the shape has a constant, the unlit lamp's.
		BlockState lampArch = block("caliberlib_gametest:gt_src_lamp_arch").defaultBlockState();
		check(lampArch.getLightEmission() == 0, "lamp arch light " + lampArch.getLightEmission());
		Block marbleArch = block("caliberlib_gametest:gt_src_marble_arch");
		check(marbleArch.defaultBlockState().requiresCorrectToolForDrops(), "marble shapes need a pickaxe to drop");
		check(marbleArch.defaultDestroyTime() == Blocks.STONE.defaultDestroyTime(), "marble hardness");
		check(block("caliberlib_gametest:gt_src_fir_arch").defaultBlockState().ignitedByLava(), "fir shapes catch fire from lava");
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void compatShapesGetTheirData(GameTestHelper helper) {
		var server = helper.getLevel().getServer();
		var access = helper.getLevel().registryAccess();
		Block arch = block("caliberlib_gametest_compat:calcite_arch");
		long cut = server.getRecipeManager().getRecipes().stream()
				.filter(r -> r.getType() == RecipeType.STONECUTTING && r.getResultItem(access).is(arch.asItem()))
				.filter(r -> r.getIngredients().stream().anyMatch(i -> i.test(new ItemStack(Items.CALCITE))))
				.peek(r -> check(r.getId().getNamespace().equals(TestFamilies.MOD_ID), "recipe owned by the test mod: " + r.getId()))
				.count();
		check(cut == 1, "calcite arch cut from calcite: " + cut);
		BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));
		List<ItemStack> drops = Block.getDrops(arch.defaultBlockState(), helper.getLevel(), pos, null, null, new ItemStack(Items.DIAMOND_PICKAXE));
		check(drops.size() == 1 && drops.get(0).is(arch.asItem()), "calcite arch drops itself: " + drops);
		check(arch.defaultBlockState().is(BlockTags.MINEABLE_WITH_PICKAXE), "stone shapes: pickaxe");
		check(block("caliberlib_gametest:gt_src_fir_arch").defaultBlockState().is(BlockTags.MINEABLE_WITH_AXE), "planks shapes: axe");
		check(block("caliberlib_gametest:gt_src_stripped_fir_beam_vertical").defaultBlockState().is(BlockTags.MINEABLE_WITH_AXE), "stripped wood shapes: axe");
		long door = server.getRecipeManager().getRecipes().stream()
				.filter(r -> r.getResultItem(access).is(block("caliberlib_gametest:gt_src_fir_tall_door").asItem())).count();
		check(door == 1, "the tall door has its recipe: " + door);
		helper.succeed();
	}

	private static Material material(String id) {
		return AssetEngine.materials().stream().filter(m -> m.id().toString().equals(id)).findFirst()
				.orElseThrow(() -> new net.minecraft.gametest.framework.GameTestAssertException("No family " + id));
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void compatFamiliesGiveTheEngineTheirInputs(GameTestHelper helper) {
		Material marble = material("caliberlib_gametest:gt_src_marble");
		check(marble.owner().equals(TestFamilies.MOD_ID) && marble.kind() == MaterialKind.STONE, "marble owner and kind");
		check(marble.displayName().equals("Gt Src Marble"), "marble name: " + marble.displayName());
		check(new ResourceLocation("caliberlib_gametest:gt_src_marble_slab").equals(marble.siblings().get("slab")), "marble's own slab: " + marble.siblings());
		check(marble.source().equals(new ResourceLocation("caliberlib_gametest:gt_src_marble")) && marble.manager().equals("gt_src_marble"), "marble source and family");
		Material fir = material("caliberlib_gametest:gt_src_fir");
		check(fir.kind() == MaterialKind.WOOD && new ResourceLocation("caliberlib_gametest:gt_src_fir_door").equals(fir.door()), "fir is wood with its door: " + fir.door());
		check(fir.manager().equals("gt_src_fir_planks"), "fir family name: " + fir.manager());
		check(material("caliberlib_gametest:gt_src_fir_shingle").manager().equals("gt_src_fir_shingle"), "shingle family name");
		Material stripped = material("caliberlib_gametest:gt_src_stripped_fir");
		check(new ResourceLocation("caliberlib_gametest:block/gt_src_stripped_fir_log").equals(stripped.baseModel()), "stripped wood base model: " + stripped.baseModel());
		check(new ResourceLocation("caliberlib_gametest:block/gt_src_stripped_fir_log").equals(stripped.textures().get("side")), "the entry's texture: " + stripped.textures());
		Material calcite = material("caliberlib_gametest_compat:calcite");
		check(calcite.owner().equals(TestFamilies.MOD_ID), "a family in a namespace no mod owns keeps its owner");
		check(calcite.shapes().size() == 2, "calcite shapes: " + calcite.shapes());
		helper.succeed();
	}
}
