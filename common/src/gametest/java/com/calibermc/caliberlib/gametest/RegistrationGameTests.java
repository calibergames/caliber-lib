package com.calibermc.caliberlib.gametest;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.BlockItem;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.calibermc.caliberlib.gametest.TestSupport.EMPTY_STRUCTURE;
import static com.calibermc.caliberlib.gametest.TestSupport.block;
import static com.calibermc.caliberlib.gametest.TestSupport.check;
import static com.calibermc.caliberlib.gametest.TestSupport.item;

public class RegistrationGameTests {

	private static final String GENERATED = "caliberlib:generated";

	static final List<String> SHAPES = List.of("gt_stone_slab", "gt_stone_slab_vertical", "gt_stone_layer", "gt_stone_corner",
			"gt_stone_arch", "gt_stone_pillar", "gt_oak_tall_door", "gt_oak_beam_vertical", "gt_oak_slab_vertical",
			"gt_oak_roof_45", "gt_crimson_beam_vertical", "gt_wool_arch", "gt_mud_arch", "gt_mud_layer");

	@GameTest(template = EMPTY_STRUCTURE)
	public void everyShapeRegistersWithItsItem(GameTestHelper helper) {
		for (String shape : SHAPES) {
			check(item(shape) instanceof BlockItem bi && bi.getBlock() == block(shape), shape + " has no item that places it");
		}
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void lateFamiliesRegisterAfterTheirSource(GameTestHelper helper) {
		// gt_mud registers through the late registration point, from vanilla's mud bricks.
		check(block("gt_mud_arch").defaultBlockState().getSoundType() == net.minecraft.world.level.block.Blocks.MUD_BRICKS
				.defaultBlockState().getSoundType(), "gt_mud_arch does not copy mud bricks");
		helper.succeed();
	}

	@GameTest(template = EMPTY_STRUCTURE)
	public void theGeneratedPackIsOn(GameTestHelper helper) {
		check(helper.getLevel().getServer().getPackRepository().getSelectedIds().contains(GENERATED), GENERATED + " is not selected");
		helper.succeed();
	}

	/** A data reload without the generated pack, as {@code /datapack disable "caliberlib:generated"} starts one, keeps it. */
	@GameTest(template = EMPTY_STRUCTURE, batch = "reload", timeoutTicks = 400)
	public void aReloadWithoutTheGeneratedPackKeepsIt(GameTestHelper helper) {
		MinecraftServer server = helper.getLevel().getServer();
		List<String> without = server.getPackRepository().getSelectedIds().stream().filter(id -> !id.equals(GENERATED)).toList();
		CompletableFuture<Void> reload = server.reloadResources(without);
		helper.succeedWhen(() -> {
			check(reload.isDone(), "the reload has not finished");
			check(!reload.isCompletedExceptionally(), "the reload failed");
			check(server.getPackRepository().getSelectedIds().contains(GENERATED), GENERATED + " was dropped by the reload");
			check(server.getRecipeManager().getRecipes().stream().anyMatch(r -> r.getId().getNamespace().equals(TestFamilies.MOD_ID)),
					"the test families have no recipes after the reload");
		});
	}
}
