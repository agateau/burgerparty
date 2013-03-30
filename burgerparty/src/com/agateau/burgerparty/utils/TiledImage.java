package com.agateau.burgerparty.utils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class TiledImage extends Widget {
	public TiledImage(TextureRegion region) {
		mRegion = region;
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.setColor(1, 1, 1, parentAlpha);
		float texWidth = mRegion.getRegionWidth();
		float texHeight = mRegion.getRegionHeight();
		float width = getWidth();
		float height = getHeight();
		for (float y = 0; y < height; y += texHeight) {
			for (float x = 0; x < width; x += texWidth) {
				batch.draw(mRegion, x, y);
			}
		}
	}

	private TextureRegion mRegion;
}
