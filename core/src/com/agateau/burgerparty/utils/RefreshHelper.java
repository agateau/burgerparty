package com.agateau.burgerparty.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class RefreshHelper {
    public RefreshHelper(Stage stage) {
        this(stage.getRoot());
    }

    public RefreshHelper(Actor actor) {
        actor.addListener(new InputListener() {
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == Input.Keys.F5) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                refresh();
                            } catch (Exception exc) {
                                NLog.e("failed: %s", exc);
                            }
                        }
                    });
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Implementation of this method must do the refresh
     */
    protected abstract void refresh();
}
