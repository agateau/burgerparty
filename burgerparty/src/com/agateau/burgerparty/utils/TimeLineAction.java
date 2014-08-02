package com.agateau.burgerparty.utils;

import java.util.PriorityQueue;

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
    private PriorityQueue<ActorAction> mQueue = new PriorityQueue<ActorAction>();

    @Override
    public boolean act(float delta) {
        if (mQueue.isEmpty()) {
            return true;
        }
        mTime += delta;
        if (mQueue.peek().time <= mTime) {
            ActorAction aa = mQueue.remove();
            aa.actor.addAction(aa.action);
        }
        return mQueue.isEmpty();
    }

    public void addAction(float time, Actor actor, Action action) {
        ActorAction aa = new ActorAction();
        aa.time = time;
        aa.actor = actor;
        aa.action = action;
        mQueue.add(aa);
    }
}
