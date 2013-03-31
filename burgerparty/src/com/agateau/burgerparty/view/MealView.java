package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.Burger;
import com.agateau.burgerparty.model.MealExtra;
import com.agateau.burgerparty.utils.ResizeToFitChildren;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

public class MealView extends Group implements ResizeToFitChildren {
	public static final float ADD_ACTION_DURATION = 0.2f;

	public MealView(Burger burger, MealExtra mealExtra, TextureAtlas atlas) {
		mBurgerView = new BurgerView(burger, atlas);
		addActor(mBurgerView);
		mMealExtraView = new MealExtraView(mealExtra, atlas);
		addActor(mMealExtraView);
	}

	public void updateGeometry() {
		mMealExtraView.setPosition(mBurgerView.getWidth(), 0);
		setSize(
			mBurgerView.getWidth() + mMealExtraView.getWidth(),
			Math.max(mBurgerView.getHeight(), mMealExtraView.getHeight())
			);
		Actor parent = getParent();
		if (parent instanceof ResizeToFitChildren) {
			((ResizeToFitChildren)parent).onChildSizeChanged();
		}
	}

	@Override
	public void onChildSizeChanged() {
		updateGeometry();
	}

	private BurgerView mBurgerView;
	private MealExtraView mMealExtraView;
}
