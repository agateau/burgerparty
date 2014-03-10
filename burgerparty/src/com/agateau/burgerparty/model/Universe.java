package com.agateau.burgerparty.model;

import java.util.HashSet;
import java.util.Set;

import com.agateau.burgerparty.utils.Signal0;
import com.badlogic.gdx.utils.Array;

/**
 * Knows all the LevelWorld instances of the game
 */
public class Universe {
	public static final int SANDBOX_MIN_STAR_COUNT = 4;

	public Signal0 saveRequested = new Signal0();
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

	public int getStarCount() {
		int stars = 0;
		for (LevelWorld world: mLevelWorlds) {
			for (Level level: world.getLevels()) {
				stars += level.getStarCount();
			}
		}
		return stars;
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

	public Set<String> updateLevel(int worldIndex, int levelIndex, int score, int levelStarCount, boolean perfect) {
		int oldStarCount = getStarCount();
		// FIXME: Temporary implementation
		Set<String> unlockedThings = new HashSet<String>();

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
		} else if (worldIndex < mLevelWorlds.size - 1){
			next = mLevelWorlds.get(worldIndex + 1).getLevel(0);
		}
		if (next != null && next.isLocked()) {
			next.unlock();
		}

		// Have we just unlocked the sandbox?
		int starCount = getStarCount();
		if (oldStarCount < SANDBOX_MIN_STAR_COUNT && SANDBOX_MIN_STAR_COUNT <= starCount) {
			unlockedThings.add("Sandbox");
		}
		saveRequested.emit();
		return unlockedThings;
	}
}
