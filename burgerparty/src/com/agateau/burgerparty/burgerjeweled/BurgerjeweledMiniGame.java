package com.agateau.burgerparty.burgerjeweled;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.model.MiniGame;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class BurgerjeweledMiniGame extends MiniGame {
	public BurgerjeweledMiniGame(Assets assets, Game game) {
		super(assets);
		mGame = game;
	}

	@Override
	public Screen createScreen() {
		return new BurgerjeweledStartScreen(this);
	}

	public void showMainScreen() {
		mGame.setScreen(new BurgerjeweledMainScreen(this));
	}

	public void showStartScreen() {
		mGame.setScreen(createScreen());
	}

	private Game mGame;
}
