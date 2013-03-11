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
}
