package com.calibermc.caliberlib.assets;

import com.calibermc.caliberlib.block.management.BlockManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlagSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Generates the resources and data of block families at load time, in
 * memory, from the shape specs: blockstates, models, item models, names,
 * loot tables, recipes, advancements and tags.
 *
 * <p>A mod opts in per namespace with {@link #generateFor}. The generated
 * packs sit at the bottom of the pack list, below vanilla, so any file a
 * mod or a resource pack ships at the same path wins.
 */
public final class AssetEngine {

	/** The id of both generated packs. Worlds save it, so it never changes. */
	public static final String PACK_ID = "caliberlib:generated";
	public static final int PACK_FORMAT = 15;
	static final Component TITLE = Component.literal("Caliber shapes");
	static final Component DESCRIPTION = Component.literal("Caliber shapes (generated)");
	static final Logger LOGGER = LoggerFactory.getLogger("CaliberLib/assets");

	private static final PackSource SOURCE = PackSource.create(UnaryOperator.identity(), false);
	private static final Map<String, String> OWNERS = new ConcurrentHashMap<>();
	@Nullable
	private static volatile Function<List<Material>, GeneratedPack> clientPacks;
	@Nullable
	private static PackIndex data;

	private AssetEngine() {
	}

	/**
	 * Generates the files of every family registered in {@code namespace}.
	 * Recipes and advancements go in the {@code owner} mod's namespace.
	 */
	public static void generateFor(String owner, String namespace) {
		OWNERS.put(namespace, owner);
	}

	@Nullable
	static String owner(String namespace) {
		return OWNERS.get(namespace);
	}

	/** Set on the client only: makes the client pack, which reads textures from the other packs. */
	public static void setClientPacks(Function<List<Material>, GeneratedPack> factory) {
		clientPacks = factory;
	}

	/** The generated pack of this type, or null when nothing is generated. */
	@Nullable
	public static Pack pack(PackType type) {
		if (OWNERS.isEmpty() || type == PackType.CLIENT_RESOURCES && clientPacks == null) {
			return null;
		}
		Pack.Info info = new Pack.Info(DESCRIPTION, PACK_FORMAT, FeatureFlagSet.of());
		return Pack.create(PACK_ID, TITLE, true, id -> open(type), info, type, Pack.Position.BOTTOM, true, SOURCE);
	}

	private static GeneratedPack open(PackType type) {
		if (type == PackType.SERVER_DATA) {
			return new GeneratedPack(type, data(), null);
		}
		return clientPacks.apply(materials());
	}

	/** The data depends only on the families, the specs and the loaded mods, so it is built once. */
	private static synchronized PackIndex data() {
		if (data == null) {
			List<Material> materials = materials();
			data = DataGenerator.index(materials, ShapeSpecs.get());
			LOGGER.info("Generated data for {} families: {} files", materials.size(), data.size());
		}
		return data;
	}

	/** The families of every opted-in namespace, sorted by id. */
	public static List<Material> materials() {
		List<Material> out = new ArrayList<>();
		for (List<BlockManager> managers : BlockManager.BLOCK_MANAGERS.values()) {
			for (BlockManager manager : managers) {
				String owner = owner(manager.modId());
				if (owner == null) {
					continue;
				}
				Material material = Material.of(manager, owner);
				if (material != null) {
					out.add(material);
				}
			}
		}
		out.sort((a, b) -> a.id().toString().compareTo(b.id().toString()));
		return List.copyOf(out);
	}

	/**
	 * The pack ids a data reload opens, with the generated pack kept in:
	 * {@code /datapack disable} and other mods' reloads would otherwise drop
	 * every generated loot table until the next full reload.
	 */
	public static Collection<String> keepGenerated(Collection<String> ids, PackRepository repository) {
		if (ids.contains(PACK_ID) || !repository.getAvailableIds().contains(PACK_ID)) {
			return ids;
		}
		List<String> out = new ArrayList<>(ids.size() + 1);
		out.add(PACK_ID);
		out.addAll(ids);
		return out;
	}
}
