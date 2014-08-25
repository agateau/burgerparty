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
import com.agateau.burgerparty.utils.IntegerListGameStat;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.StringListGameStat;

import static com.greenyetilab.linguaj.Translator.tr;
import static com.greenyetilab.linguaj.Translator.trn;

public class BurgerPartyGameStats {
    private HashSet<Object> mHandlers = new HashSet<Object>();

    public final CounterGameStat mealServedCount = new CounterGameStat();
    public final CounterGameStat levelPlayedCount = new CounterGameStat();
    public final StringListGameStat morningPlayDates = new StringListGameStat();
    public final StringListGameStat eveningPlayDates = new StringListGameStat();
    public final IntegerListGameStat distinctSandBoxMeals = new IntegerListGameStat();

    public final CounterAchievement sandBoxAchievement;

    public final AchievementManager manager = new AchievementManager();

    private final GameStatManager mGameStatManager = new GameStatManager();

    private final static int CLOSE_CALL_COUNT = 3;
    private final static int CREATIVE_MEAL_COUNT = 10;
    private Achievement mCloseCall;
    private Achievement mMorningGamer;
    private Achievement mEveningGamer;
    private Achievement mCreative;

    public BurgerPartyGameStats(Universe universe) {
        mGameStatManager.add("levelPlayedCount", levelPlayedCount);
        mGameStatManager.add("morningPlayDates", morningPlayDates);
        mGameStatManager.add("eveningPlayDates", eveningPlayDates);
        mGameStatManager.add("mealServedCount", mealServedCount);
        mGameStatManager.add("distinctSandBoxMeals", distinctSandBoxMeals);

        mGameStatManager.setFileHandle(FileUtils.getUserWritableFile("gamestats.xml"));
        mGameStatManager.load();

        int count = 50;
        CounterAchievement achievement = new CounterAchievement("burger-apprentice", tr("Burger Apprentice"), trn("ignore-n-burgers", "Serve %# burgers.", count));
        achievement.init(mealServedCount, count);
        manager.add(achievement);

        count = 100;
        achievement = new CounterAchievement("burger-master", tr("Burger Master"), trn("ignore-n-burgers", "Serve %# burgers.", count));
        achievement.init(mealServedCount, count);
        manager.add(achievement);

        count = 200;
        achievement = new CounterAchievement("burger-god", tr("Burger God"), trn("ignore-n-burgers", "Serve %# burgers.", count));
        achievement.init(mealServedCount, count);
        manager.add(achievement);

        count = 4;
        sandBoxAchievement = new CounterAchievement("sandbox", tr("Practice Area"), trn("ignore-practice", "Collect %# stars to unlock the practice area.", count));
        sandBoxAchievement.init(universe.starCount, count);
        manager.add(sandBoxAchievement);

        count = 36;
        achievement = new CounterAchievement("star-collector", tr("Star Collector"), trn("ignore-collect", "Collect %# stars.", count));
        achievement.init(universe.starCount, count);
        manager.add(achievement);

        count = 40;
        achievement = new CounterAchievement("fan", tr("Fan"), trn("ignore-fan", "Play %# levels.", count));
        achievement.init(levelPlayedCount, count);
        manager.add(achievement);

        mCloseCall = new Achievement("close-call", tr("Close Call"), trn("ignore-close-call", "Finish a level with less than %# seconds left.", CLOSE_CALL_COUNT + 1));
        manager.add(mCloseCall);

        count = 4;
        mMorningGamer = new Achievement("morning-gamer", tr("Morning Gamer"), trn("ignore-morning", "Start a game between 7AM and 10AM for %# days.", count));
        manager.add(mMorningGamer);

        mEveningGamer = new Achievement("evening-gamer", tr("Evening Gamer"), trn("ignore-evening", "Start a game between 7PM and 11PM for %# days.", count));
        manager.add(mEveningGamer);

        for (int index = 0, n = universe.getWorlds().size; index < n; ++index) {
            manager.add(new AllStarsAchievement(universe, index));
        }

        for (int index = 0, n = universe.getWorlds().size; index < n; ++index) {
            manager.add(new PerfectAchievement(universe, index));
        }

        mCreative = new Achievement("creative", tr("Creative"), trn("ignore-creative", "Create %# different burgers in the practice area.", CREATIVE_MEAL_COUNT));
        manager.add(mCreative);

        manager.setFileHandle(FileUtils.getUserWritableFile("achievements.xml"));
        manager.load();
    }

    public void onLevelStarted(final World world) {
        world.levelFinished.connect(mHandlers, new Signal0.Handler() {
            public void handle() {
                if (world.getRemainingSeconds() <= CLOSE_CALL_COUNT) {
                    mCloseCall.unlock();
                }
            }
        });
        levelPlayedCount.increase();
        updateDateGameStats();
    }

    public void onSandBoxMealDelivered(int mealHashCode) {
        if (mCreative.isUnlocked()) {
            return;
        }
        if (distinctSandBoxMeals.contains(mealHashCode)) {
            return;
        }
        distinctSandBoxMeals.add(mealHashCode);
        if (distinctSandBoxMeals.getCount() >= CREATIVE_MEAL_COUNT) {
            mCreative.unlock();
        }
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
