package com.agateau.burgerparty.model;

import java.io.IOException;
import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.Set;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

public class Level {
	public static final int SCORE_LOCKED = -2;
	public static final int SCORE_NEW = -1;
	public static final int SCORE_PLAYED = 0;
	private static class CustomerDefinition {
		public String type;
		public int burgerSize;
		public Customer create() {
			return new Customer(type, burgerSize);
		}
	}
	public static class Definition {
		public int duration;
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

		public Array<Customer> createCustomers() {
			Array<Customer> lst = new Array<Customer>();
			for (CustomerDefinition def: mCustomerDefinitions) {
				lst.add(def.create());
			}
			return lst;
		}

		public int getTotalItemCount() {
			int size = 0;
			for (CustomerDefinition def: mCustomerDefinitions) {
				size += def.burgerSize;
			}
			return size;
		}

		private Array<CustomerDefinition> mCustomerDefinitions = new Array<CustomerDefinition>();
		private Array<BurgerItem> mBurgerItems = new Array<BurgerItem>();
		private Array<MealItem> mExtraItems = new Array<MealItem>();
		private MealItem mNewItem = null;
	}

	public Level(LevelWorld world, String fileName) {
		mLevelWorld = world;
		mFileName = fileName;
	}

	public Definition definition = new Definition();
	public int score = SCORE_LOCKED;

	public int getStars() {
		return getStarsFor(score);
	}

	public boolean hasBrandNewItem() {
		return score < SCORE_PLAYED && definition.mNewItem != null;
	}

	public int getStarsFor(int value) {
		if (value >= definition.score3) {
			return 3;
		} else if (value >= definition.score2) {
			return 2;
		} else if (value > 0) {
			return 1;
		} else {
			return 0;
		}
	}

	public LevelWorld getLevelWorld() {
		return mLevelWorld;
	}

	public static Level fromXml(LevelWorld levelWorld, int levelIndex, FileHandle handle) {
		int worldIndex = levelWorld.getIndex();
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
		Level level = new Level(levelWorld, handle.path());
		int burgerSize = root.getIntAttribute("burgerSize");
		level.definition.score2 = root.getIntAttribute("score2", 15000);
		level.definition.score3 = root.getIntAttribute("score3", 30000);

		Array<MealItem> lst = MealItemDb.getInstance().getItemsForLevel(worldIndex, levelIndex);
		for (MealItem item: lst) {
			if (item.getType() == MealItem.Type.BURGER) {
				level.definition.mBurgerItems.add((BurgerItem)item);
			} else {
				level.definition.mExtraItems.add(item);
			}
		}

		XmlReader.Element elements = root.getChildByName("customers");
		assert(elements != null);
		for(int idx = 0; idx < elements.getChildCount(); ++idx) {
			XmlReader.Element element = elements.getChild(idx);
			CustomerDefinition def = new CustomerDefinition();
			def.type = element.getAttribute("type");
			def.burgerSize = element.getIntAttribute("burgerSize", burgerSize);
			level.definition.mCustomerDefinitions.add(def);
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

	public void initNewItemField(Set<MealItem> knownItems) {
		initNewItemFieldInternal(knownItems, definition.mBurgerItems);
		initNewItemFieldInternal(knownItems, definition.mExtraItems);
	}

	private void initNewItemFieldInternal(Set<MealItem> knownItems, Array<? extends MealItem> list) {
		for(MealItem item: list) {
			if (knownItems.contains(item)) {
				continue;
			}
			if (definition.mNewItem == null) {
				definition.mNewItem = item;
				knownItems.add(item);
			} else {
				throw new RuntimeException("Error in level defined in " + mFileName + ". Found new item '" + item + "', but there is already a new item: '" + definition.mNewItem + "'");
			}
		}
	}

	private LevelWorld mLevelWorld;
	private String mFileName;
}