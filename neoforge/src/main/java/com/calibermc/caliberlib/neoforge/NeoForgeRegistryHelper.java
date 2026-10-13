package com.calibermc.caliberlib.neoforge;

import com.calibermc.caliberlib.platform.RegistryHelper;
import com.calibermc.caliberlib.registry.Registrar;
import com.calibermc.caliberlib.registry.RegistryEntry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * {@link RegistryHelper} on NeoForge and Forge 1.20.1: a {@link DeferredRegister}
 * on the owning mod's event bus, so the registry event registers the entries in
 * the order they were added.
 */
public class NeoForgeRegistryHelper implements RegistryHelper {

	@Override
	public <T> Registrar<T> create(ResourceKey<? extends Registry<T>> registry, String modId) {
		DeferredRegister<T> register = DeferredRegister.create(registry, modId);
		ModContainer container = ModList.get().getModContainerById(modId)
				.orElseThrow(() -> new IllegalArgumentException("No mod " + modId));
		if (!(container instanceof FMLModContainer fml)) {
			throw new IllegalArgumentException("Mod " + modId + " has no event bus");
		}
		register.register(fml.getEventBus());
		return new ForgeRegistrar<>(modId, register);
	}

	@Override
	public void lateBlocks(String modId, Consumer<Registrar<Block>> callback) {
		LateRegistration.addBlocks(modId, callback);
	}

	@Override
	public void lateItems(String modId, Consumer<Registrar<Item>> callback) {
		LateRegistration.addItems(modId, callback);
	}

	private static final class ForgeRegistrar<T> implements Registrar<T> {

		private final String modId;
		private final DeferredRegister<T> register;
		private final List<RegistryEntry<T>> entries = new ArrayList<>();

		ForgeRegistrar(String modId, DeferredRegister<T> register) {
			this.modId = modId;
			this.register = register;
		}

		@Override
		public String modId() {
			return modId;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <V extends T> RegistryEntry<V> register(String name, Supplier<V> value) {
			ForgeEntry<V> entry = new ForgeEntry<>(register.register(name, value));
			entries.add((RegistryEntry<T>) entry);
			return entry;
		}

		@Override
		public Collection<RegistryEntry<T>> entries() {
			return Collections.unmodifiableList(entries);
		}

		@Override
		public void finish() {
			// The registry event registers the entries.
		}
	}

	private static final class ForgeEntry<V> implements RegistryEntry<V> {

		private final RegistryObject<V> object;

		ForgeEntry(RegistryObject<V> object) {
			this.object = object;
		}

		@Override
		public ResourceLocation id() {
			return object.getId();
		}

		@Override
		public V get() {
			if (!object.isPresent()) {
				throw new IllegalStateException("Not registered yet: " + object.getId());
			}
			return object.get();
		}
	}
}
