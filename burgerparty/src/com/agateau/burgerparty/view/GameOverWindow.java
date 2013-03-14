package com.agateau.burgerparty.view;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.UiUtils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class GameOverWindow extends Table {
	private BurgerPartyGame mGame;
	private TextureRegion mBackgroundRegion;
	public GameOverWindow(BurgerPartyGame game, TextureAtlas atlas, Skin skin) {
		mGame = game;

		setFillParent(true);

		mBackgroundRegion = atlas.findRegion("overlay-bg");

		Label label = new Label("Game Over", skin);

		TextButton tryAgainButton = new TextButton("Try Again", skin);
		tryAgainButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mGame.start();
			}
		});

		TextButton menuButton = new TextButton("Menu", skin);
		menuButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mGame.showMenu();
			}
		});

		add(label).padBottom(40);

		row();
		add(tryAgainButton).size(UiUtils.BUTTON_WIDTH, UiUtils.BUTTON_HEIGHT).padBottom(20);

		row();
		add(menuButton).size(UiUtils.BUTTON_WIDTH, UiUtils.BUTTON_HEIGHT);

		center();

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
	public void drawBackground(SpriteBatch batch, float parentAlpha) {
		batch.draw(mBackgroundRegion, 0, 0, getWidth(), getHeight());
		
	}
}
