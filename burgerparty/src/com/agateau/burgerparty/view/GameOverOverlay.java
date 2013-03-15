package com.agateau.burgerparty.view;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorLayout;
import com.agateau.burgerparty.utils.UiUtils;

import com.badlogic.gdx.Gdx;
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
	private Label mLabel;
	private TextButton mTryAgainButton;
	private TextButton mMenuButton;
	public GameOverOverlay(BurgerPartyGame game, TextureAtlas atlas, Skin skin) {
		super(atlas);
		mGame = game;

		mTable = new Table();
		addActor(mTable);
		mTable.setFillParent(true);

		mLabel = new Label("Game Over", skin);

		mTryAgainButton = new TextButton("Try Again", skin);
		mTryAgainButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mGame.start();
			}
		});

		mMenuButton = new TextButton("Menu", skin);
		mMenuButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mGame.showMenu();
			}
		});

		addActor(mLabel);
		addActor(mTryAgainButton);
		addActor(mMenuButton);
	}

	@Override
	public void layout() {
		mTryAgainButton.setSize(UiUtils.BUTTON_WIDTH, UiUtils.BUTTON_HEIGHT);
		mMenuButton.setSize(UiUtils.BUTTON_WIDTH, UiUtils.BUTTON_HEIGHT);
	
		AnchorLayout layout = new AnchorLayout();
		layout.setSpacing(20);
	
		layout.moveActor(mLabel, Anchor.BOTTOM_CENTER, this, Anchor.CENTER, 0, 2);
		layout.moveActor(mTryAgainButton, Anchor.TOP_CENTER, this, Anchor.CENTER);
		layout.moveActor(mMenuButton, Anchor.TOP_CENTER, mTryAgainButton, Anchor.BOTTOM_CENTER, 0, -1);
	}
}
