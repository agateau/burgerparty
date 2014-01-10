package com.agateau.burgerparty.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Tile {
	public Tile(TextureRegion region) {
		this.region = region;
	}

	public float getHeightAt(float x) {
		return region.getRegionHeight();
	}

	public final TextureRegion region;
}