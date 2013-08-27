package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.Level;
import com.agateau.burgerparty.model.World;
import com.agateau.burgerparty.view.WorldView;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class GameScreen extends BurgerPartyScreen {
	private World mWorld;
	private WorldView mWorldView;

	public GameScreen(BurgerPartyGame game, Level level, TextureAtlas atlas, Skin skin) {
		super(game, skin);
		mWorld = new World(level);
		mWorldView = new WorldView(this, game, mWorld, atlas, skin);
		getStage().addActor(mWorldView);
	}

	@Override
	public void show() {
		super.show();
		mWorld.start();
	}

	@Override
	public void pause() {
		mWorldView.pause();
	}

	@Override
	public void onBackPressed() {
		mWorldView.onBackPressed();
	}
}
