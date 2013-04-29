package com.agateau.burgerparty.model;

import com.badlogic.gdx.utils.XmlReader;

public class BurgerItem extends MealItem {
	private int mHeight;
	private int mOffset;

	protected BurgerItem(XmlReader.Element element) {
		super(element);
		mOffset = element.getIntAttribute("offset");
		mHeight = element.getIntAttribute("height");
	}

	protected BurgerItem(String name) {
		super(Type.BURGER, name);
	}

	public int getHeight() {
		return mHeight;
	}

	public int getOffset() {
		return mOffset;
	}

	public static void addTestItem(String name) {
		BurgerItem item = new BurgerItem(name);
		item.mHeight = 18;
		item.mOffset = 6;
		addTestItem(item);
	}

	public static BurgerItem get(String name) {
		MealItem item = MealItem.get(name);
		assert(item.getType() == MealItem.Type.BURGER);
		return (BurgerItem)item;
	}
}
