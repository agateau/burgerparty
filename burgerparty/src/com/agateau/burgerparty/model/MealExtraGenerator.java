package com.agateau.burgerparty.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

class MealExtraGenerator {
	private ObjectMap<MealItem.Type, Array<MealItem>> mItemsForType;

	public MealExtraGenerator(Array<MealItem> availableItems) {
		mItemsForType = new ObjectMap<MealItem.Type, Array<MealItem>>();
		for(MealItem item: availableItems) {
			Array<MealItem> lst = mItemsForType.get(item.getType(), null);
			if (lst == null) {
				lst = new Array<MealItem>();
				mItemsForType.put(item.getType(), lst);
			}
			lst.add(item);
		}
	}

	public Set<MealItem> run() {
		// Pick one item per type
		Set<MealItem> items = new HashSet<MealItem>();
		for(Iterator<Array<MealItem>> it = mItemsForType.values(); it.hasNext(); ) {
			Array<MealItem> lst = it.next();
			if (MathUtils.randomBoolean()) {
				int index = MathUtils.random(lst.size - 1);
				items.add(lst.get(index));
			}
		}
		return items;
	}
}