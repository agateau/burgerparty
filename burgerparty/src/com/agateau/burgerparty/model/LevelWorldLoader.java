package com.agateau.burgerparty.model;

import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class LevelWorldLoader {
	public Array<LevelWorld> run() {
		Array<LevelWorld> worlds = new Array<LevelWorld>();
		for (int n=1;; n++) {
			String dirName = "levels/" + n + "/";
			if (!Gdx.files.internal(dirName + "1.xml").exists()) {
				break;
			}
			Gdx.app.log("LevelWorldLoader", "dir=" + dirName);
			LevelWorld world = loadWorld(dirName);
			worlds.add(world);
		}
		checkNewItems(worlds);
		return worlds;
	}

	private LevelWorld loadWorld(String dirName) {
		LevelWorld world = new LevelWorld(dirName);
		for (int n=1;; n++) {
			String name = dirName + "/" + n + ".xml";
			FileHandle levelFile = Gdx.files.internal(name);
			if (!levelFile.exists()) {
				break;
			}
			Gdx.app.log("LevelWorldLoader", "levelFile=" + levelFile);
			world.addLevel(Level.fromXml(world, levelFile));
		}
		return world;
	}

	private void checkNewItems(Array<LevelWorld> worlds) {
		assert(worlds.size > 0);
		HashSet<MealItem> knownItems = new HashSet<MealItem>();
		Level level1 = worlds.get(0).getLevel(0);
		for (MealItem item: level1.definition.getBurgerItems()) {
			knownItems.add(item);
		}
		for (MealItem item: level1.definition.getExtraItems()) {
			knownItems.add(item);
		}
		for (LevelWorld world: worlds) {
			for(int idx = 0, n = world.getLevelCount(); idx < n; ++idx) {
				Level level = world.getLevel(idx);
				level.checkNewItems(knownItems);
			}
		}
	}
}