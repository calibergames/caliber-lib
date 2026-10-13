package com.calibermc.caliberlib.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * A {@link Registrar} that holds its entries until {@link #finish()} and then
 * hands each new instance, in order, to a sink that registers it. Entries added
 * after {@code finish()} register at once.
 */
public class QueuedRegistrar<T> implements Registrar<T> {

	private final String modId;
	private final BiConsumer<ResourceLocation, T> sink;
	private final List<Entry<? extends T>> entries = new ArrayList<>();
	private final Set<ResourceLocation> ids = new HashSet<>();
	private int registered;
	private boolean finished;

	public QueuedRegistrar(String modId, BiConsumer<ResourceLocation, T> sink) {
		this.modId = modId;
		this.sink = sink;
	}

	@Override
	public String modId() {
		return modId;
	}

	@Override
	public <V extends T> RegistryEntry<V> register(String name, Supplier<V> value) {
		ResourceLocation id = new ResourceLocation(modId, name);
		if (!ids.add(id)) {
			throw new IllegalStateException("Already registered: " + id);
		}
		Entry<V> entry = new Entry<>(id, value);
		entries.add(entry);
		if (finished) {
			registerPending();
		}
		return entry;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<RegistryEntry<T>> entries() {
		return Collections.unmodifiableList((List<RegistryEntry<T>>) (List<?>) entries);
	}

	@Override
	public void finish() {
		finished = true;
		registerPending();
	}

	private void registerPending() {
		while (registered < entries.size()) {
			Entry<? extends T> entry = entries.get(registered++);
			sink.accept(entry.id, entry.create());
		}
	}

	private static final class Entry<V> implements RegistryEntry<V> {

		private final ResourceLocation id;
		private final Supplier<V> supplier;
		private V value;

		Entry(ResourceLocation id, Supplier<V> supplier) {
			this.id = id;
			this.supplier = supplier;
		}

		V create() {
			if (value == null) {
				value = supplier.get();
			}
			return value;
		}

		@Override
		public ResourceLocation id() {
			return id;
		}

		@Override
		public V get() {
			if (value == null) {
				throw new IllegalStateException("Not registered yet: " + id);
			}
			return value;
		}
	}
}
