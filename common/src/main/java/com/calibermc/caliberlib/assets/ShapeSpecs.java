package com.calibermc.caliberlib.assets;

import com.calibermc.caliberlib.data.ModBlockFamily;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/** The shape specs in the jar, read once on first use. */
public final class ShapeSpecs {

	/** Variants the engine does not generate: no shipped Caliber material registers them. */
	public static final Set<ModBlockFamily.Variant> UNSUPPORTED = EnumSet.of(ModBlockFamily.Variant.BASE,
			ModBlockFamily.Variant.BUTTON, ModBlockFamily.Variant.PRESSURE_PLATE, ModBlockFamily.Variant.DOOR,
			ModBlockFamily.Variant.TRAPDOOR, ModBlockFamily.Variant.FENCE, ModBlockFamily.Variant.FENCE_GATE,
			ModBlockFamily.Variant.SIGN, ModBlockFamily.Variant.WALL_SIGN, ModBlockFamily.Variant.HANGING_SIGN,
			ModBlockFamily.Variant.WALL_HANGING_SIGN);

	private static final String ROOT = "caliberlib/engine/shapes/";

	private ShapeSpecs() {
	}

	/** Variant name to spec. */
	public static Map<String, ShapeSpec> get() {
		return Holder.SPECS;
	}

	private static final class Holder {
		static final Map<String, ShapeSpec> SPECS = load();
	}

	static Map<String, ShapeSpec> load() {
		Map<String, ShapeSpec> specs = new LinkedHashMap<>();
		for (ModBlockFamily.Variant variant : ModBlockFamily.Variant.values()) {
			if (UNSUPPORTED.contains(variant)) {
				continue;
			}
			String path = ROOT + variant.getName() + ".json";
			try (InputStream in = ShapeSpecs.class.getClassLoader().getResourceAsStream(path)) {
				if (in == null) {
					throw new IllegalStateException("Missing shape spec " + path);
				}
				ShapeSpec spec = ShapeSpec.read(in);
				if (!spec.variant().equals(variant.getName())) {
					throw new IllegalStateException(path + " is for " + spec.variant());
				}
				specs.put(spec.variant(), spec);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		return Map.copyOf(specs);
	}
}
