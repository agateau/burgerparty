package com.agateau.burgerparty.view;

import java.util.HashSet;

import com.agateau.burgerparty.model.MealExtra;
import com.agateau.burgerparty.model.MealItem;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class MealExtraView extends Group {
	public MealExtraView(MealExtra mealExtra, TextureAtlas atlas) {
		mMealExtra = mealExtra;
		mAtlas = atlas;

		mMealExtra.initialized.connect(mHandlers, new Signal0.Handler() {
			public void handle() {
				init();
			}
		});
		mMealExtra.cleared.connect(mHandlers, new Signal0.Handler() {
			public void handle() {
				clearItems();
			}
		});
		mMealExtra.trashed.connect(mHandlers, new Signal0.Handler() {
			public void handle() {
				// FIXME: Add proper trash anim
				clearItems();
			}
		});
	}

	private class AddItemRunnable implements Runnable {
		public AddItemRunnable(MealItem item) {
			mItem = item;
		}
		@Override
		public void run() {
			mMealExtra.addItem(mItem);
		}
		private MealItem mItem;
	}

	public void addItem(MealItem item) {
		Image image = addItemInternal(item);
		image.addAction(Actions.alpha(0));
		image.addAction(Actions.moveBy(0, ADD_ACTION_HEIGHT));
		Action animAction = Actions.parallel(
			Actions.moveBy(0, -ADD_ACTION_HEIGHT, MealView.ADD_ACTION_DURATION, Interpolation.pow2In),
			Actions.fadeIn(MealView.ADD_ACTION_DURATION)
			);
		Action addItemAction = Actions.run(new AddItemRunnable(item));
		image.addAction(Actions.sequence(animAction, addItemAction));

		updateGeometry();
	}

	public void init() {
		mImages.clear();
		clear();
		float posX = 0;
		for(MealItem item: mMealExtra.getItems()) {
			Image image = addItemInternal(item);
			image.setX(posX);
			posX += image.getWidth();
		}
		updateGeometry();
	}

	private void clearItems() {
		mImages.clear();
		clear();
		updateGeometry();
	}

	private void updateGeometry() {
		if (mImages.size == 0) {
			setSize(0, 0);
			UiUtils.notifyResizeToFitParent(this);
			return;
		}
		float width = 0;
		float height = 0;
		for(Image image: mImages) {
			width += image.getWidth();
			height = Math.max(image.getHeight(), height);
		}
		setSize(width, height);
		UiUtils.notifyResizeToFitParent(this);
	}

	private Image addItemInternal(MealItem item) {
		TextureRegion region;
		region = mAtlas.findRegion("mealitems/" + item.getName());
		assert(region != null);
		Image image = new Image(region);
		mImages.add(image);
		image.setPosition(MathUtils.ceil(getWidth()), 0);
		addActor(image);
		return image;
	}

	private MealExtra mMealExtra;
	private TextureAtlas mAtlas;
	private HashSet<Object> mHandlers = new HashSet<Object>();
	private Array<Image> mImages = new Array<Image>();

	private static final float ADD_ACTION_HEIGHT = 100;
}
