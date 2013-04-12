package com.agateau.burgerparty.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class LevelGroup {
	public LevelGroup(String dirName) {
		mDirName = dirName;

		for (int n=1;; n++) {
			String name = dirName + "/" + n + ".xml";
			FileHandle levelFile = Gdx.files.internal(name);
			if (!levelFile.exists()) {
				break;
			}
			Gdx.app.log("LevelGroup", "levelFile=" + levelFile);
			mLevels.add(Level.fromXml(levelFile));
		}
	}

	public String getDirName() {
		return mDirName;
	}

	public Level getLevel(int index) {
		return mLevels.get(index);
	}

	public int getLevelCount() {
		return mLevels.size;
	}

	private String mDirName;
	private Array<Level> mLevels = new Array<Level>();
}
