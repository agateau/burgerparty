package com.agateau.burgerparty;

import com.agateau.burgerparty.model.Level;
import com.agateau.burgerparty.model.LevelResult;
import com.agateau.burgerparty.model.Progress;
import com.agateau.burgerparty.screens.GameScreen;
import com.agateau.burgerparty.screens.LevelListScreen;
import com.agateau.burgerparty.screens.MenuScreen;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

public class BurgerPartyGame extends Game {
	private Skin mSkin;
	private TextureAtlas mAtlas;
	private Array<Level> mLevels = new Array<Level>();
	private int mLevelIndex = 0;

	private static String PROGRESS_FILE = "progress.xml";

	@Override
	public void create() {
		mAtlas = new TextureAtlas(Gdx.files.internal("burgerparty.atlas"));

		TextureAtlas skinAtlas = new TextureAtlas(Gdx.files.internal("skin/uiskin.atlas"));
		mSkin = new Skin(Gdx.files.internal("skin/uiskin.json"), skinAtlas);

		showMenu();
		loadLevelDefinitions();
		loadLevelProgress();
	}

	private void loadLevelDefinitions() {
		for (int n=1;; n++) {
			String name = "levels/" + n + ".xml";
			FileHandle levelFile = Gdx.files.internal(name);
			if (!levelFile.exists()) {
				break;
			}
			Gdx.app.log("loadLevels", "levelFile=" + levelFile);
			Level level = Level.fromXml(levelFile);
			mLevels.add(level);
		}
	}

	private void loadLevelProgress() {
		// At least, unlock first level
		mLevels.get(0).stars = 0;

		FileHandle handle = getUserWritableFile(PROGRESS_FILE);
		if (!handle.exists()) {
			return;
		}
		Array<Progress.Item> lst = Progress.load(handle);
		for(Progress.Item item: lst) {
			mLevels.get(item.level - 1).stars = item.stars;
		}
	}

	private void saveLevelProgress() {
		FileHandle handle = getUserWritableFile(PROGRESS_FILE);
		Array<Progress.Item> lst = new Array<Progress.Item>();
		int n = 1;
		for (Level level: mLevels) {
			if (level.stars > -1) {
				Progress.Item item = new Progress.Item();
				item.level = n;
				item.stars = level.stars;
				lst.add(item);
			}
			++n;
		}
		Progress.save(handle, lst);
	}

	public int getLevelIndex() {
		return mLevelIndex;
	}

	public int getLevelCount() {
		return mLevels.size;
	}

	public int getLevelStars(int index) {
		return mLevels.get(index).stars;
	}

	public void onCurrentLevelFinished(LevelResult result) {
		Level currentLevel = mLevels.get(mLevelIndex);
		if (result.stars > currentLevel.stars) {
			currentLevel.stars = result.stars;
		}
		if (mLevelIndex < mLevels.size - 1) {
			// Unlock next level if necessary
			Level next = mLevels.get(mLevelIndex + 1);
			if (next.stars == -1) {
				next.stars = 0;
			}
		}
		saveLevelProgress();
	}

	public void startLevel(int index) {
		mLevelIndex = index;
		setScreen(new GameScreen(this, mLevels.get(mLevelIndex), mAtlas, mSkin));
	}

	public void showMenu() {
		setScreen(new MenuScreen(this, mSkin));
	}

	public void selectLevel() {
		setScreen(new LevelListScreen(this, mSkin));
	}

	static private FileHandle getUserWritableFile(String name) {
		FileHandle handle;
		if (Gdx.app.getType() == ApplicationType.Desktop) {
			handle = Gdx.files.external(".local/burgerparty/" + name);
		} else {
			handle = Gdx.files.local(name);
		}
		return handle;
	}
}
