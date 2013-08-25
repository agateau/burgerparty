package com.agateau.burgerparty.model;

import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.utils.Signal1;

import com.badlogic.gdx.utils.Array;

public class Burger extends MealItemCollection {
	public Signal1<Integer> arrowIndexChanged = new Signal1<Integer>();

	public void addItem(BurgerItem item) {
		mItems.add(item);
		itemAdded.emit(item);
	}

	public void clear() {
		mItems.clear();
		cleared.emit();
	}

	public void trash() {
		mItems.clear();
		trashed.emit();
	}
	
	public Array<BurgerItem> getItems() {
		return mItems;
	}

	public CompareResult compareTo(Burger reference) {
		if (mItems.size > reference.mItems.size) {
			// Should not happen
			return CompareResult.DIFFERENT;
		}
		for (int idx = 0; idx < mItems.size; ++idx) {
			if (mItems.get(idx) != reference.mItems.get(idx)) {
				return CompareResult.DIFFERENT;
			}
		}
		return mItems.size == reference.mItems.size ? CompareResult.SAME : CompareResult.SUBSET;
	}

	public void setItems(Array<BurgerItem> items) {
		mItems = items;
		initialized.emit();
	}

	public String toString() {
		String out = "[";
		for(BurgerItem item: mItems) {
			out += item.getName() + ", ";
		}
		return out + "]";
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

	private int mArrowIndex = -1;
	private Array<BurgerItem> mItems = new Array<BurgerItem>();
}
