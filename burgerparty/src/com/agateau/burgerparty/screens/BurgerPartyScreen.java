package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.StageScreen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 * Extends StageScreen to add a reference to BurgerPartyGame
 */
public abstract class BurgerPartyScreen extends StageScreen {
	private BurgerPartyGame mGame;

	public BurgerPartyScreen(BurgerPartyGame game) {
		mGame = game;
	}

	public BurgerPartyGame getGame() {
		return mGame;
	}

	public TextureAtlas getTextureAtlas() {
		return mGame.getAssets().getTextureAtlas();
	}
}
