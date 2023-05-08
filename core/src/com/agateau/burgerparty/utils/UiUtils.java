package com.agateau.burgerparty.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static Pixmap getPixmap(int left, int top, int width, int height) {
        Gdx.gl.glPixelStorei(GL20.GL_PACK_ALIGNMENT, 1);

        final Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
        ByteBuffer pixels = pixmap.getPixels();
        Gdx.gl.glReadPixels(left, top, width, height, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, pixels);

        // Swap top and bottom lines, set alpha to 255 (otherwise some areas appear transparent)
        // Not efficient at all
        for (int y = 0; y <= height / 2; ++y) {
            int topIdx = y * width * 4;
            int bottomIdx = (height - y - 1) * width * 4;
            for (int x = 0; x < width; ++x, topIdx += 4, bottomIdx += 4) {
                byte r = pixels.get(topIdx);
                byte g = pixels.get(topIdx + 1);
                byte b = pixels.get(topIdx + 2);
                pixels.put(topIdx, pixels.get(bottomIdx));
                pixels.put(topIdx + 1, pixels.get(bottomIdx + 1));
                pixels.put(topIdx + 2, pixels.get(bottomIdx + 2));
                pixels.put(topIdx + 3, (byte)255);
                pixels.put(bottomIdx, r);
                pixels.put(bottomIdx + 1, g);
                pixels.put(bottomIdx + 2, b);
                pixels.put(bottomIdx + 3, (byte)255);
            }
        }

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

    public static String actorToString(Actor actor) {
        return String.format("%s pos=%.2fx%.2f size=%.2fx%.2f", actor, actor.getX(), actor.getY(), actor.getWidth(), actor.getHeight());
    }

    public static void setImageRegion(Image image, TextureRegion region) {
        image.setDrawable(new TextureRegionDrawable(region));
        if (image.getWidth() == 0) {
            image.setWidth(region.getRegionWidth());
        }
        if (image.getHeight() == 0) {
            image.setHeight(region.getRegionHeight());
        }
    }

    /**
     * Make the style used by an ImageButton unique. This is useful to update its imageUp drawable.
     */
    public static void makeImageButtonStyleUnique(ImageButton button) {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(button.getStyle());
        button.setStyle(style);
    }
}
