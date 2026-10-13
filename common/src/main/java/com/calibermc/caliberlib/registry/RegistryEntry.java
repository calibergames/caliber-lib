package com.calibermc.caliberlib.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * One object registered through a {@link Registrar}. {@link #get()} returns the
 * registered instance once it has been created, and throws before that.
 */
public interface RegistryEntry<T> extends Supplier<T> {

	ResourceLocation id();

	/** The same as {@link #id()}. */
	default ResourceLocation getId() {
		return id();
	}
}
