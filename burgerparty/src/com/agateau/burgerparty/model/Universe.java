package com.agateau.burgerparty.model;

import java.util.HashSet;
import java.util.Set;

import com.agateau.burgerparty.utils.CounterGameStat;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.Signal0;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

/**
 * Knows all the LevelWorld instances of the game
 */
public class Universe {
    private static final String PROGRESS_FILE = "progress%s.xml";

    public Signal0 saved = new Signal0();
    public final CounterGameStat starCount = new CounterGameStat();

    private final Difficulty mDifficulty;
    private Array<LevelWorld> mLevelWorlds = new Array<LevelWorld>();

    public Universe(Difficulty difficulty) {
        mDifficulty = difficulty;
    }

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

    public Difficulty getDifficulty() {
        return mDifficulty;
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
        saveProgress();
    }

    public void loadProgress() {
        resetProgress();
        String name = getProgressFileName(mDifficulty);
        FileHandle handle = FileUtils.getUserWritableFile(name);
        if (handle.exists()) {
            ProgressIO progressIO = new ProgressIO(mLevelWorlds);
            progressIO.load(handle);
        }
        updateStarCount();
    }

    public void saveProgress() {
        String name = getProgressFileName(mDifficulty);
        FileHandle handle = FileUtils.getUserWritableFile(name);
        ProgressIO progressIO = new ProgressIO(mLevelWorlds);
        progressIO.save(handle);
        saved.emit();
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

    private static String getProgressFileName(Difficulty difficulty) {
        return String.format(PROGRESS_FILE, difficulty.suffix);
    }
}
