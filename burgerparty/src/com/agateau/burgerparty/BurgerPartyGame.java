package com.agateau.burgerparty;

import com.agateau.burgerparty.screens.GameScreen;
import com.agateau.burgerparty.screens.MenuScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class BurgerPartyGame extends Game {
	private Skin mSkin;
	private TextureAtlas mAtlas;

	@Override
	public void create() {
		mAtlas = new TextureAtlas(Gdx.files.internal("burgerparty.atlas"));
		TextureAtlas skinAtlas = new TextureAtlas(Gdx.files.internal("skin/uiskin.atlas"));
		mSkin = new Skin(Gdx.files.internal("skin/uiskin.json"), skinAtlas);
			showMenu();
	}

	public void start() {
		setScreen(new GameScreen(mAtlas, mSkin));
	}

	public void showMenu() {
		setScreen(new MenuScreen(this, mSkin));
	}
}
