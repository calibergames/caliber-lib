package com.calibermc.caliberlib.neoforge;

import com.calibermc.caliberlib.assets.AssetEngine;
import com.calibermc.caliberlib.compat.CompatLogin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.ModMismatchDisconnectedScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.ConnectionData;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * Client set-up on NeoForge and Forge: colour handlers of generated shapes
 * (their cut-out render type is in the generated models), and the compat
 * list's refusal in words.
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
		MinecraftForge.EVENT_BUS.addListener(NeoForgeClientHooks::compatRefusal);
	}

	/**
	 * Forge's mismatch screen names mods and their versions, which are the
	 * same on both sides when only the compat lists differ. When the compat
	 * channel is the only mismatch, the refusal says so instead; with any
	 * other mismatch Forge's screen stays, so a missing mod still shows.
	 */
	private static void compatRefusal(ScreenEvent.Opening event) {
		if (!(event.getNewScreen() instanceof ModMismatchDisconnectedScreen screen)) {
			return;
		}
		ConnectionData.ModMismatchData data = field(screen, "modMismatchData");
		Screen parent = field(screen, "parent");
		if (data != null && parent != null && data.mismatchedModData().keySet().equals(Set.of(CompatLogin.CHANNEL))) {
			event.setNewScreen(new DisconnectedScreen(parent, Component.translatable("connect.failed"), CompatLogin.differs()));
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T field(Object owner, String name) {
		try {
			Field f = ModMismatchDisconnectedScreen.class.getDeclaredField(name);
			f.setAccessible(true);
			return (T) f.get(owner);
		} catch (ReflectiveOperationException | RuntimeException e) {
			return null;
		}
	}
}
