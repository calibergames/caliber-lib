package com.calibermc.caliberlib.block.custom;

import com.calibermc.caliberlib.block.shapes.WindowShape;
import com.calibermc.caliberlib.util.ModBlockStateProperties;
import com.calibermc.caliberlib.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.Direction;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.stream.Stream;

import static net.minecraft.core.Direction.*;

public class WindowBlock extends Block implements SimpleWaterloggedBlock {

    public static final BooleanProperty ARCH = ModBlockStateProperties.ARCH;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<WindowShape> TYPE = ModBlockStateProperties.WINDOW_SHAPE;

    public static final Map<Direction, VoxelShape> TOP_SHAPE = Maps.newEnumMap(ImmutableMap.of(
            NORTH, Stream.of(
                    Block.box(12, 0, 0, 16, 16, 16),
                    Block.box(0, 0, 0, 4, 16, 16),
                    Block.box(4, 14, 0, 12, 16, 16)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            SOUTH, Stream.of(
                    Block.box(12, 0, 0, 16, 16, 16),
                    Block.box(0, 0, 0, 4, 16, 16),
                    Block.box(4, 14, 0, 12, 16, 16)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            EAST, Stream.of(
                    Block.box(0, 0, 12, 16, 16, 16),
                    Block.box(0, 0, 0, 16, 16, 4),
                    Block.box(0, 14, 4, 16, 16, 12)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            WEST, Stream.of(
                    Block.box(0, 0, 12, 16, 16, 16),
                    Block.box(0, 0, 0, 16, 16, 4),
                    Block.box(0, 14, 4, 16, 16, 12)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()));

    public static final Map<Direction, VoxelShape> MIDDLE_SHAPE = Maps.newEnumMap(ImmutableMap.of(
            NORTH, Shapes.join(Block.box(12, 0, 0, 16, 16, 16),
                    Block.box(0, 0, 0, 4, 16, 16), BooleanOp.OR),
            SOUTH, Shapes.join(Block.box(12, 0, 0, 16, 16, 16),
                    Block.box(0, 0, 0, 4, 16, 16), BooleanOp.OR),
            EAST, Shapes.join(Block.box(0, 0, 12, 16, 16, 16),
                    Block.box(0, 0, 0, 16, 16, 4), BooleanOp.OR),
            WEST, Shapes.join(Block.box(0, 0, 12, 16, 16, 16),
                    Block.box(0, 0, 0, 16, 16, 4), BooleanOp.OR)));

    public static final Map<Direction, VoxelShape> BOTTOM_SHAPE = Maps.newEnumMap(ImmutableMap.of(
            NORTH, Stream.of(
                    Block.box(12, 0, 0, 16, 16, 16),
                    Block.box(0, 0, 0, 4, 16, 16),
                    Block.box(4, 0, 0, 12, 2, 16)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            SOUTH, Stream.of(
                    Block.box(12, 0, 0, 16, 16, 16),
                    Block.box(0, 0, 0, 4, 16, 16),
                    Block.box(4, 0, 0, 12, 2, 16)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            EAST, Stream.of(
                    Block.box(0, 0, 12, 16, 16, 16),
                    Block.box(0, 0, 0, 16, 16, 4),
                    Block.box(0, 0, 4, 16, 2, 12)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            WEST, Stream.of(
                    Block.box(0, 0, 12, 16, 16, 16),
                    Block.box(0, 0, 0, 16, 16, 4),
                    Block.box(0, 0, 4, 16, 2, 12)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()));

    public static final Map<Direction, VoxelShape> FULL_SHAPE = Maps.newEnumMap(ImmutableMap.of(
            NORTH, Stream.of(
                    Block.box(12, 0, 0, 16, 16, 16),
                    Block.box(0, 0, 0, 4, 16, 16),
                    Block.box(4, 14, 0, 12, 16, 16),
                    Block.box(4, 0, 0, 12, 2, 16)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            SOUTH, Stream.of(
                    Block.box(12, 0, 0, 16, 16, 16),
                    Block.box(0, 0, 0, 4, 16, 16),
                    Block.box(4, 14, 0, 12, 16, 16),
                    Block.box(4, 0, 0, 12, 2, 16)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            EAST, Stream.of(
                    Block.box(0, 0, 12, 16, 16, 16),
                    Block.box(0, 0, 0, 16, 16, 4),
                    Block.box(0, 14, 4, 16, 16, 12),
                    Block.box(0, 0, 4, 16, 2, 12)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            WEST, Stream.of(
                    Block.box(0, 0, 12, 16, 16, 16),
                    Block.box(0, 0, 0, 16, 16, 4),
                    Block.box(0, 14, 4, 16, 16, 12),
                    Block.box(0, 0, 4, 16, 2, 12)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()));

    public WindowBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ARCH, false)
                .setValue(FACING, NORTH)
                .setValue(TYPE, WindowShape.FULL_BLOCK)
                .setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, TYPE, ARCH, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        WindowShape windowShape = pState.getValue(TYPE);
        switch (windowShape) {
            case TOP -> {
                return TOP_SHAPE.get(pState.getValue(FACING));
            }
            case MIDDLE -> {
                return MIDDLE_SHAPE.get(pState.getValue(FACING));
            }
            case BOTTOM -> {
                return BOTTOM_SHAPE.get(pState.getValue(FACING));
            }
            default -> {
                return FULL_SHAPE.get(pState.getValue(FACING));
            }
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos blockpos = pContext.getClickedPos();
        FluidState fluidstate = pContext.getLevel().getFluidState(blockpos);
        return this.defaultBlockState()
                .setValue(FACING, pContext.getHorizontalDirection())
                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER)
                .setValue(TYPE, WindowShape.FULL_BLOCK)
                .setValue(ARCH, Boolean.FALSE);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        boolean isArch = pState.getValue(ARCH);

        // Check if the block is a WindowBlock, if so update the WindowShape to attach above and below
        if (pLevel.getBlockState(pCurrentPos.below()).getBlock() == this && pLevel.getBlockState(pCurrentPos.above()).getBlock() != this) {
            return pState.setValue(ARCH, isArch).setValue(TYPE, WindowShape.TOP);
        } else if (pLevel.getBlockState(pCurrentPos.below()).getBlock() == this && pLevel.getBlockState(pCurrentPos.above()).getBlock() == this) {
            return pState.setValue(ARCH, isArch).setValue(TYPE, WindowShape.MIDDLE);
        } else if (pLevel.getBlockState(pCurrentPos.below()).getBlock() != this && pLevel.getBlockState(pCurrentPos.above()).getBlock() == this) {
            return pState.setValue(ARCH, isArch).setValue(TYPE, WindowShape.BOTTOM);
        } else {
            return pState.setValue(ARCH, isArch).setValue(TYPE, WindowShape.FULL_BLOCK);
        }
    }

    private void propagateArchState(Level level, BlockPos pos, boolean newArchState) {
        // Propagate up
        BlockPos currentPos = pos.above();
        while (level.getBlockState(currentPos).getBlock() == this) {
            BlockState currentState = level.getBlockState(currentPos);
            level.setBlock(currentPos, currentState.setValue(ARCH, newArchState), 3);
            currentPos = currentPos.above();
        }

        // Propagate down
        currentPos = pos.below();
        while (level.getBlockState(currentPos).getBlock() == this) {
            BlockState currentState = level.getBlockState(currentPos);
            level.setBlock(currentPos, currentState.setValue(ARCH, newArchState), 3);
            currentPos = currentPos.below();
        }
    }

    @Override
    public InteractionResult use(BlockState pState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

        // Check if the player is holding an ArchBlock item
        if (heldItem.is(ModTags.Items.ARCHES)) {
            if (!level.isClientSide) {
                boolean newArchState = !pState.getValue(ARCH);
                level.setBlock(pos, pState.setValue(ARCH, newArchState), 3);

                // Play the block update sound
                level.levelEvent(2001, pos, Block.getId(pState));

                // Propagate the state change
                propagateArchState(level, pos, newArchState);
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    public boolean placeLiquid(LevelAccessor pLevel, BlockPos pPos, BlockState pState, FluidState pFluidState) {
        return SimpleWaterloggedBlock.super.placeLiquid(pLevel, pPos, pState, pFluidState);
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter pLevel, BlockPos pPos, BlockState pState, Fluid pFluid) {
        return SimpleWaterloggedBlock.super.canPlaceLiquid(pLevel, pPos, pState, pFluid);
    }

    @Override
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return switch (pType) {
            case LAND -> false;
            case WATER -> pLevel.getFluidState(pPos).is(FluidTags.WATER);
            case AIR -> false;
        };
    }
}
