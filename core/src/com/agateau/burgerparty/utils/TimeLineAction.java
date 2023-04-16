package com.agateau.burgerparty.utils;

import java.util.Collections;
import java.util.Vector;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class TimeLineAction extends Action {
    private static class ActorAction implements Comparable<ActorAction> {
        float time;
        Actor actor;
        Action action;
        @Override
        public int compareTo(ActorAction other) {
            if (time < other.time) {
                return -1;
            }
            if (time > other.time) {
                return 1;
            }
            return 0;
        }
    }

    private float mTime = 0;
    private Vector<ActorAction> mArray = new Vector<ActorAction>();
    private int mIndex = 0;

    @Override
    public boolean act(float delta) {
        if (mIndex >= mArray.size()) {
            return true;
        }
        mTime += delta;
        ActorAction aa = mArray.get(mIndex);
        if (aa.time <= mTime) {
            aa.actor.addAction(aa.action);
            ++mIndex;
        }
        return mIndex >= mArray.size();
    }

    public void addAction(float time, Actor actor, Action action) {
        ActorAction aa = new ActorAction();
        aa.time = time;
        aa.actor = actor;
        aa.action = action;
        mArray.add(aa);
        Collections.sort(mArray);
    }

    public void addActionRelative(float dt, Actor actor, Action action) {
        float time = mArray.isEmpty() ? 0 : mArray.lastElement().time;
        addAction(time + dt, actor, action);
    }
}
