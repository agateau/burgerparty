package com.agateau.burgerparty.model;

public class LevelResult {
    final private Level mLevel;
    final private int mScore;
    final private int mCoinCount;
    final private int mMaximumCoinCount;
    final private int mStarCost;
    final private int mRemainingSeconds;

    public LevelResult(Level level, int score, int coinCount, int maxCoinCount, int starCost, int remainingSeconds) {
        mLevel = level;
        mScore = score;
        mCoinCount = coinCount;
        mMaximumCoinCount = maxCoinCount;
        mStarCost = starCost;
        mRemainingSeconds = remainingSeconds;
    }

    public Level getLevel() {
        return mLevel;
    }

    public int getScore() {
        return mScore;
    }

    public int getCoinCount() {
        return mCoinCount;
    }

    public int getStarCost() {
        return mStarCost;
    }

    public int getMaximumCoinCount() {
        return mMaximumCoinCount;
    }

    public int getRemainingSeconds() {
        return mRemainingSeconds;
    }
}
