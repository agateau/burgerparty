package com.agateau.burgerparty.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;

import com.agateau.burgerparty.utils.Achievement;
import com.agateau.burgerparty.utils.AchievementManager;
import com.agateau.burgerparty.utils.CounterAchievement;
import com.agateau.burgerparty.utils.CounterGameStat;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.GameStatManager;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.StringListGameStat;

import static com.agateau.burgerparty.utils.I18n._;

public class BurgerPartyGameStats {
    private HashSet<Object> mHandlers = new HashSet<Object>();

    public final CounterGameStat mealServedCount = new CounterGameStat("mealServedCount");
    public final StringListGameStat morningPlayDates = new StringListGameStat("morningPlayDates");
    public final StringListGameStat eveningPlayDates = new StringListGameStat("eveningPlayDates");

    public final AchievementManager manager = new AchievementManager();

    private final GameStatManager mGameStatManager = new GameStatManager();

    private Achievement mCloseCall;
    private Achievement mMorningGamer;
    private Achievement mEveningGamer;

    public BurgerPartyGameStats() {
        mGameStatManager.add(morningPlayDates);
        mGameStatManager.add(eveningPlayDates);
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

        mMorningGamer = new Achievement("morning-gamer", _("Morning Gamer"), _("Start a game between 7AM and 10AM for 4 days."));
        manager.add(mMorningGamer);

        mEveningGamer = new Achievement("evening-gamer", _("Evening Gamer"), _("Start a game between 7PM and 11PM for 4 days."));
        manager.add(mEveningGamer);

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

        updateDateGameStats();
    }

    static private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private void updateDateGameStats() {
        GregorianCalendar calendar = new GregorianCalendar();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String dateString = DATE_FORMAT.format(calendar.getTime());

        updateDateGameStat(hour, dateString, morningPlayDates, mMorningGamer, 7, 10);
        updateDateGameStat(hour, dateString, eveningPlayDates, mEveningGamer, 19, 23);
    }

    private void updateDateGameStat(int hour, String dateString, StringListGameStat stat, Achievement achievement, int minHour, int maxHour) {
        if (achievement.isUnlocked()) {
            return;
        }
        if (hour < minHour || hour >= maxHour) {
            return;
        }
        if (stat.contains(dateString)) {
            return;
        }
        stat.add(dateString);
        if (stat.getCount() >= 4) {
            achievement.unlock();
        }
    }
}
