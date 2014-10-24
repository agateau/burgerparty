package com.agateau.burgerparty.model;

import java.util.HashSet;
import java.util.Set;

import com.agateau.burgerparty.Constants;
import com.agateau.burgerparty.utils.CounterGameStat;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.Signal0;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

/**
 * Knows all the LevelWorld instances of the game
 */
public class Universe {
    private static final String OLD_PROGRESS_FILE = "progress.xml";
    private static final String PROGRESS_FILE = "progress-%s.xml";

    public Signal0 saveRequested = new Signal0();
    public final CounterGameStat starCount = new CounterGameStat();

    private Array<LevelWorld> mLevelWorlds = new Array<LevelWorld>();

    public void addWorld(LevelWorld world) {
        mLevelWorlds.add(world);
    }

    public Array<LevelWorld> getWorlds() {
        return mLevelWorlds;
    }

    public LevelWorld get(int index) {
        return mLevelWorlds.get(index);
    }

    public int getHighScore(int world, int level) {
        return mLevelWorlds.get(world).getLevel(level).getScore();
    }

    public void updateStarCount() {
        int stars = 0;
        for (LevelWorld world: mLevelWorlds) {
            for (Level level: world.getLevels()) {
                stars += level.getStarCount();
            }
        }
        this.starCount.setValue(stars);
    }

    public Set<MealItem> getKnownItems() {
        Set<MealItem> set = new HashSet<MealItem>();
        for (LevelWorld world: mLevelWorlds) {
            for (int levelIndex = 0; levelIndex < world.getLevelCount(); ++levelIndex) {
                Level level = world.getLevel(levelIndex);
                if (level.hasBeenPlayed()) {
                    set.addAll(level.getKnownItems());
                }
            }
        }
        // This can happen when it is the first time the game is played
        if (set.isEmpty()) {
            set.addAll(mLevelWorlds.get(0).getLevel(0).getKnownItems());
        }
        return set;
    }

    public void updateLevel(int worldIndex, int levelIndex, int score, int levelStarCount, boolean perfect) {
        LevelWorld currentWorld = mLevelWorlds.get(worldIndex);
        Level currentLevel = currentWorld.getLevel(levelIndex);
        if (score > currentLevel.getScore()) {
            currentLevel.setScore(score);
        }
        if (levelStarCount > currentLevel.getStarCount()) {
            currentLevel.setStarCount(levelStarCount);
        }
        if (perfect) {
            currentLevel.markPerfect();
        }

        // Unlock next level if necessary
        Level next = null;
        if (levelIndex < currentWorld.getLevelCount() - 1) {
            next = currentWorld.getLevel(levelIndex + 1);
        } else if (worldIndex < mLevelWorlds.size - 1) {
            next = mLevelWorlds.get(worldIndex + 1).getLevel(0);
        }
        if (next != null && next.isLocked()) {
            next.unlock();
        }

        updateStarCount();
        saveRequested.emit();
    }

    public void loadProgress(Difficulty difficulty) {
        resetProgress();
        String name = getProgressFileName(difficulty);
        FileHandle handle = FileUtils.getUserWritableFile(name);
        if (handle.exists()) {
            ProgressIO progressIO = new ProgressIO(mLevelWorlds);
            progressIO.load(handle);
        }
        updateStarCount();
    }

    public void saveProgress(Difficulty difficulty) {
        String name = getProgressFileName(difficulty);
        FileHandle handle = FileUtils.getUserWritableFile(name);
        ProgressIO progressIO = new ProgressIO(mLevelWorlds);
        progressIO.save(handle);
    }

    private void resetProgress() {
        for (LevelWorld world: mLevelWorlds) {
            for (int levelIndex = 0; levelIndex < world.getLevelCount(); ++levelIndex) {
                Level level = world.getLevel(levelIndex);
                level.lock();
            }
        }
        // At least, unlock first level
        mLevelWorlds.get(0).getLevel(0).unlock();
    }

    public static void migrateOldProgress() {
        FileHandle oldHandle = FileUtils.getUserWritableFile(OLD_PROGRESS_FILE);
        if (!oldHandle.exists()) {
            return;
        }
        String name = getProgressFileName(Constants.NORMAL);
        FileHandle handle = FileUtils.getUserWritableFile(name);
        oldHandle.moveTo(handle);
    }

    private static String getProgressFileName(Difficulty difficulty) {
        return String.format(PROGRESS_FILE, difficulty.name);
    }
}
