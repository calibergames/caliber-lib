package com.calibermc.caliberlib.assets;

import javax.annotation.Nullable;

/** The tool that mines a family's shapes fastest: its {@code minecraft:mineable/*} tag, or none. */
public enum MiningTool {
	PICKAXE("minecraft:mineable/pickaxe"),
	AXE("minecraft:mineable/axe"),
	SHOVEL("minecraft:mineable/shovel"),
	HOE("minecraft:mineable/hoe"),
	/** No tool tag, as vanilla's wool: any tool mines it at hand speed. */
	NONE(null);

	@Nullable
	private final String tag;

	MiningTool(@Nullable String tag) {
		this.tag = tag;
	}

	/** The block tag, or null for {@link #NONE}. */
	@Nullable
	public String tag() {
		return tag;
	}

	/** The default for a kind: pickaxe for stone, axe for wood. */
	public static MiningTool of(MaterialKind kind) {
		return kind == MaterialKind.WOOD ? AXE : PICKAXE;
	}
}
