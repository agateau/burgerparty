package com.agateau.burgerparty.model;

import com.agateau.burgerparty.model.Inventory;

public class World {
	private Inventory mInventory;
	private BurgerStack mBurgerStack;

	public World() {
		mInventory = new Inventory();
		mBurgerStack = new BurgerStack();
	}
	
	public Inventory getInventory() {
		return mInventory;
	}

	public BurgerStack getBurgerStack() {
		return mBurgerStack;
	}
}
