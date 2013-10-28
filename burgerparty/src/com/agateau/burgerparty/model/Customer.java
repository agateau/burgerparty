package com.agateau.burgerparty.model;

import com.agateau.burgerparty.utils.Signal0;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;

public class Customer {
	private static final float MOOD_MIN_MS = 0.5f;
	private static final float MODD_MS_PER_ITEM = 0.8f;

	public Signal0 moodChanged = new Signal0();

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

	public Customer(String type) {
		mType = type;
	}

	public String getType() {
		return mType;
	}

	public Mood getMood() {
		return mMood;
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
		mMoodDelay = MOOD_MIN_MS + itemCount * MODD_MS_PER_ITEM;
		Gdx.app.log("Customer.markActive", "itemCount=" + itemCount + " => delay=" + (mMoodDelay * 1000));
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
			mMoodTimer = new Timer();
			scheduleMoodChange();
		}
	}

	private void scheduleMoodChange() {
		mMoodTimer.scheduleTask(new Timer.Task() {
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

	private String mType;
	private float mMoodDelay;
	private Mood mMood = Mood.HAPPY;
	private State mState = State.WAITING;
	private Timer mMoodTimer = new Timer();
}
