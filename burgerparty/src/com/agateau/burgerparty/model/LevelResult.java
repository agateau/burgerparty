package com.agateau.burgerparty.model;

public class LevelResult {
	public LevelResult(Level level, int score, int remainingSeconds) {
		mLevel = level;
		mScore = score;
		mRemainingSeconds = remainingSeconds;
	}

	public Level getLevel() {
		return mLevel;
	}

	public int getScore() {
		return mScore;
	}

	public int getRemainingSeconds() {
		return mRemainingSeconds;
	}

	private Level mLevel;
	private int mScore;
	private int mRemainingSeconds;
}
