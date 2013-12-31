package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.CollisionMask;
import com.agateau.burgerparty.utils.SpriteImage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

class Bullet extends SpriteImage {
	private static final float PIXEL_PER_SECOND = 480;
	public Bullet(TextureRegion region, CollisionMask mask) {
		super(region, mask);
	}

	@Override
	public void act(float delta) {
		if (!isVisible()) {
			return;
		}
		mTime += delta;
		setPosition(
			mSrcX + mTime * PIXEL_PER_SECOND * mCos - getWidth() / 2,
			mSrcY + mTime * PIXEL_PER_SECOND * mSin - getHeight() / 2
			);
		if (getRight() < 0 || getTop() < 0 || getX() > getStage().getWidth() || getY() > getStage().getHeight()) {
			setVisible(false);
		}
	}

	void start(float srcX, float srcY, float angle) {
		mTime = 0;
		mSrcX = srcX;
		mSrcY = srcY;
		mCos = MathUtils.cos(angle);
		mSin = MathUtils.sin(angle);
		setVisible(true);
		setPosition(mSrcX, mSrcY);
	}

	private float mTime;
	private float mSrcX;
	private float mSrcY;
	private float mCos, mSin;
}