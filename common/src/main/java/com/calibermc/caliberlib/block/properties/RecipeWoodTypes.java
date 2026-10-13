package com.calibermc.caliberlib.block.properties;

public enum RecipeWoodTypes {
    ACACIA("acacia"),
    ALPHA("alpha"),
    BAMBOO("bamboo"),
    BAOBAB("baobab"),
    BIRCH("birch"),
    BLACKWOOD("blackwood"),
    BLUE_BIOSHROOM("blue_bioshroom"),
    BRIMWOOD("brimwood"),
    CEDAR("cedar"),
    CHERRY("cherry"),
    COBALT("cobalt"),
    CRIMSON("crimson"),
    CYPRESS("cypress"),
    DARK_OAK("dark_oak"),
    DEAD("dead"),
    EUCALYPUS("eucalyptus"),
    FIR("fir"),
    HELLBARK("hellbark"),
    JACARANDA("jacaranda"),
    JOSHUA("joshua"),
    JUNGLE("jungle"),
    KAPOK("kapok"),
    LARCH("larch"),
    MAGIC("magic"),
    MAGNOLIA("magnolia"),
    MAHOGANY("mahogany"),
    MANGROVE("mangrove"),
    MAPLE("maple"),
    MAUVE("mauve"),
    OAK("oak"),
    PALM("palm"),
    PINE("pine"),
    PINK_BIOSHROOM("pink_bioshroom"),
    REDWOOD("redwood"),
    SOCOTRA("socotra"),
    SPRUCE("spruce"),
    UMBRAN("umbran"),
    WARPED("warped"),
    WILLOW("willow"),
    YELLOW_BIOSHROOM("yellow_bioshroom"),

    BLACK_PAINTED("black_painted"),
    BLUE_PAINTED("blue_painted"),
    BROWN_PAINTED("brown_painted"),
    CYAN_PAINTED("cyan_painted"),
    GRAY_PAINTED("gray_painted"),
    GREEN_PAINTED("green_painted"),
    LIGHT_BLUE_PAINTED("light_blue_painted"),
    LIGHT_GRAY_PAINTED("light_gray_painted"),
    LIME_PAINTED("lime_painted"),
    MAGENTA_PAINTED("magenta_painted"),
    ORANGE_PAINTED("orange_painted"),
    PINK_PAINTED("pink_painted"),
    PURPLE_PAINTED("purple_painted"),
    RED_PAINTED("red_painted"),
    WHITE_PAINTED("white_painted"),
    YELLOW_PAINTED("yellow_painted");

    private final String name;

    RecipeWoodTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
