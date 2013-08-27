package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.StageScreen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Extends StageScreen to add a reference to BurgerPartyGame
 */
public abstract class BurgerPartyScreen extends StageScreen {
	private BurgerPartyGame mGame;

	public BurgerPartyScreen(BurgerPartyGame game, Skin skin) {
		super(skin);
		mGame = game;
	}

	public BurgerPartyGame getGame() {
		return mGame;
	}
}
