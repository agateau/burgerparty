package com.agateau.burgerparty.utils;

import static com.agateau.burgerparty.utils.I18n.trn;

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
        return description + " " + trn("1 remaining.", "%n remaining.", mMinValue - mStat.getValue());
    }
}
