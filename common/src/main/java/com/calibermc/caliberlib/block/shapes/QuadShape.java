package com.calibermc.caliberlib.block.shapes;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum QuadShape implements StringRepresentable {
    LEFT("left"),
    RIGHT("right"),
    TOP_LEFT("top_left"),
    TOP_RIGHT("top_right");


    private final String name;

    QuadShape(String pName) {
        this.name = pName;
    }

    public String toString() {
        return this.name;
    }

    public @NotNull String getSerializedName() {
        return this.name;
    }
}

