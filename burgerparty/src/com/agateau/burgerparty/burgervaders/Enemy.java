package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.SpriteImage;

public class Enemy extends SpriteImage {
	public void act(float delta) {
		mTime += delta;
	}

	public void start(float posX) {
		mTime = 0;
	}

	public float getTime() {
		return mTime;
	}

	private float mTime;
}
