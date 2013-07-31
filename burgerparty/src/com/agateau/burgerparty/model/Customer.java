package com.agateau.burgerparty.model;

import com.agateau.burgerparty.utils.Signal0;
import com.badlogic.gdx.utils.Timer;

public class Customer {
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

	public void setState(State state) {
		assert(state != State.WAITING);
		if (state == State.ACTIVE) {
			assert(mState == State.WAITING);
			mState = state;
			scheduleMoodChange();
			return;
		}
		if (state == State.SERVED) {
			assert(mState == State.ACTIVE);
			mState = state;
			mMoodTimer.stop();
			return;
		}
	}

	private void scheduleMoodChange() {
		mMoodTimer.scheduleTask(new Timer.Task() {
			@Override
			public void run() {
				degradeMood();
			}
		}, MOOD_CHANGE_DELAY);
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
	private Mood mMood = Mood.HAPPY;
	private State mState = State.WAITING;
	private Timer mMoodTimer = new Timer();

	private static final float MOOD_CHANGE_DELAY = 5f;
}
