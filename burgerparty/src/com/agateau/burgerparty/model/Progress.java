package com.agateau.burgerparty.model;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

public class Progress {
	public static void load(FileHandle handle, Array<LevelWorld> worlds) {
		XmlReader reader = new XmlReader();
		XmlReader.Element root = null;
		try {
			root = reader.parse(handle);
		} catch (IOException e) {
			Gdx.app.log("Progress.load", "Failed to load progress from " + handle.path() + ". Exception: " + e.toString());
			return;
		}
		if (root == null) {
			Gdx.app.log("Progress.load", "Failed to load progress from " + handle.path() + ". No root XML element found.");
			return;
		}
		load(root, worlds);
	}

	public static void load(XmlReader.Element root, Array<LevelWorld> worlds) {
		for(int idx = 0; idx < root.getChildCount(); ++idx) {
			XmlReader.Element element = root.getChild(idx);
			int worldIndex = element.getIntAttribute("world", 1) - 1;
			int levelIndex = element.getIntAttribute("level") - 1;
			int score = element.getIntAttribute("score", -1);
			Level level = worlds.get(worldIndex).getLevel(levelIndex);
			level.score = score;
		}
	}

	public static void save(FileHandle handle, Array<LevelWorld> worlds) {
		XmlWriter writer = new XmlWriter(handle.writer(false));
		save(writer, worlds);
	}

	public static void save(XmlWriter writer, Array<LevelWorld> worlds) {
		try {
			XmlWriter root = writer.element("progress");
			int worldIndex = 0;
			for (LevelWorld world: worlds) {
				for (int levelIndex = 0; levelIndex < world.getLevelCount(); ++levelIndex) {
					Level level = world.getLevel(levelIndex);
					if (level.score > -1) {
						root.element("item")
							.attribute("world", worldIndex + 1)
							.attribute("level", levelIndex + 1)
							.attribute("score", level.score)
						.pop();
					}
				}
				worldIndex++;
			}
			writer.close();
		} catch (IOException e) {
			Gdx.app.log("Progress.save", "Failed to save progress. Exception: " + e.toString());
		}
	}

}
