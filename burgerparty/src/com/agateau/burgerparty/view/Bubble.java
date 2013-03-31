package com.agateau.burgerparty.view;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class Bubble extends WidgetGroup {
	public Bubble(TextureAtlas atlas) {
		mBgImage = new Image(atlas.createPatch("bubble"));
		mBgImage.setFillParent(true);
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

	public void layout() {
		super.layout();
		updateGeometry();
	}

	public void updateGeometry() {
		final float padding = 20;
		final float leftPadding = 40;
		mChild.setPosition(leftPadding, padding);
		setSize(
			leftPadding + mChild.getWidth() * mChild.getScaleX() + padding,
			padding + mChild.getHeight() * mChild.getScaleY() + padding);
	}

	private Image mBgImage;
	private Actor mChild = null;
}
