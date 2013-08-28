package com.agateau.burgerparty.model;

import java.util.LinkedList;

/**
 * MealItems in this class are stored in a LinkedList, but are kept in order,
 * so that a call to getItems() returns all side orders, then all drinks
 */
public class MealExtra extends MealItemCollection<MealItem> {

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
		LinkedList<MealItem> us = new LinkedList<MealItem>(mItems);
		LinkedList<MealItem> ref = new LinkedList<MealItem>(reference.mItems);
		while (!us.isEmpty()) {
			MealItem item = us.removeFirst();
			boolean found = ref.removeFirstOccurrence(item);
			if (!found) {
				return CompareResult.DIFFERENT;
			}
		}
		return ref.isEmpty() ? CompareResult.SAME : CompareResult.SUBSET;
	}

	@Override
	protected void addItemInternal(MealItem item) {
		String key = keyForItem(item);
		int idx, n = mItems.size();
		for (idx = 0; idx < n; ++idx) {
			if (keyForItem(mItems.get(idx)).compareTo(key) > 0) {
				break;
			}
		}
		mItems.add(idx, item);
	}

	@Override
	public LinkedList<MealItem> getItems() {
		return mItems;
	}

	private LinkedList<MealItem> mItems = new LinkedList<MealItem>();

	private static String keyForItem(MealItem item) {
		String key = new String();
		switch (item.getType()) {
		case BURGER:
			// Should not happen
			assert(false);
			break;
		case SIDE_ORDER:
			key = "0";
			break;
		case DRINK:
			key = "1";
			break;
		}
		key = key.concat(item.getName());
		return key;
	}
}
