package com.agateau.burgerparty.view;

import java.util.HashSet;

import com.agateau.burgerparty.model.Burger;
import com.agateau.burgerparty.utils.ResizeToFitChildren;
import com.agateau.burgerparty.utils.Signal1;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

class MealViewScrollPane extends ScrollPane implements ResizeToFitChildren {
	public MealViewScrollPane(MealView child) {
		super(child);
		mMealView = child;
		Burger burger = mMealView.getBurgerView().getBurger();
		burger.arrowIndexChanged.connect(mHandlers, new Signal1.Handler<Integer>() {
			@Override
			public void handle(Integer index) {
				updateScrollPosition();
			}
		});

	}

	public void setMaximumHeight(float maximumHeight) {
		mMaximumHeight = maximumHeight;
	}

	@Override
	public void onChildSizeChanged() {
		Actor actor = getWidget();
		float width = actor.getWidth();
		float height = Math.min(actor.getHeight(), mMaximumHeight);
		if (width != getWidth() || height != getHeight()) {
			setSize(width, height);
			UiUtils.notifyResizeToFitParent(this);
		}
		layout();
		updateScrollPosition();
	}

	private void updateScrollPosition() {
		int index = mMealView.getBurgerView().getBurger().getArrowIndex();
		Actor item = mMealView.getBurgerView().getItemAt(index);
		if (item == null) {
			setScrollPercentY(1);
			return;
		}
		setScrollY(mMealView.getHeight() - item.getTop());
	}

	private HashSet<Object> mHandlers = new HashSet<Object>();

	private float mMaximumHeight = 1000;
	private MealView mMealView;
}