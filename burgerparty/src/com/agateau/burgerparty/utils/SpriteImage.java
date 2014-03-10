package com.agateau.burgerparty.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class SpriteImage extends Image {
	private MaskedDrawable mMaskedDrawable;

	public SpriteImage() {
	}

	public SpriteImage(TextureRegion region) {
		setMaskedDrawable(new MaskedDrawable(region));
	}

	public SpriteImage(TextureRegion region, CollisionMask mask) {
		setMaskedDrawable(new MaskedDrawable(region, mask));
	}

	public SpriteImage(MaskedDrawable maskedDrawable) {
		setMaskedDrawable(maskedDrawable);
	}

	public void setMaskedDrawable(MaskedDrawable maskedDrawable) {
		mMaskedDrawable = maskedDrawable;
		setDrawable(maskedDrawable.drawable);
		setWidth(getPrefWidth());
		setHeight(getPrefHeight());
	}

	public MaskedDrawable getMaskedDrawable() {
		return mMaskedDrawable;
	}

	public CollisionMask getCollisionMask() {
		return mMaskedDrawable.mask;
	}

	public static boolean collide(SpriteImage i1, SpriteImage i2) {
		if (!boundCollide(i1, i2)) {
			return false;
		}
		return i1.mMaskedDrawable.mask.collide(i2.mMaskedDrawable.mask, (int)(i2.getX() - i1.getX()), (int)(i2.getY() - i1.getY()));
	}

	public static boolean boundCollide(Actor a1, Actor a2) {
		if (!a1.isVisible()) {
			return false;
		}
		if (!a2.isVisible()) {
			return false;
		}
		float a1Left = a1.getX();
		float a1Right = a1.getRight();
		float a1Bottom = a1.getY();
		float a1Top = a1.getTop();
		float a2Left = a2.getX();
		float a2Right = a2.getRight();
		float a2Bottom = a2.getY();
		float a2Top = a2.getTop();
		if (a1Right < a2Left) {
			return false;
		}
		if (a2Right < a1Left) {
			return false;
		}
		if (a1Top < a2Bottom) {
			return false;
		}
		if (a2Top < a1Bottom) {
			return false;
		}
		return true;
	}
}
