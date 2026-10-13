package com.calibermc.caliberlib.assets;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** What the engine asks the game about blocks and items; tests answer from fixtures. */
public interface GameView {

	boolean hasItem(ResourceLocation item);

	boolean hasRecipeSerializer(ResourceLocation serializer);

	/** The values of the block's {@code layers} property, or none. */
	List<Integer> layers(ResourceLocation block);

	/** The block's default state as property name to value name. */
	Map<String, String> defaultState(ResourceLocation block);

	/** The block an item places, or null. */
	@Nullable
	ResourceLocation blockOf(ResourceLocation item);

	/** The game's registries. */
	GameView LIVE = new GameView() {
		@Override
		public boolean hasItem(ResourceLocation item) {
			return BuiltInRegistries.ITEM.getOptional(item).filter(i -> i != Items.AIR).isPresent();
		}

		@Override
		public boolean hasRecipeSerializer(ResourceLocation serializer) {
			return BuiltInRegistries.RECIPE_SERIALIZER.containsKey(serializer);
		}

		@Override
		public List<Integer> layers(ResourceLocation block) {
			Property<?> layers = BuiltInRegistries.BLOCK.get(block).getStateDefinition().getProperty("layers");
			return layers instanceof IntegerProperty counts ? List.copyOf(counts.getPossibleValues()) : List.of();
		}

		@Override
		public Map<String, String> defaultState(ResourceLocation block) {
			Block b = BuiltInRegistries.BLOCK.getOptional(block).orElse(Blocks.AIR);
			BlockState state = b.defaultBlockState();
			Map<String, String> out = new LinkedHashMap<>();
			for (Property<?> property : state.getProperties()) {
				out.put(property.getName(), valueName(state, property));
			}
			return out;
		}

		private <T extends Comparable<T>> String valueName(BlockState state, Property<T> property) {
			return property.getName(state.getValue(property));
		}

		@Nullable
		@Override
		public ResourceLocation blockOf(ResourceLocation item) {
			Item i = BuiltInRegistries.ITEM.get(item);
			return i instanceof BlockItem blockItem ? BuiltInRegistries.BLOCK.getKey(blockItem.getBlock()) : null;
		}
	};
}
