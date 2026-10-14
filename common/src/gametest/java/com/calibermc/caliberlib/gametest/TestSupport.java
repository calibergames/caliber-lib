package com.calibermc.caliberlib.gametest;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/** Lookups the tests share. */
final class TestSupport {

	static final String EMPTY_STRUCTURE = TestFamilies.MOD_ID + ":empty";

	private TestSupport() {
	}

	static ResourceLocation id(String path) {
		return new ResourceLocation(TestFamilies.MOD_ID, path);
	}

	/** A test family's block; fails the test when it is not registered. */
	static Block block(String path) {
		Block block = BuiltInRegistries.BLOCK.get(id(path));
		if (block == Blocks.AIR) {
			throw new GameTestAssertException("Not registered: " + id(path));
		}
		return block;
	}

	static Item item(String path) {
		Item item = BuiltInRegistries.ITEM.get(id(path));
		if (item == Items.AIR) {
			throw new GameTestAssertException("No item: " + id(path));
		}
		return item;
	}

	static void check(boolean condition, String message) {
		if (!condition) {
			throw new GameTestAssertException(message);
		}
	}
}
