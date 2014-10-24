package com.agateau.burgerparty.model;

import com.agateau.burgerparty.utils.Signal0;


public class Achievement {
    public Signal0 unlocked = new Signal0();
    public Signal0 changed = new Signal0();

    private String mId;
    private String mTitle;
    private String mDescription;
    private boolean mUnlocked = false;
    private boolean mSeen = false;

    public Achievement(String id, String title, String description) {
        mId = id;
        mTitle = title;
        mDescription = description;
    }

    public void setAlreadyUnlocked(boolean value) {
        mUnlocked = value;
    }

    public void setAlreadySeen(boolean value) {
        mSeen = value;
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

    public String getIconName() {
        return getId();
    }

    public boolean isUnlocked() {
        return mUnlocked;
    }

    public void unlock() {
        if (mUnlocked) {
            return;
        }
        mUnlocked = true;
        changed.emit();
        unlocked.emit();
    }

    public boolean hasBeenSeen() {
        return mSeen;
    }

    public void markSeen() {
        assert(mUnlocked);
        mSeen = true;
        changed.emit();
    }
}
