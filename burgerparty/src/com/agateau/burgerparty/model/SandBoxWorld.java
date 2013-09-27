package com.agateau.burgerparty.model;

import com.agateau.burgerparty.model.BurgerItem.SubType;
import com.badlogic.gdx.utils.Array;

public class SandBoxWorld {
	private static int MAX_MEAL_EXTRA = 2;
	private static int MAX_BURGER_ITEMS = 200;

	/**
	 * For testing purposes
	 */
	public void setMaxBurgerItems(int value) {
		mMaxBurgerItems = value;
	}

	public Inventory getBurgerInventory() {
		return mBurgerInventory;
	}
	public Inventory getMealExtraInventory() {
		return mMealExtraInventory;
	}
	public Burger getBurger() {
		return mBurger;
	}
	public MealExtra getMealExtra() {
		return mMealExtra;
	}

	public boolean canAddBurgerItem(Array<BurgerItem> items, BurgerItem item) {
		BurgerItem.SubType subType = item.getSubType();
		if (items.size == 0) {
			return subType == SubType.BOTTOM || subType == SubType.TOP_BOTTOM;
		}
		BurgerItem topItem = items.get(items.size - 1);
		if (topItem.getSubType() == SubType.TOP) {
			return false;
		}
		if (items.size == mMaxBurgerItems) {
			return false;
		}
		if (items.size == mMaxBurgerItems - 1) {
			if (subType == SubType.TOP || subType == SubType.TOP_BOTTOM) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	public boolean canAddMealExtraItem(Array<MealItem> items, MealItem item) {
		return items.size < MAX_MEAL_EXTRA;
	}

	private Inventory mBurgerInventory = new Inventory();
	private Inventory mMealExtraInventory = new Inventory();
	private Burger mBurger = new Burger();
	private MealExtra mMealExtra = new MealExtra();
	private int mMaxBurgerItems = MAX_BURGER_ITEMS;
}