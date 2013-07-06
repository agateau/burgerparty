package com.agateau.burgerparty.view;

import java.util.HashSet;

import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.model.Burger;
import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.utils.AnimScript;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.SoundAtlas;
import com.agateau.burgerparty.utils.UiUtils;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class BurgerView extends Group {
	private HashSet<Object> mHandlers = new HashSet<Object>();
	private Burger mBurger;
	private TextureAtlas mAtlas;
	private float mPadding = 0;
	private float mNextY;

	private static final float ADD_ACTION_HEIGHT = 100;
	
	private static final float TRASH_ACTION_DURATION = 0.5f;

	public BurgerView(Burger burger, TextureAtlas atlas) {
		mBurger = burger;
		mAtlas = atlas;
		float maxWidth = mAtlas.findRegion("mealitems/bottom").getRegionWidth();
		setWidth(maxWidth);

		mNextY = 0;

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
	}

	public void setPadding(float value) {
		mPadding = value;
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

	private void trash() {
		mNextY = 0;
		setHeight(0);
		UiUtils.notifyResizeToFitParent(this);
		Kernel.getSoundAtlas().findSound("error.wav").play();
		for (Actor actor: getChildren()) {
			float xOffset = (float)(Math.random() * 200 - 100);
			float rotation = xOffset;
			actor.addAction(
				Actions.sequence(
					Actions.parallel(
						Actions.moveBy(xOffset, 0, TRASH_ACTION_DURATION),
						Actions.moveBy(0, -200, TRASH_ACTION_DURATION, Interpolation.pow2In),
						Actions.scaleTo(0.5f, 0.5f, TRASH_ACTION_DURATION),
						Actions.rotateBy(rotation, TRASH_ACTION_DURATION),
						Actions.fadeOut(TRASH_ACTION_DURATION, Interpolation.pow5In)
					),
					Actions.removeActor()
				)
			);
		}
	}

	private void onCleared() {
		mNextY = 0;
		setHeight(0);
		clear();
		UiUtils.notifyResizeToFitParent(this);
	}

	private void init() {
		mNextY = 0;
		setHeight(0);
		clear();
		for(BurgerItem item: mBurger.getItems()) {
			addItemInternal(item);
		}
		UiUtils.notifyResizeToFitParent(this);
	}

	private Image addItemInternal(BurgerItem item) {
		TextureRegion region;
		region = mAtlas.findRegion("mealitems/" + item.getName());
		assert(region != null);
		Image image = new Image(region);
		float regionW = region.getRegionWidth();
		float regionH = region.getRegionHeight();
		float posX = (getWidth() - regionW) / 2;

		image.setBounds(posX, mNextY + item.getOffset(), regionW, regionH);
		image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);
		addActor(image);

		mNextY += item.getHeight() + mPadding;
		setHeight(mNextY + regionH / 2);
		return image;
	}
}
