package com.agateau.burgerparty.model;

import com.agateau.burgerparty.model.BurgerItem.SubType;

public class SandBoxWorld {
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

	public boolean canAddItem(MealItem item) {
		if (item.getType() == MealItem.Type.BURGER) {
			return canAddBurgerItem((BurgerItem)item);
		} else {
			return canAddMealExtraItem(item);
		}
	}

	private boolean canAddBurgerItem(BurgerItem item) {
		if (mBurger.isEmpty()) {
			return item.getSubType() == SubType.BOTTOM || item.getSubType() == SubType.TOP_BOTTOM;
		}
		BurgerItem topItem = mBurger.getTopItem();
		return topItem.getSubType() != SubType.TOP;
	}

	private boolean canAddMealExtraItem(MealItem item) {
		return mMealExtra.getItems().size() < 2;
	}

	private Inventory mBurgerInventory = new Inventory();
	private Inventory mMealExtraInventory = new Inventory();
	private Burger mBurger = new Burger();
	private MealExtra mMealExtra = new MealExtra();
}