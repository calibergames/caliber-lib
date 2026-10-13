package com.calibermc.caliberlib.block.shapes;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum IntersectionShape implements StringRepresentable {
    INNER_LEFT("inner_left"),
    INNER_RIGHT("inner_right"),
    OUTER_LEFT("outer_left"),
    OUTER_RIGHT("outer_right"),
    CONNECTED("connected"),
    CROSS("cross"),
    END("end"),
    SINGLE("single"),
    T("t"),
    T_LEFT("t_left"),
    T_RIGHT("t_right"),
    T_OPPOSITE("t_opposite");




    private final String name;

    IntersectionShape(String pName) {
        this.name = pName;
    }

    public String toString() {
        return this.name;
    }

    public @NotNull String getSerializedName() {
        return this.name;
    }
}

