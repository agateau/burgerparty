package com.agateau.burgerparty.view;

import com.agateau.burgerparty.utils.ResizeToFitChildren;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Bubble extends Group implements ResizeToFitChildren {
	public Bubble(TextureAtlas atlas) {
		mBgImage = new Image(atlas.createPatch("bubble"));
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
		final float padding = 20;
		final float leftPadding = 40;
		mChild.setPosition(leftPadding, padding);
		float width = leftPadding + mChild.getWidth() * mChild.getScaleX() + padding;
		float height = padding + mChild.getHeight() * mChild.getScaleY() + padding;
		setSize(MathUtils.ceil(width), MathUtils.ceil(height));
	}

	@Override
	public void onChildSizeChanged() {
		updateGeometry();
	}

	private Image mBgImage;
	private Actor mChild = null;
}
