package com.agateau.burgerparty.burgercopter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class Gauge extends Widget {
	private static final float STEP_WIDTH = 6;
	private static final float STEP_HEIGHT = 20;

	public Gauge(TextureRegion region, int steps) {
		mRegion = region;
		mSteps = steps;
	}

	public void setValue(float value) {
		mValue = value;
	}

	public float getValue() {
		return mValue;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		Color color = getColor();
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		float x = getX();
		float y = getY();
		int onIdx = (int)(mValue * mSteps);
		boolean reducedAlpha = false;
		for (int idx = 0; idx < mSteps; ++idx, x += STEP_WIDTH) {
			if (idx > onIdx && !reducedAlpha) {
				batch.setColor(color.r, color.g, color.b, color.a * 0.5f);
				reducedAlpha = true;
			}
			batch.draw(mRegion, x, y, STEP_WIDTH - 1, STEP_HEIGHT);
		}
	}

	@Override
	public float getPrefWidth() {
		return STEP_WIDTH * mSteps - 1;
	}

	@Override
	public float getPrefHeight() {
		return STEP_HEIGHT;
	}

	private float mValue = 0;
	private final TextureRegion mRegion;
	private final int mSteps;
}
