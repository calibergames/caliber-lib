package com.calibermc.caliberlib.compat;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * One entry of a compat file, after its file's checks.
 *
 * @param block    the source block
 * @param type     the type (stone, planks, shingle, stripped_wood); null only for a {@code remove} without a type
 * @param variants the shapes to register, as variant names; null when the file gives none (the type's default set applies)
 * @param name     the shapes' namespace and id prefix; null for the default
 * @param textures texture slots set by hand, all in the source block's namespace
 * @param madeFor  the source-mod versions the entry was written for (informational)
 * @param remove   drops earlier entries with this block (and type, if given)
 * @param source   the file it came from
 * @param index    its index in that file's {@code entries}
 */
public record CompatEntry(ResourceLocation block, @Nullable String type, @Nullable List<String> variants,
		@Nullable ResourceLocation name, Map<String, ResourceLocation> textures, @Nullable String madeFor, boolean remove,
		CompatSource source, int index) {

	/** The merge key: an entry is identified by its block and type. */
	public record Key(ResourceLocation block, String type) {
		@Override
		public String toString() {
			return block + " (" + type + ")";
		}
	}

	public Key key() {
		return new Key(block, type);
	}

	public String where() {
		return source + " entry " + index;
	}
}
