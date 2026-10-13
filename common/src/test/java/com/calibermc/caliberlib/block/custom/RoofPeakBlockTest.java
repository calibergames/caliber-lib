package com.calibermc.caliberlib.block.custom;

import com.calibermc.caliberlib.block.shapes.RoofPeakShape;
import com.calibermc.caliberlib.block.shapes.RoofPitch;
import com.calibermc.caliberlib.block.shapes.RoofShape;
import com.calibermc.caliberlib.testing.GameBootstrap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoofPeakBlockTest {

	private static Roof22Block roof22;
	private static Roof45Block roof45;
	private static Roof67Block roof67;
	private static RoofPeakBlock peak;

	private static final BlockPos CAP = new BlockPos(0, 1, 0);

	@BeforeAll
	static void blocks() {
		roof22 = GameBootstrap.block(() -> new Roof22Block(BlockBehaviour.Properties.of()));
		roof45 = GameBootstrap.block(() -> new Roof45Block(BlockBehaviour.Properties.of()));
		roof67 = GameBootstrap.block(() -> new Roof67Block(BlockBehaviour.Properties.of()));
		peak = GameBootstrap.block(() -> new RoofPeakBlock(BlockBehaviour.Properties.of()));
	}

	/** Blocks by position; air elsewhere. */
	private static final class World implements BlockGetter {
		final Map<BlockPos, BlockState> blocks = new HashMap<>();

		World with(BlockPos pos, BlockState state) {
			blocks.put(pos, state);
			return this;
		}

		@Nullable
		@Override
		public BlockEntity getBlockEntity(BlockPos pos) {
			return null;
		}

		@Override
		public BlockState getBlockState(BlockPos pos) {
			return blocks.getOrDefault(pos, Blocks.AIR.defaultBlockState());
		}

		@Override
		public FluidState getFluidState(BlockPos pos) {
			return getBlockState(pos).getFluidState();
		}

		@Override
		public int getHeight() {
			return 384;
		}

		@Override
		public int getMinBuildHeight() {
			return -64;
		}
	}

	private static BlockState cap(RoofPitch pitch) {
		return peak.defaultBlockState().setValue(RoofPeakBlock.FACING, Direction.NORTH).setValue(RoofPeakBlock.PITCH, pitch);
	}

	/** An outer corner; the three roof blocks share one shape property. */
	private static BlockState corner(Block roof) {
		return roof.defaultBlockState().setValue(Roof45Block.TYPE, RoofShape.OUTER_LEFT);
	}

	@Test
	void pitchComesFromTheRoofBelowOrBeside() {
		assertEquals(RoofPitch.PITCH_22, RoofPeakBlock.getRoofPitch(cap(RoofPitch.PITCH_45),
				new World().with(CAP.below().east(), roof22.defaultBlockState()), CAP));
		assertEquals(RoofPitch.PITCH_67, RoofPeakBlock.getRoofPitch(cap(RoofPitch.PITCH_45),
				new World().with(CAP.below().south(), roof67.defaultBlockState()), CAP));
		// A 22 roof's bottom half sits level with a raised cap.
		assertEquals(RoofPitch.PITCH_22, RoofPeakBlock.getRoofPitch(cap(RoofPitch.PITCH_67),
				new World().with(CAP.west(), roof22.defaultBlockState()), CAP));
		assertEquals(RoofPitch.PITCH_45, RoofPeakBlock.getRoofPitch(cap(RoofPitch.PITCH_22),
				new World().with(CAP.below().north(), roof45.defaultBlockState()), CAP));
	}

	@Test
	void pitchFallsBackToTheNextCapThenToItsOwn() {
		World world = new World().with(CAP.east(), cap(RoofPitch.PITCH_67));
		assertEquals(RoofPitch.PITCH_67, RoofPeakBlock.getRoofPitch(cap(RoofPitch.PITCH_45), world, CAP));
		assertEquals(RoofPitch.PITCH_22, RoofPeakBlock.getRoofPitch(cap(RoofPitch.PITCH_22), new World(), CAP));
	}

	@Test
	void hipsAreOuterCornersOfEveryPitch() {
		for (Block roof : new Block[] {roof22, roof45, roof67}) {
			assertTrue(RoofPeakBlock.isHip(corner(roof)), roof.toString());
			assertTrue(RoofPeakBlock.isHip(roof.defaultBlockState().setValue(Roof45Block.TYPE, RoofShape.OUTER_RIGHT)));
			assertFalse(RoofPeakBlock.isHip(roof.defaultBlockState().setValue(Roof45Block.TYPE, RoofShape.STRAIGHT)));
			assertFalse(RoofPeakBlock.isHip(roof.defaultBlockState().setValue(Roof45Block.TYPE, RoofShape.INNER_LEFT)));
		}
		assertFalse(RoofPeakBlock.isHip(Blocks.STONE.defaultBlockState()));
	}

	@Test
	void aRidgeEndsWhereAHipOfItsPitchMeetsIt() {
		// Facing north with a cap in front: a ridge running north.
		for (RoofPitch pitch : RoofPitch.values()) {
			Block roof = switch (pitch) {
				case PITCH_22 -> roof22;
				case PITCH_45 -> roof45;
				case PITCH_67 -> roof67;
			};
			World ridge = new World().with(CAP.north(), cap(pitch));
			assertEquals(RoofPeakShape.STRAIGHT, RoofPeakBlock.getRoofShape(cap(pitch), ridge, CAP), pitch.toString());
			// The hip one level down at the back-left diagonal ends the ridge here.
			ridge.with(CAP.south().east().below(), corner(roof));
			assertEquals(RoofPeakShape.END_FACING, RoofPeakBlock.getRoofShape(cap(pitch), ridge, CAP), pitch.toString());
		}
	}

	@Test
	void aHipOfAnotherPitchDoesNotEndTheRidge() {
		World world = new World().with(CAP.north(), cap(RoofPitch.PITCH_45))
				.with(CAP.south().east().below(), corner(roof22));
		assertEquals(RoofPeakShape.STRAIGHT, RoofPeakBlock.getRoofShape(cap(RoofPitch.PITCH_45), world, CAP));
	}

	@Test
	void onlyA22RoofsHipCountsLevelWithTheCap() {
		World world22 = new World().with(CAP.north(), cap(RoofPitch.PITCH_22)).with(CAP.south().east(), corner(roof22));
		assertEquals(RoofPeakShape.END_FACING, RoofPeakBlock.getRoofShape(cap(RoofPitch.PITCH_22), world22, CAP));
		// 45 roofs keep their released rule: hips one level down only.
		World world45 = new World().with(CAP.north(), cap(RoofPitch.PITCH_45)).with(CAP.south().east(), corner(roof45));
		assertEquals(RoofPeakShape.STRAIGHT, RoofPeakBlock.getRoofShape(cap(RoofPitch.PITCH_45), world45, CAP));
		World world67 = new World().with(CAP.north(), cap(RoofPitch.PITCH_67)).with(CAP.south().east(), corner(roof67));
		assertEquals(RoofPeakShape.STRAIGHT, RoofPeakBlock.getRoofShape(cap(RoofPitch.PITCH_67), world67, CAP));
	}

	@Test
	void capsJoinIntoCornersTeesAndCrosses() {
		BlockState cap = cap(RoofPitch.PITCH_45);
		assertEquals(RoofPeakShape.CAP, RoofPeakBlock.getRoofShape(cap, new World(), CAP));
		assertEquals(RoofPeakShape.CROSS, RoofPeakBlock.getRoofShape(cap, new World().with(CAP.north(), cap)
				.with(CAP.south(), cap).with(CAP.east(), cap).with(CAP.west(), cap), CAP));
		assertEquals(RoofPeakShape.OPPOSITE_T, RoofPeakBlock.getRoofShape(cap, new World().with(CAP.north(), cap)
				.with(CAP.east(), cap).with(CAP.west(), cap), CAP));
		assertEquals(RoofPeakShape.CORNER_RIGHT, RoofPeakBlock.getRoofShape(cap, new World().with(CAP.north(), cap)
				.with(CAP.east(), cap), CAP));
	}
}
