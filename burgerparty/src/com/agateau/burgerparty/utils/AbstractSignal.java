package com.agateau.burgerparty.utils;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class AbstractSignal<H> {
	protected Set<H> mHandlers;

	public AbstractSignal() {
		mHandlers = Collections.newSetFromMap(new WeakHashMap<H, Boolean>());
	}

	public void connect(Set<Object> tracker, H handler) {
		tracker.add(handler);
		mHandlers.add(handler);
	}
}
