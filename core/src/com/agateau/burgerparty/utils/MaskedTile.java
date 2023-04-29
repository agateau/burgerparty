package com.agateau.burgerparty.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class MaskedTile extends Tile {
    private final int mHeights[];

    public MaskedTile(TextureRegion region, int typeId) {
        super(region, typeId);
        mHeights = new int[region.getRegionWidth()];
        initHeights();
    }

    @Override
    public float getHeightAt(float x) {
        return mHeights[MathUtils.floor(x)];
    }

    private void initHeights() {
        Texture texture = region.getTexture();
        TextureData data = texture.getTextureData();

        data.prepare();
        Pixmap pixmap = data.consumePixmap();
        int startX = region.getRegionX();
        int startY = region.getRegionY();
        int height = region.getRegionHeight();
        for (int x = 0; x < mHeights.length; ++x) {
            int y = height - 1;
            for (; y >= 0; --y) {
                int px = pixmap.getPixel(startX + x, startY + height - 1 - y);
                int alpha = px & 255;
                if (alpha > 0) {
                    break;
                }
            }
            mHeights[x] = y + 1;
        }
        data.disposePixmap();
    }
}
