package com.calibermc.caliberlib.registry;

import com.calibermc.caliberlib.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Registers a mod's objects of one registry on every loader.
 * <ul>
 * <li>Entries register in the order they were added.</li>
 * <li>Each entry's supplier runs once; the entry then returns that instance, so
 * a later entry's supplier can read an earlier entry's value.</li>
 * <li>{@link RegistryEntry#get()} before the entry is created throws an
 * {@link IllegalStateException} naming the id.</li>
 * <li>Each mod calls {@link #finish()} once its entries are added (from its
 * common start). On NeoForge and Forge the registry event registers them and
 * {@code finish()} does nothing; on Fabric {@code finish()} registers them.
 * On Fabric an entry added after {@code finish()} registers at once.</li>
 * </ul>
 */
public interface Registrar<T> {

	static <T> Registrar<T> create(ResourceKey<? extends Registry<T>> registry, String modId) {
		return Services.registry().create(registry, modId);
	}

	String modId();

	<V extends T> RegistryEntry<V> register(String name, Supplier<V> value);

	/** Every entry, in the order they were added. */
	Collection<RegistryEntry<T>> entries();

	/** The same as {@link #entries()}. */
	default Collection<RegistryEntry<T>> getEntries() {
		return entries();
	}

	/** Registers the entries where the loader needs it; safe to call more than once. */
	void finish();
}
