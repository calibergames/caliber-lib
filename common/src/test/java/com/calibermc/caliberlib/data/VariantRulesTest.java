package com.calibermc.caliberlib.data;

import com.calibermc.caliberlib.assets.ShapeSpecs;
import com.calibermc.caliberlib.block.management.ModBlockHelper;
import com.calibermc.caliberlib.testing.GameBootstrap;
import com.calibermc.caliberlib.testing.TestPlatform;
import com.calibermc.caliberlib.util.DataGenUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VariantRulesTest {

	private static final Set<ModBlockFamily.Variant> VANILLA = Set.of(ModBlockFamily.Variant.BASE, ModBlockFamily.Variant.BUTTON,
			ModBlockFamily.Variant.FENCE, ModBlockFamily.Variant.FENCE_GATE, ModBlockFamily.Variant.SIGN, ModBlockFamily.Variant.SLAB,
			ModBlockFamily.Variant.STAIRS, ModBlockFamily.Variant.PRESSURE_PLATE, ModBlockFamily.Variant.TRAPDOOR,
			ModBlockFamily.Variant.WALL, ModBlockFamily.Variant.WALL_SIGN);
	private boolean dataGen;

	@BeforeAll
	static void boot() {
		GameBootstrap.boot();
	}

	@BeforeEach
	void remember() {
		dataGen = DataGenUtil.isDataGen;
	}

	@AfterEach
	void restore() {
		DataGenUtil.isDataGen = dataGen;
		TestPlatform.MODS.clear();
	}

	@SuppressWarnings("unchecked")
	private static List<List<ModBlockFamily.Variant>> variantSets() throws IllegalAccessException {
		List<List<ModBlockFamily.Variant>> sets = new ArrayList<>();
		for (Field field : ModBlockHelper.class.getFields()) {
			if (Modifier.isStatic(field.getModifiers()) && List.class.isAssignableFrom(field.getType())) {
				sets.add((List<ModBlockFamily.Variant>) field.get(null));
			}
		}
		return sets;
	}

	@Test
	void everyVariantOfEverySetIsGeneratedOrLeftToItsSourceMod() throws IllegalAccessException {
		Set<String> specs = ShapeSpecs.get().keySet();
		List<List<ModBlockFamily.Variant>> sets = variantSets();
		assertTrue(sets.size() >= 15, "variant sets found: " + sets.size());
		for (List<ModBlockFamily.Variant> set : sets) {
			for (ModBlockFamily.Variant v : set) {
				assertTrue(specs.contains(v.getName()) || ShapeSpecs.UNSUPPORTED.contains(v), v.getName());
			}
		}
	}

	@Test
	void withoutCaliberModOnlyVanillaVariantsRegister() {
		DataGenUtil.isDataGen = false;
		for (ModBlockFamily.Variant v : ModBlockFamily.Variant.values()) {
			assertEquals(VANILLA.contains(v), v.caliberIsLoaded(), v.getName());
		}
		TestPlatform.MODS.add("caliber");
		for (ModBlockFamily.Variant v : ModBlockFamily.Variant.values()) {
			assertTrue(v.caliberIsLoaded(), v.getName() + " with Caliber Mod");
		}
		TestPlatform.MODS.clear();
		DataGenUtil.isDataGen = true;
		assertTrue(ModBlockFamily.Variant.ARCH.caliberIsLoaded(), "data generation registers every variant");
	}
}
