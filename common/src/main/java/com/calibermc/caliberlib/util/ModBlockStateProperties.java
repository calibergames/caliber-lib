package com.calibermc.caliberlib.util;

import com.calibermc.caliberlib.block.shapes.*;
import com.calibermc.caliberlib.block.shapes.doors.TallDoorPart;
import com.calibermc.caliberlib.block.shapes.misc.BeamConnection;
import com.calibermc.caliberlib.block.shapes.trim.ArchTrim;
import com.calibermc.caliberlib.block.shapes.trim.LargeArchTrim;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;


public class ModBlockStateProperties {
    public static final EnumProperty<ArchShape> ARCH_SHAPE = EnumProperty.create("type", ArchShape.class);
    public static final EnumProperty<BalustradeShape> BALUSTRADE_SHAPE = EnumProperty.create("type", BalustradeShape.class);
    public static final EnumProperty<LargeArchShape> LARGE_ARCH_SHAPE = EnumProperty.create("type", LargeArchShape.class);
    public static final EnumProperty<LeftRightShape> LEFT_RIGHT_SHAPE = EnumProperty.create("type", LeftRightShape.class);
    public static final EnumProperty<LeftRightDoubleShape> LEFT_RIGHT_DOUBLE_SHAPE = EnumProperty.create("type", LeftRightDoubleShape.class);
    public static final EnumProperty<QuadShape> QUAD_SHAPE = EnumProperty.create("type", QuadShape.class);
    public static final EnumProperty<RoofShape> ROOF_SHAPE = EnumProperty.create("type", RoofShape.class);
    public static final EnumProperty<RoofPeakShape> ROOF_PEAK_SHAPE = EnumProperty.create("type", RoofPeakShape.class);
    public static final EnumProperty<SingleDoubleShape> SINGLE_DOUBLE_SHAPE = EnumProperty.create("type", SingleDoubleShape.class);
    public static final EnumProperty<TopBottomShape> TOP_BOTTOM_SHAPE = EnumProperty.create("type", TopBottomShape.class);
    public static final EnumProperty<TopBottomDoubleShape> TOP_BOTTOM_DOUBLE_SHAPE = EnumProperty.create("type", TopBottomDoubleShape.class);
    public static final EnumProperty<WindowShape> WINDOW_SHAPE = EnumProperty.create("type", WindowShape.class);

    public static final IntegerProperty FIVE_LAYERS = IntegerProperty.create("layers", 1, 5);
    public static final IntegerProperty VERTICAL_BEAM_SHAPE = IntegerProperty.create("beam", 1, 7);
    public static final IntegerProperty HORIZONTAL_BEAM_SHAPE = IntegerProperty.create("beam", 1, 6);
    public static final IntegerProperty DIAGONAL_BEAM_SHAPE = IntegerProperty.create("beam", 1, 2);
    public static final IntegerProperty LINTEL_SHAPE = IntegerProperty.create("beam", 1, 4);
    public static final IntegerProperty FRAME_SHAPE = IntegerProperty.create("beam", 1, 3);
    public static final EnumProperty<ArchTrim> ARCH_TRIM = EnumProperty.create("trim", ArchTrim.class);
    public static final EnumProperty<LargeArchTrim> LARGE_ARCH_TRIM = EnumProperty.create("trim", LargeArchTrim.class);
    public static final EnumProperty<RoofPitch> ROOF_PITCH = EnumProperty.create("pitch", RoofPitch.class);
    public static final EnumProperty<TallDoorPart> TALL_DOOR_PART = EnumProperty.create("part", TallDoorPart.class);
    public static final EnumProperty<BeamConnection> BEAM_CONNECTION = EnumProperty.create("connect", BeamConnection.class);
    public static final EnumProperty<IntersectionShape> INTERSECTION_SHAPE = EnumProperty.create("connect", IntersectionShape.class);

    public static final BooleanProperty ARCH = BooleanProperty.create("arch");

    public static boolean isVertical(Direction direction) {
        return direction == Direction.UP || direction == Direction.DOWN;
    }

    public static boolean isSide(Direction direction) {
        return direction == Direction.NORTH || direction == Direction.EAST || direction == Direction.SOUTH || direction == Direction.WEST;
    }

}

