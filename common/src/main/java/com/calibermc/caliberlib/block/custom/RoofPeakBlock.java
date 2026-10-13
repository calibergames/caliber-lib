package com.calibermc.caliberlib.block.custom;

import com.calibermc.caliberlib.block.shapes.ModelOcclusion;
import com.calibermc.caliberlib.block.shapes.RoofPeakShape;
import com.calibermc.caliberlib.block.shapes.RoofPitch;
import com.calibermc.caliberlib.block.shapes.RoofShape;
import com.calibermc.caliberlib.util.ModBlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class RoofPeakBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {

    /** The pitch of the roof the cap sits on, read from the roof pieces beside it. */
    public static final EnumProperty<RoofPitch> PITCH = ModBlockStateProperties.ROOF_PITCH;
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<RoofPeakShape> TYPE = ModBlockStateProperties.ROOF_PEAK_SHAPE;

    protected static final VoxelShape BOTTOM_SHAPE = Block.box(0, 0, 0, 16, 8, 16);
    protected static final VoxelShape TOP_SHAPE = Block.box(0, .01, 0, 16, 16, 16);

    public RoofPeakBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(PITCH, RoofPitch.PITCH_45)
                .setValue(FACING, Direction.NORTH)
                .setValue(TYPE, RoofPeakShape.STRAIGHT)
                .setValue(HALF, Half.BOTTOM)
                .setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    /** What blocks light and hides neighbouring faces: the solid parts of the model this state shows. */
    @Override
    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return ModelOcclusion.of("roof_peak", pState, () -> super.getOcclusionShape(pState, pLevel, pPos));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, TYPE, HALF, PITCH, WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        Half Half = pState.getValue(HALF);
        switch (Half) {
            case TOP -> {
                return TOP_SHAPE;
            }
            default -> {
                return BOTTOM_SHAPE;
            }
        }
    }

