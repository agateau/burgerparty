package com.agateau.burgerparty.burgervaders;

import com.badlogic.gdx.Gdx;

public class FriesEnemy extends Enemy {
	private static final float PIXEL_PER_SECOND = 90;

	@Override
	public void act(float delta) {
		float y = getY() - PIXEL_PER_SECOND * delta;
		setY(y);
	}

	@Override
	public void start(float initialX) {
		setPosition(initialX, Gdx.graphics.getHeight());			
	}

}
