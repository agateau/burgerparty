package com.agateau.burgerparty.model;

import java.io.IOException;

import com.agateau.burgerparty.model.MealItem.Type;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.XmlReader;

public class MealItemDb {
	public void addTestItem(MealItem item) {
		mMap.put(item.getName(), item);
	}

	public MealItem get(int worldIndex, String name) {
		return mMap.get(name);
	}

	public void initFromXml(String xmlFileName) {
		FileHandle handle = Gdx.files.internal(xmlFileName);
		XmlReader.Element root = null;
		try {
			XmlReader reader = new XmlReader();
			root = reader.parse(handle);
		} catch (IOException e) {
			Gdx.app.error("MealItem.initMap", "Failed to load items definition from " + handle.path() + ". Exception: " + e.toString());
			return;
		}

		for(int idx = 0; idx < root.getChildCount(); ++idx) {
			XmlReader.Element element = root.getChild(idx);
			String type = element.getAttribute("type");
			MealItem item = null;
			if (type.equals("burger")) {
				item = new BurgerItem(element);
			} else if (type.equals("drink")) {
				item = new MealItem(Type.DRINK, element);
			} else if (type.equals("side-order")) {
				item = new MealItem(Type.SIDE_ORDER, element);
			}
			assert(item != null);
			mMap.put(item.getName(), item);
		}
	}

	public static MealItemDb getInstance() {
		if (sInstance == null) {
			sInstance = new MealItemDb();
		}
		return sInstance;
	}

	private OrderedMap<String, MealItem> mMap = new OrderedMap<String, MealItem>();
	private static MealItemDb sInstance = null;
}