package com.agateau.burgerparty.model;

import java.util.HashSet;

import com.agateau.burgerparty.model.World.Score;
import com.agateau.burgerparty.utils.Signal1;

public class BurgerPartyGameStats {
	private HashSet<Object> mHandlers = new HashSet<Object>();

	public final CounterGameStat mealServedCount;

	public final AchievementManager manager = new AchievementManager();
	
	public BurgerPartyGameStats() {
		mealServedCount = new CounterGameStat("mealServedCount") {
			@Override
			public void onLevelStarted(World world) {
				world.mealFinished.connect(mHandlers, new Signal1.Handler<Score>() {
					@Override
					public void handle(Score score) {
						increase();
					}
				});
			}
		};
		manager.addGameStat(mealServedCount);
	}
}
