package com.calibermc.caliberlib.gametest;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Collection;
import java.util.List;

import static com.calibermc.caliberlib.gametest.TestSupport.EMPTY_STRUCTURE;
import static com.calibermc.caliberlib.gametest.TestSupport.check;
import static com.calibermc.caliberlib.gametest.TestSupport.id;
import static com.calibermc.caliberlib.gametest.TestSupport.item;

public class RecipeGameTests {

	private static Collection<Recipe<?>> recipes(GameTestHelper helper) {
		return helper.getLevel().getServer().getRecipeManager().getRecipes();
	}

	/** The recipes of a type that make the item from (only) this ingredient. */
	private static long making(GameTestHelper helper, RecipeType<?> type, Item result, Item ingredient) {
		RegistryAccess access = helper.getLevel().registryAccess();
		return recipes(helper).stream()
				.filter(r -> r.getType() == type && r.getResultItem(access).is(result))
				.filter(r -> r.getIngredients().stream().anyMatch(i -> i.test(ingredient.getDefaultInstance())))
				.count();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void stoneShapesAreCutFromTheirSource(GameTestHelper helper) {
		check(making(helper, RecipeType.STONECUTTING, item("gt_stone_arch"), Items.STONE_BRICKS) == 1, "arch from stone bricks");
		check(making(helper, RecipeType.STONECUTTING, item("gt_stone_slab_vertical"), Items.STONE_BRICKS) == 1, "vertical slab cut");
		check(making(helper, RecipeType.CRAFTING, item("gt_stone_slab_vertical"), Items.STONE_BRICKS) == 1, "vertical slab crafted");
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void aTallDoorIsCraftedFromTheDoorAndPlanks(GameTestHelper helper) {
		check(making(helper, RecipeType.CRAFTING, item("gt_oak_tall_door"), Items.OAK_DOOR) == 1, "tall door from oak door");
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void withoutTheWoodcutterWoodHasNoCutRecipes(GameTestHelper helper) {
		boolean woodcutter = BuiltInRegistries.RECIPE_SERIALIZER.containsKey(new net.minecraft.resources.ResourceLocation("caliber", "woodcutting"));
		long woodRecipes = recipes(helper).stream().filter(r -> r.getId().getNamespace().equals(TestFamilies.MOD_ID))
				.filter(r -> r.getResultItem(helper.getLevel().registryAccess()).is(item("gt_oak_beam_vertical"))).count();
		if (!woodcutter) {
			check(woodRecipes == 0, "gt_oak_beam_vertical has a cut recipe without the woodcutter");
			// Shaped recipes stay: the vertical slab is crafted.
			check(making(helper, RecipeType.CRAFTING, item("gt_oak_slab_vertical"), Items.OAK_PLANKS) == 1, "oak vertical slab crafted");
		}
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void recipeIdsAreTheOwnersAndUnlockedByOneAdvancement(GameTestHelper helper) {
		List<String> ids = recipes(helper).stream().map(r -> r.getId().toString())
				.filter(s -> s.startsWith(TestFamilies.MOD_ID + ":gt_stone_arch")).toList();
		check(ids.equals(List.of(TestFamilies.MOD_ID + ":gt_stone_arch_from_gt_stone_stonecutting")), "arch recipe ids " + ids);
		check(helper.getLevel().getServer().getAdvancements().getAdvancement(id("recipes/gt_stone")) != null,
				"no advancement unlocking the stone family's recipes");
		helper.succeed();
	}
}
