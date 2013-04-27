package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.Burger;
import com.agateau.burgerparty.model.MealExtra;
import com.agateau.burgerparty.utils.ResizeToFitChildren;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;

public class MealView extends Group implements ResizeToFitChildren {
	public static final float ADD_ACTION_DURATION = 0.2f;

	public MealView(Burger burger, MealExtra mealExtra, TextureAtlas atlas, SoundAtlas soundAtlas) {
		mBurgerView = new BurgerView(burger, atlas, soundAtlas);
		addActor(mBurgerView);
		mMealExtraView = new MealExtraView(mealExtra, atlas);
		addActor(mMealExtraView);
	}

	public BurgerView getBurgerView() {
		return mBurgerView;
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
