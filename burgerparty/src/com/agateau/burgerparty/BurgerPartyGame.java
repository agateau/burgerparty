package com.agateau.burgerparty;

import com.agateau.burgerparty.model.Level;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.model.MealItem;
import com.agateau.burgerparty.model.Progress;
import com.agateau.burgerparty.screens.GameScreen;
import com.agateau.burgerparty.screens.LevelListScreen;
import com.agateau.burgerparty.screens.MenuScreen;
import com.agateau.burgerparty.utils.AnimScriptLoader;
import com.agateau.burgerparty.utils.StringArgumentDefinition;

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
	private Array<LevelWorld> mLevelWorlds = new Array<LevelWorld>();
	private int mLevelWorldIndex = 0;
	private int mLevelIndex = 0;

	private static final String PROGRESS_FILE = "progress.xml";

	@Override
	public void create() {
		Gdx.input.setCatchBackKey(true);
		mAtlas = Kernel.getTextureAtlas();
		mSkin = Kernel.getSkin();

		setupAnimScriptLoader();
		loadLevelWorlds();
		assert(mLevelWorlds.size > 0);
		loadLevelProgress();
		showMenu();
	}

	void setupAnimScriptLoader()
	{
		AnimScriptLoader loader = Kernel.getAnimScriptLoader();
		loader.registerMemberMethod("play", Kernel.getSoundAtlas(), "createPlayAction", new StringArgumentDefinition());
		loader.registerStaticMethod("playMealItem", MealItem.class, "createPlayMealItemAction", new StringArgumentDefinition());
	}

	private void loadLevelWorlds() {
		for (int n=1;; n++) {
			String dirName = "levels/" + n + "/";
			if (!Gdx.files.internal(dirName + "1.xml").exists()) {
				break;
			}
			Gdx.app.log("loadLevelWorlds", "dir=" + dirName);
			mLevelWorlds.add(new LevelWorld(dirName));
		}
	}

	private void loadLevelProgress() {
		// At least, unlock first level
		mLevelWorlds.get(0).getLevel(0).score = 0;

		FileHandle handle = getUserWritableFile(PROGRESS_FILE);
		if (!handle.exists()) {
			return;
		}
		Array<Progress.Item> lst = Progress.load(handle);
		for(Progress.Item item: lst) {
			Level level = mLevelWorlds.get(item.levelWorld - 1).getLevel(item.level - 1);
			level.score = item.score;
		}
	}

	private void saveLevelProgress() {
		FileHandle handle = getUserWritableFile(PROGRESS_FILE);
		Array<Progress.Item> lst = new Array<Progress.Item>();
		int levelWorldIndex = 0;
		for (LevelWorld world: mLevelWorlds) {
			for (int levelIndex = 0; levelIndex < world.getLevelCount(); ++levelIndex) {
				Level level = world.getLevel(levelIndex);
				if (level.score > -1) {
					Progress.Item item = new Progress.Item();
					item.levelWorld = levelWorldIndex + 1;
					item.level = levelIndex + 1;
					item.score = level.score;
					lst.add(item);
				}
			}
			levelWorldIndex++;
		}
		Progress.save(handle, lst);
	}

	public int getHighScore(int world, int level) {
		int value = mLevelWorlds.get(world).getLevel(level).score;
		return Math.max(value, 0);
	}

	public int getLevelWorldIndex() {
		return mLevelWorldIndex;
	}

	public int getLevelIndex() {
		return mLevelIndex;
	}

	public int getLevelWorldCount() {
		return mLevelWorlds.size;
	}

	public LevelWorld getLevelWorld(int index) {
		return mLevelWorlds.get(index);
	}

	public void onCurrentLevelFinished(int score) {
		LevelWorld currentGroup = mLevelWorlds.get(mLevelWorldIndex);
		Level currentLevel = currentGroup.getLevel(mLevelIndex);
		if (score > currentLevel.score) {
			currentLevel.score = score;
		}
		// Unlock next level if necessary
		Level next = null;
		if (mLevelIndex < currentGroup.getLevelCount() - 1) {
			next = currentGroup.getLevel(mLevelIndex + 1);
		} else if (mLevelWorldIndex < mLevelWorlds.size - 1){
			next = mLevelWorlds.get(mLevelWorldIndex + 1).getLevel(0);
		}
		if (next != null && next.score == -1) {
			next.score = 0;
		}
		saveLevelProgress();
	}

	public void startLevel(int levelWorldIndex, int levelIndex) {
		mLevelWorldIndex = levelWorldIndex;
		mLevelIndex = levelIndex;
		setScreen(new GameScreen(this, mLevelWorlds.get(mLevelWorldIndex).getLevel(mLevelIndex), mAtlas, mSkin));
	}

	public void showMenu() {
		setScreen(new MenuScreen(this, mAtlas, mSkin));
	}

	public void selectLevel(int worldIndex) {
		setScreen(new LevelListScreen(this, worldIndex, mAtlas, mSkin));
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
