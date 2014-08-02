package com.agateau.burgerparty.utils;

import com.badlogic.gdx.utils.TimeUtils;

public class TimeTracker {
    private static final long NANOSECS = 1000 * 1000 * 1000;
    private long mStartTime;
    private long mLastLogTime;

    public TimeTracker() {
        mStartTime = TimeUtils.nanoTime();
        mLastLogTime = mStartTime;
    }

    public String restart() {
        final long logTime = TimeUtils.nanoTime();
        final long startDelta = (logTime - mStartTime) / NANOSECS;
        final long hours = startDelta / 3600;
        final long minutes = (startDelta / 60) % 60;
        final long seconds = startDelta % 60;
        final float deltaSeconds = (logTime - mLastLogTime) / (float)NANOSECS;
        mLastLogTime = logTime;
        return String.format("%02d:%02d:%02d (+%02.3fs)", hours, minutes, seconds, deltaSeconds);
    }
}