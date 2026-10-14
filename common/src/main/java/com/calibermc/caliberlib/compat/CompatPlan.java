package com.calibermc.caliberlib.compat;

import com.calibermc.caliberlib.assets.ShapeSpecs;
import com.calibermc.caliberlib.data.ModBlockFamily.Variant;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Decides, in load order, which entries register and with which shapes,
 * once every mod's blocks are registered: absent source blocks, switches,
 * the ceiling, and ids that are taken. Nothing here registers anything.
 */
public final class CompatPlan {

	/** What the plan asks of the game: which ids are registered, and which variants may register at all. */
	public interface Game {
		boolean registered(ResourceLocation id);

		/** Whether shapes of this variant register in this game (CaliberLib's Caliber gate). */
		boolean allowed(Variant variant);
	}

	/**
	 * The player's switches and the ceiling.
	 *
	 * @param mods       per source mod; a mod not listed is on, apart from {@link #OFF_BY_DEFAULT}
	 * @param types      per type; a type not listed is on
	 * @param maxShapes  the most compat shapes to register; released entries are never refused
	 */
	public record Settings(Map<String, Boolean> mods, Map<String, Boolean> types, long maxShapes) {
		/** Mods whose entries are off unless the config turns them on: Chipped, for its thousands of palette blocks. */
		public static final Set<String> OFF_BY_DEFAULT = Set.of("chipped");
		public static final long DEFAULT_MAX_SHAPES = 20_000;

		public static Settings defaults() {
			return new Settings(Map.of(), Map.of(), DEFAULT_MAX_SHAPES);
		}

		boolean modOn(String mod) {
			return mods.getOrDefault(mod, !OFF_BY_DEFAULT.contains(mod));
		}

		boolean typeOn(String type) {
			return types.getOrDefault(type, true);
		}
	}

	/**
	 * An entry that registers.
	 *
	 * @param entry     the merged entry
	 * @param type      its type
	 * @param name      the shapes' namespace and id prefix
	 * @param family    the family name, unique in the namespace
	 * @param variants  the shapes to register, in the entry's order
	 * @param door      the door a tall door is made from, when the shapes include one
	 * @param siblings  the source mod's own slab, stairs and wall
	 * @param baseModel the full-block model, or null for the source's own
	 */
	public record Planned(CompatEntry entry, CompatTypes.Type type, ResourceLocation name, String family,
			List<Variant> variants, @Nullable ResourceLocation door, Map<Variant, ResourceLocation> siblings,
			@Nullable ResourceLocation baseModel) {

		public boolean released() {
			return entry.source().released();
		}

		public ResourceLocation id(Variant v) {
			return new ResourceLocation(name.getNamespace(), name.getPath() + "_" + v.getName());
		}
	}

	/** The outcome: what registers, and the counts for the summary line. */
	public record Result(List<Planned> planned, int entries, int absent,
			int switchedOff, int overCeiling, int shapes, Map<String, Integer> shapesPerMod,
			Map<CompatEntry.Key, String> notRegistered) {
	}

	private CompatPlan() {
	}

