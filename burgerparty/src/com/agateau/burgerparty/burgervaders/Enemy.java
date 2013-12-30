package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.SpriteImage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class Enemy extends SpriteImage {
	private static final float PIXEL_PER_SECOND = 90;
	public Enemy(TextureRegion region, CollisionMask mask) {
		super(region, mask);
	}

	public void act(float delta) {
		if (!isVisible()) {
			return;
		}
		float y = getY() - PIXEL_PER_SECOND * delta;
		setY(y);
	}

	public void start(float initialY) {
		setVisible(true);
		float width = getWidth();
		float x = MathUtils.random(Gdx.graphics.getWidth() - width);
		setPosition(x, Gdx.graphics.getHeight() + initialY);
	}
}
