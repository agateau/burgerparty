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
	private static final float PLATTER_ANIM_DURATION = 0.2f;

	private static final float BURGER_X = 40f;
	private static final float MEAL_Y = 30f;

	public MealView(Burger burger, MealExtra mealExtra, TextureAtlas atlas, boolean withPlatter) {
		if (withPlatter) {
			mPlatter = new Image(atlas.findRegion("platter"));
			addActor(mPlatter);
			mPlatter.setPosition(-mPlatter.getWidth(), 0);
			mPlatter.setColor(1, 1, 1, 0);
			mPlatter.addAction(Actions.moveBy(mPlatter.getWidth(), 0, PLATTER_ANIM_DURATION, Interpolation.pow2Out));
			mPlatter.addAction(Actions.alpha(1, PLATTER_ANIM_DURATION, Interpolation.pow2Out));
		}
		mMealExtraView = new MealExtraView(mealExtra, atlas);
		addActor(mMealExtraView);
		mBurgerView = new BurgerView(burger, atlas);
		addActor(mBurgerView);

		if (withPlatter) {
			mBurgerView.setPosition(BURGER_X, MEAL_Y);
		}
		updateGeometry();
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

	public void pop(MealItem.Type itemType) {
		if (itemType == MealItem.Type.BURGER) {
			mBurgerView.pop();
		} else {
			mMealExtraView.pop();
		}
	}

	public void updateGeometry() {
		mMealExtraView.setPosition(mBurgerView.getRight(), mBurgerView.getY());
		setSize(
			mPlatter == null ? (mBurgerView.getWidth() + mMealExtraView.getWidth()) : mPlatter.getWidth(),
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

	private Image mPlatter = null;
	private BurgerView mBurgerView;
	private MealExtraView mMealExtraView;
}
