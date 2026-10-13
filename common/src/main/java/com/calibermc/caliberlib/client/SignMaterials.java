package com.calibermc.caliberlib.client;

import com.calibermc.caliberlib.CaliberLib;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.Collection;
import java.util.List;

/**
 * A self-check after client set-up: every wood type the library set up has
 * its sign and hanging-sign materials, so a failed set-up shows in the log.
 */
public final class SignMaterials {

	private SignMaterials() {
	}

	/** The names of the wood types missing a sign or hanging-sign material. */
	public static List<String> missing(Collection<WoodType> types) {
		return types.stream()
				.filter(type -> !Sheets.SIGN_MATERIALS.containsKey(type) || !Sheets.HANGING_SIGN_MATERIALS.containsKey(type))
				.map(WoodType::name)
				.toList();
	}

	/** Logs the wood types still missing a material, whose signs would render without a texture. */
	public static void check(Collection<WoodType> types) {
		List<String> missing = missing(types);
		if (!missing.isEmpty()) {
			CaliberLib.LOGGER.warn("No sign material for wood types {}; their signs render without a texture", missing);
		}
	}
}
