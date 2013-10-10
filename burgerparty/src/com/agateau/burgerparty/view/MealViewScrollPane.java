package com.agateau.burgerparty.view;

import java.util.HashSet;

import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.model.Burger;
import com.agateau.burgerparty.utils.ResizeToFitChildren;
import com.agateau.burgerparty.utils.Signal1;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

class MealViewScrollPane extends ScrollPane implements ResizeToFitChildren {
	private static final float EDGE_SIZE = 20f;

	public MealViewScrollPane(MealView child) {
		super(child);
		mMealView = child;
		// Disable smooth scrolling for now so that the bottom of the meal is displayed
		// first, without scrolling. Smooth scrolling is enabled when the player adds
		// his first burger item.
		setSmoothScrolling(false);

		Burger burger = mMealView.getBurgerView().getBurger();
		burger.arrowIndexChanged.connect(mHandlers, new Signal1.Handler<Integer>() {
			@Override
			public void handle(Integer index) {
				if (index > 0) {
					setSmoothScrolling(true);
				}
				updateScrollPosition();
			}
		});
	}

	public void setMaximumHeight(float maximumHeight) {
		mMaximumHeight = maximumHeight;
		updateSize();
	}

	@Override
	public void onChildSizeChanged() {
		updateSize();
	}

	@Override
	public void layout() {
		super.layout();
		updateEdgeImages();
	}

	private void updateSize() {
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
		// Compute scrollY so that at least half of the bubble is visible above item
		float scrollY = item.getTop() + getHeight() / 2;
		// scrollY goes from top to bottom, so invert it
		setScrollY(mMealView.getHeight() - scrollY);
	}

	@Override
	protected void visualScrollY(float pixelsY) {
		super.visualScrollY(pixelsY);
		updateEdgeImages();
	}

	private void updateEdgeImages() {
		if (mTopEdge == null) {
			// This is a bit hackish, but we can't add the edge images to the pane itself: it is forbidden to call ScrollPane.addActor()
			TextureRegion region = Kernel.getTextureAtlas().findRegion("ui/vertical-gradient");
			mTopEdge = new Image(region);
			TextureRegion region2 = new TextureRegion(region);
			region2.flip(false, true);
			mBottomEdge = new Image(region2);
			getParent().addActor(mTopEdge);
			getParent().addActor(mBottomEdge);
		}
		mTopEdge.setVisible(false);
		mBottomEdge.setVisible(false);
		if (getWidget().getHeight() <= getHeight()) {
			return;
		}
		float percent = getScrollPercentY();
		if (percent < 1) {
			mBottomEdge.setVisible(true);
			mBottomEdge.setBounds(getX(), getY(), getWidth() * getScaleX(), EDGE_SIZE);
		}
		if (percent > 0) {
			mTopEdge.setVisible(true);
			mTopEdge.setBounds(getX(), getY() + getHeight() * getScaleY() - EDGE_SIZE, getWidth() * getScaleX(), EDGE_SIZE);
		}
	}

	private HashSet<Object> mHandlers = new HashSet<Object>();

	private float mMaximumHeight = 1000;
	private MealView mMealView;
	private Image mTopEdge;
	private Image mBottomEdge;
}