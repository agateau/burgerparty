package com.agateau.burgerparty.utils;


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

}
