package com.calibermc.caliberlib.block.properties;

public enum RecipeStoneTypes {
    ANDESITE("andesite"),
    ASURINE("asurine"),
    BASALT("basalt"),
    BLACKSTONE("blackstone"),
    BRICK("brick"),
    CALCITE("calcite"),
    CHALK("chalk"),
    CRIMSITE("crimsite"),
    DIORITE("diorite"),
    DRIPSTONE("dripstone"),
    END_STONE("end_stone"),
    GRANITE("granite"),
    LIMESTONE("limestone"),
    MARBLE("marble"),
    NETHERRACK("netherrack"),
    NETHERITE("netherite"),
    OBSIDIAN("obsidian"),
    OCHRUM("ochrum"),
    PRISMARINE("prismarine"),
    PURPUR("purpur"),
    QUARTZ("quartz"),
    SANDSTONE("sandstone"),
    SCORIA("scoria"),
    SCORCHIA("scorchia"),
    SLATE("slate"),
    STONE("stone"),
    TUFF("tuff"),
    TERRACOTTA("terracotta"),
    VERIDIUM("veridium"),
    WARPED("warped")
;

    private final String name;

    RecipeStoneTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
