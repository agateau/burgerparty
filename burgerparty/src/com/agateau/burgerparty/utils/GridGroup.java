package com.agateau.burgerparty.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;

public class GridGroup extends WidgetGroup {
	private int mColCount = 1;
	private float mSpacing = 0;
	private float mCellWidth = 10;
	private float mCellHeight = 10;
	private Array<Actor> mChildren = new Array<Actor>();

	public void setColumnCount(int colCount) {
		if (mColCount == colCount) {
			return;
		}
		assert(colCount > 0);
		mColCount = colCount;
		updateSize();
		invalidate();
	}

	public float getSpacing() {
		return mSpacing;
	}

	public void setSpacing(float spacing) {
		if (mSpacing == spacing) {
			return;
		}
		mSpacing = spacing;
		updateSize();
		invalidate();
	}

	public void setCellSize(float width, float height) {
		mCellWidth = width;
		mCellHeight = height;
		updateSize();
		invalidate();
	}

	public void addActor(Actor actor) {
		super.addActor(actor);
		mChildren.add(actor);
		updateSize();
		invalidate();
	}

	public void layout() {
		float posX = 0;
		float posY = getHeight() - mCellHeight;
		int col = 0;
		for (Actor actor: mChildren) {
			actor.setBounds(posX, posY, mCellWidth, mCellHeight);
			col++;
			if (col < mColCount) {
				posX += mCellWidth + mSpacing;
			} else {
				col = 0;
				posX = 0;
				posY -= mCellHeight + mSpacing;
			}
		}
	}

	private void updateSize() {
		int rowCount = mChildren.size / mColCount;
		if (mChildren.size % mColCount > 0) {
			rowCount++;
		}
		setSize(
			(mCellWidth + mSpacing) * mColCount - mSpacing,
			(mCellHeight + mSpacing) * rowCount - mSpacing);
	}

	@Override
	public float getPrefWidth() {
		return getWidth();
	}

	@Override
	public float getPrefHeight() {
		return getHeight();
	}
}