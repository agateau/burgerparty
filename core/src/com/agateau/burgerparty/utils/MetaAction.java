package com.agateau.burgerparty.utils;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

/**
 * An action which can be used to create other actions or anything else by implementing its setup() method.
 *
 * @author aurelien
 *
 */
public abstract class MetaAction extends Action {
    private static enum ActionState {
        NEW,
        RUNNING,
        DONE
    };

    private MetaAction.ActionState mActionState = ActionState.NEW;

    @Override
    public boolean act(float delta) {
        switch (mActionState) {
        case NEW:
            setup();
            mActionState = ActionState.RUNNING;
            return false;
        case RUNNING:
            return false;
        case DONE:
            return true;
        }
        return false;
    }

    @Override
    public void setActor(Actor actor) {
        super.setActor(actor);
        if (actor == null && mActionState == ActionState.RUNNING) {
            abort();
        }
    }

    /**
     * Called first time act() is called. This is the place to create new actions or do whatever you want.
     */
    protected abstract void setup();

    /**
     * Called if the action is removed from its actor while it's still running. This is the place to do
     * whatever clean up is necessary, for example if you added actions to an actor in setup(),
     * you can call Actor.clearActions() on this actor from here.
     */
    protected void abort() {
    }

    /**
     * Create an action which will mark the action as done, so that act() returns true.
     * @return The created action
     */
    protected Action createDoneAction() {
        return Actions.run(new Runnable() {
            @Override
            public void run() {
                mActionState = ActionState.DONE;
            }
        });
    }
}