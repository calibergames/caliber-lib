package com.calibermc.caliberlib.assets.mixin;

import com.calibermc.caliberlib.assets.AssetEngine;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Collection;

/** Keeps the generated data pack in every data reload, whatever list the caller passes. */
@Mixin(MinecraftServer.class)
abstract class MinecraftServerMixin {

	@ModifyVariable(method = "reloadResources", at = @At("HEAD"), argsOnly = true)
	private Collection<String> caliberlib$keepGenerated(Collection<String> ids) {
		return AssetEngine.keepGenerated(ids, ((MinecraftServer) (Object) this).getPackRepository());
	}
}
