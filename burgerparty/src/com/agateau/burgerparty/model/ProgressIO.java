package com.agateau.burgerparty.model;

import java.io.IOException;

import com.agateau.burgerparty.utils.NLog;
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
 * </progress>
 *
 * V3:
 *
 * <progress version="3">
 *   <levels>
 *     <level world="$world" level="$level" score="$score" stars="$starCount"/>
 *     ...
 *   </levels>
 * </progress>
 */
public class ProgressIO {
	static final int SCORE_LOCKED = -2;
	static final int SCORE_NEW = -1;

	public ProgressIO(Array<LevelWorld> worlds) {
		if (log == null) {
			log = NLog.getRoot().create("ProgressIO");
		}
		mWorlds = worlds;
	}

	public void load(FileHandle handle) {
		XmlReader reader = new XmlReader();
		XmlReader.Element root = null;
		try {
			root = reader.parse(handle);
		} catch (IOException e) {
			log.e("load: Failed to load progress from %s. Exception: %s", handle.path(), e);
			return;
		}
		if (root == null) {
			log.e("load: Failed to load progress from %s. No root XML element found.", handle.path());
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
		} else if (version == 3) {
			loadV3(root);
		} else {
			log.e("load: Don't know how to load progress version " + version + ". Did not load anything.");
		}
	}

	private void loadV1(XmlReader.Element root) {
		for(int idx = 0; idx < root.getChildCount(); ++idx) {
			XmlReader.Element element = root.getChild(idx);
			int worldIndex = element.getIntAttribute("world", 1) - 1;
			int levelIndex = element.getIntAttribute("level") - 1;
			int score = element.getIntAttribute("score", -1);
			Level level = mWorlds.get(worldIndex).getLevel(levelIndex);
			level.setScore(score);
			if (score >= 30000) {
				level.setStarCount(3);
			} else if (score >= 15000) {
				level.setStarCount(2);
			} else if (score > 0) {
				level.setStarCount(1);
			}
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
			int score = element.getIntAttribute("score", SCORE_LOCKED);
			if (worldIndex >= mWorlds.size) {
				log.e("loadV2: No world with index " + (worldIndex + 1));
				continue;
			}
			LevelWorld world = mWorlds.get(worldIndex);
			if (levelIndex >= world.getLevelCount()) {
				log.e("loadV2: No level with index " + (levelIndex + 1) + " in world " + (worldIndex + 1));
				continue;
			}
			Level level = world.getLevel(levelIndex);
			level.setScore(score);
			if (score >= 30000) {
				level.setStarCount(3);
			} else if (score >= 15000) {
				level.setStarCount(2);
			} else if (score > 0) {
				level.setStarCount(1);
			}
		}

		boolean previousWasWon = true;
		for (LevelWorld world: mWorlds) {
			for (int idx = 0, n = world.getLevelCount(); idx < n; ++idx) {
				Level level = world.getLevel(idx);
				if (previousWasWon && level.isLocked()) {
					level.unlock();
				}
				previousWasWon = level.getScore() > 0;
			}
		}
	}

	private void loadV3(XmlReader.Element root) {
		XmlReader.Element levelsElement = root.getChildByName("levels");
		if (levelsElement == null) {
			return;
		}
		for(XmlReader.Element element: levelsElement.getChildrenByName("level")) {
			int worldIndex = element.getIntAttribute("world", 1) - 1;
			int levelIndex = element.getIntAttribute("level") - 1;
			int score = element.getIntAttribute("score", SCORE_LOCKED);
			int starCount = element.getIntAttribute("stars", 0);
			if (worldIndex >= mWorlds.size) {
				log.e("loadV3: No world with index " + (worldIndex + 1));
				continue;
			}
			LevelWorld world = mWorlds.get(worldIndex);
			if (levelIndex >= world.getLevelCount()) {
				log.e("loadV3: No level with index " + (levelIndex + 1) + " in world " + (worldIndex + 1));
				continue;
			}
			Level level = world.getLevel(levelIndex);
			level.setScore(score);
			if (starCount == 4) {
				level.setStarCount(3);
				level.markPerfect();
			} else {
				level.setStarCount(starCount);
			}
		}

		boolean previousWasWon = true;
		for (LevelWorld world: mWorlds) {
			for (int idx = 0, n = world.getLevelCount(); idx < n; ++idx) {
				Level level = world.getLevel(idx);
				if (previousWasWon && level.isLocked()) {
					level.unlock();
				}
				previousWasWon = level.getScore() > 0;
			}
		}
	}

	public void save(FileHandle handle) {
		XmlWriter writer = new XmlWriter(handle.writer(false));
		save(writer);
	}

	public void save(XmlWriter writer) {
		try {
			XmlWriter root = writer.element("progress");
			root.attribute("version", 3);
			XmlWriter levelsElement = root.element("levels");
			int worldIndex = 0;
			for (LevelWorld world: mWorlds) {
				for (int levelIndex = 0; levelIndex < world.getLevelCount(); ++levelIndex) {
					Level level = world.getLevel(levelIndex);
					if (!level.isLocked()) {
						int score = level.isNew() ? SCORE_NEW : level.getScore();
						boolean perfect = level.isPerfect();
						levelsElement.element("level")
							.attribute("world", worldIndex + 1)
							.attribute("level", levelIndex + 1)
							.attribute("score", score)
							.attribute("stars", perfect ? 4 : level.getStarCount())
						.pop();
					}
				}
				worldIndex++;
			}
			writer.close();
		} catch (IOException e) {
			log.e("save: Failed to save progress. Exception: %s", e);
		}
	}

	private static NLog log;
	private Array<LevelWorld> mWorlds;
}
