package com.agateau.burgerparty.utils;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class AbstractSignal<H extends Signal.Handler> implements Signal {
    protected List<WeakReference<H>> mHandlers = new CopyOnWriteArrayList<WeakReference<H>>();

    public void connect(Set<Object> tracker, H handler) {
        tracker.add(handler);
        mHandlers.add(new WeakReference<H>(handler));
    }

    public void connect(ConnectionManager manager, H handler) {
        manager.add(this, handler);
        mHandlers.add(new WeakReference<H>(handler));
    }

    @Override
    public void disconnect(Signal.Handler handler) {
        for (int idx = mHandlers.size() - 1; idx >= 0; --idx) {
            WeakReference<H> ref = mHandlers.get(idx);
            if (ref.get() == handler) {
                mHandlers.remove(idx);
            }
        }
    }

    public int getHandlerCount() {
        return mHandlers.size();
    }
}
