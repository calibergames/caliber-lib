package com.calibermc.caliberlib.block.shapes.misc;

import com.calibermc.caliberlib.block.custom.DiagonalBeamBlock;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum BeamConnection implements StringRepresentable {
    /**
     * Coordinates a, b, c and d are equivalent to the following:
     * A = forward and bottom
     * B = forward and top
     * C = backward and top
     * D = backward and bottom
     * |-|
     * The following connections are possible:
     * H = horizontal
     * V = vertical
     * N = diagonal top
     * M = diagonal bottom
     */
    NONE("none"),

    // 1 way connections
    AH("ah"),
    CH("ch"),
    DH("dh"),
    AV("av"),
    CV("cv"),
    DV("dv"),
    M("m"),
    N("n"),

    // 2 way connections
    AH_CH("ah_ch"),
    AH_AV("ah_av"),
    AH_DH("ah_dh"),
    AH_CV("ah_cv"),
    AH_DV("ah_dv"),
    AH_N("ah_n"),
    AV_CH("av_ch"),
    AV_CV("av_cv"),
    AV_DH("av_dh"),
    AV_N("av_n"),
    CH_CV("ch_cv"),
    DH_M("dh_m"),
    DV_M("dv_m"),

    // 3 way connections
    AH_AV_CH("ah_av_ch"),
    AH_AV_CV("ah_av_cv"),
    AH_CH_CV("ah_ch_cv"),
    AV_CH_CV("av_ch_cv"),
    AH_AV_N("ah_av_n"),
    DH_DV_M("dh_dv_m"),

    // 4 way connections
    AH_AV_CH_CV("ah_av_ch_cv");



    private final String name;

    BeamConnection(String pName) {
        this.name = pName;
    }

    public String toString() {
        return this.name;
    }

    public @NotNull String getSerializedName() {
        return this.name;
    }
}
