package com.agateau.burgerparty.model;

import java.util.HashMap;

public class BurgerItem {
	private String mName;

	private static HashMap<String, Integer> sHeightMap = new HashMap<String, Integer>();
	private static HashMap<String, Integer> sOffsetMap = new HashMap<String, Integer>();

	public BurgerItem(String name) {
		mName = name;
		if (sHeightMap.isEmpty()) {
			initMaps();
		}
	}

	public String getName() {
		return mName;
	}

	public int getHeight() {
		return sHeightMap.get(mName);
	}

	public int getOffset() {
		return sOffsetMap.get(mName);
	}

	private void initMaps() {
		sHeightMap.put("bottom", 24);
		sHeightMap.put("cheese", 8);
		sHeightMap.put("onion", 8);
		sHeightMap.put("steak", 22);
		sHeightMap.put("tomato", 12);
		sHeightMap.put("salad", 8);
		sHeightMap.put("top", 32);
		sHeightMap.put("cucumber", 8);

		sOffsetMap.put("bottom", 0);
		sOffsetMap.put("cheese", -19);
		sOffsetMap.put("onion", -4);
		sOffsetMap.put("steak", 0);
		sOffsetMap.put("tomato", -4);
		sOffsetMap.put("salad", -4);
		sOffsetMap.put("top", 0);
		sOffsetMap.put("cucumber", -4);
	}
}
