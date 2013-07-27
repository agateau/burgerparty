package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.Burger;
import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.model.MealExtra;
import com.agateau.burgerparty.model.MealItem;
import com.agateau.burgerparty.utils.ResizeToFitChildren;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;

public class MealView extends Group implements ResizeToFitChildren {
	public static final float ADD_ACTION_DURATION = 0.2f;
	public static final float TRASH_ACTION_DURATION = 0.5f;

	public MealView(Burger burger, MealExtra mealExtra, TextureAtlas atlas) {
		mBurgerView = new BurgerView(burger, atlas);
		addActor(mBurgerView);
		mMealExtraView = new MealExtraView(mealExtra, atlas);
		addActor(mMealExtraView);
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

	private BurgerView mBurgerView;
	private MealExtraView mMealExtraView;
}
