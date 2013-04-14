package com.agateau.burgerparty.model;

import java.io.IOException;
import java.util.MissingResourceException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

public class Level {
	public static class Definition {
		public Array<String> burgerItems = new Array<String>();
		public Array<String> extraItems = new Array<String>();
		public int minBurgerSize;
		public int maxBurgerSize;
		public int duration;
		public int customerCount;
		public Array<Objective> objectives = new Array<Objective>();
	}

	public Definition definition = new Definition();
	public int stars = -1;

	public LevelGroup getGroup() {
		return mLevelGroup;
	}

	public static Level fromXml(LevelGroup levelGroup, FileHandle handle) {
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
		level.mLevelGroup = levelGroup;
		level.definition.minBurgerSize = root.getIntAttribute("minBurgerSize");
		level.definition.maxBurgerSize = root.getIntAttribute("maxBurgerSize");
		level.definition.customerCount = root.getIntAttribute("customerCount");
		level.definition.duration = root.getIntAttribute("duration");

		readObjectives(level, root.getChildByName("objectives"));

		level.definition.burgerItems.add("top");
		level.definition.burgerItems.add("bottom");

		XmlReader.Element items = root.getChildByName("items");
		assert(items != null);
		for(int idx = 0; idx < items.getChildCount(); ++idx) {
			XmlReader.Element element = items.getChild(idx);
			String name = element.getAttribute("name");
			MealItem item = MealItem.get(name);
			if (item.getType() == MealItem.Type.BURGER) {
				level.definition.burgerItems.add(name);
			} else {
				level.definition.extraItems.add(name);
			}
		}

		return level;
	}

	private static void readObjectives(Level level, XmlReader.Element objRoot) {
		assert(objRoot != null);
		for(int idx = 0; idx < objRoot.getChildCount(); ++idx) {
			XmlReader.Element element = objRoot.getChild(idx);
			String type = element.getAttribute("type");
			Objective objective = null;
			if (type.equals("maxTrashed")) {
				int value = element.getIntAttribute("value");
				objective = new MaxTrashedObjective(value);
			} else if (type.equals("maxDuration")) {
				int value = element.getIntAttribute("value");
				objective = new MaxDurationObjective(value);
			} else {
				throw new RuntimeException("Don't know how to read objective from " + element);
			}
			assert(objective != null);
			level.definition.objectives.add(objective);
		}
	}

	private LevelGroup mLevelGroup;
}