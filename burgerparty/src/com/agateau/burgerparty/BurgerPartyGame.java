package com.agateau.burgerparty;

import com.agateau.burgerparty.screens.GameScreen;

import com.badlogic.gdx.Game;

public class BurgerPartyGame extends Game {
	@Override
	public void create() {
		setScreen(new GameScreen());
	}
}
