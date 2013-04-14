package com.agateau.burgerparty.model;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

public class Progress {
	public static class Item {
		public int levelWorld;
		public int level;
		public int stars;
	}

	public static Array<Item> load(FileHandle handle) {
		Array<Item> lst = new Array<Item>();
		XmlReader reader = new XmlReader();
		XmlReader.Element root = null;
		try {
			root = reader.parse(handle);
		} catch (IOException e) {
			Gdx.app.log("Progress.load", "Failed to load progress from " + handle.path() + ". Exception: " + e.toString());
			return lst;
		}
		if (root == null) {
			Gdx.app.log("Progress.load", "Failed to load progress from " + handle.path() + ". No root XML element found.");
			return lst;
		}
		for(int idx = 0; idx < root.getChildCount(); ++idx) {
			XmlReader.Element element = root.getChild(idx);
			Item item = new Item();
			item.levelWorld = element.getIntAttribute("world", 1);
			item.level = element.getIntAttribute("level");
			item.stars = element.getIntAttribute("stars");
			lst.add(item);
		}
		return lst;
	}

	public static void save(FileHandle handle, Array<Item> lst) {
		XmlWriter writer = new XmlWriter(handle.writer(false));
		try {
			XmlWriter root = writer.element("progress");
			for (Item item: lst) {
				root.element("item")
					.attribute("world", item.levelWorld)
					.attribute("level", item.level)
					.attribute("stars", item.stars)
				.pop();
			}
			writer.close();
		} catch (IOException e) {
			Gdx.app.log("Progress.save", "Failed to save progress to " + handle.path() + ". Exception: " + e.toString());
		}
	}

}
