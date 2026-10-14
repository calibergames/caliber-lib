package com.calibermc.caliberlib.compat;

import com.calibermc.caliberlib.block.management.BlockManager;
import com.calibermc.caliberlib.data.ModBlockFamily.Variant;
import com.calibermc.caliberlib.registry.Registrar;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Registers planned entries as families, with their items. */
final class CompatRegistration {

	private CompatRegistration() {
	}

	/** Registers the families of one namespace; returns them for their items. */
	static List<BlockManager> blocks(String owner, String namespace, List<CompatPlan.Planned> planned, Registrar<Block> registrar) {
		List<BlockManager> out = new ArrayList<>();
		for (CompatPlan.Planned p : planned) {
			if (!p.name().getNamespace().equals(namespace)) {
				continue;
			}
			Block source = BuiltInRegistries.BLOCK.get(p.entry().block());
			BlockManager manager = BlockManager.register(p.family(), b -> {
				b.idPrefix(p.name().getPath()).owner(owner).kind(p.type().kind()).type(blockSetType(p))
						.displayName(CompatTypes.displayName(p.name()));
				if (p.door() != null) {
					ResourceLocation door = p.door();
					b.door(() -> BuiltInRegistries.ITEM.get(door));
				}
				if (p.baseModel() != null) {
					b.baseModel(p.baseModel());
				}
				p.entry().textures().forEach(b::texture);
				for (Map.Entry<Variant, ResourceLocation> s : p.siblings().entrySet()) {
					ResourceLocation id = s.getValue();
					b.sibling(s.getKey(), () -> BuiltInRegistries.BLOCK.get(id));
				}
			}, registrar, () -> properties(source), () -> source, p.variants()).build();
			out.add(manager);
		}
		return out;
	}

	/** A plain block item for every shape of these families. */
	static void items(List<BlockManager> managers, Registrar<Item> registrar) {
		for (BlockManager m : managers) {
			m.getBlocks().values().forEach(pair -> registrar.register(pair.getFirst().getPath(),
					() -> new BlockItem(pair.getSecond().get(), new Item.Properties())));
		}
	}

	/**
	 * The source mod's own block set type ({@code biomesoplenty:fir}) when it
	 * registers one; otherwise oak for wood and stone for stone. It sets the
	 * sounds of tall doors.
	 */
	static BlockSetType blockSetType(CompatPlan.Planned p) {
		String name = CompatTypes.blockSetTypeName(p.entry().block());
		// Vanilla's types have bare names ("crimson").
		String bare = name.startsWith("minecraft:") ? name.substring("minecraft:".length()) : name;
		return BlockSetType.values().filter(t -> t.name().equals(name) || t.name().equals(bare)).findFirst()
				.orElse(p.type().kind() == com.calibermc.caliberlib.assets.MaterialKind.WOOD ? BlockSetType.OAK : BlockSetType.STONE);
	}

	/**
	 * The source block's behaviour as plain values, read from its default
	 * state. {@code Properties.copy} would carry the source's callbacks, which
	 * read the source block's own state properties and fail on our shapes.
	 */
	static BlockBehaviour.Properties properties(Block source) {
		BlockState s = source.defaultBlockState();
		BlockBehaviour.Properties p = BlockBehaviour.Properties.of()
				.strength(source.defaultDestroyTime(), source.getExplosionResistance())
				.sound(s.getSoundType())
				.mapColor(s.getMapColor(EmptyBlockGetter.INSTANCE, BlockPos.ZERO))
				.instrument(s.instrument())
				.pushReaction(s.getPistonPushReaction())
				.friction(source.getFriction())
				.speedFactor(source.getSpeedFactor())
				.jumpFactor(source.getJumpFactor());
		int light = s.getLightEmission();
		if (light > 0) {
			p.lightLevel(state -> light);
		}
		if (s.requiresCorrectToolForDrops()) {
			p.requiresCorrectToolForDrops();
		}
		if (s.ignitedByLava()) {
			p.ignitedByLava();
		}
		if (!s.canOcclude()) {
			p.noOcclusion();
		}
		return p;
	}
}
