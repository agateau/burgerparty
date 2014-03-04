package com.agateau.burgerparty.model;

import java.util.HashSet;

import com.agateau.burgerparty.utils.Achievement;
import com.agateau.burgerparty.utils.AchievementManager;
import com.agateau.burgerparty.utils.CounterAchievement;
import com.agateau.burgerparty.utils.CounterGameStat;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.GameStatManager;
import com.agateau.burgerparty.utils.Signal0;

import static com.agateau.burgerparty.utils.I18n._;

public class BurgerPartyGameStats {
	private HashSet<Object> mHandlers = new HashSet<Object>();

	public final CounterGameStat mealServedCount;

	public final AchievementManager manager = new AchievementManager();

	private final GameStatManager mGameStatManager = new GameStatManager();

	private Achievement mCloseCall;

	public BurgerPartyGameStats() {
		mealServedCount = new CounterGameStat("mealServedCount");
		mGameStatManager.add(mealServedCount);
		mGameStatManager.setFileHandle(FileUtils.getUserWritableFile("gamestats.xml"));
		mGameStatManager.load();

		CounterAchievement achievement = new CounterAchievement("burger-master", _("Burger Master"), _("Serve 50 burgers."));
		achievement.init(mealServedCount, 50);
		manager.add(achievement);

		achievement = new CounterAchievement("burger-god", _("Burger God"), _("Serve 100 burgers."));
		achievement.init(mealServedCount, 100);
		manager.add(achievement);

		mCloseCall = new Achievement("close-call", _("Close Call"), _("Finish a level with 3 seconds left."));
		manager.add(mCloseCall);

		manager.setFileHandle(FileUtils.getUserWritableFile("achievements.xml"));
		manager.load();
	}

	public void onLevelStarted(final World world) {
		world.levelFinished.connect(mHandlers, new Signal0.Handler() {
			public void handle() {
				if (world.getRemainingSeconds() <= 3) {
					mCloseCall.unlock();
				}
			}
		});
	}
}
