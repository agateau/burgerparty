package com.agateau.burgerparty.model;


import com.agateau.burgerparty.utils.AnimScript;
import com.agateau.burgerparty.utils.AnimScriptLoader;
import com.agateau.burgerparty.utils.SoundAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
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
		return MealItemDb.getInstance().get(0, name);
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

	public AnimScript getAnimScript(AnimScriptLoader loader) {
		if (mAnimScript == null) {
			assert(!mAnim.isEmpty());
			mAnimScript = loader.load(mAnim);
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

	public static Action createPlayMealItemAction(SoundAtlas atlas, String name) {
		if (atlas.contains("add-item-" + name)) {
			return atlas.createPlayAction("add-item-" + name);
		} else {
			return atlas.createPlayAction("add-item");
		}
	}

	protected MealItem(Type type, XmlReader.Element element) {
		mType = type;
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
		MealItemDb.getInstance().addTestItem(item);
		return item;
	}

	private Type mType;
	private String mName;
	private String mAnim;
	private AnimScript mAnimScript;
	private int mColumn;
	private int mRow;
}
