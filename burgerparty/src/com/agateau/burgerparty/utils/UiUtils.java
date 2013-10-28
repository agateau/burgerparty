package com.agateau.burgerparty.utils;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Pixmap.Format;
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

	public static Pixmap getPixmap(int x, int y, int w, int h) {
		Gdx.gl.glPixelStorei(GL10.GL_PACK_ALIGNMENT, 1);

		final Pixmap pixmap = new Pixmap(w, h, Format.RGBA8888);
		ByteBuffer pixels = pixmap.getPixels();
		Gdx.gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, pixels);

		final int numBytes = w * h * 4;
		byte[] lines = new byte[numBytes];

		final int numBytesPerLine = w * 4;
		for (int i = 0; i < h; i++) {
			pixels.position((h - i - 1) * numBytesPerLine);
			pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
		}
		pixels.clear();
		pixels.put(lines);

		return pixmap;
	}

	public static String saveScreenshot() {
		Pixmap pix = getPixmap(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss");
		String name = format.format(date) + ".png";
		FileHandle handle = Gdx.files.external(name);

		PixmapIO.writePNG(handle, pix);
		pix.dispose();
		return handle.path();
	}
}
