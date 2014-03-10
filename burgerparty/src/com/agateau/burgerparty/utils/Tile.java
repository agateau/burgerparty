package com.agateau.burgerparty.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Tile {
	public final TextureRegion region;
	public final int typeId;

	public Tile(TextureRegion region, int typeId) {
		assert(region != null);
		this.region = region;
		this.typeId = typeId;
	}

	public float getHeightAt(float x) {
		return region.getRegionHeight();
	}
}