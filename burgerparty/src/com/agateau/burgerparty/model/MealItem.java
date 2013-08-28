package com.agateau.burgerparty.model;

import java.io.IOException;

import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.utils.AnimScript;
import com.agateau.burgerparty.utils.SoundAtlas;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.XmlReader;

public class MealItem {
	private static final String DEFAULT_ANIM =
		"parallel\n" +
		"    alpha 0\n" +
		"    moveBy 0 1\n" +
		"end\n" +
		"parallel\n" +
		"    alpha 1 1\n" +
		"    moveBy 0 -1 1 pow2In\n" +
		"    playMealItem @itemName@\n" +
		"end\n";

	public enum Type {
		SIDE_ORDER,
		DRINK,
		BURGER
	}
	public static MealItem get(String name) {
		if (sMap.size == 0) {
			initMap();
		}
		return sMap.get(name);
	}

	public String toString() {
		return getName();
	}

	public Type getType() {
		return mType;
	}

	public String getName() {
		return mName;
	}

	public AnimScript getAnimScript() {
		if (mAnimScript == null) {
			assert(!mAnim.isEmpty());
			mAnimScript = Kernel.getAnimScriptLoader().load(mAnim);
		}
		return mAnimScript;
	}

	public boolean equals(MealItem other) {
		return mName.equals(other.mName);
	}

	public int hashCode() {
		return mName.hashCode();
	}

	public int getColumn() {
		return mColumn;
	}

	public int getRow() {
		return mRow;
	}

	public static Action createPlayMealItemAction(String name) {
		SoundAtlas atlas = Kernel.getSoundAtlas();
		if (atlas.contains("add-item-" + name)) {
			return atlas.createPlayAction("add-item-" + name);
		} else {
			return atlas.createPlayAction("add-item");
		}
	}

	protected MealItem(XmlReader.Element element) {
		mName = element.getAttribute("name");
		mColumn = element.getIntAttribute("column");
		mRow = element.getIntAttribute("row");
		mAnim = element.get("anim", DEFAULT_ANIM)
			.replace("@itemName@", mName);
	}

	protected MealItem(Type type, String name) {
		mType = type;
		mName = name;
	}

	public static MealItem addTestItem(Type type, String name) {
		MealItem item = new MealItem(type, name);
		addTestItem(item);
		return item;
	}

	protected static void addTestItem(MealItem item) {
		sMap.put(item.mName, item);
	}

	private static void initMap() {
		FileHandle handle = Gdx.files.internal("mealitems.xml");
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
				item.mType = Type.BURGER;
			} else if (type.equals("drink")) {
				item = new MealItem(element);
				item.mType = Type.DRINK;
			} else if (type.equals("side-order")) {
				item = new MealItem(element);
				item.mType = Type.SIDE_ORDER;
			}
			assert(item != null);
			sMap.put(item.mName, item);
		}
	}

	private Type mType;
	private String mName;
	private String mAnim;
	private AnimScript mAnimScript;
	private int mColumn;
	private int mRow;

	private static OrderedMap<String, MealItem> sMap = new OrderedMap<String, MealItem>();
}
