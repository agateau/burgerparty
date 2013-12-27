package com.agateau.burgerparty;

import java.util.HashSet;

import com.agateau.burgerparty.model.Level;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.model.Universe;
import com.agateau.burgerparty.model.UniverseLoader;
import com.agateau.burgerparty.model.MealItem;
import com.agateau.burgerparty.model.MealItemDb;
import com.agateau.burgerparty.model.ProgressIO;
import com.agateau.burgerparty.screens.AboutScreen;
import com.agateau.burgerparty.screens.GameScreen;
import com.agateau.burgerparty.screens.LevelListScreen;
import com.agateau.burgerparty.screens.LoadingScreen;
import com.agateau.burgerparty.screens.NewItemScreen;
import com.agateau.burgerparty.screens.SandBoxGameScreen;
import com.agateau.burgerparty.screens.StartScreen;
import com.agateau.burgerparty.screens.WorldListScreen;
import com.agateau.burgerparty.utils.AnimScriptLoader;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.MusicController;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.StringArgumentDefinition;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Action;

public class BurgerPartyGame extends Game {
	private HashSet<Object> mHandlers = new HashSet<Object>();

	private Assets mAssets;
	private MusicController mMusicController;
	private Universe mUniverse = new Universe();
	private int mLevelWorldIndex = 0;
	private int mLevelIndex = 0;

	private static final String PROGRESS_FILE = "progress.xml";

	@Override
	public void create() {
		mAssets = new Assets();
		MealItemDb.getInstance().load(Gdx.files.internal("mealitems.xml"));
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
		UniverseLoader loader = new UniverseLoader();
		loader.run(mUniverse);
	}

	private void loadLevelProgress() {
		// At least, unlock first level
		mUniverse.get(0).getLevel(0).unlock();

		FileHandle handle = FileUtils.getUserWritableFile(PROGRESS_FILE);
		if (!handle.exists()) {
			return;
		}
		ProgressIO progressIO = new ProgressIO(mUniverse.getWorlds());
		progressIO.load(handle);
	}

	private void saveLevelProgress() {
		FileHandle handle = FileUtils.getUserWritableFile(PROGRESS_FILE);
		ProgressIO progressIO = new ProgressIO(mUniverse.getWorlds());
		progressIO.save(handle);
	}

	public Assets getAssets() {
		return mAssets;
	}

	public int getLevelWorldIndex() {
		return mLevelWorldIndex;
	}

	public int getLevelIndex() {
		return mLevelIndex;
	}

	public Universe getUniverse() {
		return mUniverse;
	}

	public void onCurrentLevelFinished(int score) {
		LevelWorld currentGroup = mUniverse.get(mLevelWorldIndex);
		Level currentLevel = currentGroup.getLevel(mLevelIndex);
		if (score > currentLevel.getScore()) {
			currentLevel.setScore(score);
		}
		// Unlock next level if necessary
		Level next = null;
		if (mLevelIndex < currentGroup.getLevelCount() - 1) {
			next = currentGroup.getLevel(mLevelIndex + 1);
		} else if (mLevelWorldIndex < mUniverse.getWorlds().size - 1){
			next = mUniverse.get(mLevelWorldIndex + 1).getLevel(0);
		}
		if (next != null && next.isLocked()) {
			next.unlock();
		}
		saveLevelProgress();
	}

	public void startLevel(int levelWorldIndex, int levelIndex) {
		mMusicController.fadeOut();
		mLevelWorldIndex = levelWorldIndex;
		mLevelIndex = levelIndex;
		final Level level = mUniverse.get(mLevelWorldIndex).getLevel(mLevelIndex);
		if (level.hasBrandNewItem()) {
			NewItemScreen screen = new NewItemScreen(this, mLevelWorldIndex, level.definition.getNewItem());
			screen.done.connect(mHandlers, new Signal0.Handler() {
				@Override
				public void handle() {
					level.setScore(0);
					saveLevelProgress();
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
		assert(mUniverse.getWorlds().size > 0);
		loadLevelProgress();
		showStartScreen();
	}

	public void showWorldListScreen() {
		mMusicController.play();
		setScreen(new WorldListScreen(this));
	}

	public void showStartScreen() {
		mMusicController.play();
		setScreen(new StartScreen(this));
	}

	public void showLevelListScreen(int worldIndex) {
		mMusicController.play();
		setScreen(new LevelListScreen(this, worldIndex));
	}

	public void showAboutScreen() {
		mMusicController.play();
		setScreen(new AboutScreen(this));
	}

	private void doStartLevel() {
		Level level = mUniverse.get(mLevelWorldIndex).getLevel(mLevelIndex);
		setScreen(new GameScreen(this, level));
	}
}
