package com.agateau.burgerparty.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.OrderedMap;

public class BurgerItem {
	private String mName;

	static private class Data {
		public String name;
		public int height;
		public int offset;
	}
	private static OrderedMap<String, Data> sDataMap = new OrderedMap<String, Data>();

	static private class Reader extends JsonReader {
		@Override
		protected void startObject(String name) {
			mData = new Data();
			mLst.add(mData);
		}
		@Override
		protected void string(String name, String value) {
			mData.name = value;
		}
		@Override
		protected void number(String name, float value) {
			if (name.equals("height")) {
				mData.height = (int)value;
			} else {
				mData.offset = (int)value;
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

		public Array<Data> mLst = new Array<Data>();
		private Data mData;
	};

	private BurgerItem(String name) {
		mName = name;
	}

	public String getName() {
		return mName;
	}

	public int getHeight() {
		return sDataMap.get(mName).height;
	}

	public int getOffset() {
		return sDataMap.get(mName).offset;
	}

	public static BurgerItem get(String name) {
		if (sDataMap.size == 0) {
			initMap();
		}
		return new BurgerItem(name);
	}

	private static void initMap() {
		FileHandle handle = Gdx.files.internal("burgeritems.json");
		Reader reader = new Reader();
		reader.parse(handle);
		
		for(Data data: reader.mLst) {
			sDataMap.put(data.name, data);
		}
	}
}
