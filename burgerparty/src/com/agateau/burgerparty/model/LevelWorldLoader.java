package com.agateau.burgerparty.model;

import java.util.Set;

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
			LevelWorld world = loadWorld(n - 1, dirName);
			worlds.add(world);
		}
		initNewItemFields(worlds);
		return worlds;
	}

	private LevelWorld loadWorld(int index, String dirName) {
		LevelWorld world = new LevelWorld(index, dirName);
		for (int n=1; n <= LevelWorld.LEVEL_PER_WORLD; n++) {
			String name = dirName + "/" + n + ".xml";
			FileHandle levelFile = Gdx.files.internal(name);
			assert(levelFile.exists());
			Gdx.app.log("LevelWorldLoader", "levelFile=" + levelFile);
			world.addLevel(Level.fromXml(world, n - 1, levelFile));
		}
		return world;
	}

	private void initNewItemFields(Array<LevelWorld> worlds) {
		assert(worlds.size > 0);
		Level level1 = worlds.get(0).getLevel(0);
		Set<MealItem> knownItems = level1.getKnownItems();
		for (LevelWorld world: worlds) {
			for(int idx = 0, n = world.getLevelCount(); idx < n; ++idx) {
				Level level = world.getLevel(idx);
				level.initNewItemField(knownItems);
			}
		}
	}
}