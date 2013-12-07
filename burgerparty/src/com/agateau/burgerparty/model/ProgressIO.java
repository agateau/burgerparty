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
public class ProgressIO {
	public ProgressIO(Array<LevelWorld> worlds) {
		mWorlds = worlds;
	}

	public void load(FileHandle handle) {
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
		load(root);
	}

	public void load(XmlReader.Element root) {
		int version = root.getIntAttribute("version", 1);
		if (version == 1) {
			loadV1(root);
		} else if (version == 2) {
			loadV2(root);
		} else {
			Gdx.app.error("ProgressIO", "Don't know how to load progress version " + version + ". Did not load anything.");
		}
	}

	private void loadV1(XmlReader.Element root) {
		for(int idx = 0; idx < root.getChildCount(); ++idx) {
			XmlReader.Element element = root.getChild(idx);
			int worldIndex = element.getIntAttribute("world", 1) - 1;
			int levelIndex = element.getIntAttribute("level") - 1;
			int score = element.getIntAttribute("score", -1);
			Level level = mWorlds.get(worldIndex).getLevel(levelIndex);
			level.score = score;
		}
	}

	private void loadV2(XmlReader.Element root) {
		XmlReader.Element levelsElement = root.getChildByName("levels");
		if (levelsElement == null) {
			return;
		}
		for(XmlReader.Element element: levelsElement.getChildrenByName("level")) {
			int worldIndex = element.getIntAttribute("world", 1) - 1;
			int levelIndex = element.getIntAttribute("level") - 1;
			int score = element.getIntAttribute("score", Level.SCORE_LOCKED);
			if (worldIndex >= mWorlds.size) {
				Gdx.app.error("ProgressIO", "No world with index " + (worldIndex + 1));
				continue;
			}
			LevelWorld world = mWorlds.get(worldIndex);
			if (levelIndex >= world.getLevelCount()) {
				Gdx.app.error("ProgressIO", "No level with index " + (levelIndex + 1) + " in world " + (worldIndex + 1));
				continue;
			}
			Level level = world.getLevel(levelIndex);
			level.score = score;
		}
	}

	public void save(FileHandle handle) {
		XmlWriter writer = new XmlWriter(handle.writer(false));
		save(writer);
	}

	public void save(XmlWriter writer) {
		try {
			XmlWriter root = writer.element("progress");
			root.attribute("version", 2);
			XmlWriter levelsElement = root.element("levels");
			int worldIndex = 0;
			for (LevelWorld world: mWorlds) {
				for (int levelIndex = 0; levelIndex < world.getLevelCount(); ++levelIndex) {
					Level level = world.getLevel(levelIndex);
					if (level.score > Level.SCORE_LOCKED) {
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

	private Array<LevelWorld> mWorlds;
}
