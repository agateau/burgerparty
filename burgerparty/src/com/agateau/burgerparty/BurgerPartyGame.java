package com.agateau.burgerparty;

import com.agateau.burgerparty.screens.GameScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class BurgerPartyGame extends Game {
	@Override
	public void create() {
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("burgerparty.atlas"));
		TextureAtlas skinAtlas = new TextureAtlas(Gdx.files.internal("skin/uiskin.atlas"));
		Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"), skinAtlas);
		setScreen(new GameScreen(atlas, skin));
	}
}
