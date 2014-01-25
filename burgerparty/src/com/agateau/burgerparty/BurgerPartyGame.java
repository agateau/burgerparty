package com.agateau.burgerparty;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;

import com.agateau.burgerparty.burgercopter.BurgerCopterMiniGame;
import com.agateau.burgerparty.burgervaders.BurgerVadersMiniGame;
import com.agateau.burgerparty.burgerjeweled.BurgerjeweledMiniGame;
import com.agateau.burgerparty.model.Level;
import com.agateau.burgerparty.model.MiniGame;
import com.agateau.burgerparty.model.Universe;
import com.agateau.burgerparty.model.UniverseLoader;
import com.agateau.burgerparty.model.MealItem;
import com.agateau.burgerparty.model.MealItemDb;
import com.agateau.burgerparty.model.ProgressIO;
import com.agateau.burgerparty.screens.AboutScreen;
import com.agateau.burgerparty.screens.CheatScreen;
import com.agateau.burgerparty.screens.GameScreen;
import com.agateau.burgerparty.screens.LevelListScreen;
import com.agateau.burgerparty.screens.LoadingScreen;
import com.agateau.burgerparty.screens.NewItemScreen;
import com.agateau.burgerparty.screens.SandBoxGameScreen;
import com.agateau.burgerparty.screens.StartScreen;
import com.agateau.burgerparty.screens.WorldListScreen;
import com.agateau.burgerparty.utils.AnimScriptLoader;
import com.agateau.burgerparty.utils.FileLogPrinter;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.MusicController;
import com.agateau.burgerparty.utils.NLog;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.StringArgumentDefinition;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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
		setupLog();
		if (log == null) {
			log = NLog.getRoot().create(getClass().getSimpleName());
		}
		String timeStamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(Calendar.getInstance().getTime());
		log.i("create: date=%s", timeStamp);

		mMusicController = new MusicController(getPreferences());
		mAssets = new Assets(mMusicController);
		MealItemDb.getInstance().load(Gdx.files.internal("mealitems.xml"));
		Gdx.input.setCatchBackKey(true);
		showLoadingScreen();
	}

	@Override
	public void dispose() {
		super.dispose();
		log.i("dispose");
	}

	@Override
	public void resume() {
		super.resume();
		AssetManager manager = mAssets.getAssetManager();
		log.i("resume: assetManager=%h", manager);
		log.i("resume: assetManager.getProgress()=%f", manager.getProgress());
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

	public void setScreenAndDispose(Screen screen) {
		Screen old = getScreen();
		log.i("setScreenAndDispose old=%s new=%s", old, screen);
		if (old != null) {
			old.dispose();
		}
		setScreen(screen);
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

	private static FileLogPrinter createFileLogPrinter() {
		FileHandle cacheDir = FileUtils.getCacheDir("burgerparty");
		if (cacheDir == null) {
			Gdx.app.error("createFileLogPrinter", "Could not create cache dir");
			return null;
		}
		FileHandle logHandle = cacheDir.child("burgerparty.log");
		FileLogPrinter.rotate(logHandle, 6);
		return new FileLogPrinter(logHandle);
	}

	private void setupLog() {
		NLog.Printer printer = createFileLogPrinter();
		if (printer == null) {
			printer = new NLog.GdxPrinter();
		}
		NLog.init(printer, "BP");
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}

	private void setupUniverse() {
		mUniverse.saveRequested.connect(mHandlers, new Signal0.Handler() {
			@Override
			public void handle() {
				saveLevelProgress();
			}
		});
		UniverseLoader loader = new UniverseLoader();
		loader.run(mUniverse);
		assert(mUniverse.getWorlds().size > 0);

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

	public MusicController getMusicController() {
		return mMusicController;
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
			setScreenAndDispose(screen);
		} else {
			doStartLevel();
		}
	}

	public void startSandBox() {
		mMusicController.fadeOut();
		setScreenAndDispose(new SandBoxGameScreen(this));
	}

	private void showLoadingScreen() {
		LoadingScreen screen = new LoadingScreen(mAssets.getAssetManager());
		screen.ready.connect(mHandlers, new Signal0.Handler() {
			@Override
			public void handle() {
				finishLoad();
			}
		});
		setScreenAndDispose(screen);
	}

	private void finishLoad() {
		mAssets.finishLoad();
		Music music = mAssets.getMusic();
		music.setLooping(true);
		mMusicController.setMusic(music);
		mMusicController.play();
		setupAnimScriptLoader();
		setupUniverse();
		showStartScreen();
	}

	public void showWorldListScreen() {
		mMusicController.play();
		setScreenAndDispose(new WorldListScreen(this));
	}

	public void showStartScreen() {
		mMusicController.play();
		setScreenAndDispose(new StartScreen(this));
	}

	public void showLevelListScreen(int worldIndex) {
		mMusicController.play();
		setScreenAndDispose(new LevelListScreen(this, worldIndex));
	}

	public void showAboutScreen() {
		mMusicController.play();
		setScreenAndDispose(new AboutScreen(this));
	}

	public void showCheatScreen() {
		mMusicController.play();
		setScreenAndDispose(new CheatScreen(this));
	}

	private void doStartLevel() {
		Level level = mUniverse.get(mLevelWorldIndex).getLevel(mLevelIndex);
		setScreenAndDispose(new GameScreen(this, level));
	}

	public void startMiniGame(final int worldIndex) {
		mMusicController.fadeOut();
		MiniGame game = null;
		switch (worldIndex) {
		case 0:
			game = new BurgerCopterMiniGame(mAssets, this);
			break;
		case 1:
			game = new BurgerVadersMiniGame(mAssets, this);
			break;
		case 2:
			game = new BurgerjeweledMiniGame(mAssets, this);
			break;
		default:
			throw new RuntimeException("No minigame for world " + (worldIndex + 1));
		}
		game.exiting.connect(mHandlers, new Signal0.Handler() {
			@Override
			public void handle() {
				showLevelListScreen(worldIndex);
			}
		});
		game.showStartScreen();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		mWidth = width;
		mHeight = height;
	}

	public int getWidth() {
		return mWidth;
	}

	public int getHeight() {
		return mHeight;
	}

	public Preferences getPreferences() {
		return Gdx.app.getPreferences("burgerparty");
	}

	private int mWidth = 0;
	private int mHeight = 0;
	private static NLog log;
}
