package com.calibermc.caliberlib.block.custom;

import com.calibermc.caliberlib.CaliberLib;
import com.calibermc.caliberlib.block.shapes.TopBottomShape;
import com.calibermc.caliberlib.block.shapes.misc.BeamConnection;
import com.calibermc.caliberlib.util.ModBlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AirBlock;
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
import org.apache.commons.logging.Log;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

import java.util.stream.Stream;

import static com.calibermc.caliberlib.util.ModBlockStateProperties.isSide;

public class DiagonalBeamBlock extends Block implements SimpleWaterloggedBlock {

    private static final Logger LOGGER = LoggerFactory.getLogger(CaliberLib.class);

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<BeamConnection> CONNECT = ModBlockStateProperties.BEAM_CONNECTION;
    public static final IntegerProperty BEAM = ModBlockStateProperties.DIAGONAL_BEAM_SHAPE;;
    public final int beamShape = 2;

    public static final VoxelShape[] SHAPE_NORTH = new VoxelShape[]{Shapes.empty(),
            Stream.of(
                    Block.box(5.10938, 2, 11.25, 10.92188, 4, 14.75),
                    Block.box(5.10938, 4, 9.25, 10.92188, 6, 12.75),
                    Block.box(5.10938, 6, 7.25, 10.92188, 8, 10.75),
                    Block.box(5.10938, 8, 5.25, 10.92188, 10, 8.75),
                    Block.box(5.10938, 10, 3.25, 10.92188, 12, 6.75),
                    Block.box(5.10938, 12, 1.25, 10.92188, 14, 4.75)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(5.10938, 2, 11.25, 10.92188, 4, 14.75),
                    Block.box(5.10938, 4, 9.25, 10.92188, 6, 12.75),
                    Block.box(5.07812, 2, 1.25, 10.89062, 4, 4.75),
                    Block.box(5.07812, 4, 3.25, 10.89062, 6, 6.75),
                    Block.box(5.10938, 6, 5.25, 10.92188, 8, 10.75),
                    Block.box(5.10938, 8, 7.25, 10.92188, 10, 8.75)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()};

    public static final VoxelShape[] SHAPE_EAST = new VoxelShape[]{Shapes.empty(),
            Stream.of(
                    Block.box(1.25, 2, 5.10938, 4.75, 4, 10.92188),
                    Block.box(3.25, 4, 5.10938, 6.75, 6, 10.92188),
                    Block.box(5.25, 6, 5.10938, 8.75, 8, 10.92188),
                    Block.box(7.25, 8, 5.10938, 10.75, 10, 10.92188),
                    Block.box(9.25, 10, 5.10938, 12.75, 12, 10.92188),
                    Block.box(11.25, 12, 5.10938, 14.75, 14, 10.92188)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(1.25, 2, 5.10938, 4.75, 4, 10.92188),
                    Block.box(3.25, 4, 5.10938, 6.75, 6, 10.92188),
                    Block.box(11.25, 2, 5.07812, 14.75, 4, 10.89062),
                    Block.box(9.25, 4, 5.07812, 12.75, 6, 10.89062),
                    Block.box(5.25, 6, 5.10938, 10.75, 8, 10.92188),
                    Block.box(7.25, 8, 5.10938, 8.75, 10, 10.92188)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()};

    public static final VoxelShape[] SHAPE_SOUTH = new VoxelShape[]{Shapes.empty(),
            Stream.of(
                    Block.box(5.07812, 2, 1.25, 10.89062, 4, 4.75),
                    Block.box(5.07812, 4, 3.25, 10.89062, 6, 6.75),
                    Block.box(5.07812, 6, 5.25, 10.89062, 8, 8.75),
                    Block.box(5.07812, 8, 7.25, 10.89062, 10, 10.75),
                    Block.box(5.07812, 10, 9.25, 10.89062, 12, 12.75),
                    Block.box(5.07812, 12, 11.25, 10.89062, 14, 14.75)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(5.07812, 2, 1.25, 10.89062, 4, 4.75),
                    Block.box(5.07812, 4, 3.25, 10.89062, 6, 6.75),
                    Block.box(5.10938, 2, 11.25, 10.92188, 4, 14.75),
                    Block.box(5.10938, 4, 9.25, 10.92188, 6, 12.75),
                    Block.box(5.07812, 6, 5.25, 10.89062, 8, 10.75),
                    Block.box(5.07812, 8, 7.25, 10.89062, 10, 8.75)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()};

    public static final VoxelShape[] SHAPE_WEST = new VoxelShape[]{Shapes.empty(),
            Stream.of(
                    Block.box(11.25, 2, 5.07812, 14.75, 4, 10.89062),
                    Block.box(9.25, 4, 5.07812, 12.75, 6, 10.89062),
                    Block.box(7.25, 6, 5.07812, 10.75, 8, 10.89062),
                    Block.box(5.25, 8, 5.07812, 8.75, 10, 10.89062),
                    Block.box(3.25, 10, 5.07812, 6.75, 12, 10.89062),
                    Block.box(1.25, 12, 5.07812, 4.75, 14, 10.89062)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(),
            Stream.of(
                    Block.box(11.25, 2, 5.07812, 14.75, 4, 10.89062),
                    Block.box(9.25, 4, 5.07812, 12.75, 6, 10.89062),
                    Block.box(1.25, 2, 5.10938, 4.75, 4, 10.92188),
                    Block.box(3.25, 4, 5.10938, 6.75, 6, 10.92188),
                    Block.box(5.25, 6, 5.07812, 10.75, 8, 10.89062),
                    Block.box(7.25, 8, 5.07812, 8.75, 10, 10.89062)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get()};

    public DiagonalBeamBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(BEAM, 1)
                .setValue(CONNECT, BeamConnection.NONE)
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, Boolean.FALSE));

    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, BEAM, CONNECT, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Direction direction = pState.getValue(FACING);
        return switch (direction) {
            case EAST -> SHAPE_EAST[pState.getValue(BEAM)];
            case SOUTH -> SHAPE_SOUTH[pState.getValue(BEAM)];
            case WEST -> SHAPE_WEST[pState.getValue(BEAM)];
            default -> SHAPE_NORTH[pState.getValue(BEAM)];
        };
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos pCurrentPos = pContext.getClickedPos();
        BlockState blockstate = pContext.getLevel().getBlockState(pCurrentPos);
        FluidState fluidstate = pContext.getLevel().getFluidState(pCurrentPos);
        LevelAccessor pLevel = pContext.getLevel();
        Direction pFacing = pContext.getHorizontalDirection().getOpposite();
        Direction pOppositeFacing = pContext.getHorizontalDirection();

        if (blockstate.is(this)) {
            int newCount = blockstate.getValue(BEAM) + 1;
            if (newCount > beamShape) {
                newCount = 1;
            }
            pContext.getItemInHand().grow(1);
            return blockstate.setValue(BEAM, Integer.valueOf(newCount)).
                    setValue(WATERLOGGED, Boolean.valueOf((newCount < beamShape) && fluidstate.is(FluidTags.WATER)));
        } else {
            blockstate = this.defaultBlockState().setValue(BEAM, 1).setValue(FACING, pFacing).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
        }

        return getConnectionState(blockstate, pCurrentPos, pLevel, pFacing, pOppositeFacing);
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext pContext) {
        if (pContext.getItemInHand().getItem() == this.asItem()) {
            Direction clickedFace = pContext.getClickedFace();
            return isSide(clickedFace) && pContext.replacingClickedOnBlock();
        }
        return false;
    }

    /**
     * Update the provided state given the provided neighbor direction and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific direction passed in.
     */
    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {

        Direction placedBlockFacing = pState.getValue(FACING);
        Direction placedBlockOppositeFacing = pState.getValue(FACING).getOpposite();

        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        return getConnectionState(pState, pCurrentPos, pLevel, placedBlockFacing, placedBlockOppositeFacing);
    }

    @NotNull
    public BlockState getConnectionState(BlockState blockstate, BlockPos pCurrentPos, LevelAccessor pLevel, Direction placedBlockFacing, Direction placedBlockOppositeFacing) {// BlockState upState, BlockState downState, BlockState forwardState, BlockState backwardState, BlockState forwardUpState, BlockState backwardUpState, Direction placedBlockFacing, Direction placedBlockOppositeFacing

        BlockPos backwardPos = pCurrentPos.relative(placedBlockFacing);
        BlockPos backward2Pos = backwardPos.relative(placedBlockFacing);
        BlockPos forwardPos = pCurrentPos.relative(placedBlockFacing.getOpposite());
        BlockPos backwardUpPos = pCurrentPos.relative(placedBlockFacing).above();
        BlockPos forwardUpPos = pCurrentPos.relative(placedBlockFacing.getOpposite()).above();
        BlockPos backwardDownPos = pCurrentPos.relative(placedBlockFacing).below();
        BlockPos forwardDownPos = pCurrentPos.relative(placedBlockFacing.getOpposite()).below();
        BlockPos upPos = pCurrentPos.above();
        BlockPos downPos = pCurrentPos.below();

        BlockState backwardState = pLevel.getBlockState(backwardPos);
        BlockState backward2State = pLevel.getBlockState(backward2Pos);
        BlockState forwardState = pLevel.getBlockState(forwardPos);
        BlockState backwardUpState = pLevel.getBlockState(backwardUpPos);
        BlockState forwardUpState = pLevel.getBlockState(forwardUpPos);
        BlockState backwardDownState = pLevel.getBlockState(backwardDownPos);
        BlockState forwardDownState = pLevel.getBlockState(forwardDownPos);
        BlockState upState = pLevel.getBlockState(upPos);
        BlockState downState = pLevel.getBlockState(downPos);

        TopBottomShape backwardHorizontal = backwardState.getBlock() instanceof HorizontalBeamBlock ? backwardState.getValue(HorizontalBeamBlock.TYPE) : null;
        TopBottomShape forwardHorizontal = forwardState.getBlock() instanceof HorizontalBeamBlock ? forwardState.getValue(HorizontalBeamBlock.TYPE) : null;

        Direction upBlockVertical = upState.getBlock() instanceof VerticalBeamBlock ? upState.getValue(FACING) : null;
        Direction downBlockVertical = downState.getBlock() instanceof VerticalBeamBlock ? downState.getValue(FACING) : null;
        Direction backwardDownVertical = backwardDownState.getBlock() instanceof VerticalBeamBlock ? backwardDownState.getValue(FACING) : null;
        Direction backwardBlockDiagonal = backwardState.getBlock() instanceof DiagonalBeamBlock && (backwardState.getValue(BEAM) == 1) ? backwardState.getValue(FACING) : null;
        Direction forwardBlockDiagonal = forwardState.getBlock() instanceof DiagonalBeamBlock && (forwardState.getValue(BEAM) == 1) ? forwardState.getValue(FACING) : null;
        Direction backwardBlockDiagonal2 = backwardState.getBlock() instanceof DiagonalBeamBlock && (backwardState.getValue(BEAM) == 2) ? backwardState.getValue(FACING) : null;
        Direction forwardBlockDiagonal2 = forwardState.getBlock() instanceof DiagonalBeamBlock && (forwardState.getValue(BEAM) == 2) ? forwardState.getValue(FACING) : null;
        Direction backwardUpDiagonal = backwardUpState.getBlock() instanceof DiagonalBeamBlock ? backwardUpState.getValue(FACING) : null;
        Direction forwardUpDiagonal = forwardUpState.getBlock() instanceof DiagonalBeamBlock ? forwardUpState.getValue(FACING) : null;
        Direction upDiagonal = upState.getBlock() instanceof DiagonalBeamBlock ? upState.getValue(FACING) : null;
        Direction downDiagonal = downState.getBlock() instanceof DiagonalBeamBlock ? downState.getValue(FACING) : null;

        Direction backwardUpBlockDiagonal = backwardUpState.getBlock() instanceof DiagonalBeamBlock && (backwardUpState.getValue(BEAM) == 1) ? backwardUpState.getValue(FACING) : null;
        Direction forwardDownBlockDiagonal = forwardDownState.getBlock() instanceof DiagonalBeamBlock && (forwardDownState.getValue(BEAM) == 1) ? forwardDownState.getValue(FACING) : null;
//        Direction forwardUpBlockDiagonal = forwardUpState.getBlock() instanceof DiagonalBeamBlock && (forwardUpState.getValue(BEAM) == 1) ? forwardUpState.getValue(FACING) : null;



        BeamConnection connect = BeamConnection.NONE;
        if (forwardHorizontal == TopBottomShape.BOTTOM && downBlockVertical == placedBlockFacing
                && backwardHorizontal == TopBottomShape.TOP && upBlockVertical == placedBlockOppositeFacing) {
            connect = BeamConnection.AH_AV_CH_CV;

        } else if (forwardHorizontal == TopBottomShape.BOTTOM && downBlockVertical == placedBlockFacing
                && backwardHorizontal == TopBottomShape.TOP) {
            connect = BeamConnection.AH_AV_CH;

        } else if (forwardHorizontal == TopBottomShape.BOTTOM && downBlockVertical == placedBlockFacing
                && upBlockVertical == placedBlockOppositeFacing) {
            connect = BeamConnection.AH_AV_CV;

        } else if (forwardHorizontal == TopBottomShape.BOTTOM && backwardHorizontal == TopBottomShape.TOP
                && upBlockVertical == placedBlockOppositeFacing) {
            connect = BeamConnection.AH_CH_CV;

        } else if (downBlockVertical == placedBlockFacing && backwardHorizontal == TopBottomShape.TOP
                && upBlockVertical == placedBlockOppositeFacing) {
            connect = BeamConnection.AV_CH_CV;

        } else if (forwardHorizontal == TopBottomShape.BOTTOM && downBlockVertical == placedBlockFacing
                && backwardBlockDiagonal == placedBlockOppositeFacing && (backwardBlockDiagonal == Direction.NORTH || backwardBlockDiagonal == Direction.EAST)) {
            connect = BeamConnection.AH_AV_N;

        } else if (forwardHorizontal == TopBottomShape.BOTTOM && backwardBlockDiagonal == placedBlockOppositeFacing
                && (backwardBlockDiagonal == Direction.NORTH || backwardBlockDiagonal == Direction.EAST)) {
            connect = BeamConnection.AH_N;

        } else if (downBlockVertical == placedBlockFacing && backwardBlockDiagonal == placedBlockOppositeFacing
                && (backwardBlockDiagonal == Direction.NORTH || backwardBlockDiagonal == Direction.EAST)) {
            connect = BeamConnection.AV_N;

            //------------------- | BEAM 2 SPECIFIC CONDITIONS |------------------------------------------------------------

        } else if (forwardHorizontal == TopBottomShape.BOTTOM && downBlockVertical == placedBlockFacing
                && (backwardBlockDiagonal2 == Direction.NORTH || backwardBlockDiagonal2 == Direction.EAST)) {
            connect = BeamConnection.AH_AV_N;

        } else if (forwardHorizontal == TopBottomShape.BOTTOM
                && (backwardBlockDiagonal2 == Direction.NORTH || backwardBlockDiagonal2 == Direction.EAST)) {
            connect = BeamConnection.AH_N;

        } else if (!(backwardDownVertical == placedBlockFacing) && (downBlockVertical == placedBlockFacing
                && (backwardBlockDiagonal2 == Direction.NORTH || backwardBlockDiagonal2 == Direction.EAST))) {
            connect = BeamConnection.AV_N;

        } else if (backwardHorizontal == TopBottomShape.BOTTOM && downBlockVertical == placedBlockOppositeFacing
                && (forwardBlockDiagonal2 == Direction.NORTH || forwardBlockDiagonal2 == Direction.EAST)) {
            connect = BeamConnection.DH_DV_M;

        } else if (backwardHorizontal == TopBottomShape.BOTTOM
                && (forwardBlockDiagonal2 == Direction.NORTH || forwardBlockDiagonal2 == Direction.EAST)) {
            connect = BeamConnection.DH_M;

        } else if (downBlockVertical == placedBlockOppositeFacing && forwardBlockDiagonal2 == placedBlockOppositeFacing
                 && !((forwardState.getValue(CONNECT) == BeamConnection.DV_M) && (forwardBlockDiagonal2 == Direction.NORTH || forwardBlockDiagonal2 == Direction.EAST))) {
            connect = BeamConnection.DV_M;

            //--------------------------------------------------------------------------------------------------------------

        } else if (forwardHorizontal == TopBottomShape.BOTTOM
                && downBlockVertical == placedBlockFacing) {
            connect = BeamConnection.AH_AV;

        } else if (downBlockVertical == placedBlockFacing
                && backwardHorizontal == TopBottomShape.TOP) {
            connect = BeamConnection.AV_CH;

        } else if (downBlockVertical == placedBlockFacing
                && upBlockVertical == placedBlockOppositeFacing) {
            connect = BeamConnection.AV_CV;

        } else if (forwardHorizontal == TopBottomShape.BOTTOM
                && upBlockVertical == placedBlockOppositeFacing) {
            connect = BeamConnection.AH_CV;

        } else if (forwardHorizontal == TopBottomShape.BOTTOM
                && backwardHorizontal == TopBottomShape.TOP) {
            connect = BeamConnection.AH_CH;

        } else if (forwardHorizontal == TopBottomShape.BOTTOM
                && backwardHorizontal == TopBottomShape.BOTTOM) {
            connect = BeamConnection.AH_DH;

        } else if (downBlockVertical == placedBlockFacing
                && backwardHorizontal == TopBottomShape.BOTTOM) {
            connect = BeamConnection.AV_DH;

        } else if (backwardHorizontal == TopBottomShape.TOP
                && upBlockVertical == placedBlockOppositeFacing) {
            connect = BeamConnection.CH_CV;

        } else if (forwardHorizontal == TopBottomShape.BOTTOM) {
            connect = BeamConnection.AH;

        } else if (backwardHorizontal == TopBottomShape.TOP) {
            connect = BeamConnection.CH;

        } else if (upBlockVertical == placedBlockOppositeFacing) {
            connect = BeamConnection.CV;

        } else if (downBlockVertical == placedBlockFacing) {
            connect = BeamConnection.AV;

        } else if (downBlockVertical == placedBlockOppositeFacing) {
            connect = BeamConnection.DV;

        } else if (backwardHorizontal == TopBottomShape.BOTTOM) {
            connect = BeamConnection.DH;

            //------------------- | BEAM 2 SPECIFIC CONDITIONS |------------------------------------------------------------

        } else if (backwardBlockDiagonal2 ==  Direction.NORTH || backwardBlockDiagonal2 == Direction.EAST) {
            connect = BeamConnection.N;

        } else if (forwardBlockDiagonal2 == placedBlockOppositeFacing && !(forwardDownState.getBlock() instanceof VerticalBeamBlock) && (forwardBlockDiagonal2 == Direction.SOUTH || forwardBlockDiagonal2 == Direction.WEST)) {
            connect = BeamConnection.M;

            //--------------------------------------------------------------------------------------------------------------

        } else if (forwardBlockDiagonal == placedBlockOppositeFacing && !(forwardDownState.getBlock() instanceof VerticalBeamBlock) && (forwardBlockDiagonal == Direction.SOUTH || forwardBlockDiagonal == Direction.WEST)) {
            connect = BeamConnection.M;


        } else if ((backwardBlockDiagonal == Direction.NORTH || backwardBlockDiagonal == Direction.EAST)
                && ((backwardUpDiagonal == placedBlockOppositeFacing) || (isAir(backwardUpState) && isAir(forwardUpState)))) { //forwardUpDiagonal == placedBlockFacing ||
            connect = BeamConnection.N;


        } else {
            connect = BeamConnection.NONE;
        }

//        } else if (backwardBlockDiagonal == Direction.NORTH || backwardBlockDiagonal == Direction.EAST) {
//            if ((upDiagonal == placedBlockFacing && backwardUpBlockDiagonal == placedBlockOppositeFacing)
//                    || (isAir(backwardUpState) && isAir(upState))
//                    || !(backwardUpDiagonal == placedBlockFacing)) {
//                connect = BeamConnection.N;
//            }

            //OLD
//        } else if ((backwardBlockDiagonal == Direction.NORTH || backwardBlockDiagonal == Direction.EAST)
//                && !((forwardUpDiagonal == placedBlockFacing || backwardUpDiagonal == placedBlockFacing))) {
//            connect = BeamConnection.N;




//        LOGGER.info("Block: " + pCurrentPos.toString() + " State: " + pState.getValue(CONNECT) + " Facing: " + placedBlockFacing);
//        LOGGER.info("Forward: " + forwardPos.toString() + " State: " + (forwardState.getBlock() instanceof DiagonalBeamBlock || forwardState.getBlock() instanceof HorizontalBeamBlock ? forwardState.getValue(BEAM) + " Facing: " + forwardState.getValue(FACING) : null));
//        LOGGER.info("Backward: " + backwardPos.toString() + " State: " + (backwardState.getBlock() instanceof DiagonalBeamBlock || backwardState.getBlock() instanceof HorizontalBeamBlock ? backwardState.getValue(BEAM) + " Facing: " + backwardState.getValue(FACING) : null));
//        LOGGER.info("Up: " + upPos.toString() + " State: " + (upState.getBlock() instanceof VerticalBeamBlock ? upState.getValue(BEAM) + " Facing: " + upState.getValue(FACING) : null));
//        LOGGER.info("Down: " + downPos.toString() + " State: " + (downState.getBlock() instanceof VerticalBeamBlock ? downState.getValue(BEAM) + " Facing: " + downState.getValue(FACING) : null));

//        LOGGER.info("Connect: " + connect);

        return blockstate.setValue(CONNECT, connect);
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