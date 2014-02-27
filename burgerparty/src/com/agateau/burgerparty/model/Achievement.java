package com.agateau.burgerparty.model;

import com.agateau.burgerparty.utils.Signal0;

public class Achievement {
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

	public void onLevelStarted(World world) {}

	protected void unlock() {
		if (mUnlocked) {
			return;
		}
		mUnlocked = true;
		unlocked.emit();
	}
}
