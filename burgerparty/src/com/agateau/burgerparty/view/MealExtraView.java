package com.agateau.burgerparty.view;

import java.util.HashSet;

import com.agateau.burgerparty.model.MealExtra;
import com.agateau.burgerparty.model.MealItem;
import com.agateau.burgerparty.utils.AnimScript;
import com.agateau.burgerparty.utils.AnimScriptLoader;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class MealExtraView extends Group {
	private static final float ADD_ACTION_HEIGHT = 100;

	private static final float ITEM0_X = 0;
	private static final float ITEM0_Y = 0;
	private static final float ITEM1_X = -30;
	private static final float ITEM1_Y = 10;

	private static class ItemImage extends Image {
		public ItemImage(MealItem item, TextureRegion region) {
			super(region);
			mItem = item;
		}
		public MealItem getItem() {
			return mItem;
		}
		private MealItem mItem;
	}

	public MealExtraView(MealExtra mealExtra, TextureAtlas atlas, AnimScriptLoader loader) {
		mMealExtra = mealExtra;
		mAtlas = atlas;
		mAnimScriptLoader = loader;

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
				trash();
			}
		});
	}

	public void setOverlapping(boolean overlapping) {
		mOverlapping = overlapping;
	}

	public Array<MealItem> getItems() {
		Array<MealItem> items = new Array<MealItem>(mItemActors.size);
		for(ItemImage actor: mItemActors) {
			items.add(actor.getItem());
		}
		return items;
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
		AnimScript anim = item.getAnimScript(mAnimScriptLoader);
		Action animAction = anim.createAction(ADD_ACTION_HEIGHT, ADD_ACTION_HEIGHT, MealView.ADD_ACTION_DURATION);
		Action addItemAction = Actions.run(new AddItemRunnable(item));
		image.addAction(Actions.sequence(animAction, addItemAction));
		updateGeometry();
	}

	public void pop() {
		assert(mItemActors.size > 0);
		mMealExtra.pop();
		Image image = mItemActors.removeIndex(mItemActors.size - 1);
		image.remove();
	}

	public void init() {
		mItemActors.clear();
		clear();
		for(MealItem item: mMealExtra.getItems()) {
			addItemInternal(item);
		}
		updateGeometry();
	}

	private void trash() {
		for(Image image: mItemActors) {
			MealView.addTrashActions(image);
		}
		mItemActors.clear();
	}

	private void clearItems() {
		mItemActors.clear();
		clear();
		updateGeometry();
	}

	private void updateGeometry() {
		if (mItemActors.size == 0) {
			setSize(0, 0);
			UiUtils.notifyResizeToFitParent(this);
			return;
		}
		float width = 0;
		float height = 0;
		for(Image image: mItemActors) {
			width = Math.max(image.getRight(), width);
			height = Math.max(image.getHeight(), height);
		}
		setSize(width, height);
		UiUtils.notifyResizeToFitParent(this);
	}

	private Image addItemInternal(MealItem item) {
		TextureRegion region;
		region = mAtlas.findRegion("mealitems/" + item.getPath());
		assert(region != null);
		ItemImage image = new ItemImage(item, region);
		addActor(image);
		if (mOverlapping) {
			if (mItemActors.size == 0) {
				image.setPosition(ITEM0_X, ITEM0_Y);
			} else {
				image.setPosition(ITEM1_X, ITEM1_Y);
				image.setZIndex(0);
			}
		} else {
			float posX = 0;
			if (mItemActors.size > 0) {
				posX = mItemActors.get(mItemActors.size - 1).getRight() + MealView.MEAL_ITEM_PADDING;
			}
			image.setPosition(posX, 0);
		}
		mItemActors.add(image);
		return image;
	}

	private MealExtra mMealExtra;
	private TextureAtlas mAtlas;
	private AnimScriptLoader mAnimScriptLoader;
	private HashSet<Object> mHandlers = new HashSet<Object>();
	private Array<ItemImage> mItemActors = new Array<ItemImage>();
	private boolean mOverlapping = true;
}
