package com.calibermc.caliberlib.assets;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The files a generated pack serves: paths are known up front, and each
 * file's bytes are made when it is read. Built on one thread, then only
 * read, so lookups take no lock.
 */
public final class PackIndex {

	private static final int LOG_LIMIT = 50;
	private static final AtomicInteger LOGGED = new AtomicInteger();

	private final Map<String, TreeMap<String, Supplier<byte[]>>> files = new HashMap<>();
	@Nullable
	private Function<ResourceLocation, Supplier<byte[]>> unlisted;

	/** Adds a file; a path added twice keeps its first content. */
	public void add(String namespace, String path, Supplier<byte[]> content) {
		files.computeIfAbsent(namespace, ns -> new TreeMap<>()).putIfAbsent(path, content);
	}

	/**
	 * Answers lookups of files that are never listed (texture metadata),
	 * returning null when there is none.
	 */
	public void unlisted(Function<ResourceLocation, Supplier<byte[]>> lookup) {
		this.unlisted = lookup;
	}

	public Set<String> namespaces() {
		return files.keySet();
	}

	public int size() {
		return files.values().stream().mapToInt(Map::size).sum();
	}

	@Nullable
	public IoSupplier<InputStream> get(ResourceLocation location) {
		TreeMap<String, Supplier<byte[]>> ns = files.get(location.getNamespace());
		Supplier<byte[]> content = ns != null ? ns.get(location.getPath()) : null;
		if (content == null && unlisted != null) {
			content = unlisted.apply(location);
		}
		return content != null ? open(location, content) : null;
	}

	/** Lists every file under {@code dir} (e.g. {@code models}) in one namespace. */
	public void list(String namespace, String dir, PackResources.ResourceOutput output) {
		TreeMap<String, Supplier<byte[]>> ns = files.get(namespace);
		if (ns == null) {
			return;
		}
		String prefix = dir.endsWith("/") ? dir : dir + "/";
		for (Map.Entry<String, Supplier<byte[]>> e : ns.subMap(prefix, prefix + Character.MAX_VALUE).entrySet()) {
			ResourceLocation location = new ResourceLocation(namespace, e.getKey());
			output.accept(location, open(location, e.getValue()));
		}
	}

	private static IoSupplier<InputStream> open(ResourceLocation location, Supplier<byte[]> content) {
		return () -> {
			try {
				return new ByteArrayInputStream(content.get());
			} catch (RuntimeException e) {
				if (LOGGED.incrementAndGet() <= LOG_LIMIT) {
					AssetEngine.LOGGER.error("Could not generate {}", location, e);
				}
				throw new IOException("Could not generate " + location, e);
			}
		};
	}
}
