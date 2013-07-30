package com.agateau.burgerparty.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.SnapshotArray;

public class HorizontalGroup extends WidgetGroup {
	private boolean mSizeInvalid = true;
	private float mPrefWidth, mPrefHeight;
	private int mAlignment = Align.top;

	public void invalidate() {
		super.invalidate();
		mSizeInvalid = true;
	}

	private void computeSize() {
		mSizeInvalid = false;
		mPrefWidth = 0;
		mPrefHeight = 0;
		SnapshotArray<Actor> children = getChildren();
		for (int i = 0, n = children.size; i < n; i++) {
			Actor child = children.get(i);
			if (child instanceof Layout) {
				Layout layout = (Layout)child;
				mPrefWidth += layout.getPrefWidth();
				mPrefHeight = Math.max(mPrefHeight, layout.getPrefHeight());
			} else {
				mPrefWidth += child.getWidth();
				mPrefHeight = Math.max(mPrefHeight, child.getHeight());
			}
		}
	}

	public void layout() {
		float groupHeight = getHeight();
		float x = 0;
		SnapshotArray<Actor> children = getChildren();
		for (int i = 0, n = children.size; i < n; i++) {
			Actor child = children.get(i);
			float width, height;
			if (child instanceof Layout) {
				Layout layout = (Layout)child;
				width = layout.getPrefWidth();
				height = layout.getPrefHeight();
			} else {
				width = child.getWidth();
				height = child.getHeight();
			}
			float y;
			if ((mAlignment & Align.bottom) != 0) {
				y = 0;
			} else if ((mAlignment & Align.top) != 0) {
				y = groupHeight - height;
			} else {
				y = (groupHeight - height) / 2;
			}
			child.setBounds(x, y, width, height);
			x += width;
		}
	}

	public float getPrefWidth() {
		if (mSizeInvalid) {
			computeSize();
		}
		return mPrefWidth;
	}

	public float getPrefHeight() {
		if (mSizeInvalid) {
			computeSize();
		}
		return mPrefHeight;
	}
}