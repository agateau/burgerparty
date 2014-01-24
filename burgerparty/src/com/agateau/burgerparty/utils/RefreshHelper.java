package com.agateau.burgerparty.utils;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;

public abstract class RefreshHelper {
	public RefreshHelper(Stage stage) {
		stage.getRoot().addListener(new InputListener() {
			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				if (keycode == Input.Keys.F5) {
					Timer.post(new Timer.Task() {
						@Override
						public void run() {
							try {
								refresh();
							} catch (Exception exc) {
								NLog.i("RefreshHelper.keyUp failed: %s", exc);
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
