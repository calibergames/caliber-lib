package com.calibermc.caliberlib.platform;

import java.util.ServiceLoader;

/**
 * The loader-specific implementations of the platform interfaces, found with
 * {@link ServiceLoader}: each loader's jar lists its implementations in
 * {@code META-INF/services}. Each is loaded on first use, so a missing one
 * fails only where it is needed.
 */
public final class Services {

	private Services() {
	}

	public static PlatformHelper platform() {
		return PlatformHolder.INSTANCE;
	}

	public static RegistryHelper registry() {
		return RegistryHolder.INSTANCE;
	}

	public static PlatformHooks hooks() {
		return HooksHolder.INSTANCE;
	}

	private static final class HooksHolder {
		static final PlatformHooks INSTANCE = load(PlatformHooks.class);
	}

	private static final class PlatformHolder {
		static final PlatformHelper INSTANCE = load(PlatformHelper.class);
	}

	private static final class RegistryHolder {
		static final RegistryHelper INSTANCE = load(RegistryHelper.class);
	}

	static <T> T load(Class<T> type) {
		return ServiceLoader.load(type, Services.class.getClassLoader()).findFirst()
				.orElseThrow(() -> new IllegalStateException("No implementation of " + type.getName() + " for this loader"));
	}
}
