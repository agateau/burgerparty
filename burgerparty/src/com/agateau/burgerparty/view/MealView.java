package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.Burger;
import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.model.MealExtra;
import com.agateau.burgerparty.model.MealItem;
import com.agateau.burgerparty.utils.ResizeToFitChildren;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class MealView extends Group implements ResizeToFitChildren {
	public static final float ADD_ACTION_DURATION = 0.2f;
	public static final float TRASH_ACTION_DURATION = 0.5f;

	private static final float PLATTER_X = -40f;
	private static final float PLATTER_Y = -30f;

	public MealView(Burger burger, MealExtra mealExtra, TextureAtlas atlas, boolean withPlatter) {
		if (withPlatter) {
			Image platter = new Image(atlas.findRegion("platter"));
			platter.setPosition(PLATTER_X, PLATTER_Y);
			addActor(platter);
		}
		mMealExtraView = new MealExtraView(mealExtra, atlas);
		addActor(mMealExtraView);
		mBurgerView = new BurgerView(burger, atlas);
		addActor(mBurgerView);
	}

	public BurgerView getBurgerView() {
		return mBurgerView;
	}

	public void addItem(MealItem item) {
		if (item.getType() == MealItem.Type.BURGER) {
			addBurgerItem((BurgerItem)item);
		} else {
			addExtraItem(item);
		}
	}

	private void addBurgerItem(BurgerItem item) {
		mBurgerView.addItem(item);
	}

	private void addExtraItem(MealItem item) {
		mMealExtraView.addItem(item);
	}

	public void updateGeometry() {
		mMealExtraView.setPosition(mBurgerView.getWidth(), 0);
		setSize(
			mBurgerView.getWidth() + mMealExtraView.getWidth(),
			Math.max(mBurgerView.getHeight(), mMealExtraView.getHeight())
			);
		UiUtils.notifyResizeToFitParent(this);
	}

	@Override
	public void onChildSizeChanged() {
		updateGeometry();
	}

	public static void addTrashActions(Actor actor) {
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

	private BurgerView mBurgerView;
	private MealExtraView mMealExtraView;
}
