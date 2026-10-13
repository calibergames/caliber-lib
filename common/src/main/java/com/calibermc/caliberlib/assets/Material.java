package com.calibermc.caliberlib.assets;

import com.calibermc.caliberlib.block.management.BlockManager;
import com.calibermc.caliberlib.data.ModBlockFamily;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/**
 * One family as the asset engine sees it: ids and strings only, taken once
 * when a pack opens, after every block has registered.
 *
 * @param id          the shapes' namespace and id prefix, e.g. {@code biomesoplenty:fir}
 * @param owner       the mod whose recipes and advancements these are
 * @param manager     the family name, e.g. {@code fir_planks}
 * @param source      the full block the shapes copy
 * @param ingredient  the item they are cut and crafted from
 * @param door        the door a tall door is crafted from
 * @param baseModel   the full-block model; null means the source's own
 * @param textures    texture slots set by hand, replacing what the source's model gives
 * @param siblings    variant name to block id, for every shape of the family, its own and other mods'
 * @param shapes      the shapes the family registers
 * @param tint        the block whose colour the shapes take
 */
public record Material(ResourceLocation id, String owner, String manager, MaterialKind kind, String displayName,
		ResourceLocation source, ResourceLocation ingredient, @Nullable ResourceLocation door,
		@Nullable ResourceLocation baseModel, Map<String, ResourceLocation> textures,
		Map<String, ResourceLocation> siblings, List<Shape> shapes, boolean cutout, @Nullable ResourceLocation tint) {

	/** A registered shape: its variant name (as the specs name it) and block id. */
	public record Shape(String variant, ResourceLocation block) {
	}

	public String namespace() {
		return id.getNamespace();
	}

	/** Reads a family, or returns null when none of its shapes is registered. */
	@Nullable
	static Material of(BlockManager manager, String owner) {
		BlockManager.Assets assets = manager.assets();
		List<Shape> shapes = new ArrayList<>();
		Map<String, ResourceLocation> siblings = new LinkedHashMap<>();
		for (Map.Entry<BlockManager.BlockAdditional, Pair<ResourceLocation, Supplier<Block>>> e : manager.getBlocks().entrySet()) {
			ModBlockFamily.Variant variant = e.getKey().variant;
			ResourceLocation block = e.getValue().getFirst();
			if (variant == ModBlockFamily.Variant.BASE || !BuiltInRegistries.BLOCK.containsKey(block)) {
				continue;
			}
			shapes.add(new Shape(variant.getName(), block));
			siblings.put(variant.getName(), block);
		}
		if (shapes.isEmpty()) {
			return null;
		}
		assets.siblings.forEach((variant, block) -> siblings.putIfAbsent(variant.getName(), key(block.get())));
		Block base = manager.baseBlock();
		ResourceLocation source = key(base);
		ResourceLocation ingredient = assets.ingredient != null ? itemKey(assets.ingredient.get()) : itemKey(base);
		ResourceLocation door = assets.door != null ? itemKey(assets.door.get()) : null;
		String display = assets.displayName != null ? assets.displayName : titleCase(manager.idPrefix());
		ResourceLocation tint = assets.tint != null ? key(assets.tint.get()) : null;
		return new Material(new ResourceLocation(manager.modId(), manager.idPrefix()), owner, manager.getName(), assets.kind,
				display, source, ingredient, door, assets.baseModel, assets.textures, Map.copyOf(siblings),
				List.copyOf(shapes), assets.cutout, tint);
	}

	private static ResourceLocation key(Block block) {
		return BuiltInRegistries.BLOCK.getKey(block);
	}

	/** The item's id; {@code minecraft:air} when the block has no item. */
	private static ResourceLocation itemKey(ItemLike like) {
		return BuiltInRegistries.ITEM.getKey(like.asItem());
	}

	/** "stripped_oak" gives "Stripped Oak". */
	static String titleCase(String path) {
		StringBuilder out = new StringBuilder();
		for (String word : path.split("_")) {
			if (word.isEmpty()) {
				continue;
			}
			if (!out.isEmpty()) {
				out.append(' ');
			}
			out.append(word.substring(0, 1).toUpperCase(Locale.ROOT)).append(word.substring(1));
		}
		return out.toString();
	}
}
