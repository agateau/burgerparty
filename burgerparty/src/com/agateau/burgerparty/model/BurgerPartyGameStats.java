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
import static com.agateau.burgerparty.utils.I18n.trn;

public class BurgerPartyGameStats {
    private HashSet<Object> mHandlers = new HashSet<Object>();

    public final CounterGameStat mealServedCount = new CounterGameStat();
    public final StringListGameStat morningPlayDates = new StringListGameStat();
    public final StringListGameStat eveningPlayDates = new StringListGameStat();

    public final CounterAchievement sandBoxAchievement;

    public final AchievementManager manager = new AchievementManager();

    private final GameStatManager mGameStatManager = new GameStatManager();

    private final static int CLOSE_CALL_COUNT = 3;
    private Achievement mCloseCall;
    private Achievement mMorningGamer;
    private Achievement mEveningGamer;

    public BurgerPartyGameStats(Universe universe) {
        mGameStatManager.add("morningPlayDates", morningPlayDates);
        mGameStatManager.add("eveningPlayDates", eveningPlayDates);
        mGameStatManager.add("mealServedCount", mealServedCount);

        mGameStatManager.setFileHandle(FileUtils.getUserWritableFile("gamestats.xml"));
        mGameStatManager.load();

        int count = 50;
        CounterAchievement achievement = new CounterAchievement("burger-master", _("Burger Master"), trn("ignore-n-burgers", "Serve %n burgers.", count));
        achievement.init(mealServedCount, count);
        manager.add(achievement);

        count = 100;
        achievement = new CounterAchievement("burger-god", _("Burger God"), trn("ignore-n-burgers", "Serve %n burgers.", count));
        achievement.init(mealServedCount, count);
        manager.add(achievement);

        count = 4;
        sandBoxAchievement = new CounterAchievement("sandbox", _("Practice Area"), trn("ignore-practice", "Collect %n stars to unlock the practice area.", count));
        sandBoxAchievement.init(universe.starCount, count);
        manager.add(sandBoxAchievement);

        count = 36;
        achievement = new CounterAchievement("star-collector", _("Star Collector"), trn("ignore-collect", "Collect %n stars.", count));
        achievement.init(universe.starCount, count);
        manager.add(achievement);

        mCloseCall = new Achievement("close-call", _("Close Call"), trn("ignore-close-call", "Finish a level with %n seconds left.", CLOSE_CALL_COUNT));
        manager.add(mCloseCall);

        count = 4;
        mMorningGamer = new Achievement("morning-gamer", _("Morning Gamer"), trn("ignore-morning", "Start a game between 7AM and 10AM for %n days.", count));
        manager.add(mMorningGamer);

        mEveningGamer = new Achievement("evening-gamer", _("Evening Gamer"), trn("ignore-evening", "Start a game between 7PM and 11PM for %n days.", count));
        manager.add(mEveningGamer);

        manager.setFileHandle(FileUtils.getUserWritableFile("achievements.xml"));
        manager.load();
    }

    public void onLevelStarted(final World world) {
        world.levelFinished.connect(mHandlers, new Signal0.Handler() {
            public void handle() {
                if (world.getRemainingSeconds() <= CLOSE_CALL_COUNT) {
                    mCloseCall.setUnlocked(true);
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
            achievement.setUnlocked(true);
        }
    }
}
