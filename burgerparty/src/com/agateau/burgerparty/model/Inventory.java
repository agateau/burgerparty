package com.agateau.burgerparty.model;

import com.agateau.burgerparty.model.MealItem;

import com.badlogic.gdx.utils.Array;

public class Inventory {
	private Array<MealItem> mItems;

	public Inventory(Array<String> itemNames) {
		mItems = new Array<MealItem>();
		loadStoreItem("top");
		loadStoreItem("bottom");
		for (String name: itemNames) {
			loadStoreItem(name);
		}
	}

	private void loadStoreItem(String name) {
		mItems.add(MealItem.get(name));
	}
	
	public Array<MealItem> getItems() {
		return mItems;
	}
	
	public MealItem get(int index) {
		if (index >=0 && index < mItems.size) {
			return mItems.get(index);
		} else {
			return null;
		}
	}
}
