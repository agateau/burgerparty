package com.agateau.burgerparty.model;

import com.agateau.burgerparty.utils.FixedTimer;
import com.agateau.burgerparty.utils.NLog;
import com.agateau.burgerparty.utils.Signal0;

public class Customer {
    public Signal0 moodChanged = new Signal0();

    private final String mType;
    private final int mBurgerSize;
    private float mMoodDelay;
    private Mood mMood = Mood.HAPPY;
    private State mState = State.WAITING;
    private FixedTimer mMoodTimer = new FixedTimer();
    private Difficulty mDifficulty;

    public enum Mood {
        HAPPY("happy"),
        NEUTRAL("neutral"),
        ANGRY("angry");

        Mood(String text) {
            mText = text;
        }

        public String toString() {
            return mText;
        }

        private final String mText;

        public static final Mood[] moods = {HAPPY, NEUTRAL, ANGRY};

        public static Mood fromString(String str) {
            for (Mood mood: moods) {
                if (mood.mText.equals(str)) {
                    return mood;
                }
            }
            throw new RuntimeException("Unknown mood: " + str);
        }
    };

    public enum State {
        WAITING,
        ACTIVE,
        SERVED
    }

    public Customer(String type, int burgerSize) {
        mType = type;
        mBurgerSize = burgerSize;
    }

    public String getType() {
        return mType;
    }

    public Mood getMood() {
        return mMood;
    }

    public int getBurgerSize() {
        return mBurgerSize;
    }

    /*
     * Only useful for tools
     */
    public void setMood(Mood mood) {
        if (mMood == mood) {
            return;
        }
        mMood = mood;
        moodChanged.emit();
    }

    public State getState() {
        return mState;
    }

    public void markActive(int itemCount) {
        assert(mState == State.WAITING);
        mState = State.ACTIVE;
        mMoodDelay = mDifficulty.moodMinSeconds + itemCount * mDifficulty.moodSecondPerItem;
        NLog.i("itemCount=%d => delay=%dms", itemCount, (int)(mMoodDelay * 1000));
        scheduleMoodChange();
    }

    public void markServed() {
        assert(mState == State.ACTIVE);
        mState = State.SERVED;
        mMoodTimer.stop();
    }

    public void pause() {
        mMoodTimer.stop();
    }

    public void resume() {
        if (mState == State.ACTIVE && mMood != Mood.ANGRY) {
            // HACK: The content of the `if` block should be mMoodTimer.start()
            // but stopping a timer does not suspend its time :/ Recreating the
            // timer is a workaround, but it could be used to cheat since it
            // essentially resets the mood timer.
            mMoodTimer = new FixedTimer();
            scheduleMoodChange();
        }
    }

    private void scheduleMoodChange() {
        mMoodTimer.scheduleTask(new FixedTimer.Task() {
            @Override
            public void run() {
                degradeMood();
            }
        }, mMoodDelay);
    }

    private void degradeMood() {
        assert(mMood != Mood.ANGRY);
        if (mMood == Mood.HAPPY) {
            mMood = Mood.NEUTRAL;
            scheduleMoodChange();
        } else {
            mMood = Mood.ANGRY;
        }
        moodChanged.emit();
    }

    public void setDifficulty(Difficulty difficulty) {
        mDifficulty = difficulty;
    }
}
