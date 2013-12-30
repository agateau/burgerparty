package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.SpriteImage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class SaladEnemyType extends EnemyType {
	private static final float PIXEL_PER_SECOND = 60;

	public SaladEnemyType(TextureAtlas atlas) {
		TextureRegion region = atlas.findRegion("mealitems/0/salad-inventory");
		mDrawable = new TextureRegionDrawable(region);
		mMask = new SpriteImage.CollisionMask(region);
	}

	@Override
	public void act(Enemy enemy, float delta) {
		float y = enemy.getY() - PIXEL_PER_SECOND * delta;
		enemy.setY(y);
		float maxX = Gdx.graphics.getWidth() - enemy.getWidth();
		enemy.setX(maxX / 2 + MathUtils.sin(enemy.getTime()) * maxX / 2);
	}

	@Override
	public void start(Enemy enemy, float initialY) {
		float maxX = Gdx.graphics.getWidth() - enemy.getWidth();
		enemy.setPosition(maxX / 2, Gdx.graphics.getHeight() + initialY);
	}
}