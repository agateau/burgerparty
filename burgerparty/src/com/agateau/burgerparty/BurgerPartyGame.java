package com.agateau.burgerparty;

import java.util.HashSet;
import java.util.Set;

import com.agateau.burgerparty.model.Level;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.model.LevelWorldLoader;
import com.agateau.burgerparty.model.MealItem;
import com.agateau.burgerparty.model.Progress;
import com.agateau.burgerparty.screens.GameScreen;
import com.agateau.burgerparty.screens.LevelListScreen;
import com.agateau.burgerparty.screens.LoadingScreen;
import com.agateau.burgerparty.screens.StartScreen;
import com.agateau.burgerparty.screens.NewItemScreen;
import com.agateau.burgerparty.screens.SandBoxGameScreen;
import com.agateau.burgerparty.utils.AnimScriptLoader;
import com.agateau.burgerparty.utils.MusicController;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.StringArgumentDefinition;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Array;

public class BurgerPartyGame extends Game {
	private HashSet<Object> mHandlers = new HashSet<Object>();

	private Assets mAssets;
	private MusicController mMusicController;
	private Array<LevelWorld> mLevelWorlds = new Array<LevelWorld>();
	private int mLevelWorldIndex = 0;
	private int mLevelIndex = 0;

	private static final String PROGRESS_FILE = "progress.xml";

	@Override
	public void create() {
		mAssets = new Assets();
		Gdx.input.setCatchBackKey(true);
		showLoadingScreen();
	}

	@Override
	public void dispose() {
		super.dispose();
		Gdx.app.log("BurgerPartyGame", "dispose");
	}

	@Override
	public void resume() {
		super.resume();
		AssetManager manager = mAssets.getAssetManager();
		Gdx.app.log("BurgerPartyGame", "resume: assetManager=" + manager);
		Gdx.app.log("BurgerPartyGame", "resume: assetManager.getProgress()=" + manager.getProgress());
		if (manager.getQueuedAssets() > 0) {
			final Screen oldScreen = getScreen();
			LoadingScreen loadingScreen = new LoadingScreen(manager);
			loadingScreen.ready.connect(mHandlers, new Signal0.Handler() {
				@Override
				public void handle() {
					setScreen(oldScreen);
				}
			});
		}
	}

	void setupAnimScriptLoader()
	{
		AnimScriptLoader loader = mAssets.getAnimScriptLoader();
		loader.registerMemberMethod("play", mAssets.getSoundAtlas(), "createPlayAction", new StringArgumentDefinition());
		loader.registerMemberMethod("playMealItem", this, "createPlayMealItemAction", new StringArgumentDefinition());
	}

	public Action createPlayMealItemAction(String name) {
		return MealItem.createPlayMealItemAction(mAssets.getSoundAtlas(), name);
	}

	private void loadLevelWorlds() {
		LevelWorldLoader loader = new LevelWorldLoader();
		mLevelWorlds = loader.run();
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

	public Assets getAssets() {
		return mAssets;
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

	public Array<LevelWorld> getLevelWorlds() {
		return mLevelWorlds;
	}

	public Set<MealItem> getKnownItems() {
		Set<MealItem> set = new HashSet<MealItem>();
		for (LevelWorld world: mLevelWorlds) {
			for (int levelIndex = 0; levelIndex < world.getLevelCount(); ++levelIndex) {
				Level level = world.getLevel(levelIndex);
				if (level.score > -1) {
					set.addAll(level.getKnownItems());
				}
			}
		}
		return set;
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
		if (next != null && next.score == Level.LOCKED_SCORE) {
			next.score = 0;
		}
		saveLevelProgress();
	}

	public void startLevel(int levelWorldIndex, int levelIndex) {
		mMusicController.fadeOut();
		mLevelWorldIndex = levelWorldIndex;
		mLevelIndex = levelIndex;
		Level level = mLevelWorlds.get(mLevelWorldIndex).getLevel(mLevelIndex);
		if (level.hasBrandNewItem()) {
			NewItemScreen screen = new NewItemScreen(this, mLevelWorldIndex, level.definition.getNewItem());
			screen.done.connect(mHandlers, new Signal0.Handler() {
				@Override
				public void handle() {
					doStartLevel();
				}
			});
			setScreen(screen);
		} else {
			doStartLevel();
		}
	}

	public void startSandBox() {
		mMusicController.fadeOut();
		setScreen(new SandBoxGameScreen(this));
	}

	private void showLoadingScreen() {
		LoadingScreen screen = new LoadingScreen(mAssets.getAssetManager());
		screen.ready.connect(mHandlers, new Signal0.Handler() {
			@Override
			public void handle() {
				finishLoad();
			}
		});
		setScreen(screen);
	}

	private void finishLoad() {
		mAssets.finishLoad();
		Music music = mAssets.getMusic();
		music.setLooping(true);
		mMusicController = new MusicController(music);
		mMusicController.play();
		setupAnimScriptLoader();
		loadLevelWorlds();
		assert(mLevelWorlds.size > 0);
		loadLevelProgress();
		showMenu();
	}
	
	public void showMenu() {
		mMusicController.play();
		setScreen(new StartScreen(this));
	}

	public void selectLevel(int worldIndex) {
		mMusicController.play();
		setScreen(new LevelListScreen(this, worldIndex));
	}
	
	private void doStartLevel() {
		Level level = mLevelWorlds.get(mLevelWorldIndex).getLevel(mLevelIndex);
		setScreen(new GameScreen(this, level));
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
