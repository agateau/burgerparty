package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.SpriteImage;

public class Enemy extends SpriteImage {
	public Enemy(EnemyType type) {
		setEnemyType(type);
	}

	public void act(float delta) {
		if (!isVisible()) {
			return;
		}
		assert(mEnemyType != null);
		mTime += delta;
		mEnemyType.act(this, delta);
	}

	public void start(float initialY) {
		setVisible(true);
		mTime = 0;
		assert(mEnemyType != null);
		mEnemyType.start(this, initialY);
	}

	public void setEnemyType(EnemyType type) {
		mEnemyType = type;
		mEnemyType.init(this);
	}

	public EnemyType getEnemyType() {
		return mEnemyType;
	}

	public float getTime() {
		return mTime;
	}

	private EnemyType mEnemyType;
	private float mTime;
}
