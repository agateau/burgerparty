package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class NewWorldScreen extends BurgerPartyScreen {
	private int mWorldIndex;

	public NewWorldScreen(BurgerPartyGame game, int worldIndex) {
		super(game);
		mWorldIndex = worldIndex;

		setBackgroundActor(new Image(getTextureAtlas().findRegion("newworld/map")));
	}

	@Override
	public void onBackPressed() {
		getGame().startLevel(mWorldIndex, 0);
	}
}
