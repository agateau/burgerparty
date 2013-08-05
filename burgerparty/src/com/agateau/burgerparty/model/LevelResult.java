package com.agateau.burgerparty.model;

public class LevelResult {
	public LevelResult(Level level, int score, int remainingSeconds) {
		mLevel = level;
		mScore = score;
		mRemainingSeconds = remainingSeconds;
	}

	public int computeStars() {
		// FIXME: Get star minimum scores from mLevel
		if (mScore > 10000) {
			return 3;
		} else if (mScore > 5000) {
			return 2;
		} else {
			return 1;
		}
	}

	public int getScore() {
		return mScore;
	}

	private Level mLevel;
	private int mScore;
	private int mRemainingSeconds;
}
