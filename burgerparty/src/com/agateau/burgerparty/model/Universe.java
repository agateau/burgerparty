package com.agateau.burgerparty.model;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.utils.Array;

/**
 * Knows all the LevelWorld instance of the game
 */
public class Universe {
	public static final int SANDBOX_MIN_STAR_COUNT = 4;

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
		int value = mLevelWorlds.get(world).getLevel(level).score;
		return Math.max(value, 0);
	}

	public int getStarCount() {
		int stars = 0;
		for (LevelWorld world: mLevelWorlds) {
			for (Level level: world.getLevels()) {
				stars += level.getStars();
			}
		}
		return stars;
	}

	public Set<MealItem> getKnownItems() {
		Set<MealItem> set = new HashSet<MealItem>();
		for (LevelWorld world: mLevelWorlds) {
			for (int levelIndex = 0; levelIndex < world.getLevelCount(); ++levelIndex) {
				Level level = world.getLevel(levelIndex);
				if (level.score >= Level.SCORE_PLAYED) {
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

	Array<LevelWorld> mLevelWorlds = new Array<LevelWorld>();
}
