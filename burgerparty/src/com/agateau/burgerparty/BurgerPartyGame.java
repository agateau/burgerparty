package com.agateau.burgerparty;

import com.agateau.burgerparty.screens.GameScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class BurgerPartyGame extends Game {
	@Override
	public void create() {
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("burgerparty.atlas"));
		setScreen(new GameScreen(atlas));
	}
}
