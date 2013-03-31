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

	public int getHeight() {
		return mHeight;
	}

	public int getOffset() {
		return mOffset;
	}

	public static BurgerItem get(String name) {
		MealItem item = MealItem.get(name);
		assert(item.getType() == MealItem.Type.BURGER);
		return (BurgerItem)item;
	}
}
