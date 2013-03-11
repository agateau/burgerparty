package com.agateau.burgerparty.model;

import com.agateau.burgerparty.model.Inventory;

public class World {
	private Inventory mInventory;
	private BurgerStack mBurgerStack;
	private BurgerStack mTargetBurgerStack;

	public World() {
		mInventory = new Inventory();
		mBurgerStack = new BurgerStack();
		mTargetBurgerStack = new BurgerStack();
		generateTarget();
	}
	
	public Inventory getInventory() {
		return mInventory;
	}

	public BurgerStack getBurgerStack() {
		return mBurgerStack;
	}
	
	public BurgerStack getTargetBurgerStack() {
		return mTargetBurgerStack;
	}

	public void generateTarget() {
		final String[] names = {"steak", "salad", "cheese", "tomato"};
		int count = 2 + (int)(4 * Math.random());

		mTargetBurgerStack.clear();

		mTargetBurgerStack.addItem(new BurgerItem("bottom"));

		for (; count >= 0; count--) {
			String name = names[(int)(Math.random() * names.length)];
			mTargetBurgerStack.addItem(new BurgerItem(name));
		}

		mTargetBurgerStack.addItem(new BurgerItem("top"));
	}

	public void checkStackStatus() {
		if (mBurgerStack.sameAs(mTargetBurgerStack)) {
			mBurgerStack.clear();
			generateTarget();
		}
	}
}
