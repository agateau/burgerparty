package com.agateau.burgerparty;

import com.agateau.burgerparty.model.Difficulty;

public class Constants {
    public static final String VERSION = "1.1.0";

    public static final int LEVEL_PER_WORLD = 15;
    public static final int WORLD_COUNT = 3;

    public static final float SECOND_PER_MEALITEM = 0.6f;
    public static final float DIFFICULTY_STARTS[] = {2.4f, 1.45f, 1.1f};
    public static final float DIFFICULTY_SLOPES[] = {-1f, -0.4f, -0.3f};

    public static final int SCORE_BONUS_PER_REMAINING_SECOND = 100;

    public static final int BIG_BURGER_SIZE = 11;
    public static final int MED_BURGER_SIZE = 7;

    public static final int COMBO_SCORE = 1000;
    public static final int HAPPY_SCORE = 4000;
    public static final int NEUTRAL_SCORE = 2000;
    public static final int ANGRY_SCORE = 1000;

    public static final int HAPPY_COIN_COUNT = 3;
    public static final int NEUTRAL_COIN_COUNT = 2;
    public static final int ANGRY_COIN_COUNT = 1;

    public static final int HAPPY_EXTRA_SECS = 2;
    public static final int NEUTRAL_EXTRA_SECS = 1;
    public static final int ANGRY_EXTRA_SECS = 0;

    public static final int BIG_BURGER_EXTRA_SECS = 2;
    public static final int MED_BURGER_EXTRA_SECS = 1;

    public static Difficulty EASY = new Difficulty();
    public static Difficulty NORMAL = new Difficulty();
    public static Difficulty HARD = new Difficulty();

    static {
        EASY.suffix = "-easy";
        EASY.timeLimited = false;
        EASY.showArrow = true;

        NORMAL.suffix = "";
        NORMAL.timeLimited = true;
        NORMAL.showArrow = true;

        HARD.suffix = "-hard";
        HARD.timeLimited = true;
        HARD.showArrow = false;
    }
}