	public static Result plan(List<CompatEntry> merged, Settings settings, Game game, CompatLog log) {
		List<Planned> planned = new ArrayList<>();
		Map<ResourceLocation, CompatEntry> claimed = new HashMap<>();
		Set<String> families = new HashSet<>();
		Map<String, Integer> perMod = new TreeMap<>();
		List<String> refusedByCeiling = new ArrayList<>();
		Map<CompatEntry.Key, String> notRegistered = new LinkedHashMap<>();
		int absent = 0;
		int switchedOff = 0;
		int shapes = 0;
		boolean ceilingReached = false;
		for (CompatEntry e : merged) {
			CompatTypes.Type type = CompatTypes.type(e.type());
			if (type == null) {
				continue;
			}
			if (!game.registered(e.block())) {
				absent++;
				notRegistered.put(e.key(), "its block " + e.block() + " is not registered (is its mod installed?)");
				log.debug(e.where() + ": " + e.block() + " is not registered; skipped");
				continue;
			}
			String mod = e.block().getNamespace();
			if (!settings.modOn(mod) || !settings.typeOn(e.type())) {
				switchedOff++;
				notRegistered.put(e.key(), settings.modOn(mod) ? "its type is switched off ([compat.types] " + e.type() + " = false)"
						: "its mod is switched off ([compat.mods] " + mod + " = false)");
				log.debug(e.where() + ": " + e.key() + " is switched off (" + (settings.modOn(mod) ? "type " + e.type() : "mod " + mod) + ")");
				continue;
			}
			ResourceLocation name = CompatTypes.nameOf(e);
			ResourceLocation door = null;
			List<Variant> variants = new ArrayList<>();
			for (String v : CompatMerge.variants(e)) {
				Variant variant = CompatTypes.variant(v);
				if (variant == null || ShapeSpecs.UNSUPPORTED.contains(variant) || !game.allowed(variant)) {
					log.debug(e.where() + ": no " + v + " shape here; left out");
					continue;
				}
				if (variant == Variant.TALL_DOOR) {
					door = CompatTypes.door(e.block());
					if (!game.registered(door)) {
						log.warn(e.where() + ": " + e.block() + " has no door " + door + "; its tall door is left out");
						door = null;
						continue;
					}
				}
				ResourceLocation id = new ResourceLocation(name.getNamespace(), name.getPath() + "_" + v);
				if (game.registered(id)) {
					log.warn(e.where() + ": " + id + " is already registered by its mod; that block stays and this shape is left out");
					continue;
				}
				CompatEntry holder = claimed.get(id);
				if (holder != null) {
					log.warn(e.where() + ": " + id + " is already made by " + holder.where() + " (" + holder.key() + "); left out here");
					continue;
				}
				variants.add(variant);
			}
			if (variants.isEmpty()) {
				log.warn(e.where() + ": " + e.key() + " has no shape left to register; skipped");
				notRegistered.put(e.key(), "none of its shapes can register here");
				continue;
			}
			if (!e.source().released() && (ceilingReached || shapes + variants.size() > settings.maxShapes())) {
				ceilingReached = true;
				refusedByCeiling.add(e.key().toString());
				notRegistered.put(e.key(), "it is past the ceiling ([compat] max_shapes = " + settings.maxShapes() + ")");
				continue;
			}
			for (Variant v : variants) {
				claimed.put(new ResourceLocation(name.getNamespace(), name.getPath() + "_" + v.getName()), e);
			}
			Map<Variant, ResourceLocation> siblings = new LinkedHashMap<>();
			for (Variant v : CompatTypes.SIBLINGS) {
				ResourceLocation sibling = CompatTypes.sibling(e.block(), v);
				if (game.registered(sibling)) {
					siblings.put(v, sibling);
				}
			}
			String family = e.type().equals("shingle") ? name.getPath() : e.block().getPath();
			if (!families.add(name.getNamespace() + ":" + family)) {
				family = family + "_" + e.type();
				families.add(name.getNamespace() + ":" + family);
			}
			planned.add(new Planned(e, type, name, family, List.copyOf(variants), door, siblings,
					CompatTypes.baseModel(e.block(), e.type())));
			shapes += variants.size();
			perMod.merge(mod, variants.size(), Integer::sum);
		}
		if (!refusedByCeiling.isEmpty()) {
			log.warn("calibermod-compat: " + refusedByCeiling.size() + (refusedByCeiling.size() == 1 ? " entry" : " entries") + " refused, past the ceiling of "
					+ settings.maxShapes() + " shapes ([compat] max_shapes): " + String.join(", ", refusedByCeiling));
		}
		return new Result(List.copyOf(planned), merged.size(), absent, switchedOff, refusedByCeiling.size(),
				shapes, perMod, notRegistered);
	}
}
