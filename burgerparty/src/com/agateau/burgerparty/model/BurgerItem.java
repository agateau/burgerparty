package com.agateau.burgerparty.model;

import com.badlogic.gdx.utils.XmlReader;

public class BurgerItem extends MealItem {
	public static enum SubType {
		MIDDLE,
		TOP,
		BOTTOM,
		TOP_BOTTOM,
	}

	protected BurgerItem(XmlReader.Element element) {
		super(Type.BURGER, element);
		mOffset = element.getIntAttribute("offset");
		mHeight = element.getIntAttribute("height");
		String subType = element.getAttribute("subType", "middle");
		if (subType.equals("middle")) {
			mSubType = SubType.MIDDLE;
		} else if (subType.equals("top")) {
			mSubType = SubType.TOP;
			mBottomName = element.getAttribute("bottom");
		} else if (subType.equals("bottom")) {
			mSubType = SubType.BOTTOM;
		} else if (subType.equals("top-bottom")) {
			mSubType = SubType.TOP_BOTTOM;
		} else {
			throw new RuntimeException("Invalid BurgerItem subType: " + subType);
		}
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

	public SubType getSubType() {
		return mSubType;
	}

	public String getBottomName() {
		return mBottomName;
	}

	public static BurgerItem addTestItem(String name) {
		return addTestItem(name, SubType.MIDDLE);
	}

	public static BurgerItem addTestItem(String name, SubType subType) {
		BurgerItem item = new BurgerItem(name);
		item.mHeight = 18;
		item.mOffset = 6;
		item.mSubType = subType;
		MealItemDb.getInstance().addTestItem(item);
		return item;
	}

	public static BurgerItem get(String name) {
		MealItem item = MealItem.get(name);
		assert(item.getType() == MealItem.Type.BURGER);
		return (BurgerItem)item;
	}

	private int mHeight;
	private int mOffset;
	private SubType mSubType;
	private String mBottomName;
}
