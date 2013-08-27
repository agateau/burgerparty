package com.agateau.burgerparty.utils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public abstract class Overlay extends WidgetGroup {
	private TextureRegion mBackgroundRegion;
	public Overlay(TextureAtlas atlas) {
		mBackgroundRegion = atlas.findRegion("overlay-bg");
		setFillParent(true);

		// Disable clicks behind us
		setTouchable(Touchable.enabled);
		addListener(new InputListener() {
			@Override
			public boolean handle(Event event) {
				event.cancel();
				return true;
			}
		});
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.draw(mBackgroundRegion, 0, 0, getWidth(), getHeight());
		super.draw(batch, parentAlpha);
	}

	/**
	 * Must be reimplemented to handle pressing the "back" button
	 */
	public abstract void onBackPressed();
}
