package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.SpriteImage;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public abstract class EnemyType {
	public void init(Enemy enemy) {
		enemy.init(mDrawable, mMask);
	}

	public abstract void act(Enemy enemy, float delta);
	public abstract void start(Enemy enemy, float initialY);

	protected Drawable mDrawable;
	protected SpriteImage.CollisionMask mMask;
}