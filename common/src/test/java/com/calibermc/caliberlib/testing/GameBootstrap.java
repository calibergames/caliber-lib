package com.calibermc.caliberlib.testing;

import net.minecraft.SharedConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.block.Block;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.function.Supplier;

/** Starts the game's registries once for tests, and makes blocks outside the registry. */
public final class GameBootstrap {

	private static boolean booted;

	private GameBootstrap() {
	}

	public static synchronized void boot() {
		if (!booted) {
			SharedConstants.tryDetectVersion();
			Bootstrap.bootStrap();
			booted = true;
		}
	}

	/**
	 * A block made after the registries froze. The block registry hands out
	 * holders for new blocks only before it freezes, so this opens that for
	 * the call; the block stays unregistered. It sets two private fields of
	 * {@link MappedRegistry} ({@code frozen}, {@code unregisteredIntrusiveHolders})
	 * by their Mojang names, which the dev classpath uses.
	 */
	public static synchronized <T extends Block> T block(Supplier<T> factory) {
		boot();
		try {
			Field holders = MappedRegistry.class.getDeclaredField("unregisteredIntrusiveHolders");
			Field frozen = MappedRegistry.class.getDeclaredField("frozen");
			holders.setAccessible(true);
			frozen.setAccessible(true);
			Object holdersBefore = holders.get(BuiltInRegistries.BLOCK);
			boolean frozenBefore = frozen.getBoolean(BuiltInRegistries.BLOCK);
			holders.set(BuiltInRegistries.BLOCK, new IdentityHashMap<>());
			frozen.setBoolean(BuiltInRegistries.BLOCK, false);
			try {
				return factory.get();
			} finally {
				holders.set(BuiltInRegistries.BLOCK, holdersBefore);
				frozen.setBoolean(BuiltInRegistries.BLOCK, frozenBefore);
			}
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(e);
		}
	}
}
