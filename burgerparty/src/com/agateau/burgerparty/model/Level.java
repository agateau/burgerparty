package com.agateau.burgerparty.model;

import java.io.IOException;
import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.Set;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

public class Level {
	public static final int LOCKED_SCORE = -1;
	public static class Definition {
		public int minBurgerSize;
		public int maxBurgerSize;
		public int duration;
		public Array<String> customers = new Array<String>();
		public int score2;
		public int score3;

		public Array<BurgerItem> getBurgerItems() {
			return mBurgerItems;
		}

		public Array<MealItem> getExtraItems() {
			return mExtraItems;
		}

		public MealItem getNewItem() {
			return mNewItem;
		}

		private Array<BurgerItem> mBurgerItems = new Array<BurgerItem>();
		private Array<MealItem> mExtraItems = new Array<MealItem>();
		private MealItem mNewItem = null;
	}

	public Definition definition = new Definition();
	public int score = LOCKED_SCORE;

	public int getStars() {
		return getStarsFor(score);
	}

	public boolean hasBrandNewItem() {
		return score <= 0 && definition.mNewItem != null;
	}

	public int getStarsFor(int value) {
		if (value >= definition.score3) {
			return 3;
		} else if (value >= definition.score2) {
			return 2;
		} else if (value > 0) {
			return 1;
		} else if (value == 0) {
			return 0;
		} else {
			return -1;
		}
	}

	public LevelWorld getLevelWorld() {
		return mLevelWorld;
	}

	public static Level fromXml(LevelWorld levelWorld, FileHandle handle) {
		XmlReader reader = new XmlReader();
		XmlReader.Element root = null;
		try {
			root = reader.parse(handle);
		} catch (IOException e) {
			throw new MissingResourceException("Failed to load level from " + handle.path() + ". Exception: " + e.toString() + ".", "Level", handle.path());
		}
		if (root == null) {
			throw new MissingResourceException("Failed to load level from " + handle.path() + ". No root element.", "Level", handle.path());
		}
		Level level = new Level();
		level.mFileName = handle.path();
		level.mLevelWorld = levelWorld;
		level.definition.minBurgerSize = root.getIntAttribute("minBurgerSize");
		level.definition.maxBurgerSize = root.getIntAttribute("maxBurgerSize");
		level.definition.duration = root.getIntAttribute("duration");
		level.definition.score2 = root.getIntAttribute("score2", 15000);
		level.definition.score3 = root.getIntAttribute("score3", 30000);

		XmlReader.Element elements = root.getChildByName("items");
		assert(elements != null);
		for(int idx = 0; idx < elements.getChildCount(); ++idx) {
			XmlReader.Element element = elements.getChild(idx);
			String name = element.getAttribute("name");
			MealItem item = MealItem.get(name);
			assert(item != null);
			if (item.getType() == MealItem.Type.BURGER) {
				level.definition.mBurgerItems.add((BurgerItem)item);
			} else {
				level.definition.mExtraItems.add(item);
			}
		}

		elements = root.getChildByName("customers");
		assert(elements != null);
		for(int idx = 0; idx < elements.getChildCount(); ++idx) {
			XmlReader.Element element = elements.getChild(idx);
			String name = element.getAttribute("type");
			level.definition.customers.add(name);
		}

		return level;
	}

	public Set<MealItem> getKnownItems() {
		Set<MealItem> set = new HashSet<MealItem>();
		for (BurgerItem item: definition.mBurgerItems) {
			set.add(item);
		}
		for (MealItem item: definition.mExtraItems) {
			set.add(item);
		}
		return set;
	}

	public void checkNewItems(Set<MealItem> knownItems) {
		checkNewItemsInternal(knownItems, definition.mBurgerItems);
		checkNewItemsInternal(knownItems, definition.mExtraItems);
	}

	private void checkNewItemsInternal(Set<MealItem> knownItems, Array<? extends MealItem> list) {
		for(MealItem item: list) {
			String name = item.getName();
			if (knownItems.contains(item)) {
				continue;
			}
			if (definition.mNewItem == null) {
				definition.mNewItem = item;
				knownItems.add(item);
			} else {
				throw new RuntimeException("Error in level defined in " + mFileName + ". Found new item '" + name + "', but there is already a new item: '" + definition.mNewItem + "'");
			}
		}
	}

	private LevelWorld mLevelWorld;
	private String mFileName;
}