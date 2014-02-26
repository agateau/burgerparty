package com.agateau.burgerparty.model;

import java.util.HashSet;

import com.agateau.burgerparty.utils.Signal0;

public abstract class Achievement {
	private HashSet<Object> mHandlers = new HashSet<Object>();

	public Signal0 unlocked = new Signal0();

	private String mId;
	private String mTitle;
	private String mDescription;
	private boolean mUnlocked = false;

	public Achievement(String id, String title, String description) {
		mId = id;
		mTitle = title;
		mDescription = description;
	}

	public void setAlreadyUnlocked(boolean value) {
		mUnlocked = value;
	}

	public String getId() {
		return mId;
	}

	public String getTitle() {
		return mTitle;
	}

	public String getDescription() {
		return mDescription;
	}

	public boolean isUnlocked() {
		return mUnlocked;
	}

	public abstract void update();

	public void onLevelStarted(World world) {}

	public void addDependentGameStat(GameStat stat) {
		stat.changed.connect(mHandlers, new Signal0.Handler() {
			@Override
			public void handle() {
				update();
			}
		});
	}

	protected void unlock() {
		if (mUnlocked) {
			return;
		}
		mUnlocked = true;
		unlocked.emit();
	}
}
