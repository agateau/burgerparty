package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.SpriteImage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class Cannon extends SpriteImage {
	public Cannon(TextureRegion region) {
		super(region);
		setOriginX(getWidth() / 2);
		setOriginY(getHeight() / 2);
	}

	public void act(float delta) {
		if (!Gdx.input.isTouched()) {
			return;
		}
		float touchX = Gdx.input.getX();
		float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

		float srcX = getX() + getWidth() / 2;
		float srcY = getY() + getHeight() / 2;

		float angle = MathUtils.atan2((touchY - srcY), (touchX - srcX));
		float dx = MathUtils.cos(angle);
		float dy = MathUtils.sin(angle);

		//mMiniGame.fire(srcX, srcY, dx, dy);
		setRotation((float)(180 * angle / Math.PI) - 90);
	}
}
