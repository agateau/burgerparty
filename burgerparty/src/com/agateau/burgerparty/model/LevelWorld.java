package com.agateau.burgerparty.model;

import java.io.IOException;
import java.util.MissingResourceException;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

public class LevelWorld {
	public LevelWorld(String dirName) {
		mDirName = dirName;

		loadConfig();
		for (int n=1;; n++) {
			String name = dirName + "/" + n + ".xml";
			FileHandle levelFile = Gdx.files.internal(name);
			if (!levelFile.exists()) {
				break;
			}
			Gdx.app.log("LevelWorld", "levelFile=" + levelFile);
			mLevels.add(Level.fromXml(this, levelFile));
		}
	}

	public void checkNewItems(Set<MealItem> knownItems) {
		for(Level level: mLevels) {
			level.checkNewItems(knownItems);
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

	public XmlReader.Element getConfig() {
		return mConfig;
	}

	private void loadConfig() {
		FileHandle handle = Gdx.files.internal(mDirName + "/config.xml");
		XmlReader reader = new XmlReader();
		XmlReader.Element root = null;
		try {
			root = reader.parse(handle);
		} catch (IOException e) {
			throw new MissingResourceException("Failed to load world config from " + handle.path() + ". Exception: " + e.toString() + ".", "LevelWorld", handle.path());
		}
		if (root == null) {
			throw new MissingResourceException("Failed to load world config from " + handle.path() + ". No root element.", "LevelWorld", handle.path());
		}

		mConfig = root;
	}

	private String mDirName;
	private Array<Level> mLevels = new Array<Level>();
	private XmlReader.Element mConfig;
}
