package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.MaskedDrawableAtlas;
import com.agateau.burgerparty.utils.SpriteImage;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.SnapshotArray;

public abstract class BurgerEnemy extends Enemy {
	private static final float PIXEL_PER_SECOND = 120;

	public BurgerEnemy(MaskedDrawableAtlas atlas, Pool<BurgerItemEnemy> itemPool, BurgerVadersMainScreen mainScreen) {
		mAtlas = atlas;
		mScreen = mainScreen;
		mItemPool = itemPool;

		float y = 0;
		y = addItem(y, "mealitems/0/bottom-inventory");
		y = addItem(y, "mealitems/0/steak-inventory");
		y = addItem(y, "mealitems/0/tomato-inventory");
		y = addItem(y, "mealitems/0/top-inventory");

		updateSize();
	}

	private float addItem(float y, String path) {
		SpriteImage image = new SpriteImage(mAtlas.get(path));
		image.setY(y);
		addActor(image);
		return image.getTop() - 30;
	}

	@Override
	public void doAct(float delta) {
		setY(getY() - PIXEL_PER_SECOND * delta);
	}

	@Override
	public void onHit() {
		SnapshotArray<Actor> array = getChildren();
		float step = getWidth() * 1.4f;
		float minCenter = step * (array.size - 1) / 2f;
		float center = MathUtils.clamp(getX(), minCenter, getStage().getWidth() - minCenter - getWidth());
		for (int idx = 0, n = array.size; idx < n; ++idx) {
			SpriteImage image = (SpriteImage)array.get(idx);
			if (image != null) {
				createEnemy(image, center + step * (idx - (array.size - 1) / 2f));
			}
		}
		mustBeRemoved();
	}

	private void createEnemy(SpriteImage image, float x) {
		BurgerItemEnemy enemy = mItemPool.obtain();
		getStage().addActor(enemy);
		enemy.init(image.getMaskedDrawable());
		enemy.setPosition(getX() + image.getX(), getY() + image.getY());
		enemy.addAction(Actions.moveTo(x, getY() + 40, 0.2f));
		mScreen.addEnemy(enemy);
	}

	private MaskedDrawableAtlas mAtlas;
	private BurgerVadersMainScreen mScreen;
	private Pool<BurgerItemEnemy> mItemPool;
}
