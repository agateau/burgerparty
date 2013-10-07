package com.agateau.burgerparty.view;

import com.agateau.burgerparty.utils.ResizeToFitChildren;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

class BubbleScrollPane extends ScrollPane implements ResizeToFitChildren {
	public BubbleScrollPane(Actor child) {
		super(child);
		//setScrollingDisabled(true, false);
	}

	public void setMaximumHeight(float maximumHeight) {
		mMaximumHeight = maximumHeight;
	}

	@Override
	public void onChildSizeChanged() {
		Actor actor = getWidget();
		float width = actor.getWidth();
		if (width == getWidth()) {
			return;
		}
		setWidth(width);
		setHeight(Math.min(actor.getHeight(), mMaximumHeight));
		UiUtils.notifyResizeToFitParent(this);
	}

	private float mMaximumHeight = 1000;
}