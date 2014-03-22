package com.agateau.burgerparty.utils;

import static com.greenyetilab.linguaj.Translator.trn;

public class CounterAchievement extends GameStatAchievement {
    private CounterGameStat mStat;
    private int mMinValue;

    public CounterAchievement(String id, String title, String description) {
        super(id, title, description);
    }

    public void init(CounterGameStat stat, int minValue) {
        addDependentGameStat(stat);
        mStat = stat;
        mMinValue = minValue;
        setAlreadyUnlocked(mStat.getValue() >= mMinValue);
    }

    @Override
    public void update() {
        setUnlocked(mStat.getValue() >= mMinValue);
    }

    public String getDescription() {
        String description = super.getDescription();
        if (isUnlocked()) {
            return description;
        }
        return description + " " + trn("1 remaining.", "%# remaining.", mMinValue - mStat.getValue());
    }
}
