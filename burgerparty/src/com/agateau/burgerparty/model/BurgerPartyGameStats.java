package com.agateau.burgerparty.model;

import com.agateau.burgerparty.utils.CounterGameStat;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.GameStatManager;

public class BurgerPartyGameStats {
	public final CounterGameStat mealServedCount;

	public final AchievementManager manager = new AchievementManager();

	private final GameStatManager mGameStatManager = new GameStatManager();

	public BurgerPartyGameStats() {
		mealServedCount = new CounterGameStat("mealServedCount");
		mGameStatManager.add(mealServedCount);
		mGameStatManager.setFileHandle(FileUtils.getUserWritableFile("gamestats.xml"));
		mGameStatManager.load();

		CounterAchievement achievement = new CounterAchievement("burger-master", "Burger Master", "Serve 10 burgers");
		achievement.init(mealServedCount, 10);
		manager.add(achievement);

		manager.setFileHandle(FileUtils.getUserWritableFile("achievements.xml"));
		manager.load();
	}
}
