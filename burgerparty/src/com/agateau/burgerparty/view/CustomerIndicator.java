package com.agateau.burgerparty.view;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class CustomerIndicator extends Widget {
	private TextureRegion mRegion;
	private int mCount = 0;

	private static final float SPACING = 10;

	public CustomerIndicator(TextureAtlas atlas) {
		mRegion = atlas.findRegion("face");
		setHeight(mRegion.getRegionHeight());
	}

	public void setCount(int value) {
		mCount = value;
		setWidth(mCount * mRegion.getRegionWidth() + (mCount - 1) * SPACING);
		invalidate();
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.setColor(1, 1, 1, parentAlpha);
		float width = mRegion.getRegionWidth() * getScaleX();
		float height = getHeight() * getScaleX();
		float posX = 0;
		for(int n = 0; n < mCount; ++n, posX += width + SPACING * getScaleX()) {
			batch.draw(mRegion, getX() + posX, getY(), width, height);
		}
	}
}
