package com.calibermc.caliberlib.fabric;

import com.calibermc.caliberlib.assets.AssetEngine;
import com.calibermc.caliberlib.block.properties.ModWoodType;
import com.calibermc.caliberlib.client.SignMaterials;
import com.calibermc.caliberlib.fabric.client.ObjModelProvider;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * Client set-up on Fabric and Quilt, after late registration: the OBJ roof
 * models, sign materials of the library's wood types, cut-out render layers
 * and colour handlers of generated shapes.
 */
final class FabricClientHooks {

	private FabricClientHooks() {
	}

	static void init() {
		ModelLoadingRegistry.INSTANCE.registerResourceProvider(ObjModelProvider::new);
		// Sign materials for the namespaced wood types, under their own namespace, as Forge's Sheets.addWoodType does.
		for (WoodType type : ModWoodType.SIGN_TYPES) {
			ResourceLocation id = new ResourceLocation(type.name());
			Sheets.SIGN_MATERIALS.putIfAbsent(type, new Material(Sheets.SIGN_SHEET, id.withPrefix("entity/signs/")));
			Sheets.HANGING_SIGN_MATERIALS.putIfAbsent(type, new Material(Sheets.SIGN_SHEET, id.withPrefix("entity/signs/hanging/")));
		}
		SignMaterials.check(ModWoodType.SIGN_TYPES);
		for (Block block : AssetEngine.cutoutBlocks()) {
			BlockRenderLayerMap.INSTANCE.putBlock(block, RenderType.cutout());
		}
		AssetEngine.tintedBlocks().forEach((source, blocks) -> {
			Block[] shapes = blocks.toArray(Block[]::new);
			ColorProviderRegistry.BLOCK.register((state, level, pos, tint) ->
					Minecraft.getInstance().getBlockColors().getColor(source.defaultBlockState(), level, pos, tint), shapes);
			// Items take the block's colour outside a world, as vanilla's grass and leaves items do.
			ColorProviderRegistry.ITEM.register((stack, tint) ->
					Minecraft.getInstance().getBlockColors().getColor(source.defaultBlockState(), null, null, tint), shapes);
		});
	}
}
