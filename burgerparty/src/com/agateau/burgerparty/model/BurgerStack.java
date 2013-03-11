package com.agateau.burgerparty.model;

import com.agateau.burgerparty.model.BurgerItem;

import com.badlogic.gdx.utils.Array;

public class BurgerStack {
	Array<BurgerItem> mItems;

	public BurgerStack() {
		mItems = new Array<BurgerItem>();
	}

	public void addItem(BurgerItem item) {
		mItems.add(item);
	}
	
	public void clear() {
		mItems.clear();
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
