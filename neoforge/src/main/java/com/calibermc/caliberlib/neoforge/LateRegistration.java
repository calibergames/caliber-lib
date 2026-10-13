package com.calibermc.caliberlib.neoforge;

import com.calibermc.caliberlib.CaliberLib;
import com.calibermc.caliberlib.registry.QueuedRegistrar;
import com.calibermc.caliberlib.registry.Registrar;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Late registration on NeoForge and Forge 1.20.1. Mods are not ordered unless
 * they ask to be, so the only point where every mod's blocks are in is inside
 * the block registry event, after every mod's own listeners. A lowest-priority
 * listener on the bus of the last mod in load order runs there; it registers
 * through that event.
 */
final class LateRegistration {

	private record Callback<T>(String modId, Consumer<Registrar<T>> callback) {
	}

	private static final List<Callback<Block>> BLOCKS = new ArrayList<>();
	private static final List<Callback<Item>> ITEMS = new ArrayList<>();
	private static boolean attached;

	private LateRegistration() {
	}

	static synchronized void addBlocks(String modId, Consumer<Registrar<Block>> callback) {
		attach();
		BLOCKS.add(new Callback<>(modId, callback));
	}

	static synchronized void addItems(String modId, Consumer<Registrar<Item>> callback) {
		attach();
		ITEMS.add(new Callback<>(modId, callback));
	}

	private static void attach() {
		if (attached) {
			return;
		}
		attached = true;
		List<ModContainer> containers = new ArrayList<>();
		ModList.get().forEachModInOrder(containers::add);
		FMLModContainer last = null;
		for (int i = containers.size() - 1; i >= 0 && last == null; i--) {
			if (containers.get(i) instanceof FMLModContainer fml) {
				last = fml;
			}
		}
		if (last == null) {
			throw new IllegalStateException("No mod with an event bus to register late blocks on");
		}
		CaliberLib.LOGGER.debug("Late registration runs on the event bus of {}", last.getModId());
		last.getEventBus().addListener(EventPriority.LOWEST, LateRegistration::onRegister);
	}

	private static void onRegister(RegisterEvent event) {
		if (event.getRegistryKey().equals(Registries.BLOCK)) {
			run(event, Registries.BLOCK, BLOCKS);
		} else if (event.getRegistryKey().equals(Registries.ITEM)) {
			run(event, Registries.ITEM, ITEMS);
		}
	}

	private static <T> void run(RegisterEvent event, ResourceKey<Registry<T>> key, List<Callback<T>> callbacks) {
		for (Callback<T> c : callbacks) {
			// Entries register through the event once the callback has added them all, as a family needs.
			QueuedRegistrar<T> registrar = new QueuedRegistrar<>(c.modId(), (id, value) -> event.register(key, id, () -> value));
			c.callback().accept(registrar);
			registrar.finish();
		}
	}
}
