package com.agateau.burgerparty.view;

import com.agateau.burgerparty.utils.ResizeToFitChildren;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Bubble extends Group implements ResizeToFitChildren {
	public Bubble(NinePatch patch) {
		mPatch = patch;
		mBgImage = new Image(mPatch);
		mBgImage.setFillParent(true);
		addActor(mBgImage);
	}

	public void setChild(Actor child) {
		if (mChild != null) {
			removeActor(mChild);
		}
		mChild = child;
		if (mChild != null) {
			addActor(mChild);
			updateGeometry();
		}
	}

	public void updateGeometry() {
		final float padLeft = mPatch.getPadLeft();
		final float padRight = mPatch.getPadRight();
		final float padTop = mPatch.getPadTop();
		final float padBottom = mPatch.getPadBottom();
		mChild.setPosition(padLeft, padBottom);
		float width = padLeft + mChild.getWidth() * mChild.getScaleX() + padRight;
		float height = padTop + mChild.getHeight() * mChild.getScaleY() + padBottom;
		setSize(MathUtils.ceil(width), MathUtils.ceil(height));
	}

	@Override
	public void onChildSizeChanged() {
		updateGeometry();
	}

	private Image mBgImage;
	private NinePatch mPatch;
	private Actor mChild = null;
}
