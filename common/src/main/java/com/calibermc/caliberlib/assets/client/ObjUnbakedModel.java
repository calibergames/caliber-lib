package com.calibermc.caliberlib.assets.client;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * An OBJ block model with its texture variables, for loaders without a
 * built-in OBJ loader. It bakes to a plain vanilla model.
 */
public final class ObjUnbakedModel implements UnbakedModel {

	private static final int MAX_REFERENCES = 8;

	private final ObjModel obj;
	private final Map<String, String> textures;
	private final ObjModel.Options options;

	/**
	 * @param textures texture variable (without {@code #}) to a texture id or another {@code #variable}
	 */
	public ObjUnbakedModel(ObjModel obj, Map<String, String> textures, ObjModel.Options options) {
		this.obj = obj;
		this.textures = Map.copyOf(textures);
		this.options = options;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return List.of();
	}

	@Override
	public void resolveParents(Function<ResourceLocation, UnbakedModel> resolver) {
	}

	@Nullable
	@Override
	public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state,
			ResourceLocation location) {
		Function<String, TextureAtlasSprite> sprites = variable ->
				spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, texture(variable)));
		Map<Direction, List<BakedQuad>> culled = new EnumMap<>(Direction.class);
		List<BakedQuad> unculled = new ArrayList<>();
		obj.bake(sprites, state.getRotation(), options, (quad, cull) -> {
			if (cull == null) {
				unculled.add(quad);
			} else {
				culled.computeIfAbsent(cull, d -> new ArrayList<>()).add(quad);
			}
		});
		return new Baked(List.copyOf(unculled), Map.copyOf(culled), sprites.apply("#particle"));
	}

	/** Follows {@code #variable} references to a texture id; the missing texture when there is none. */
	private ResourceLocation texture(String variable) {
		String value = variable;
		for (int i = 0; i < MAX_REFERENCES && value != null && value.startsWith("#"); i++) {
			value = textures.get(value.substring(1));
		}
		ResourceLocation id = value == null ? null : ResourceLocation.tryParse(value);
		return id != null ? id : MissingTextureAtlasSprite.getLocation();
	}

	private record Baked(List<BakedQuad> unculled, Map<Direction, List<BakedQuad>> culled, TextureAtlasSprite particle)
			implements BakedModel {

		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
			return direction == null ? unculled : culled.getOrDefault(direction, List.of());
		}

		/** Off: Forge draws these OBJ faces without ambient occlusion, and the two loaders should look alike. */
		@Override
		public boolean useAmbientOcclusion() {
			return false;
		}

		@Override
		public boolean isGui3d() {
			return true;
		}

		@Override
		public boolean usesBlockLight() {
			return true;
		}

		@Override
		public boolean isCustomRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleIcon() {
			return particle;
		}

		@Override
		public ItemTransforms getTransforms() {
			return ItemTransforms.NO_TRANSFORMS;
		}

		@Override
		public ItemOverrides getOverrides() {
			return ItemOverrides.EMPTY;
		}
	}
}
