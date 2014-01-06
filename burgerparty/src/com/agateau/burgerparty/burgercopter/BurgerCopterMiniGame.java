package com.agateau.burgerparty.burgercopter;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.MiniGame;

public class BurgerCopterMiniGame extends MiniGame {
	public BurgerCopterMiniGame(Assets assets, BurgerPartyGame game) {
		super(assets, game);
	}

	@Override
	public void showStartScreen() {
		setScreenAndDispose(new BurgerCopterStartScreen(this));
	}

	@Override
	public void showMainScreen() {
		setScreenAndDispose(new BurgerCopterMainScreen(this));
	}
}
