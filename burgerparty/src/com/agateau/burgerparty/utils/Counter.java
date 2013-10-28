package com.agateau.burgerparty.utils;

import com.badlogic.gdx.utils.TimeUtils;

public class Counter {
	public Counter() {
		start();
	}

	public void start() {
		mStart = TimeUtils.millis();
	}

	public long restart() {
		long now = TimeUtils.millis();
		long delta = now - mStart;
		mStart = now;
		return delta;
	}

	private long mStart = 0;
}