package com.calibermc.caliberlib.block.custom;

import com.calibermc.caliberlib.block.shapes.QuadShape;
import com.calibermc.caliberlib.util.ModBlockStateProperties;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Map;

import static com.calibermc.caliberlib.util.ModBlockStateProperties.isSide;
import static net.minecraft.core.Direction.*;

public class VerticalCornerSlabLayerBlock extends Block implements SimpleWaterloggedBlock {

    public static final IntegerProperty LAYERS = ModBlockStateProperties.FIVE_LAYERS;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<QuadShape> TYPE = ModBlockStateProperties.QUAD_SHAPE;
    public final int layerCount = 5;

    public static final VoxelShape[] RIGHT_WEST = new VoxelShape[]{Shapes.empty(),
            Shapes.join(Block.box(14, 0, 14, 16, 16, 16), Block.box(0, 0, 14, 14, 2, 16), BooleanOp.OR),
            Shapes.join(Block.box(12, 0, 12, 16, 16, 16), Block.box(0, 0, 12, 12, 4, 16), BooleanOp.OR),
            Shapes.join(Block.box(8, 0, 8, 16, 16, 16), Block.box(0, 0, 8, 8, 8, 16), BooleanOp.OR),
            Shapes.join(Block.box(4, 0, 4, 16, 16, 16), Block.box(0, 0, 4, 4, 12, 16), BooleanOp.OR),
            Block.box(0, 0.1, 0, 16, 16, 16)};
    public static final VoxelShape[] RIGHT_NORTH = new VoxelShape[]{Shapes.empty(),
            Shapes.join(Block.box(0, 0, 14, 2, 16, 16), Block.box(0, 0, 0, 2, 2, 14), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 12, 4, 16, 16), Block.box(0, 0, 0, 4, 4, 12), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 8, 8, 16, 16), Block.box(0, 0, 0, 8, 8, 8), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 4, 12, 16, 16), Block.box(0, 0, 0, 12, 12, 4), BooleanOp.OR),
            Block.box(0, 0.1, 0, 16, 16, 16)};
    public static final VoxelShape[] RIGHT_EAST = new VoxelShape[]{Shapes.empty(),
            Shapes.join(Block.box(0, 0, 0, 2, 16, 2), Block.box(2, 0, 0, 16, 2, 2), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 0, 4, 16, 4), Block.box(4, 0, 0, 16, 4, 4), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 0, 8, 16, 8), Block.box(8, 0, 0, 16, 8, 8), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 0, 12, 16, 12), Block.box(12, 0, 0, 16, 12, 12), BooleanOp.OR),
            Block.box(0, 0.1, 0, 16, 16, 16)};
    public static final VoxelShape[] RIGHT_SOUTH = new VoxelShape[]{Shapes.empty(),
            Shapes.join(Block.box(14, 0, 0, 16, 16, 2), Block.box(14, 0, 2, 16, 2, 16), BooleanOp.OR),
            Shapes.join(Block.box(12, 0, 0, 16, 16, 4), Block.box(12, 0, 4, 16, 4, 16), BooleanOp.OR),
            Shapes.join(Block.box(8, 0, 0, 16, 16, 8), Block.box(8, 0, 8, 16, 8, 16), BooleanOp.OR),
            Shapes.join(Block.box(4, 0, 0, 16, 16, 12), Block.box(4, 0, 12, 16, 12, 16), BooleanOp.OR),
            Block.box(0, 0.1, 0, 16, 16, 16)};

    public static final VoxelShape[] TOP_RIGHT_WEST = new VoxelShape[]{Shapes.empty(),
            Shapes.join(Block.box(14, 0, 14, 16, 16, 16), Block.box(0, 14, 14, 14, 16, 16), BooleanOp.OR),
            Shapes.join(Block.box(12, 0, 12, 16, 16, 16), Block.box(0, 12, 12, 12, 16, 16), BooleanOp.OR),
            Shapes.join(Block.box(8, 0, 8, 16, 16, 16), Block.box(0, 8, 8, 8, 16, 16), BooleanOp.OR),
            Shapes.join(Block.box(4, 0, 4, 16, 16, 16), Block.box(0, 4, 4, 4, 16, 16), BooleanOp.OR),
            Block.box(0, 0.1, 0, 16, 16, 16)};
    public static final VoxelShape[] TOP_RIGHT_NORTH = new VoxelShape[]{Shapes.empty(),
            Shapes.join(Block.box(0, 0, 14, 2, 16, 16), Block.box(0, 14, 0, 2, 16, 14), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 12, 4, 16, 16), Block.box(0, 12, 0, 4, 16, 12), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 8, 8, 16, 16), Block.box(0, 8, 0, 8, 16, 8), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 4, 12, 16, 16), Block.box(0, 4, 0, 12, 16, 4), BooleanOp.OR),
            Block.box(0, 0.1, 0, 16, 16, 16)};
    public static final VoxelShape[] TOP_RIGHT_EAST = new VoxelShape[]{Shapes.empty(),
            Shapes.join(Block.box(0, 0, 0, 2, 16, 2), Block.box(2, 14, 0, 16, 16, 2), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 0, 4, 16, 4), Block.box(4, 12, 0, 16, 16, 4), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 0, 8, 16, 8), Block.box(8, 8, 0, 16, 16, 8), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 0, 12, 16, 12), Block.box(12, 4, 0, 16, 16, 12), BooleanOp.OR),
            Block.box(0, 0.1, 0, 16, 16, 16)};
    public static final VoxelShape[] TOP_RIGHT_SOUTH = new VoxelShape[]{Shapes.empty(),
            Shapes.join(Block.box(14, 0, 0, 16, 16, 2), Block.box(14, 14, 2, 16, 16, 16), BooleanOp.OR),
            Shapes.join(Block.box(12, 0, 0, 16, 16, 4), Block.box(12, 12, 4, 16, 16, 16), BooleanOp.OR),
            Shapes.join(Block.box(8, 0, 0, 16, 16, 8), Block.box(8, 8, 8, 16, 16, 16), BooleanOp.OR),
            Shapes.join(Block.box(4, 0, 0, 16, 16, 12), Block.box(4, 4, 12, 16, 16, 16), BooleanOp.OR),
            Block.box(0, 0.1, 0, 16, 16, 16)};

    public static final VoxelShape[] LEFT_WEST = new VoxelShape[]{Shapes.empty(),
            Shapes.join(Block.box(14, 0, 0, 16, 16, 2), Block.box(0, 0, 0, 14, 2, 2), BooleanOp.OR),
            Shapes.join(Block.box(12, 0, 0, 16, 16, 4), Block.box(0, 0, 0, 12, 4, 4), BooleanOp.OR),
            Shapes.join(Block.box(8, 0, 0, 16, 16, 8), Block.box(0, 0, 0, 8, 8, 8), BooleanOp.OR),
            Shapes.join(Block.box(4, 0, 0, 16, 16, 12), Block.box(0, 0, 0, 4, 12, 12), BooleanOp.OR),
            Block.box(0, 0.1, 0, 16, 16, 16)};
    public static final VoxelShape[] LEFT_NORTH = new VoxelShape[]{Shapes.empty(),
            Shapes.join(Block.box(14, 0, 14, 16, 16, 16), Block.box(14, 0, 0, 16, 2, 14), BooleanOp.OR),
            Shapes.join(Block.box(12, 0, 12, 16, 16, 16), Block.box(12, 0, 0, 16, 4, 12), BooleanOp.OR),
            Shapes.join(Block.box(8, 0, 8, 16, 16, 16), Block.box(8, 0, 0, 16, 8, 8), BooleanOp.OR),
            Shapes.join(Block.box(4, 0, 4, 16, 16, 16), Block.box(4, 0, 0, 16, 12, 4), BooleanOp.OR),
            Block.box(0, 0.1, 0, 16, 16, 16)};
    public static final VoxelShape[] LEFT_EAST = new VoxelShape[]{Shapes.empty(),
            Shapes.join(Block.box(0, 0, 14, 2, 16, 16), Block.box(2, 0, 14, 16, 2, 16), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 12, 4, 16, 16), Block.box(4, 0, 12, 16, 4, 16), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 8, 8, 16, 16), Block.box(8, 0, 8, 16, 8, 16), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 4, 12, 16, 16), Block.box(12, 0, 4, 16, 12, 16), BooleanOp.OR),
            Block.box(0, 0.1, 0, 16, 16, 16)};
    public static final VoxelShape[] LEFT_SOUTH = new VoxelShape[]{Shapes.empty(),
            Shapes.join(Block.box(0, 0, 0, 2, 16, 2), Block.box(0, 0, 2, 2, 2, 16), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 0, 4, 16, 4), Block.box(0, 0, 4, 4, 4, 16), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 0, 8, 16, 8), Block.box(0, 0, 8, 8, 8, 16), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 0, 12, 16, 12), Block.box(0, 0, 12, 12, 12, 16), BooleanOp.OR),
            Block.box(0, 0.1, 0, 16, 16, 16)};

    public static final VoxelShape[] TOP_LEFT_WEST = new VoxelShape[]{Shapes.empty(),
            Shapes.join(Block.box(14, 0, 0, 16, 16, 2), Block.box(0, 14, 0, 14, 16, 2), BooleanOp.OR),
            Shapes.join(Block.box(12, 0, 0, 16, 16, 4), Block.box(0, 12, 0, 12, 16, 4), BooleanOp.OR),
            Shapes.join(Block.box(8, 0, 0, 16, 16, 8), Block.box(0, 8, 0, 8, 16, 8), BooleanOp.OR),
            Shapes.join(Block.box(4, 0, 0, 16, 16, 12), Block.box(0, 4, 0, 4, 16, 12), BooleanOp.OR),
            Block.box(0, 0.1, 0, 16, 16, 16)};
    public static final VoxelShape[] TOP_LEFT_NORTH = new VoxelShape[]{Shapes.empty(),
            Shapes.join(Block.box(14, 0, 14, 16, 16, 16), Block.box(14, 14, 0, 16, 16, 14), BooleanOp.OR),
            Shapes.join(Block.box(12, 0, 12, 16, 16, 16), Block.box(12, 12, 0, 16, 16, 12), BooleanOp.OR),
            Shapes.join(Block.box(8, 0, 8, 16, 16, 16), Block.box(8, 8, 0, 16, 16, 8), BooleanOp.OR),
            Shapes.join(Block.box(4, 0, 4, 16, 16, 16), Block.box(4, 4, 0, 16, 16, 4), BooleanOp.OR),
            Block.box(0, 0.1, 0, 16, 16, 16)};
    public static final VoxelShape[] TOP_LEFT_EAST = new VoxelShape[]{Shapes.empty(),
            Shapes.join(Block.box(0, 0, 14, 2, 16, 16), Block.box(2, 14, 14, 16, 16, 16), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 12, 4, 16, 16), Block.box(4, 12, 12, 16, 16, 16), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 8, 8, 16, 16), Block.box(8, 8, 8, 16, 16, 16), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 4, 12, 16, 16), Block.box(12, 4, 4, 16, 16, 16), BooleanOp.OR),
            Block.box(0, 0.1, 0, 16, 16, 16)};
    public static final VoxelShape[] TOP_LEFT_SOUTH = new VoxelShape[]{Shapes.empty(),
            Shapes.join(Block.box(0, 0, 0, 2, 16, 2), Block.box(0, 14, 2, 2, 16, 16), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 0, 4, 16, 4), Block.box(0, 12, 4, 4, 16, 16), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 0, 8, 16, 8), Block.box(0, 8, 8, 8, 16, 16), BooleanOp.OR),
            Shapes.join(Block.box(0, 0, 0, 12, 16, 12), Block.box(0, 4, 12, 12, 16, 16), BooleanOp.OR),
            Block.box(0, 0.1, 0, 16, 16, 16)};

    public VerticalCornerSlabLayerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LAYERS, 1)
                .setValue(FACING, Direction.NORTH)
                .setValue(TYPE, QuadShape.RIGHT)
                .setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(LAYERS, FACING, TYPE, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        QuadShape cornerSlabShape = pState.getValue(TYPE);
        Direction direction = pState.getValue(FACING);
        switch (cornerSlabShape) {
            case TOP_LEFT -> {
                return switch (direction) {
                    case EAST -> TOP_LEFT_EAST[pState.getValue(LAYERS)];
                    case SOUTH -> TOP_LEFT_SOUTH[pState.getValue(LAYERS)];
                    case WEST -> TOP_LEFT_WEST[pState.getValue(LAYERS)];
                    default -> TOP_LEFT_NORTH[pState.getValue(LAYERS)];
                };
            }
            case TOP_RIGHT -> {
                return switch (direction) {
                    case EAST -> TOP_RIGHT_EAST[pState.getValue(LAYERS)];
                    case SOUTH -> TOP_RIGHT_SOUTH[pState.getValue(LAYERS)];
                    case WEST -> TOP_RIGHT_WEST[pState.getValue(LAYERS)];
                    default -> TOP_RIGHT_NORTH[pState.getValue(LAYERS)];
                };
            }
            case LEFT -> {
                return switch (direction) {
                    case EAST -> LEFT_EAST[pState.getValue(LAYERS)];
                    case SOUTH -> LEFT_SOUTH[pState.getValue(LAYERS)];
                    case WEST -> LEFT_WEST[pState.getValue(LAYERS)];
                    default -> LEFT_NORTH[pState.getValue(LAYERS)];
                };
            }
            default -> {
                return switch (direction) {
                    case EAST -> RIGHT_EAST[pState.getValue(LAYERS)];
                    case SOUTH -> RIGHT_SOUTH[pState.getValue(LAYERS)];
                    case WEST -> RIGHT_WEST[pState.getValue(LAYERS)];
                    default -> RIGHT_NORTH[pState.getValue(LAYERS)];
                };
            }
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos blockpos = pContext.getClickedPos();
        BlockState blockstate = pContext.getLevel().getBlockState(blockpos);
        Direction direction = pContext.getHorizontalDirection().getOpposite();
        Direction clickedFace = pContext.getClickedFace();
        FluidState fluidstate = pContext.getLevel().getFluidState(blockpos);
        double hitY = pContext.getClickLocation().y - (double) blockpos.getY();
        double hitX = pContext.getClickLocation().x - (double) blockpos.getX();
        double hitZ = pContext.getClickLocation().z - (double) blockpos.getZ();

        if (blockstate.is(this) && clickedFace != Direction.UP && clickedFace != Direction.DOWN) {
            int i = blockstate.getValue(LAYERS);
            int newCount = Math.min(layerCount, i + 1);

            return blockstate.setValue(LAYERS, Integer.valueOf(newCount)).
                    setValue(WATERLOGGED, Boolean.valueOf((newCount < layerCount) && fluidstate.is(FluidTags.WATER)));
        } else {
            BlockState blockstate1 = this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite())
                    .setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));

            if ((direction == NORTH && hitX < 0.5 || direction == EAST && hitZ < 0.5) && hitY < 0.5) {
                return blockstate1.setValue(TYPE, QuadShape.RIGHT).setValue(LAYERS, 1);
            } else if ((direction == NORTH && hitX > 0.5 || direction == EAST && hitZ > 0.5) && hitY < 0.5) {
                return blockstate1.setValue(TYPE, QuadShape.LEFT).setValue(LAYERS, 1);
            } else if ((direction == SOUTH && hitX > 0.5 || direction == WEST && hitZ > 0.5) && hitY < 0.5) {
                return blockstate1.setValue(TYPE, QuadShape.RIGHT).setValue(LAYERS, 1);
            } else if ((direction == SOUTH && hitX < 0.5 || direction == WEST && hitZ < 0.5) && hitY < 0.5) {
                return blockstate1.setValue(TYPE, QuadShape.LEFT).setValue(LAYERS, 1);
            } else if ((direction == NORTH && hitX < 0.5 || direction == EAST && hitZ < 0.5) && hitY > 0.5) {
                return blockstate1.setValue(TYPE, QuadShape.TOP_RIGHT).setValue(LAYERS, 1);
            } else if ((direction == NORTH && hitX > 0.5 || direction == EAST && hitZ > 0.5) && hitY > 0.5) {
                return blockstate1.setValue(TYPE, QuadShape.TOP_LEFT).setValue(LAYERS, 1);
            } else if ((direction == SOUTH && hitX > 0.5 || direction == WEST && hitZ > 0.5) && hitY > 0.5) {
                return blockstate1.setValue(TYPE, QuadShape.TOP_RIGHT).setValue(LAYERS, 1);
            } else if ((direction == SOUTH && hitX < 0.5 || direction == WEST && hitZ < 0.5) && hitY > 0.5) {
                return blockstate1.setValue(TYPE, QuadShape.TOP_LEFT).setValue(LAYERS, 1);
            } else {
                return blockstate1.setValue(TYPE, QuadShape.RIGHT).setValue(LAYERS, 1);
            }
        }
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext pContext) {
        int currentLayers = state.getValue(LAYERS);
        if (pContext.getItemInHand().getItem() == this.asItem()) {
            Direction clickedFace = pContext.getClickedFace();
            // Allow replacement if it's a side click and not at max layers
            return isSide(clickedFace) && currentLayers < layerCount;
        }
        return false;
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluid) {
        return state.getValue(LAYERS) < layerCount && SimpleWaterloggedBlock.super.placeLiquid(world, pos, state, fluid);
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter world, BlockPos pos, BlockState state, Fluid fluid) {
        return state.getValue(LAYERS) < layerCount && SimpleWaterloggedBlock.super.canPlaceLiquid(world, pos, state, fluid);
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