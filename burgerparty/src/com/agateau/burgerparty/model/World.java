package com.agateau.burgerparty.model;

import com.agateau.burgerparty.model.Inventory;
import com.badlogic.gdx.Gdx;

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
		mTargetBurgerStack.clear();
		mTargetBurgerStack.addItem(new BurgerItem("bottom"));
		mTargetBurgerStack.addItem(new BurgerItem("steak"));
		mTargetBurgerStack.addItem(new BurgerItem("salad"));
		mTargetBurgerStack.addItem(new BurgerItem("cheese"));
		mTargetBurgerStack.addItem(new BurgerItem("top"));
	}
	
	public void checkStackStatus() {
		if (mBurgerStack.sameAs(mTargetBurgerStack)) {
			Gdx.app.log("World", "Won");
		}
	}
}
