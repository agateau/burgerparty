package com.agateau.burgerparty.model;

import com.agateau.burgerparty.model.BurgerItem;

import com.badlogic.gdx.utils.Array;

public class Inventory {
	private Array<BurgerItem> mItems;

	public Inventory(Array<String> itemNames) {
		mItems = new Array<BurgerItem>();
		loadStoreItem("top");
		loadStoreItem("bottom");
		for (String name: itemNames) {
			loadStoreItem(name);
		}
	}

	private void loadStoreItem(String name) {
		mItems.add(new BurgerItem(name));
	}
	
	public Array<BurgerItem> getItems() {
		return mItems;
	}
	
	public BurgerItem get(int index) {
		if (index >=0 && index < mItems.size) {
			return mItems.get(index);
		} else {
			return null;
		}
	}
}
