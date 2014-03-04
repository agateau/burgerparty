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
	}

	@Override
	public void update() {
		if (mStat.getValue() >= mMinValue) {
			unlock();
		}
	}

	public String getDescription() {
		String description = super.getDescription();
		if (isUnlocked()) {
			return description;
		}
		return description + "\n" + trn("1 remaining.", "%n remaining.", mMinValue - mStat.getValue());
	}
}
