package com.agateau.burgerparty.view;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.UiUtils;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class GameOverOverlay extends Overlay {
	private BurgerPartyGame mGame;
	private Table mTable; 
	public GameOverOverlay(BurgerPartyGame game, TextureAtlas atlas, Skin skin) {
		super(atlas);
		mGame = game;

		mTable = new Table();
		addActor(mTable);
		mTable.setFillParent(true);

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

		mTable.add(label).padBottom(40);

		mTable.row();
		mTable.add(tryAgainButton).size(UiUtils.BUTTON_WIDTH, UiUtils.BUTTON_HEIGHT).padBottom(20);

		mTable.row();
		mTable.add(menuButton).size(UiUtils.BUTTON_WIDTH, UiUtils.BUTTON_HEIGHT);

		mTable.center();
	}
}
