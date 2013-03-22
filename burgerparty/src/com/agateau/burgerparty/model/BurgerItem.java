package com.agateau.burgerparty.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.OrderedMap;

public class BurgerItem {
	private String mName;
	private int mHeight;
	private int mOffset;

	private static OrderedMap<String, BurgerItem> sMap = new OrderedMap<String, BurgerItem>();

	private static class Reader extends JsonReader {
		@Override
		protected void startObject(String name) {
			mItem = new BurgerItem();
		}
		@Override
		protected void string(String name, String value) {
			mItem.mName = value;
			sMap.put(mItem.mName, mItem);
		}
		@Override
		protected void number(String name, float value) {
			if (name.equals("height")) {
				mItem.mHeight = (int)value;
			} else {
				mItem.mOffset = (int)value;
			}
		}

		// Must be overridden to completely disable JsonReader behavior
		@Override
		protected void startArray(String name) {
		}
		// Must be overridden to completely disable JsonReader behavior
		@Override
		protected void pop() {
		}

		private BurgerItem mItem;
	};

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
		FileHandle handle = Gdx.files.internal("burgeritems.json");
		Reader reader = new Reader();
		reader.parse(handle);
	}
}
