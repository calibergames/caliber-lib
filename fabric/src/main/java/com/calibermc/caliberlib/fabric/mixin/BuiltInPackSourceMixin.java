package com.calibermc.caliberlib.fabric.mixin;

import com.calibermc.caliberlib.assets.AssetEngine;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

/**
 * Adds the generated pack to vanilla's built-in pack source, which every
 * client and server pack repository has; Fabric has no pack-finder event.
 */
@Mixin(BuiltInPackSource.class)
abstract class BuiltInPackSourceMixin {

	@Shadow
	@Final
	private PackType packType;

	@Inject(method = "loadPacks", at = @At("RETURN"))
	private void caliberlib$addGenerated(Consumer<Pack> packs, CallbackInfo ci) {
		Pack pack = AssetEngine.pack(packType);
		if (pack != null) {
			packs.accept(pack);
		}
	}
}
