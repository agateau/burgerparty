package com.agateau.burgerparty.model;

import com.agateau.burgerparty.model.MealItem;

import com.badlogic.gdx.utils.Array;

public class Inventory {
	public void addItems(Array<String> itemNames) {
		for (String name: itemNames) {
			addItem(name);
		}
	}

	public void addItem(String name) {
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

	private Array<MealItem> mItems = new Array<MealItem>();
}
