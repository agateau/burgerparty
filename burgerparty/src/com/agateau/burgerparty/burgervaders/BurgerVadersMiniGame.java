package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.model.MiniGame;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class BurgerVadersMiniGame extends MiniGame {
	public BurgerVadersMiniGame(Assets assets, Game game) {
		super(assets);
		mGame = game;
	}

	@Override
	public Screen createScreen() {
		return new BurgerVadersStartScreen(this);
	}

	public void showMainScreen() {
		mGame.setScreen(new BurgerVadersMainScreen(this));
	}

	public void showStartScreen() {
		mGame.setScreen(createScreen());
	}

	private Game mGame;
}
