package com.calibermc.caliberlib.fabric;

import com.calibermc.caliberlib.platform.RegistryHelper;
import com.calibermc.caliberlib.registry.QueuedRegistrar;
import com.calibermc.caliberlib.registry.Registrar;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;

/**
 * {@link RegistryHelper} on Fabric and Quilt. Entries wait until the mod calls
 * {@link Registrar#finish()}: a block's supplier may read its family, which is
 * complete only once every entry of it has been added.
 */
public class FabricRegistryHelper implements RegistryHelper {

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
