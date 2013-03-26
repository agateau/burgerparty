package com.agateau.burgerparty.model;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.XmlReader;

public class BurgerItem {
	private String mName;
	private int mHeight;
	private int mOffset;

	private static OrderedMap<String, BurgerItem> sMap = new OrderedMap<String, BurgerItem>();

	private BurgerItem() {
	}

	public String getName() {
		return mName;
	}

	public int getHeight() {
		return mHeight;
	}

	public int getOffset() {
		return mOffset;
	}

	public static BurgerItem get(String name) {
		if (sMap.size == 0) {
			initMap();
		}
		return sMap.get(name);
	}

	private static void initMap() {
		FileHandle handle = Gdx.files.internal("burgeritems.xml");
		XmlReader.Element root = null;
		try {
			XmlReader reader = new XmlReader();
			root = reader.parse(handle);
		} catch (IOException e) {
			Gdx.app.error("BurgerItem.initMap", "Failed to load burger items definition from " + handle.path() + ". Exception: " + e.toString());
			return;
		}

		for(int idx = 0; idx < root.getChildCount(); ++idx) {
			XmlReader.Element element = root.getChild(idx);
			BurgerItem item = new BurgerItem();
			item.mName = element.getAttribute("name");
			item.mOffset = element.getIntAttribute("offset");
			item.mHeight = element.getIntAttribute("height");
			sMap.put(item.mName, item);
		}
	}
}
