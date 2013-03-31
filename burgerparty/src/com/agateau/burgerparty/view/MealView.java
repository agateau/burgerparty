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
	}

	public void layout() {
		super.layout();
		setSize(mBurgerView.getWidth(), mBurgerView.getHeight());
	}

	private BurgerView mBurgerView;
}
