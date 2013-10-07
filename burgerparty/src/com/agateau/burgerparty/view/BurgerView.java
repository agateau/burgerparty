package com.agateau.burgerparty.view;

import java.util.HashSet;

import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.model.Burger;
import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.utils.AnimScript;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;
import com.agateau.burgerparty.utils.UiUtils;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class BurgerView extends Group {
	private static final float ADD_ACTION_HEIGHT = 100;
	private static final float HPADDING = 15;

	private static class ItemImage extends Image {
		public ItemImage(BurgerItem item, TextureRegion region) {
			super(region);
			mItem = item;
		}
		public BurgerItem getItem() {
			return mItem;
		}
		private BurgerItem mItem;
	}

	public BurgerView(Burger burger, TextureAtlas atlas) {
		mBurger = burger;
		mAtlas = atlas;
		float maxWidth = mAtlas.findRegion("mealitems/bottom").getRegionWidth();
		setWidth(maxWidth + HPADDING * 2);

		mBurger.initialized.connect(mHandlers, new Signal0.Handler() {
			public void handle() {
				init();
			}
		});
		mBurger.cleared.connect(mHandlers, new Signal0.Handler() {
			public void handle() {
				onCleared();
			}
		});
		mBurger.trashed.connect(mHandlers, new Signal0.Handler() {
			public void handle() {
				trash();
			}
		});
		mBurger.arrowIndexChanged.connect(mHandlers, new Signal1.Handler<Integer>() {
			@Override
			public void handle(Integer index) {
				setArrowIndex(index);
			}
		});
	}

	public Burger getBurger() {
		return mBurger;
	}

	public void setPadding(float value) {
		mPadding = value;
	}

	public Actor getItemAtArrow() {
		return mArrowIndex > -1 ? mItemActors.get(mArrowIndex) : null;
	}

	public Array<BurgerItem> getItems() {
		Array<BurgerItem> items = new Array<BurgerItem>(mItemActors.size);
		for(ItemImage actor: mItemActors) {
			items.add(actor.getItem());
		}
		return items;
	}

	private class AddItemRunnable implements Runnable {
		public AddItemRunnable(BurgerItem item) {
			mItem = item;
		}
		@Override
		public void run() {
			mBurger.addItem(mItem);
		}
		private BurgerItem mItem;
	}

	public void addItem(BurgerItem item) {
		Image image = addItemInternal(item);
		AnimScript anim = item.getAnimScript();
		Action animAction = anim.createAction(ADD_ACTION_HEIGHT, ADD_ACTION_HEIGHT, MealView.ADD_ACTION_DURATION);
		Action addItemAction = Actions.run(new AddItemRunnable(item));
		image.addAction(Actions.sequence(animAction, addItemAction));

		UiUtils.notifyResizeToFitParent(this);
	}

	public void pop() {
		assert(mItemActors.size > 0);
		mBurger.pop();
		ItemImage image = mItemActors.removeIndex(mItemActors.size - 1);
		image.remove();
		if (mItemActors.size > 0) {
			setHeight(mItemActors.get(mItemActors.size - 1).getTop());
		} else {
			setHeight(0);
		}
		UiUtils.notifyResizeToFitParent(this);
	}

	public void setArrowIndex(int index) {
		initArrowActor();
		mArrowIndex = index;
		if (index == -1) {
			mArrowActor.setVisible(false);
			return;
		}
		mArrowActor.setVisible(true);
		Image item = mItemActors.get(index);
		float deltaY = item.getY() - mArrowActor.getY();
		mArrowActor.addAction(Actions.moveBy(0, deltaY, 0.3f, Interpolation.pow3Out));
	}

	private void trash() {
		Kernel.getSoundAtlas().findSound("trash").play();
		for (Actor actor: mItemActors) {
			MealView.addTrashActions(actor);
		}
		setHeight(0);
		mItemActors.clear();
		UiUtils.notifyResizeToFitParent(this);
	}

	private void onCleared() {
		setHeight(0);
		removeItemActors();
		mItemActors.clear();
		UiUtils.notifyResizeToFitParent(this);
	}

	private void init() {
		setHeight(0);
		removeItemActors();
		mItemActors.clear();
		for(BurgerItem item: mBurger.getItems()) {
			addItemInternal(item);
		}
		UiUtils.notifyResizeToFitParent(this);
	}

	private Image addItemInternal(BurgerItem item) {
		TextureRegion region;
		region = mAtlas.findRegion("mealitems/" + item.getName());
		assert(region != null);
		ItemImage image = new ItemImage(item, region);
		float regionW = region.getRegionWidth();
		float regionH = region.getRegionHeight();
		float posX = (getWidth() - regionW) / 2;

		float nextY = getNextY();
		image.setBounds(posX, nextY + item.getOffset(), regionW, regionH);
		image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);
		addActor(image);

		setHeight(image.getTop());

		mItemActors.add(image);
		return image;
	}

	private void initArrowActor() {
		if (mArrowActor != null) {
			return;
		}
		TextureRegion region;
		region = mAtlas.findRegion("ui/icon-next-item");
		mArrowActor = new Image(region);
		mArrowActor.setX(HPADDING - mArrowActor.getWidth() - 2);
		mArrowActor.addAction(Actions.forever(
			Actions.sequence(
				Actions.moveBy(-HPADDING * 0.6f, 0, .3f, Interpolation.pow2Out),
				Actions.moveBy(HPADDING * 0.6f, 0, .3f, Interpolation.pow2In)
			)
		));
		addActor(mArrowActor);
	}

	private float getNextY() {
		float value = 0;
		for (ItemImage image: mItemActors) {
			value += image.getItem().getHeight() + mPadding;
		}
		return value;
	}

	private void removeItemActors() {
		// This method must be used instead of Group.clear() because mArrowActor
		// should not be removed
		for (ItemImage image: mItemActors) {
			image.remove();
		}
	}

	private HashSet<Object> mHandlers = new HashSet<Object>();
	private Burger mBurger;
	private TextureAtlas mAtlas;
	private float mPadding = 0;
	private Array<ItemImage> mItemActors = new Array<ItemImage>();
	private int mArrowIndex = -1;
	private Image mArrowActor = null;
}
