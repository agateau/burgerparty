package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.MiniGame;

public class BurgerVadersMiniGame extends MiniGame {
	public BurgerVadersMiniGame(Assets assets, BurgerPartyGame game) {
		super(assets, game);
	}

	public void showMainScreen() {
		setScreen(new BurgerVadersMainScreen(this));
	}

	@Override
	public void showStartScreen() {
		setScreen(new BurgerVadersStartScreen(this));
	}
}
