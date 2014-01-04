package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.MaskedDrawableAtlas;
import com.badlogic.gdx.math.MathUtils;

public abstract class SaladEnemy extends Enemy {
	private static final float PIXEL_PER_SECOND = 60;
	private static final float WAVE_WIDTH = 200;

	public SaladEnemy(MaskedDrawableAtlas atlas) {
		init(atlas.get("mealitems/0/salad-inventory"));
	}

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