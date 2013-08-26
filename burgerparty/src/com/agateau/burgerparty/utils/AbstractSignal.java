package com.agateau.burgerparty.utils;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class AbstractSignal<H extends Signal.Handler> implements Signal {
	public void connect(Set<Object> tracker, H handler) {
		tracker.add(handler);
		mHandlers.add(handler);
	}

	public void connect(ConnectionManager manager, H handler) {
		manager.add(this, handler);
		mHandlers.add(handler);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void disconnect(Signal.Handler handler) {
		mHandlers.remove((H)handler);
	}

	protected Set<H> mHandlers = Collections.newSetFromMap(new WeakHashMap<H, Boolean>());
}
