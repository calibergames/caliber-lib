package com.calibermc.caliberlib.fabric;

import com.calibermc.caliberlib.platform.RegistryHelper;
import com.calibermc.caliberlib.registry.QueuedRegistrar;
import com.calibermc.caliberlib.registry.Registrar;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * {@link RegistryHelper} on Fabric and Quilt. Entries wait until the mod calls
 * {@link Registrar#finish()}: a block's supplier may read its family, which is
 * complete only once every entry of it has been added.
 * <p>
 * Late callbacks run from the library's {@code server} and {@code client}
 * entrypoints, which Fabric runs after every mod's {@code main} entrypoint;
 * one of the two runs in each game, so they run once.
 */
public class FabricRegistryHelper implements RegistryHelper {

	private record Callback<T>(String modId, Consumer<Registrar<T>> callback) {
	}

	private static final List<Callback<Block>> LATE_BLOCKS = new ArrayList<>();
	private static final List<Callback<Item>> LATE_ITEMS = new ArrayList<>();
	private static boolean lateDone;

	@Override
	public void lateBlocks(String modId, Consumer<Registrar<Block>> callback) {
		check();
		LATE_BLOCKS.add(new Callback<>(modId, callback));
	}

	@Override
	public void lateItems(String modId, Consumer<Registrar<Item>> callback) {
		check();
		LATE_ITEMS.add(new Callback<>(modId, callback));
	}

	private static void check() {
		if (lateDone) {
			throw new IllegalStateException("Late registration has already run");
		}
	}

	/** Runs the late callbacks; the server and client entrypoints call it. */
	static synchronized void runLate() {
		if (lateDone) {
			return;
		}
		lateDone = true;
		run(BuiltInRegistries.BLOCK, LATE_BLOCKS);
		run(BuiltInRegistries.ITEM, LATE_ITEMS);
	}

	private static <T> void run(Registry<T> registry, List<Callback<T>> callbacks) {
		for (Callback<T> c : callbacks) {
			// Entries register once the callback has added them all, as a family needs.
			QueuedRegistrar<T> registrar = new QueuedRegistrar<>(c.modId(), (id, value) -> Registry.register(registry, id, value));
			c.callback().accept(registrar);
			registrar.finish();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Registrar<T> create(ResourceKey<? extends Registry<T>> registry, String modId) {
		Registry<T> target = (Registry<T>) BuiltInRegistries.REGISTRY.get(registry.location());
		if (target == null) {
			throw new IllegalArgumentException("No built-in registry " + registry.location());
		}
		return new QueuedRegistrar<>(modId, (id, value) -> Registry.register(target, id, value));
	}
}
