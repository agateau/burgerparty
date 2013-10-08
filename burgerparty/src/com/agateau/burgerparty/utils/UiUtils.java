package com.agateau.burgerparty.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class UiUtils {
	public static final int BUTTON_WIDTH = 200;
	public static final int BUTTON_HEIGHT = 48;
	public static final int SPACING = 20;

	public static void setButtonSize(Actor actor) {
		actor.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
	}

	public static void adjustToPrefSize(Widget widget) {
		widget.setSize(widget.getPrefWidth(), widget.getPrefHeight());
	}

	/**
	 * If the parent of @p actor implements the ResizeToFitChildren interface,
	 * notify it of size changes.
	 *
	 * @param actor
	 */
	public static void notifyResizeToFitParent(Actor actor) {
		Actor parent = actor.getParent();
		if (parent instanceof ResizeToFitChildren) {
			((ResizeToFitChildren)parent).onChildSizeChanged();
		}
	}

	/**
	 * Same as Actor.toAscendantCoordinates(), but supports scaled actors
	 */
	public static Vector2 toAscendantCoordinates(Actor ascendant, Actor _actor, Vector2 _coords) {
		Vector2 coords = new Vector2(_coords);
		Actor actor = _actor;
		while (actor != ascendant) {
			coords.x = actor.getX() + coords.x * actor.getScaleX();
			coords.y = actor.getY() + coords.y * actor.getScaleY();
			actor = actor.getParent();
			if (actor == null) {
				throw new RuntimeException("actor " + ascendant + " is not an ascendant of actor " + _actor);
			}
		}
		return coords;
	}
}
