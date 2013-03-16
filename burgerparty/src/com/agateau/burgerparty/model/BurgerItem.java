package com.agateau.burgerparty.model;

import java.util.HashMap;

public class BurgerItem {
	private String mName;

	private static HashMap<String, Integer> sHeightMap = new HashMap<String, Integer>();

	public BurgerItem(String name) {
		mName = name;
		if (sHeightMap.isEmpty()) {
			initHeightMap();
		}
	}

	public String getName() {
		return mName;
	}

	public int getHeight() {
		return sHeightMap.get(mName);
	}

	private void initHeightMap() {
		sHeightMap.put("bottom", 20);
		sHeightMap.put("cheese", 8);
		sHeightMap.put("onion", 8);
		sHeightMap.put("steak", 22);
		sHeightMap.put("tomato", 12);
		sHeightMap.put("salad", 8);
		sHeightMap.put("top", 32);
	}
}
