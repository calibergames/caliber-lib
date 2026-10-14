package com.calibermc.caliberlib.compat;

import com.calibermc.caliberlib.block.management.BlockManager;
import com.calibermc.caliberlib.config.ConfigFile;
import com.calibermc.caliberlib.data.ModBlockFamily.Variant;
import com.calibermc.caliberlib.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Shapes for other mods' blocks from {@code calibermod-compat.json} files
 * (the bundled list in the owner's jar, files in other mods' jars, and the
 * config folder). Only a mod that calls {@link #enable} gets them: CaliberLib
 * alone registers none.
 */
public final class CompatLists {

	/** Heap per registered shape with and without FerriteCore, as measured on 1.20.1 (bytes). */
	private static final long BYTES_WITH_FERRITECORE = 29 * 1024;
	private static final long BYTES_WITHOUT_FERRITECORE = 190 * 1024;

	private static final class State {
		final String owner;
		final ConfigFile config;
		final CompatLog log;
		final List<CompatEntry> read;
		final List<CompatEntry> merged;
		CompatPlan.Result result;
		String hash;
		final Map<String, List<BlockManager>> families = new HashMap<>();

		State(String owner, ConfigFile config, CompatLog log, List<CompatEntry> read, List<CompatEntry> merged) {
			this.owner = owner;
			this.config = config;
			this.log = log;
			this.read = read;
			this.merged = merged;
		}
	}

	@Nullable
	private static State state;

	private CompatLists() {
	}

	/**
	 * Reads the compat files and registers their entries once every mod's
	 * blocks are in. Call it while mods are constructed (NeoForge, Forge) or
	 * from a {@code main} entrypoint (Fabric).
	 *
	 * @param owner         the mod whose recipes and advancements the shapes get, and whose jar holds the bundled list
	 * @param bundledFolder the bundled list's folder in the owner's jar, e.g. {@code data/caliber/calibermod-compat}
	 * @param configFile    the owner's config file in the config folder, which gets a {@code [compat]} table
	 */
	public static synchronized void enable(String owner, String bundledFolder, String configFile) {
		if (state != null) {
			CompatLog.LOGGER.error("{} asked to own compat shapes, but {} already does; ignored", owner, state.owner);
			return;
		}
		ConfigFile config = config(Services.platform().configDir().resolve(configFile)).load();
		CompatLog log = new CompatLog(config.getBoolean("compat.debug"));
		List<CompatEntry> read = CompatSources.read(owner, bundledFolder, Services.platform().modFiles(),
				Services.platform().configDir(), log);
		State s = new State(owner, config, log, read, CompatMerge.merge(read, log));
		state = s;
		TreeSet<String> namespaces = new TreeSet<>();
		for (CompatEntry e : s.merged) {
			namespaces.add(CompatTypes.nameOf(e).getNamespace());
		}
		// Plan first, in load order, once every mod's blocks are in; then register namespace by namespace.
		Services.registry().lateBlocks(owner, r -> plan(s));
		for (String ns : namespaces) {
			Services.registry().lateBlocks(ns, r -> s.families.put(ns, CompatRegistration.blocks(owner, ns, s.result.planned(), r)));
			Services.registry().lateItems(ns, r -> CompatRegistration.items(s.families.getOrDefault(ns, List.of()), r));
		}
	}

	/** The compat keys of the owner's config file. */
	static ConfigFile config(java.nio.file.Path path) {
		ConfigFile c = new ConfigFile(path)
				.comment("compat", "Shapes for other mods' blocks, from calibermod-compat.json files.")
				.define("compat", "max_shapes", CompatPlan.Settings.DEFAULT_MAX_SHAPES,
						"The most compat shapes to register. Entries past it are refused whole, in load order;\n"
								+ "entries that hold released block ids never are.")
				.define("compat", "debug", false, "Lists every skipped entry in the log.")
				.comment("compat.mods", "Shapes for the blocks of these mods; false turns a mod's entries off.\nMods not listed are on.")
				.define("compat.mods", "chipped", false, "Chipped's thousands of palette blocks: off unless turned on here.")
				.comment("compat.types", "Shapes per type; false turns a type off.");
		for (String type : new TreeSet<>(CompatTypes.TYPES.keySet())) {
			c.define("compat.types", type, true, null);
		}
		return c;
	}

	private static void plan(State s) {
		CompatPlan.Settings settings = new CompatPlan.Settings(s.config.booleans("compat.mods"),
				s.config.booleans("compat.types"), s.config.getLong("compat.max_shapes"));
		CompatPlan.Game game = new CompatPlan.Game() {
			@Override
			public boolean registered(ResourceLocation id) {
				return BuiltInRegistries.BLOCK.containsKey(id);
			}

			@Override
			public boolean allowed(Variant variant) {
				return variant.caliberIsLoaded();
			}
		};
		CompatPlan.Result result = CompatPlan.plan(s.merged, settings, game, s.log);
		synchronized (CompatLists.class) {
			s.result = result;
			s.hash = CompatHash.of(result.planned());
		}
		summary(s);
	}

	private static void summary(State s) {
		CompatPlan.Result r = s.result;
		// Entries as the files list them, before merging.
		Map<CompatSource.Kind, Integer> kinds = new java.util.EnumMap<>(CompatSource.Kind.class);
		Map<String, int[]> perFile = new LinkedHashMap<>();
		for (CompatEntry e : s.read) {
			kinds.merge(e.source().kind(), 1, Integer::sum);
			perFile.computeIfAbsent(e.source().label(), k -> new int[2])[0]++;
		}
		List<String> bySource = new ArrayList<>();
		kinds.forEach((kind, n) -> bySource.add(kind.summary + " " + n));
		s.log.info(String.format("calibermod-compat: %d entries from %d source%s (%s), %d after merging; %d skipped (source block absent); "
						+ "%d switched off; %d refused (ceiling); %,d shapes registered",
				s.read.size(), kinds.size(), kinds.size() == 1 ? "" : "s", String.join(", ", bySource), r.entries(), r.absent(), r.switchedOff(), r.overCeiling(), r.shapes()));
		for (CompatPlan.Planned p : r.planned()) {
			perFile.computeIfAbsent(p.entry().source().label(), k -> new int[2])[1]++;
		}
		perFile.forEach((file, n) -> s.log.info("calibermod-compat: " + file + ": " + n[0] + " entries listed, "
				+ n[1] + " registering as their own family"));
		boolean ferrite = Services.platform().isModLoaded("ferritecore");
		new TreeMap<>(r.shapesPerMod()).forEach((mod, n) -> s.log.info(String.format("calibermod-compat: %s: %,d shapes, about %,.1f MB of heap%s",
				mod, n, n * (ferrite ? BYTES_WITH_FERRITECORE : BYTES_WITHOUT_FERRITECORE) / (1024.0 * 1024),
				ferrite ? "" : String.format(" (FerriteCore cuts it to about %,.1f MB)", n * BYTES_WITH_FERRITECORE / (1024.0 * 1024)))));
		s.log.info("calibermod-compat: list hash " + s.hash);
	}

	/** A world is about to load: warns about shapes it holds that are not registered now, and records what is. */
	public static void worldLoading(java.nio.file.Path worldDir) {
		List<String> current;
		Map<CompatEntry.Key, String> notRegistered;
		synchronized (CompatLists.class) {
			current = canonical();
			notRegistered = state != null && state.result != null ? state.result.notRegistered() : Map.of();
		}
		CompatWorldCheck.worldLoading(worldDir, current, notRegistered);
	}

	/** The registered list's hash, which server and client compare at login; the empty list's when nothing registered. */
	public static synchronized String hash() {
		return state != null && state.hash != null ? state.hash : CompatHash.of(List.of());
	}

	/** Whether any compat entry registered; when none did, clients without compat shapes are not refused. */
	public static synchronized boolean active() {
		return state != null && state.result != null && !state.result.planned().isEmpty();
	}

	/** The registered entries' canonical forms, sorted (for logging what differs from another list). */
	public static synchronized List<String> canonical() {
		return state != null && state.result != null ? CompatHash.canonicalList(state.result.planned()) : List.of();
	}

	/** Eight-byte hashes of the registered entries, sorted. */
	public static synchronized List<Long> shortHashes() {
		return state != null && state.result != null ? CompatHash.shortHashes(state.result.planned()) : List.of();
	}
}
