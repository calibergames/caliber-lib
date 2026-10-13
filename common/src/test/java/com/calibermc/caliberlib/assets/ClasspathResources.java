package com.calibermc.caliberlib.assets;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Client resources read from the test classpath: vanilla's (the client
 * resources jar), CaliberLib's own templates and the test fixtures.
 */
public final class ClasspathResources implements ResourceManager {

	private static final PackResources SOURCE = new PackResources() {
		@Nullable
		@Override
		public IoSupplier<InputStream> getRootResource(String... elements) {
			return null;
		}

		@Nullable
		@Override
		public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
			return null;
		}

		@Override
		public void listResources(PackType type, String namespace, String path, ResourceOutput output) {
		}

		@Override
		public Set<String> getNamespaces(PackType type) {
			return Set.of();
		}

		@Nullable
		@Override
		public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) {
			return null;
		}

		@Override
		public String packId() {
			return "classpath";
		}

		@Override
		public void close() {
		}
	};

	@Override
	public Optional<Resource> getResource(ResourceLocation location) {
		URL url = url(location);
		return url == null ? Optional.empty() : Optional.of(new Resource(SOURCE, url::openStream));
	}

	@Nullable
	static URL url(ResourceLocation location) {
		return ClasspathResources.class.getClassLoader()
				.getResource("assets/" + location.getNamespace() + "/" + location.getPath());
	}

	@Override
	public Set<String> getNamespaces() {
		return Set.of("minecraft", "caliberlib", "fixturemod");
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
		return Stream.of(SOURCE);
	}
}
