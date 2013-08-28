package com.agateau.burgerparty.model;

import java.util.LinkedList;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

class BurgerGenerator {
	public BurgerGenerator(Array<String> names, int count) {
		mItemNames = new Array<String>(names);
		mCount = count;
	}

	public LinkedList<BurgerItem> run(String bottom, String top) {
		LinkedList<BurgerItem> items = new LinkedList<BurgerItem>();
		items.add(BurgerItem.get(bottom));

		// Generate content, make sure items cannot appear two times consecutively
		String lastName = new String();
		for (; mCount > 0; mCount--) {
			int index = MathUtils.random(mItemNames.size - 1);
			String name = mItemNames.removeIndex(index);
			if (!lastName.isEmpty()) {
				mItemNames.add(lastName);
			}
			lastName = name;
			items.add(BurgerItem.get(name));
		}
		items.add(BurgerItem.get(top));
		return items;
	}

	private Array<String> mItemNames;
	private int mCount;
}