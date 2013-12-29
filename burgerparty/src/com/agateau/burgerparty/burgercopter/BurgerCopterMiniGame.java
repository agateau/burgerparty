package com.agateau.burgerparty.burgercopter;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.model.MiniGame;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class BurgerCopterMiniGame extends MiniGame {
	public BurgerCopterMiniGame(Assets assets, Game game) {
		super(assets);
		mGame = game;
	}

	@Override
	public Screen createScreen() {
		return new BurgerCopterStartScreen(this);
	}

	public void showMainScreen() {
		mGame.setScreen(new BurgerCopterMainScreen(this));
	}

	public void showStartScreen() {
		mGame.setScreen(createScreen());
	}

	private Game mGame;
}
