package com.calibermc.caliberlib.platform;

import com.calibermc.caliberlib.registry.Registrar;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

/** Registration on the loader the game runs on. */
public interface RegistryHelper {

	<T> Registrar<T> create(ResourceKey<? extends Registry<T>> registry, String modId);
}
