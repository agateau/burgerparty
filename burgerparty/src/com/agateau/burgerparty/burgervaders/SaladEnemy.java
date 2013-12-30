package com.agateau.burgerparty.burgervaders;

import com.badlogic.gdx.math.MathUtils;

public class SaladEnemy extends Enemy {
	private static final float PIXEL_PER_SECOND = 60;
	private static final float WAVE_WIDTH = 200;

	@Override
	public void doAct(float delta) {
		setY(getY() - PIXEL_PER_SECOND * delta);
		setX(mStartX + MathUtils.sin(getTime()) * WAVE_WIDTH / 2);
	}

	@Override
	public void reset(float posX) {
		super.reset(posX);
		mStartX = posX;
	}

	private float mStartX;
}