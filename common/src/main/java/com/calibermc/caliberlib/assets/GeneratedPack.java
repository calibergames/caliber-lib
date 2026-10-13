package com.calibermc.caliberlib.assets;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Set;

/**
 * A pack held in memory: one for client resources, one for server data.
 * Every query reads the index; no query reads another pack.
 */
public final class GeneratedPack implements PackResources {

	/** Called once when the pack closes, e.g. to drop per-reload caches. */
	public interface OnClose {
		void close();
	}

	private final PackType type;
	private final PackIndex index;
	@Nullable
	private OnClose onClose;

	public GeneratedPack(PackType type, PackIndex index, @Nullable OnClose onClose) {
		this.type = type;
		this.index = index;
		this.onClose = onClose;
	}

	@Nullable
	@Override
	public IoSupplier<InputStream> getRootResource(String... elements) {
		return null;
	}

	@Nullable
	@Override
	public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
		return type == this.type ? index.get(location) : null;
	}

	@Override
	public void listResources(PackType type, String namespace, String path, ResourceOutput output) {
		if (type == this.type) {
			index.list(namespace, path, output);
		}
	}

	@Override
	public Set<String> getNamespaces(PackType type) {
		return type == this.type ? index.namespaces() : Set.of();
	}

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) {
		if (serializer == PackMetadataSection.TYPE) {
			return (T) new PackMetadataSection(AssetEngine.DESCRIPTION, AssetEngine.PACK_FORMAT);
		}
		return null;
	}

	@Override
	public String packId() {
		return AssetEngine.PACK_ID;
	}

	@Override
	public boolean isBuiltin() {
		return true;
	}

	@Override
	public synchronized void close() {
		if (onClose != null) {
			onClose.close();
			onClose = null;
		}
	}
}
