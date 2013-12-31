package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.SpriteImage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

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

		Vector2 v = new Vector2(Gdx.input.getX(), Gdx.input.getY());
		v = getStage().screenToStageCoordinates(v);
		float srcX = getX() + getWidth() / 2;
		float srcY = getY() + getHeight() / 2;

		float angle = MathUtils.atan2((v.y - srcY), (v.x - srcX));

		mMainScreen.fire(srcX, srcY, angle);
		setRotation((float)(180 * angle / Math.PI) - 90);
	}

	private BurgerVadersMainScreen mMainScreen;
}
