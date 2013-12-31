package com.agateau.burgerparty.utils;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

class MaskedDrawable {
	public MaskedDrawable(Drawable drawable, CollisionMask mask) {
		this.drawable = drawable;
		this.mask = mask;
	}
	final Drawable drawable;
	final CollisionMask mask;
}