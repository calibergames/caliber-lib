package com.calibermc.caliberlib.platform;

import com.calibermc.caliberlib.registry.Registrar;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

/** Registration on the loader the game runs on. */
public interface RegistryHelper {

	<T> Registrar<T> create(ResourceKey<? extends Registry<T>> registry, String modId);

	/**
	 * Runs {@code callback} once every mod has registered its own blocks, with a
	 * registrar for {@code modId} that registers at once. For blocks made from
	 * other mods' blocks. Call it while mods are being constructed (NeoForge,
	 * Forge) or from a {@code main} entrypoint (Fabric).
	 */
	void lateBlocks(String modId, Consumer<Registrar<Block>> callback);

	/** As {@link #lateBlocks}, for the items of late blocks; runs after every late block callback. */
	void lateItems(String modId, Consumer<Registrar<Item>> callback);
}
