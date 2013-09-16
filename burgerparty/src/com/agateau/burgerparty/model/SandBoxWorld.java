package com.agateau.burgerparty.model;


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
	private Inventory mBurgerInventory = new Inventory();
	private Inventory mMealExtraInventory = new Inventory();
	private Burger mBurger = new Burger();
	private MealExtra mMealExtra = new MealExtra();
}