package com.calibermc.caliberlib.block.shapes;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum LeftRightDoubleShape implements StringRepresentable {

    LEFT("left"),
    RIGHT("right"),
    DOUBLE("double");


    private final String name;

    LeftRightDoubleShape(String pName) {
        this.name = pName;
    }

    public String toString() {
        return this.name;
    }

    public @NotNull String getSerializedName() {
        return this.name;
    }
}

