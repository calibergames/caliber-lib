package com.calibermc.caliberlib.neoforge;

import com.calibermc.caliberlib.assets.AssetEngine;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Client set-up on NeoForge and Forge: colour handlers of generated shapes.
 * Their cut-out render type is in the generated models.
 */
final class NeoForgeClientHooks {

	private NeoForgeClientHooks() {
	}

	static void init(IEventBus bus) {
		bus.addListener((RegisterColorHandlersEvent.Block event) ->
				AssetEngine.tintedBlocks().forEach((source, blocks) -> event.register((state, level, pos, tint) ->
						Minecraft.getInstance().getBlockColors().getColor(source.defaultBlockState(), level, pos, tint),
						blocks.toArray(Block[]::new))));
		bus.addListener((RegisterColorHandlersEvent.Item event) ->
				// Items take the block's colour outside a world, as vanilla's grass and leaves items do.
				AssetEngine.tintedBlocks().forEach((source, blocks) -> event.register((stack, tint) ->
						Minecraft.getInstance().getBlockColors().getColor(source.defaultBlockState(), null, null, tint),
						blocks.toArray(Block[]::new))));
	}
}
