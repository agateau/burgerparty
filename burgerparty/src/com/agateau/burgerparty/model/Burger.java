package com.agateau.burgerparty.model;

import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;

import com.badlogic.gdx.utils.Array;

public class Burger {
	private Array<BurgerItem> mItems;

	public Signal1<BurgerItem> burgerItemAdded;
	public Signal0 initialized = new Signal0();
	public Signal0 cleared;
	public Signal0 trashed;
	public Signal1<Integer> arrowIndexChanged = new Signal1<Integer>();

	public enum CompareResult {
		SUBSET,
		SAME,
		DIFFERENT,
	}

	public Burger() {
		burgerItemAdded = new Signal1<BurgerItem>();
		cleared = new Signal0();
		trashed = new Signal0();
		mItems = new Array<BurgerItem>();
	}

	public void addItem(BurgerItem item) {
		mItems.add(item);
		burgerItemAdded.emit(item);
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

	public int getSize() {
		return mItems.size;
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
}
