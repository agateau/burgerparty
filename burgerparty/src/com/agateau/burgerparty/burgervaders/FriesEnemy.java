package com.agateau.burgerparty.burgervaders;

public class FriesEnemy extends Enemy {
	private static final float PIXEL_PER_SECOND = 90;

	@Override
	public void doAct(float delta) {
		setY(getY() - PIXEL_PER_SECOND * delta);
	}
}
