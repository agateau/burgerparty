package com.agateau.burgerparty.view;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.UiUtils;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class GameOverOverlay extends Overlay {
	private BurgerPartyGame mGame;

	public GameOverOverlay(BurgerPartyGame game, TextureAtlas atlas, Skin skin) {
		super(atlas);
		mGame = game;

		Label label = new Label("Game Over", skin);

		TextButton tryAgainButton = new TextButton("Try Again", skin);
		tryAgainButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mGame.startLevel(mGame.getLevelIndex());
			}
		});

		TextButton menuButton = new TextButton("Menu", skin);
		menuButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mGame.showMenu();
			}
		});

		UiUtils.setButtonSize(tryAgainButton);
		UiUtils.setButtonSize(menuButton);

		AnchorGroup group = new AnchorGroup();
		addActor(group);
		group.setFillParent(true);
		group.setSpacing(UiUtils.SPACING);

		group.moveActor(label, Anchor.BOTTOM_CENTER, this, Anchor.CENTER, 0, 2);
		group.moveActor(tryAgainButton, Anchor.TOP_CENTER, this, Anchor.CENTER);
		group.moveActor(menuButton, Anchor.TOP_CENTER, tryAgainButton, Anchor.BOTTOM_CENTER, 0, -1);
	}
}
