package com.agateau.burgerparty.model;

import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class LevelWorldLoader {
	public Array<LevelWorld> run() {
		Array<LevelWorld> worlds = new Array<LevelWorld>();
		for (int n=1;; n++) {
			String dirName = "levels/" + n + "/";
			if (!Gdx.files.internal(dirName + "1.xml").exists()) {
				break;
			}
			Gdx.app.log("loadLevelWorlds", "dir=" + dirName);
			LevelWorld world = new LevelWorld(dirName);
			worlds.add(world);
		}
		checkNewItems(worlds);
		return worlds;
	}

	private void checkNewItems(Array<LevelWorld> worlds) {
		assert(worlds.size > 0);
		HashSet<String> knownItems = new HashSet<String>();
		Level level1 = worlds.get(0).getLevel(0);
		for (String name: level1.definition.burgerItems) {
			knownItems.add(name);
		}
		for (String name: level1.definition.extraItems) {
			knownItems.add(name);
		}
		for (LevelWorld world: worlds) {
			world.checkNewItems(knownItems);
		}
	}
}