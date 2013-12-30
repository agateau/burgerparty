package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.SpriteImage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class Cannon extends SpriteImage {
	public Cannon(BurgerVadersMainScreen mainScreen, TextureRegion region) {
		super(region);
		mMainScreen = mainScreen;
		setOriginX(getWidth() / 2);
		setOriginY(getHeight() / 2);
	}

	public void act(float delta) {
		if (!Gdx.input.justTouched()) {
			return;
		}
		float touchX = Gdx.input.getX();
		float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

		float srcX = getX() + getWidth() / 2;
		float srcY = getY() + getHeight() / 2;

		float angle = MathUtils.atan2((touchY - srcY), (touchX - srcX));

		mMainScreen.fire(srcX, srcY, angle);
		setRotation((float)(180 * angle / Math.PI) - 90);
	}

	private BurgerVadersMainScreen mMainScreen;
}
