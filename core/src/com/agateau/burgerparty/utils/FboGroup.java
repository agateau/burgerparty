package com.agateau.burgerparty.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Disposable;

/**
 * A group which uses a Frame Buffer Object to render its children
 *
 * @author aurelien
 *
 */
public class FboGroup extends Group implements Disposable {
    private FrameBuffer mFrameBuffer = null;

    @Override
    public void dispose() {
        mFrameBuffer.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (mFrameBuffer == null) {
            createFrameBuffer();
        }
        batch.end();
        fillFrameBuffer(batch);
        batch.begin();

        Texture texture = mFrameBuffer.getColorBufferTexture();
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        batch.draw(texture, 0, 0, mFrameBuffer.getWidth(), mFrameBuffer.getHeight(), 0f, 0f, 1f, 1f);
    }

    private void createFrameBuffer() {
        int w = (int)getWidth();
        int h = (int)getHeight();
        mFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, w, h, false);
    }

    private void fillFrameBuffer(Batch batch) {
        float oldAlpha = getColor().a;
        getColor().a = 1;

        mFrameBuffer.begin();
        batch.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.draw(batch, 1);
        batch.end();
        mFrameBuffer.end();

        getColor().a = oldAlpha;
    }
}
