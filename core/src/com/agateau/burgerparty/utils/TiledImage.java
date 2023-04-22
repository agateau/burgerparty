package com.agateau.burgerparty.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

public class TiledImage extends Actor {
    private TiledDrawable mDrawable;

    public TiledImage(TextureRegion region) {
        mDrawable = new TiledDrawable(region);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        mDrawable.draw(batch, getX(), getY(), getWidth(), getHeight());
    }
}
