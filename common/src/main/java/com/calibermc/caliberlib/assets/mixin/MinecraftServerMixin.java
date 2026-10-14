package com.calibermc.caliberlib.assets.mixin;

import com.calibermc.caliberlib.assets.AssetEngine;
import com.calibermc.caliberlib.compat.CompatLists;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Collection;

/**
 * Keeps the generated data pack in every data reload, whatever list the
 * caller passes; and checks a world's compat shapes before its chunks load.
 */
@Mixin(MinecraftServer.class)
abstract class MinecraftServerMixin {

	@ModifyVariable(method = "reloadResources", at = @At("HEAD"), argsOnly = true)
	private Collection<String> caliberlib$keepGenerated(Collection<String> ids) {
		return AssetEngine.keepGenerated(ids, ((MinecraftServer) (Object) this).getPackRepository());
	}

	@Inject(method = "loadLevel", at = @At("HEAD"))
	private void caliberlib$checkCompatShapes(CallbackInfo ci) {
		CompatLists.worldLoading(((MinecraftServer) (Object) this).getWorldPath(LevelResource.ROOT));
	}
}
