package com.agateau.burgerparty;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;

import com.agateau.burgerparty.model.AdController;
import com.agateau.burgerparty.model.BurgerPartyGameStats;
import com.agateau.burgerparty.model.Level;
import com.agateau.burgerparty.model.Universe;
import com.agateau.burgerparty.model.UniverseLoader;
import com.agateau.burgerparty.model.MealItemDb;
import com.agateau.burgerparty.model.ProgressIO;
import com.agateau.burgerparty.screens.AboutScreen;
import com.agateau.burgerparty.screens.AchievementsScreen;
import com.agateau.burgerparty.screens.CheatScreen;
import com.agateau.burgerparty.screens.GameScreen;
import com.agateau.burgerparty.screens.LevelListScreen;
import com.agateau.burgerparty.screens.LoadingScreen;
import com.agateau.burgerparty.screens.NewItemScreen;
import com.agateau.burgerparty.screens.NewWorldScreen;
import com.agateau.burgerparty.screens.SandBoxGameScreen;
import com.agateau.burgerparty.screens.StartScreen;
import com.agateau.burgerparty.screens.WorldListScreen;
import com.agateau.burgerparty.utils.Achievement;
import com.agateau.burgerparty.utils.AnimScriptLoader;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.MusicController;
import com.agateau.burgerparty.utils.NLog;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;
import com.agateau.burgerparty.utils.StringArgumentDefinition;
import com.agateau.burgerparty.view.AchievementViewController;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;

public class BurgerPartyGame extends Game {
    private static final String PROGRESS_FILE = "progress.xml";

    private HashSet<Object> mHandlers = new HashSet<Object>();

    private Assets mAssets;
    private MusicController mMusicController;
    private Universe mUniverse = new Universe();
    private int mLevelWorldIndex = 0;
    private int mLevelIndex = 0;
    private AdController mAdController;
    private BurgerPartyGameStats mGameStats;
    private AchievementViewController mAchievementViewController = new AchievementViewController(this);
    private int mWidth = 0;
    private int mHeight = 0;
    private boolean mWaitInLoadingScreen = false;

    private Difficulty mDifficulty = Constants.NORMAL;

    @Override
    public void create() {
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(Calendar.getInstance().getTime());
        NLog.i("date=%s", timeStamp);

        mMusicController = new MusicController(getPreferences());
        mAssets = new Assets(mMusicController);
        initMealItemDb();
        Gdx.input.setCatchBackKey(true);
        showLoadingScreen();
    }

    public void initMealItemDb() {
        MealItemDb.getInstance().load(Gdx.files.internal("mealitems.xml"));
    }

    @Override
    public void dispose() {
        NLog.i("");
        super.dispose();
        mAssets.dispose();
        mAssets = null;
    }

    @Override
    public void pause() {
        super.pause();
        NLog.i("");
    }

    @Override
    public void resume() {
        super.resume();
        NLog.i("");
        AssetManager manager = mAssets.getAssetManager();
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

    public void waitInLoadingScreen() {
        mWaitInLoadingScreen = true;
    }

    public void setScreenAndDispose(Screen screen) {
        Screen old = getScreen();
        NLog.i("%s => %s",
              old == null ? "(null)" : old.getClass().getSimpleName(),
              screen.getClass().getSimpleName());
        if (old != null) {
            old.dispose();
        }
        setScreen(screen);
    }

    void setupAnimScriptLoader() {
        AnimScriptLoader loader = mAssets.getAnimScriptLoader();
        loader.registerMemberMethod("play", mAssets.getSoundAtlas(), "createPlayAction", new StringArgumentDefinition());
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
        mUniverse.updateStarCount();
    }

    private void setupAchievements() {
        mGameStats = new BurgerPartyGameStats(mUniverse);
        mGameStats.manager.achievementUnlocked.connect(mHandlers, new Signal1.Handler<Achievement>() {
            @Override
            public void handle(Achievement achievement) {
                onAchievementUnlocked(achievement);
            }
        });
    }

    private void onAchievementUnlocked(Achievement achievement) {
        NLog.i("%s", achievement.getTitle());
        mAchievementViewController.show(achievement);
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

    public BurgerPartyGameStats getGameStats() {
        return mGameStats;
    }

    public void startLevel(int levelWorldIndex, int levelIndex) {
        NLog.i("%d-%d", levelWorldIndex + 1, levelIndex + 1);
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
                    showAd();
                }
            });
            setScreenAndDispose(screen);
        } else {
            showAd();
        }
    }

    public void startSandBox() {
        mMusicController.fadeOut();
        setScreenAndDispose(new SandBoxGameScreen(this));
    }

    private void showLoadingScreen() {
        LoadingScreen screen = new LoadingScreen(mAssets.getAssetManager());
        screen.setWaitForClick(mWaitInLoadingScreen);
        screen.ready.connect(mHandlers, new Signal0.Handler() {
            @Override
            public void handle() {
                finishLoad();
            }
        });
        setScreenAndDispose(screen);
    }

    private void finishLoad() {
        NLog.i("");
        mAssets.finishLoad();
        Music music = mAssets.getMusic();
        music.setLooping(true);
        mMusicController.setMusic(music);
        mMusicController.play();
        setupAnimScriptLoader();
        setupUniverse();
        setupAchievements();
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

    public void showNewWorldScreen(int worldIndex) {
        mMusicController.fadeOut();
        setScreenAndDispose(new NewWorldScreen(this, worldIndex));
    }

    public void showLevelListScreen(int worldIndex) {
        mMusicController.play();
        setScreenAndDispose(new LevelListScreen(this, worldIndex));
    }

    public void showAboutScreen() {
        mMusicController.play();
        setScreenAndDispose(new AboutScreen(this));
    }

    public void showAchievementsScreen() {
        mMusicController.play();
        setScreenAndDispose(new AchievementsScreen(this));
    }

    public void showCheatScreen() {
        mMusicController.play();
        setScreenAndDispose(new CheatScreen(this));
    }

    private void showAd() {
        mAdController.maybeShowAd(new Runnable() {
            @Override
            public void run() {
                doStartLevel();
            }
        });
    }

    private void doStartLevel() {
        NLog.i("%d-%d", mLevelWorldIndex + 1, mLevelIndex + 1);
        Level level = mUniverse.get(mLevelWorldIndex).getLevel(mLevelIndex);
        setScreenAndDispose(new GameScreen(this, level, mDifficulty ));
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

    public AdController getAdController() {
        assert(mAdController != null);
        return mAdController;
    }

    public void setAdSystem(AdSystem adSystem) {
        mAdController = new AdController(getPreferences(), adSystem);
    }

    public void setDifficulty(Difficulty difficulty) {
        mDifficulty = difficulty;
    }

    public Difficulty getDifficulty() {
        return mDifficulty;
    }
}
