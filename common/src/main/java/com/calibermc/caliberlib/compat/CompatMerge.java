package com.calibermc.caliberlib.compat;

import com.calibermc.caliberlib.data.ModBlockFamily.Variant;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Merges the entries of every file, in load order. An entry is keyed by
 * block and type; a later entry with the same key takes the earlier one's
 * place. A released entry (from a bundled {@code released-*.json}) can gain
 * shapes and textures from a later entry, but never loses shapes or changes
 * its name. A config {@code remove} drops earlier entries.
 */
public final class CompatMerge {

	private CompatMerge() {
	}

	public static List<CompatEntry> merge(List<CompatEntry> entries, CompatLog log) {
		Map<CompatEntry.Key, CompatEntry> merged = new LinkedHashMap<>();
		for (CompatEntry e : entries) {
			if (e.remove()) {
				int removed = 0;
				for (Iterator<CompatEntry> it = merged.values().iterator(); it.hasNext(); ) {
					CompatEntry old = it.next();
					if (old.block().equals(e.block()) && (e.type() == null || e.type().equals(old.type()))) {
						it.remove();
						removed++;
						log.info(e.where() + " removes " + old.key() + " from " + old.source());
					}
				}
				if (removed == 0) {
					log.debug(e.where() + ": nothing to remove for " + e.block());
				}
				continue;
			}
			CompatEntry old = merged.get(e.key());
			if (old == null) {
				merged.put(e.key(), e);
			} else if (old.source().released()) {
				merged.put(e.key(), extendReleased(old, e, log));
			} else {
				merged.put(e.key(), e);
				log.info(e.where() + " replaces " + old.key() + " from " + old.source());
			}
		}
		return List.copyOf(merged.values());
	}

	/** A released entry keeps its name and shapes; a later entry may add shapes and set textures. */
	private static CompatEntry extendReleased(CompatEntry released, CompatEntry later, CompatLog log) {
		List<String> shapes = new ArrayList<>(variants(released));
		List<String> added = new ArrayList<>();
		for (String v : variants(later)) {
			if (!shapes.contains(v)) {
				shapes.add(v);
				added.add(v);
			}
		}
		List<String> kept = new ArrayList<>(variants(released));
		kept.removeAll(variants(later));
		if (!kept.isEmpty()) {
			log.warn(later.where() + ": " + released.key() + " holds released ids; its shapes " + kept + " stay");
		}
		ResourceLocation name = released.name();
		if (later.name() != null && !CompatTypes.nameOf(later).equals(CompatTypes.nameOf(released))) {
			log.warn(later.where() + ": " + released.key() + " holds released ids; its name " + CompatTypes.nameOf(released) + " stays");
		}
		Map<String, ResourceLocation> textures = later.textures().isEmpty() ? released.textures() : later.textures();
		if (!added.isEmpty()) {
			log.info(later.where() + " adds " + added + " to " + released.key() + " from " + released.source());
		}
		return new CompatEntry(released.block(), released.type(), List.copyOf(shapes), name, textures, released.madeFor(), false,
				released.source(), released.index());
	}

	/** The entry's shapes as variant names: its own list, or its type's default set. */
	static List<String> variants(CompatEntry e) {
		if (e.variants() != null) {
			return e.variants();
		}
		List<String> out = new ArrayList<>();
		for (Variant v : Objects.requireNonNull(CompatTypes.type(e.type())).defaults()) {
			out.add(v.getName());
		}
		return out;
	}
}
