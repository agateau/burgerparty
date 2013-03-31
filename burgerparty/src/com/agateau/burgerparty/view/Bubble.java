package com.agateau.burgerparty.view;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class Bubble extends WidgetGroup {
	public Bubble(TextureAtlas atlas) {
		mBgImage = new Image(atlas.createPatch("bubble"));
		addActor(mBgImage);
	}

	public void setChild(Actor actor) {
		if (mChild != null) {
			removeActor(mChild);
		}
		mChild = actor;
		if (mChild != null) {
			addActor(mChild);
			updateGeometry();
		}
	}

	public void updateGeometry() {
		final float padding = 20;
		final float leftPadding = 40;
		mChild.setPosition(leftPadding, padding);
		mBgImage.setSize(
			leftPadding + mChild.getWidth() * mChild.getScaleX() + padding,
			padding + mChild.getHeight() * mChild.getScaleY() + padding);
		setSize(mBgImage.getWidth(), mBgImage.getHeight());
	}

	private Image mBgImage;
	private Actor mChild = null;
}
