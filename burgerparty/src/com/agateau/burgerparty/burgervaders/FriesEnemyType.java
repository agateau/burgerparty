package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.SpriteImage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class FriesEnemyType extends EnemyType {
	private static final float PIXEL_PER_SECOND = 90;

	public FriesEnemyType(TextureAtlas atlas) {
		TextureRegion region = atlas.findRegion("mealitems/0/big-fries-inventory");
		mDrawable = new TextureRegionDrawable(region);
		mMask = new SpriteImage.CollisionMask(region);
	}

	@Override
	public void act(Enemy enemy, float delta) {
		float y = enemy.getY() - PIXEL_PER_SECOND * delta;
		enemy.setY(y);
	}

	@Override
	public void start(Enemy enemy, float initialY) {
		float width = enemy.getWidth();
		float x = MathUtils.random(Gdx.graphics.getWidth() - width);
		enemy.setPosition(x, Gdx.graphics.getHeight() + initialY);			
	}
}