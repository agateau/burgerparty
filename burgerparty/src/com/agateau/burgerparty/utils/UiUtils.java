package com.agateau.burgerparty.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class UiUtils {
	public static final int BUTTON_WIDTH = 200;
	public static final int BUTTON_HEIGHT = 40;

	public static void setButtonSize(Actor actor) {
		actor.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
	}
}
