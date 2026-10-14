package com.calibermc.caliberlib.compat;

import com.calibermc.caliberlib.assets.MaterialKind;
import com.calibermc.caliberlib.block.management.ModBlockHelper;
import com.calibermc.caliberlib.data.ModBlockFamily.Variant;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** The entry types, their default shapes, and the rules that turn an entry into a family's inputs. */
public final class CompatTypes {

	/** A type: its default variant set (the library's variant sets) and its material kind. */
	public record Type(String name, List<Variant> defaults, MaterialKind kind) {
	}

	public static final Map<String, Type> TYPES = Map.of(
			"stone", new Type("stone", ModBlockHelper.STONE_VARIANTS, MaterialKind.STONE),
			"planks", new Type("planks", ModBlockHelper.PLANK_VARIANTS, MaterialKind.WOOD),
			"shingle", new Type("shingle", ModBlockHelper.ROOF_VARIANTS, MaterialKind.WOOD),
			"stripped_wood", new Type("stripped_wood", ModBlockHelper.STRIPPED_WOOD_VARIANTS, MaterialKind.WOOD));

	/** Types the format names that a later version supports; entries of these types are skipped for now. */
	public static final Set<String> NOT_YET = Set.of("terracotta", "wool", "thatch", "board");

	/** The source mod's blocks a family uses instead of making its own: the stone trim set. */
	public static final List<Variant> SIBLINGS = List.of(Variant.SLAB, Variant.STAIRS, Variant.WALL);

	private static final String[][] NAME_ENDINGS = {{"_bricks", "_brick"}, {"_tiles", "_tile"}, {"_planks", ""},
			{"_wood", ""}, {"_log", ""}, {"_stem", ""}, {"_hyphae", ""}};

	private CompatTypes() {
	}

	@Nullable
	public static Type type(String name) {
		return TYPES.get(name);
	}

	/** A variant by its name in compat files (the constant in lower case), or null. */
	@Nullable
	public static Variant variant(String name) {
		try {
			Variant v = Variant.valueOf(name.toUpperCase(Locale.ROOT));
			return v.getName().equals(name) ? v : null;
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	/**
	 * The shapes' default namespace and id prefix: the source block's id with
	 * a trailing material word made singular or dropped; shingles add
	 * {@code _shingle}.
	 */
	public static ResourceLocation defaultName(ResourceLocation block, String type) {
		String path = block.getPath();
		for (String[] ending : NAME_ENDINGS) {
			if (path.endsWith(ending[0]) && path.length() > ending[0].length()) {
				path = path.substring(0, path.length() - ending[0].length()) + ending[1];
				break;
			}
		}
		if (type.equals("shingle")) {
			path += "_shingle";
		}
		return new ResourceLocation(block.getNamespace(), path);
	}

	/** The entry's namespace and id prefix: its own {@code name}, or the default. */
	public static ResourceLocation nameOf(CompatEntry e) {
		return e.name() != null ? e.name() : defaultName(e.block(), e.type());
	}

	/** The material word of the shapes' names: the id prefix in title case ("dark_fir" gives "Dark Fir"). */
	public static String displayName(ResourceLocation name) {
		StringBuilder b = new StringBuilder();
		for (String word : name.getPath().split("_")) {
			if (word.isEmpty()) {
				continue;
			}
			if (b.length() > 0) {
				b.append(' ');
			}
			b.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
		}
		return b.toString();
	}

	/**
	 * The full-block model of whole layer stacks and double slabs: for
	 * stripped wood, the matching stripped log's (or stem's) model; for the
	 * others, null (the source's own).
	 */
	@Nullable
	public static ResourceLocation baseModel(ResourceLocation block, String type) {
		if (!type.equals("stripped_wood")) {
			return null;
		}
		String path = block.getPath();
		if (path.endsWith("_wood")) {
			path = path.substring(0, path.length() - "_wood".length()) + "_log";
		} else if (path.endsWith("_hyphae")) {
			path = path.substring(0, path.length() - "_hyphae".length()) + "_stem";
		} else {
			return null;
		}
		return new ResourceLocation(block.getNamespace(), "block/" + path);
	}

	/**
	 * Where the source mod's own shape of this variant would be: its
	 * default name's prefix (from the source block, whatever the entry's
	 * {@code name}) plus the variant, e.g. {@code regions_unexplored:chalk_slab}.
	 */
	public static ResourceLocation sibling(ResourceLocation block, Variant variant) {
		return new ResourceLocation(block.getNamespace(), sourcePrefix(block) + "_" + variant.getName());
	}

	/** The door a planks family's tall door is made from: {@code <ns>:<prefix>_door}. */
	public static ResourceLocation door(ResourceLocation block) {
		return new ResourceLocation(block.getNamespace(), sourcePrefix(block) + "_door");
	}

	/** The block set type a wood family's doors use, if the source mod registers one: {@code <ns>:<prefix>}. */
	public static String blockSetTypeName(ResourceLocation block) {
		return block.getNamespace() + ":" + sourcePrefix(block);
	}

	private static String sourcePrefix(ResourceLocation block) {
		return defaultName(block, "").getPath();
	}
}
