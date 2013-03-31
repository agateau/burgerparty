package com.agateau.burgerparty.model;

import java.io.IOException;
import java.util.MissingResourceException;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

public class Level {
	public static class Definition {
		public Array<String> inventoryItems = new Array<String>();
		public int minBurgerSize;
		public int maxBurgerSize;
		public int duration;
		public int customerCount;
		public Array<Objective> objectives = new Array<Objective>();
	}

	public Definition definition = new Definition();
	public int stars = -1;

	public static Level fromXml(FileHandle handle) {
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
		level.definition.minBurgerSize = root.getIntAttribute("minBurgerSize");
		level.definition.maxBurgerSize = root.getIntAttribute("maxBurgerSize");
		level.definition.customerCount = root.getIntAttribute("customerCount");
		level.definition.duration = root.getIntAttribute("duration");

		int maxTrashed = root.getIntAttribute("maxTrashed", -1);
		if (maxTrashed >= 0) {
			level.definition.objectives.add(new MaxTrashedObjective(maxTrashed));
		}
		int maxDuration = root.getIntAttribute("maxDuration", -1);
		if (maxDuration > 0) {
			level.definition.objectives.add(new MaxDurationObjective(maxDuration));
		}

		XmlReader.Element items = root.getChildByName("items");
		assert(items != null);
		for(int idx = 0; idx < items.getChildCount(); ++idx) {
			XmlReader.Element element = items.getChild(idx);
			level.definition.inventoryItems.add(element.getAttribute("name"));
		}

		return level;
	}
}