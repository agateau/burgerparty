package com.agateau.burgerparty.model;

import java.util.HashSet;
import java.util.Set;

import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;

public class MealExtra {
	public Signal1<MealItem> itemAdded = new Signal1<MealItem>();
	public Signal0 cleared = new Signal0();

	public Set<MealItem> getItems() {
		return mItems;
	}

	public void clear() {
		mItems.clear();
		cleared.emit();
	}

	public void addItem(MealItem item) {
		assert(item.getType() == MealItem.Type.DRINK || item.getType() == MealItem.Type.SIDE_ORDER);
		mItems.add(item);
		itemAdded.emit(item);
	}

	public boolean isMissing(MealExtra other, MealItem item) {
		return other.mItems.contains(item) && !mItems.contains(item);
	}

	private Set<MealItem> mItems = new HashSet<MealItem>();
}
