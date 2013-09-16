package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.view.SandBoxGameView;

public class SandBoxGameScreen extends BurgerPartyScreen {
	public SandBoxGameScreen(BurgerPartyGame game, LevelWorld world) {
		super(game, Kernel.getSkin());
		mSandBoxGameView = new SandBoxGameView(this, game, world);
		getStage().addActor(mSandBoxGameView);
	}

	@Override
	public void onBackPressed() {
		mSandBoxGameView.onBackPressed();
	}

	private SandBoxGameView mSandBoxGameView;
}
