package com.agateau.burgerparty.model;

import java.util.LinkedList;

public class MealExtra extends MealItemCollection<MealItem> {
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

	public void pop() {
		assert(!mItems.isEmpty());
		mItems.removeLast();
	}

	@Override
	protected void addItemInternal(MealItem item) {
		mItems.add(item);
	}

	@Override
	public LinkedList<MealItem> getItems() {
		return mItems;
	}

	private LinkedList<MealItem> mItems = new LinkedList<MealItem>();
}
