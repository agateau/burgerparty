package com.agateau.burgerparty.model;

import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;

import com.badlogic.gdx.utils.Array;

public class BurgerStack {
	private Array<BurgerItem> mItems;

	public Signal1<BurgerItem> burgerItemAdded;
	public Signal0 cleared;

	public BurgerStack() {
		burgerItemAdded = new Signal1<BurgerItem>();
		cleared = new Signal0();
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
	
	public Array<BurgerItem> getItems() {
		return mItems;
	}
	
	public boolean sameAs(BurgerStack other) {
		if (mItems.size != other.mItems.size) {
			return false;
		}
		for (int idx = 0; idx < mItems.size; ++idx) {
			if (mItems.get(idx).getName() != other.mItems.get(idx).getName()) {
				return false;
			}
		}
		return true;
	}
}
