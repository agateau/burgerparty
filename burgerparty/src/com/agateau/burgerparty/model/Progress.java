package com.agateau.burgerparty.model;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

/*
 * V1:
 *
 * <progress>
 *   <item world="$world" level="$level" score="$score"/>
 *   ...
 * </progress>
 *
 * V2:
 *
 * <progress version="2">
 *   <levels>
 *     <level world="$world" level="$level" score="$score"/>
 *     ...
 *   </levels>
 *   <unlockedItems>
 *     <unlockedItem name="$foo"/>
 *     <unlockedItem name="$bar"/>
 *     ...
 *   </unlockedItems>
 * </progress>
 */
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
		int version = root.getIntAttribute("version", 1);
		if (version == 1) {
			loadV1(root, worlds);
		} else if (version == 2) {
			loadV2(root, worlds);
		} else {
			Gdx.app.error("Progress", "Don't know how to load progress version " + version + ". Did not load anything.");
		}
	}

	private static void loadV1(XmlReader.Element root, Array<LevelWorld> worlds) {
		for(int idx = 0; idx < root.getChildCount(); ++idx) {
			XmlReader.Element element = root.getChild(idx);
			int worldIndex = element.getIntAttribute("world", 1) - 1;
			int levelIndex = element.getIntAttribute("level") - 1;
			int score = element.getIntAttribute("score", -1);
			Level level = worlds.get(worldIndex).getLevel(levelIndex);
			level.score = score;
		}
	}

	private static void loadV2(XmlReader.Element root, Array<LevelWorld> worlds) {
		XmlReader.Element levelsElement = root.getChildByName("levels");
		if (levelsElement == null) {
			return;
		}
		for(XmlReader.Element element: levelsElement.getChildrenByName("level")) {
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
			XmlWriter levelsElement = root.element("levels");
			int worldIndex = 0;
			for (LevelWorld world: worlds) {
				for (int levelIndex = 0; levelIndex < world.getLevelCount(); ++levelIndex) {
					Level level = world.getLevel(levelIndex);
					if (level.score > -1) {
						levelsElement.element("level")
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
