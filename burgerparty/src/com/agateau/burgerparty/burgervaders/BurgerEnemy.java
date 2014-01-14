package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.MaskedDrawableAtlas;
import com.agateau.burgerparty.utils.SpriteImage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

public abstract class BurgerEnemy extends Enemy implements Poolable {
	private static final float PIXEL_PER_SECOND = 90;
	private static final float ITEM_OVERLAP = 35;
	private static final float COLLAPSE_DURATION = 0.2f;

	public BurgerEnemy(MaskedDrawableAtlas atlas) {
		Gdx.app.log("Burger", "new");
		mBottomItem = new SpriteImage(atlas.get("mealitems/0/bottom-inventory"));
		mTopItem = new SpriteImage(atlas.get("mealitems/0/top-inventory"));

		addToMiddleItems(atlas, "mealitems/0/tomato-inventory");
		addToMiddleItems(atlas, "mealitems/0/steak-inventory");
		addToMiddleItems(atlas, "mealitems/0/salad-inventory");
		addToMiddleItems(atlas, "mealitems/0/cheese-inventory");
	}

	private void addToMiddleItems(MaskedDrawableAtlas atlas, String path) {
		SpriteImage image = new SpriteImage(atlas.get(path));
		image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);
		mMiddleItems.add(image);
	}

	@Override
	public void init(float posX) {
		super.init(posX);
		Gdx.app.log("Burger", "init x=" + getX() +" y=" + getY());
		mCurrentStack.clear();
		addItem(mBottomItem);
		int size = MathUtils.random(3, mMiddleItems.size - 1);
		for (int idx = 0; idx < size; ++idx) {
			addItem(mMiddleItems.get(idx));
		}
		addItem(mTopItem);

		updateItemPositions(false);
		updateSize();
	}

	@Override
	public void reset() {
		super.reset();
		Gdx.app.log("Burger", "reset");
		for (SpriteImage image: mMiddleItems) {
			image.remove();
			image.clearActions();
			image.setScale(1);
			image.setColor(Color.WHITE);
		}
	}

	private void updateItemPositions(boolean animated) {
		int y = 0;
		for (SpriteImage image: mCurrentStack) {
			if (animated) {
				image.addAction(Actions.moveTo(image.getX(), y, COLLAPSE_DURATION));
			} else {
				image.setPosition(0, y);
			}
			y += image.getHeight() - ITEM_OVERLAP;
		}
	}

	private void addItem(SpriteImage image) {
		addActor(image);
		mCurrentStack.add(image);
	}

	@Override
	public void doAct(float delta) {
		setY(getY() - PIXEL_PER_SECOND * delta);
	}

	@Override
	public void onHit() {
		if (mCurrentStack.size > 3) {
			SpriteImage image = mCurrentStack.removeIndex(1);
			// move item out of the group so that it is not affected by the group death animation
			getParent().addActor(image);
			image.clearActions();
			image.setPosition(getX() + image.getX(), getY() + image.getY());
			image.setZIndex(getZIndex() - 1);

			image.addAction(Actions.sequence(
					Actions.parallel(
							Actions.scaleBy(2f, 0.1f, COLLAPSE_DURATION),
							Actions.fadeOut(COLLAPSE_DURATION)
					),
					Actions.removeActor()
					));
			updateItemPositions(true);
		} else {
			super.onHit();
		}
	}

	private SpriteImage mBottomItem;
	private SpriteImage mTopItem;
	private Array<SpriteImage> mMiddleItems = new Array<SpriteImage>();

	private Array<SpriteImage> mCurrentStack = new Array<SpriteImage>();
}
