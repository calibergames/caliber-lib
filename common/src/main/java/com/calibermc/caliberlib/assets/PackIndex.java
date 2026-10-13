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
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;
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

	/** Counts the files made and the thread time spent making them. */
	public static final class Timer {
		private final LongAdder nanos = new LongAdder();
		private final LongAdder count = new LongAdder();

		public long count() {
			return count.sum();
		}

		public long millis() {
			return nanos.sum() / 1_000_000;
		}
	}

	private final Map<String, TreeMap<String, Supplier<byte[]>>> files = new HashMap<>();
	@Nullable
	private Timer timer;
	@Nullable
	private Function<ResourceLocation, Supplier<byte[]>> unlisted;

	/** Adds a file; a path added twice keeps its first content. */
	public void add(String namespace, String path, Supplier<byte[]> content) {
		files.computeIfAbsent(namespace, ns -> new TreeMap<>()).putIfAbsent(path, content);
	}

	/** Times every file this index makes from now on. */
	public void timed(Timer timer) {
		this.timer = timer;
	}

	/**
	 * Answers lookups of files that are never listed (texture metadata),
	 * returning null when there is none.
	 */
	public void unlisted(Function<ResourceLocation, Supplier<byte[]>> lookup) {
		this.unlisted = lookup;
	}

	/** Every listed file, in namespace and path order. */
	public void forEach(BiConsumer<ResourceLocation, Supplier<byte[]>> action) {
		new TreeMap<>(files).forEach((ns, paths) -> paths.forEach((path, content) ->
				action.accept(new ResourceLocation(ns, path), content)));
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

	private IoSupplier<InputStream> open(ResourceLocation location, Supplier<byte[]> content) {
		Timer t = timer;
		return () -> {
			long start = System.nanoTime();
			try {
				return new ByteArrayInputStream(content.get());
			} catch (RuntimeException e) {
				if (LOGGED.incrementAndGet() <= LOG_LIMIT) {
					AssetEngine.LOGGER.error("Could not generate {}", location, e);
				}
				throw new IOException("Could not generate " + location, e);
			} finally {
				if (t != null) {
					t.nanos.add(System.nanoTime() - start);
					t.count.increment();
				}
			}
		};
	}
}
