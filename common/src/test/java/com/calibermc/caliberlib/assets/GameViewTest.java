package com.calibermc.caliberlib.assets;

import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GameViewTest {

	@Test
	void aBlockThatIsNotRegisteredCountsAsBurning() {
		Fixtures.bootstrap();
		// Only a source known not to burn keeps its shapes out of the furnace.
		assertTrue(GameView.LIVE.burns(new ResourceLocation("missingmod", "missing_planks")));
	}
}
