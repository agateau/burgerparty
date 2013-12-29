package com.agateau.burgerparty.model;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.utils.Signal0;
import com.badlogic.gdx.Screen;


public abstract class MiniGame {
	public Signal0 exiting = new Signal0();

	public MiniGame(Assets assets) {
		assert(assets != null);
		mAssets = assets;
	}

	public abstract Screen createScreen();

	public Assets getAssets() {
		assert(mAssets != null);
		return mAssets;
	}

	private Assets mAssets;
}
