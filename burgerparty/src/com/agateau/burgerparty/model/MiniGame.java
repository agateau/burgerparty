package com.agateau.burgerparty.model;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;


public abstract class MiniGame {
	public Signal0 exiting = new Signal0();

	public MiniGame(Assets assets, BurgerPartyGame game) {
		assert(assets != null);
		mAssets = assets;
		mGame = game;
	}

	public abstract void showStartScreen();

	public abstract void showMainScreen();

	public void showGameOverScreen() {
		Pixmap pix = UiUtils.getPixmap(0, 0, mGame.getWidth(), mGame.getHeight());
		mGame.setScreen(new MiniGameOverScreen(this, pix));
	}

	public Assets getAssets() {
		assert(mAssets != null);
		return mAssets;
	}

	public void setScreen(Screen screen) {
		mGame.setScreen(screen);
	}

	private Assets mAssets;
	private BurgerPartyGame mGame;
}
