package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.StageScreen;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Extends StageScreen to add a reference to BurgerPartyGame
 */
public abstract class BaseScreen extends StageScreen {
	private BurgerPartyGame mGame;

	public BaseScreen(BurgerPartyGame game, Skin skin) {
		super(skin);
		mGame = game;
	}

	public BurgerPartyGame getGame() {
		return mGame;
	}
}
