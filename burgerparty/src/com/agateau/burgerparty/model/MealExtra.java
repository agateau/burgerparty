package com.agateau.burgerparty.model;

import java.util.HashSet;
import java.util.Set;

public class MealExtra extends MealItemCollection {
	public Set<MealItem> getItems() {
		return mItems;
	}

	public void clear() {
		mItems.clear();
		cleared.emit();
	}

	public void trash() {
		mItems.clear();
		trashed.emit();
	}

	public boolean isEmpty() {
		return mItems.isEmpty();
	}

	public void addItem(MealItem item) {
		assert(item.getType() == MealItem.Type.DRINK || item.getType() == MealItem.Type.SIDE_ORDER);
		mItems.add(item);
		itemAdded.emit(item);
	}

	public boolean isMissing(MealExtra other, MealItem item) {
		return other.mItems.contains(item) && !mItems.contains(item);
	}

	@Override
	public boolean equals(Object other) {
		return mItems.equals(((MealExtra)other).mItems);
	}

	@Override
	public int hashCode() {
		return mItems.hashCode();
	}

	@Override
	public String toString() {
		String txt = new String();
		for(MealItem item: mItems) {
			txt += item.getName() + ", ";
		}
		return txt;
	}

	public void setItems(Set<MealItem> items) {
		mItems = items;
		initialized.emit();
	}

	public CompareResult compareTo(MealExtra reference) {
		if (mItems.equals(reference.mItems)) {
			return CompareResult.SAME;
		}
		if (reference.mItems.containsAll(mItems)) {
			return CompareResult.SUBSET;
		}
		return CompareResult.DIFFERENT;
	}

	private Set<MealItem> mItems = new HashSet<MealItem>();
}
