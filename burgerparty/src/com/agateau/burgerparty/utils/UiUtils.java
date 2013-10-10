package com.agateau.burgerparty.utils;

import com.badlogic.gdx.math.Matrix3;
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
	public static Vector2 toAscendantCoordinates(Actor ascendant, Actor actor, Vector2 coords) {
		Matrix3 matrix = getAscendantMatrix(ascendant, actor);
		return coords.mul(matrix);
	}

	public static Vector2 toChildCoordinates(Actor ascendant, Actor actor, Vector2 coords) {
		Matrix3 matrix = getAscendantMatrix(ascendant, actor);
		return coords.mul(matrix.inv());
	}

	/**
	 * Returns the matrix to transform @p actor coordinates into @p ascendant coordinates
	 */
	public static Matrix3 getAscendantMatrix(Actor ascendant, Actor actor) {
		Matrix3 matrix = new Matrix3();
		Actor actor2 = actor;
		while (actor2 != ascendant) {
			matrix = getActorMatrix(actor2).mul(matrix);
			actor2 = actor2.getParent();
			if (actor2 == null) {
				throw new RuntimeException("actor " + ascendant + " is not an ascendant of actor " + actor);
			}
		}
		return matrix;
	}

	public static Matrix3 getActorMatrix(Actor actor) {
		Matrix3 matrix = new Matrix3();
		matrix.translate(actor.getX(), actor.getY());
		matrix.scale(actor.getScaleX(), actor.getScaleY());
		matrix.rotate(actor.getRotation());
		return matrix;
	}
}
