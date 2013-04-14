package com.agateau.burgerparty;

import com.agateau.burgerparty.model.Level;
import com.agateau.burgerparty.model.LevelGroup;
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
	private Array<LevelGroup> mLevelGroups = new Array<LevelGroup>();
	private int mLevelGroupIndex = 0;
	private int mLevelIndex = 0;

	private static String PROGRESS_FILE = "progress.xml";

	@Override
	public void create() {
		mAtlas = new TextureAtlas(Gdx.files.internal("burgerparty.atlas"));
		mSkin = new Skin(Gdx.files.internal("ui/skin.json"), mAtlas);

		showMenu();
		loadLevelGroups();
		assert(mLevelGroups.size > 0);
		loadLevelProgress();
	}

	private void loadLevelGroups() {
		for (int n=1;; n++) {
			String dirName = "levels/" + n + "/";
			if (!Gdx.files.internal(dirName + "1.xml").exists()) {
				break;
			}
			Gdx.app.log("loadLevelGroups", "dir=" + dirName);
			mLevelGroups.add(new LevelGroup(dirName));
		}
	}

	private void loadLevelProgress() {
		// At least, unlock first level
		mLevelGroups.get(0).getLevel(0).stars = 0;

		FileHandle handle = getUserWritableFile(PROGRESS_FILE);
		if (!handle.exists()) {
			return;
		}
		Array<Progress.Item> lst = Progress.load(handle);
		for(Progress.Item item: lst) {
			mLevelGroups.get(item.levelGroup - 1).getLevel(item.level - 1).stars = item.stars;
		}
	}

	private void saveLevelProgress() {
		FileHandle handle = getUserWritableFile(PROGRESS_FILE);
		Array<Progress.Item> lst = new Array<Progress.Item>();
		int levelGroupIndex = 0;
		for (LevelGroup group: mLevelGroups) {
			for (int levelIndex = 0; levelIndex < group.getLevelCount(); ++levelIndex) {
				Level level = group.getLevel(levelIndex);
				if (level.stars > -1) {
					Progress.Item item = new Progress.Item();
					item.levelGroup = levelGroupIndex + 1;
					item.level = levelIndex + 1;
					item.stars = level.stars;
					lst.add(item);
				}
			}
			levelGroupIndex++;
		}
		Progress.save(handle, lst);
	}

	public int getLevelGroupIndex() {
		return mLevelGroupIndex;
	}

	public int getLevelIndex() {
		return mLevelIndex;
	}

	public int getLevelGroupCount() {
		return mLevelGroups.size;
	}

	public LevelGroup getLevelGroup(int index) {
		return mLevelGroups.get(index);
	}

	public void onCurrentLevelFinished(LevelResult result) {
		LevelGroup currentGroup = mLevelGroups.get(mLevelGroupIndex);
		Level currentLevel = currentGroup.getLevel(mLevelIndex);
		int stars = result.computeStars();
		if (stars > currentLevel.stars) {
			currentLevel.stars = stars;
		}
		// Unlock next level if necessary
		Level next = null;
		if (mLevelIndex < currentGroup.getLevelCount() - 1) {
			next = currentGroup.getLevel(mLevelIndex + 1);
		} else if (mLevelGroupIndex < mLevelGroups.size - 1){
			next = mLevelGroups.get(mLevelGroupIndex + 1).getLevel(0);
		}
		if (next != null && next.stars == -1) {
			next.stars = 0;
		}
		saveLevelProgress();
	}

	public void startLevel(int groupIndex, int levelIndex) {
		mLevelGroupIndex = groupIndex;
		mLevelIndex = levelIndex;
		setScreen(new GameScreen(this, mLevelGroups.get(mLevelGroupIndex).getLevel(mLevelIndex), mAtlas, mSkin));
	}

	public void showMenu() {
		setScreen(new MenuScreen(this, mAtlas, mSkin));
	}

	public void selectLevel() {
		setScreen(new LevelListScreen(this, mAtlas, mSkin));
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
