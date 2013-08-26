package com.agateau.burgerparty.model;

import java.util.HashSet;
import java.util.Set;

public class MealExtra extends MealItemCollection<MealItem> {
	public Set<MealItem> getItems() {
		return mItems;
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
