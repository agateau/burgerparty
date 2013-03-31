package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.Burger;
import com.agateau.burgerparty.model.MealExtra;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class MealView extends WidgetGroup {
	public static final float ADD_ACTION_DURATION = 0.2f;

	public MealView(Burger burger, MealExtra mealExtra, TextureAtlas atlas) {
		mBurgerView = new BurgerView(burger, atlas);
		addActor(mBurgerView);
		mMealExtraView = new MealExtraView(mealExtra, atlas);
		addActor(mMealExtraView);
	}

	public void layout() {
		super.layout();
		mMealExtraView.setPosition(mBurgerView.getWidth(), 0);
		setSize(mMealExtraView.getRight(), Math.max(mBurgerView.getHeight(), mMealExtraView.getHeight()));
	}

	private BurgerView mBurgerView;
	private MealExtraView mMealExtraView;
}
