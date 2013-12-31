package com.agateau.burgerparty.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class MaskedDrawable {
	public MaskedDrawable(TextureRegion region) {
		this.drawable = new TextureRegionDrawable(region);
		this.mask = new CollisionMask(region);
	}
	public MaskedDrawable(Drawable drawable, CollisionMask mask) {
		this.drawable = drawable;
		this.mask = mask;
	}
	final Drawable drawable;
	final CollisionMask mask;
}