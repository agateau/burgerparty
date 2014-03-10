package com.agateau.burgerparty.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MaskedDrawable {
	public final TextureRegionDrawable drawable;
	public final CollisionMask mask;

	public MaskedDrawable(TextureRegion region) {
		this.drawable = new TextureRegionDrawable(region);
		this.mask = new CollisionMask(region);
	}

	public MaskedDrawable(TextureRegion region, CollisionMask mask) {
		this.drawable = new TextureRegionDrawable(region);
		this.mask = mask;
	}

	public MaskedDrawable(TextureRegionDrawable drawable, CollisionMask mask) {
		this.drawable = drawable;
		this.mask = mask;
	}

	public int getWidth() {
		return drawable.getRegion().getRegionWidth();
	}

	public int getHeight() {
		return drawable.getRegion().getRegionHeight();
	}
}