//    @Override
//    @Nullable
//    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
//        BlockPos blockpos = pContext.getClickedPos();
//        FluidState fluidstate = pContext.getLevel().getFluidState(blockpos);
//        BlockState blockstate1 = this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection()) //.getOpposite()
//                .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
//
//        return blockstate1.setValue(TYPE, getRoofShape(blockstate1, pContext.getLevel(), blockpos));//.setValue(PITCH, getRoofPitch(blockstate1, pContext.getLevel(), blockpos));
//    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos blockpos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        BlockState blockstate = level.getBlockState(blockpos);

        if (blockstate.is(this)) {
            return blockstate.setValue(HALF, Half.TOP).setValue(WATERLOGGED, Boolean.FALSE);
        } else {
            FluidState fluidstate = level.getFluidState(blockpos);
            Direction facing = pContext.getHorizontalDirection().getOpposite();
            Half half = (pContext.getClickedFace() != Direction.DOWN && (pContext.getClickedFace() == Direction.UP ||
                    !(pContext.getClickLocation().y - (double) blockpos.getY() > 0.5D))) ? Half.BOTTOM : Half.TOP;

            BlockState blockstate1 = this.defaultBlockState()
                    .setValue(FACING, facing)
                    .setValue(HALF, half)
                    .setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);

            // Determine RoofShape based on surrounding blocks
            blockstate1 = blockstate1.setValue(PITCH, getRoofPitch(blockstate1, level, blockpos));
            RoofPeakShape roofShape = getRoofShape(blockstate1, level, blockpos);
            blockstate1 = blockstate1.setValue(TYPE, roofShape);

            return blockstate1;
        }
    }

    @Override
    public boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
        ItemStack itemstack = pUseContext.getItemInHand();
        Half Half = pState.getValue(HALF);
        if (Half != net.minecraft.world.level.block.state.properties.Half.TOP && itemstack.is(this.asItem())) {
            if (pUseContext.replacingClickedOnBlock()) {
                boolean flag = pUseContext.getClickLocation().y - (double) pUseContext.getClickedPos().getY() > 0.5D;
                Direction direction = pUseContext.getClickedFace();
                if (Half == net.minecraft.world.level.block.state.properties.Half.BOTTOM) {
                    return direction == Direction.UP || flag && direction.getAxis().isHorizontal();
                } else {
                    return direction == Direction.DOWN || !flag && direction.getAxis().isHorizontal();
                }
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Update the provided state given the provided neighbor direction and neighbor state, returning a new state.
     * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
     * returns its solidified counterpart.
     * Note that this method should ideally consider only the specific direction passed in.
     */
    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        pState = pState.setValue(PITCH, getRoofPitch(pState, pLevel, pCurrentPos));
        if (pFacing.getAxis().isHorizontal()) {
            // Set the TYPE value based on the BlockShape
            pState = pState.setValue(TYPE, getRoofShape(pState, pLevel, pCurrentPos));
        } else {
            pState = super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
        }

        return pState;
    }


    /**
     * The cap's shape from the caps beside it and the hips (outer roof
     * corners) at its diagonals. Only the six direct neighbours update a
     * block, so a hip changed at a diagonal shows once a neighbour of the cap
     * changes.
     */
    static RoofPeakShape getRoofShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        Direction facing = pState.getValue(FACING);
        RoofPitch pitch = pState.getValue(PITCH);
        Direction opposite = facing.getOpposite();
        boolean front = isRoof(pLevel.getBlockState(pPos.relative(facing)));
        boolean back = isRoof(pLevel.getBlockState(pPos.relative(opposite)));
        boolean left = isRoof(pLevel.getBlockState(pPos.relative(facing.getCounterClockWise())));
        boolean right = isRoof(pLevel.getBlockState(pPos.relative(facing.getClockWise())));

        boolean downFrontLeft = isHipAt(pLevel, pPos, facing, facing.getCounterClockWise(), pitch);
        boolean downFrontRight = isHipAt(pLevel, pPos, facing, facing.getClockWise(), pitch);
        boolean downBackLeft = isHipAt(pLevel, pPos, opposite, opposite.getCounterClockWise(), pitch);
        boolean downBackRight = isHipAt(pLevel, pPos, opposite, opposite.getClockWise(), pitch);

        // Check for peak cap
        if (!front && !back && !left && !right) {
            return RoofPeakShape.CAP;
        }
        // Check for corner (left)
        if ((left && front && !right && !back) || (right && back && !left && !front)) {
            return RoofPeakShape.CORNER_LEFT;
        }
        // Check for corner (right)
        if ((right && front && !left && !back) || (left && back && !right && !front)) {
            return RoofPeakShape.CORNER_RIGHT;
        }
        // Check for 3-way intersection (forward)
        if (back && left && right && !front) {
            return RoofPeakShape.FACING_T;
        }
        // Check for 3-way intersection (backward)
        if (front && left && right && !back) {
            return RoofPeakShape.OPPOSITE_T;
        }
        // Check for 3-way intersection (left)
        if (left && front && back && !right) {
            return RoofPeakShape.LEFT_T;
        }
        // Check for 3-way intersection (right)
        if (right && front && back && !left) {
            return RoofPeakShape.RIGHT_T;
        }
        // Check for 4-way intersection
        if (right && left && front && back) {
            return RoofPeakShape.CROSS;
        }
        // Check for roof end hip
        if ((downFrontRight || downFrontLeft || downBackRight || downBackLeft)) {
            if (front && !back && !left && !right) {
                return RoofPeakShape.END_FACING;
            }
            if (!front && back && !left && !right) {
                return RoofPeakShape.END_OPPOSITE;
            }
        }
        return RoofPeakShape.STRAIGHT;
    }

    /**
     * The pitch of the roof below the cap: from a roof piece beside it, at its
     * own level or one below (a 22 roof's bottom half sits level with a
     * raised cap), or else from a neighbouring cap; the state's own pitch
     * when there is neither. Pieces are read north, east, south, west, and
     * the first one found decides where pitches are mixed. Roof pieces one
     * level down are not neighbours, so changing one updates the pitch only
     * once a neighbour of the cap changes.
     */
    static RoofPitch getRoofPitch(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        for (BlockPos level : new BlockPos[] {pPos.below(), pPos}) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                RoofPitch pitch = pitchOf(pLevel.getBlockState(level.relative(direction)));
                if (pitch != null) {
                    return pitch;
                }
            }
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState neighbour = pLevel.getBlockState(pPos.relative(direction));
            if (isRoof(neighbour)) {
                return neighbour.getValue(PITCH);
            }
        }
        return pState.getValue(PITCH);
    }

    @Nullable
    static RoofPitch pitchOf(BlockState pState) {
        if (pState.getBlock() instanceof Roof22Block) {
            return RoofPitch.PITCH_22;
        }
        if (pState.getBlock() instanceof Roof45Block) {
            return RoofPitch.PITCH_45;
        }
        if (pState.getBlock() instanceof Roof67Block) {
            return RoofPitch.PITCH_67;
        }
        return null;
    }

    /**
     * Whether a hip (an outer roof corner of the cap's pitch) meets the cap at
     * this diagonal: one level down, or, on a 22 roof, level with it (its
     * bottom halves sit level with a raised cap).
     */
    static boolean isHipAt(BlockGetter pLevel, BlockPos pPos, Direction first, Direction second, RoofPitch pitch) {
        BlockPos diagonal = pPos.relative(first).relative(second);
        if (isHip(pLevel.getBlockState(diagonal.below()), pitch)) {
            return true;
        }
        return pitch == RoofPitch.PITCH_22 && isHip(pLevel.getBlockState(diagonal), pitch);
    }

    private static boolean isHip(BlockState pState, RoofPitch pitch) {
        return isHip(pState) && pitchOf(pState) == pitch;
    }

    public static boolean isRoof(BlockState pState) {
        return pState.getBlock() instanceof RoofPeakBlock;
    }

    /** An outer roof corner of any pitch. */
    public static boolean isHip(BlockState pState) {
        RoofShape shape = null;
        if (pState.getBlock() instanceof Roof22Block) {
            shape = pState.getValue(Roof22Block.TYPE);
        } else if (pState.getBlock() instanceof Roof45Block) {
            shape = pState.getValue(Roof45Block.TYPE);
        } else if (pState.getBlock() instanceof Roof67Block) {
            shape = pState.getValue(Roof67Block.TYPE);
        }
        return shape == RoofShape.OUTER_LEFT || shape == RoofShape.OUTER_RIGHT;
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