package com.agateau.burgerparty.model;

import java.util.Collection;
import java.util.LinkedList;

import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.utils.Signal1;

public class Burger extends MealItemCollection<BurgerItem> {
	public Signal1<Integer> arrowIndexChanged = new Signal1<Integer>();
	
	public Collection<BurgerItem> getItems() {
		return mItems;
	}

	public CompareResult compareTo(Burger reference) {
		if (mItems.size() > reference.mItems.size()) {
			// Should not happen
			return CompareResult.DIFFERENT;
		}
		for (int idx = 0, n = mItems.size(); idx < n; ++idx) {
			if (mItems.get(idx) != reference.mItems.get(idx)) {
				return CompareResult.DIFFERENT;
			}
		}
		return mItems.size() == reference.mItems.size() ? CompareResult.SAME : CompareResult.SUBSET;
	}

	public int getArrowIndex() {
		return mArrowIndex;
	}

	public void moveUpArrow() {
		mArrowIndex++;
		arrowIndexChanged.emit(mArrowIndex);
	}

	public void resetArrow() {
		mArrowIndex = 0;
		arrowIndexChanged.emit(mArrowIndex);
	}

	public void hideArrow() {
		mArrowIndex = -1;
		arrowIndexChanged.emit(mArrowIndex);
	}

	@Override
	protected void addItemInternal(BurgerItem item) {
		mItems.add(item);
	}

	private int mArrowIndex = -1;
	private LinkedList<BurgerItem> mItems = new LinkedList<BurgerItem>();
}
