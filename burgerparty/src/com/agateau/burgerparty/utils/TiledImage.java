package com.agateau.burgerparty.utils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

public class TiledImage extends Widget {
	public TiledImage(TextureRegion region) {
		mDrawable = new TiledDrawable(region);
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.setColor(1, 1, 1, parentAlpha);
		mDrawable.draw(batch, 0, 0, getWidth(), getHeight());
	}

	private TiledDrawable mDrawable;
}
