package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.SpriteImage;
import com.badlogic.gdx.Gdx;

public abstract class Enemy extends SpriteImage {
	static private final float DEATH_DURATION = 0.5f;

	public void act(float delta) {
		mTime += delta;
		if (mDying) {
			float scale = 1.0f - mTime / DEATH_DURATION;
			if (scale < 0) {
				removalRequested.emit();
				return;
			}
			setScale(scale);
			setRotation(mTime / DEATH_DURATION * 540);
		} else {
			doAct(delta);
		}
	}

	public abstract void doAct(float delta);

	public void reset(float posX) {
		mTime = 0;
		mDying = false;
		setOriginX(getWidth() / 2);
		setOriginY(getHeight() / 2);
		setScale(1);
		setRotation(0);

		setPosition(posX, Gdx.graphics.getHeight());
	}

	public void onHit() {
		mDying = true;
		mTime = 0;
	}

	public boolean isDying() {
		return mDying;
	}

	public float getTime() {
		return mTime;
	}

	private float mTime;
	private boolean mDying = false;
}
