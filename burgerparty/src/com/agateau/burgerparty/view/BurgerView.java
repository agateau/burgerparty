package com.agateau.burgerparty.view;

import java.util.HashSet;

import com.agateau.burgerparty.model.Burger;
import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;
import com.agateau.burgerparty.utils.UiUtils;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class BurgerView extends Group {
	private HashSet<Object> mHandlers = new HashSet<Object>();
	private Burger mBurger;
	private TextureAtlas mAtlas;
	private float mNextY;

	private static final float ADD_ACTION_HEIGHT = 100;
	
	private static final float TRASH_ACTION_DURATION = 0.5f;

	public BurgerView(Burger burger, TextureAtlas atlas) {
		mBurger = burger;
		mAtlas = atlas;
		float maxWidth = mAtlas.findRegion("burgeritems/bottom").getRegionWidth();
		setWidth(maxWidth);

		mNextY = 0;

		mBurger.burgerItemAdded.connect(mHandlers, new Signal1.Handler<BurgerItem>() {
			public void handle(BurgerItem item) {
				addItem(item);
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

	private void addItem(BurgerItem item) {
		TextureRegion region = mAtlas.findRegion("burgeritems/" + item.getName());
		Image image = new Image(region);
		float regionW = region.getRegionWidth();
		float regionH = region.getRegionHeight();
		float posX = (getWidth() - regionW) / 2;

		image.setBounds(posX, mNextY + item.getOffset() + ADD_ACTION_HEIGHT, regionW, regionH);
		image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);
		addActor(image);

		image.addAction(Actions.alpha(0));
		image.addAction(Actions.parallel(
			Actions.moveBy(0, -ADD_ACTION_HEIGHT, MealView.ADD_ACTION_DURATION, Interpolation.pow2In),
			Actions.fadeIn(MealView.ADD_ACTION_DURATION)
			));

		mNextY += item.getHeight();
		// Subtract ADD_ACTION_HEIGHT because we want the final height, not the height when the item is falling on the stack
		setHeight(image.getTop() - ADD_ACTION_HEIGHT);
		UiUtils.notifyResizeToFitParent(this);
	}

	private void trash() {
		mNextY = 0;
		setHeight(0);
		UiUtils.notifyResizeToFitParent(this);
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
		UiUtils.notifyResizeToFitParent(this);
		clear();
	}
}
