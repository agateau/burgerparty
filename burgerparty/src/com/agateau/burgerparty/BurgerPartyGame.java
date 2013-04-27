package com.agateau.burgerparty;

import com.agateau.burgerparty.model.Level;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.model.LevelResult;
import com.agateau.burgerparty.model.Progress;
import com.agateau.burgerparty.screens.GameScreen;
import com.agateau.burgerparty.screens.LevelListScreen;
import com.agateau.burgerparty.screens.MenuScreen;
import com.agateau.burgerparty.utils.AnimScriptLoader;
import com.agateau.burgerparty.utils.StringArgumentDefinition;
import com.agateau.burgerparty.view.SoundAtlas;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

public class BurgerPartyGame extends Game {
	private SoundAtlas mSoundAtlas = new SoundAtlas();
	private Skin mSkin;
	private TextureAtlas mAtlas;
	private Array<LevelWorld> mLevelWorlds = new Array<LevelWorld>();
	private int mLevelWorldIndex = 0;
	private int mLevelIndex = 0;

	private static String PROGRESS_FILE = "progress.xml";

	@Override
	public void create() {
		mAtlas = new TextureAtlas(Gdx.files.internal("burgerparty.atlas"));
		mSkin = new Skin(Gdx.files.internal("ui/skin.json"), mAtlas);

		setupAnimScriptLoader();
		loadLevelWorlds();
		assert(mLevelWorlds.size > 0);
		loadLevelProgress();
		showMenu();
	}

	public SoundAtlas getSoundAtlas() {
		return mSoundAtlas;
	}

	void setupAnimScriptLoader()
	{
		AnimScriptLoader loader = AnimScriptLoader.getInstance();
		loader.registerMemberMethod("play", mSoundAtlas, "createPlayAction", new StringArgumentDefinition());
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
		mLevelWorlds.get(0).getLevel(0).stars = 0;

		FileHandle handle = getUserWritableFile(PROGRESS_FILE);
		if (!handle.exists()) {
			return;
		}
		Array<Progress.Item> lst = Progress.load(handle);
		for(Progress.Item item: lst) {
			mLevelWorlds.get(item.levelWorld - 1).getLevel(item.level - 1).stars = item.stars;
		}
	}

	private void saveLevelProgress() {
		FileHandle handle = getUserWritableFile(PROGRESS_FILE);
		Array<Progress.Item> lst = new Array<Progress.Item>();
		int levelWorldIndex = 0;
		for (LevelWorld world: mLevelWorlds) {
			for (int levelIndex = 0; levelIndex < world.getLevelCount(); ++levelIndex) {
				Level level = world.getLevel(levelIndex);
				if (level.stars > -1) {
					Progress.Item item = new Progress.Item();
					item.levelWorld = levelWorldIndex + 1;
					item.level = levelIndex + 1;
					item.stars = level.stars;
					lst.add(item);
				}
			}
			levelWorldIndex++;
		}
		Progress.save(handle, lst);
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

	public void onCurrentLevelFinished(LevelResult result) {
		LevelWorld currentGroup = mLevelWorlds.get(mLevelWorldIndex);
		Level currentLevel = currentGroup.getLevel(mLevelIndex);
		int stars = result.computeStars();
		if (stars > currentLevel.stars) {
			currentLevel.stars = stars;
		}
		// Unlock next level if necessary
		Level next = null;
		if (mLevelIndex < currentGroup.getLevelCount() - 1) {
			next = currentGroup.getLevel(mLevelIndex + 1);
		} else if (mLevelWorldIndex < mLevelWorlds.size - 1){
			next = mLevelWorlds.get(mLevelWorldIndex + 1).getLevel(0);
		}
		if (next != null && next.stars == -1) {
			next.stars = 0;
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
