package com.calibermc.caliberlib.assets;

/** What a family is made of: it picks the cutter, the mining tool and whether the shapes burn. */
public enum MaterialKind {
	STONE,
	WOOD;

	public String key() {
		return this == STONE ? "stone" : "wood";
	}
}
