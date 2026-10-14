package com.calibermc.caliberlib.assets;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/** Client resources from mod jars, first jar first, then the test classpath (vanilla and CaliberLib). */
final class JarResources implements ResourceManager {

	private final List<ZipFile> jars = new ArrayList<>();
	private final ClasspathResources classpath = new ClasspathResources();
	private final Set<String> namespaces = new HashSet<>(classpath.getNamespaces());

	JarResources(List<Path> paths) {
		try {
			for (Path path : paths) {
				ZipFile zip = new ZipFile(path.toFile());
				jars.add(zip);
				zip.stream().map(ZipEntry::getName).filter(n -> n.startsWith("assets/") && n.indexOf('/', 7) > 7)
						.forEach(n -> namespaces.add(n.substring(7, n.indexOf('/', 7))));
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public Optional<Resource> getResource(ResourceLocation location) {
		String name = "assets/" + location.getNamespace() + "/" + location.getPath();
		for (ZipFile zip : jars) {
			ZipEntry entry = zip.getEntry(name);
			if (entry != null) {
				byte[] bytes;
				try {
					bytes = zip.getInputStream(entry).readAllBytes();
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
				PackResources pack = classpath.listPacks().findFirst().orElseThrow();
				return Optional.of(new Resource(pack, () -> new ByteArrayInputStream(bytes)));
			}
		}
		return classpath.getResource(location);
	}

	@Override
	public Set<String> getNamespaces() {
		return Set.copyOf(namespaces);
	}

	@Override
	public List<Resource> getResourceStack(ResourceLocation location) {
		return getResource(location).stream().toList();
	}

	@Override
	public Map<ResourceLocation, Resource> listResources(String path, Predicate<ResourceLocation> filter) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<ResourceLocation, List<Resource>> listResourceStacks(String path, Predicate<ResourceLocation> filter) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Stream<PackResources> listPacks() {
		return classpath.listPacks();
	}
}
