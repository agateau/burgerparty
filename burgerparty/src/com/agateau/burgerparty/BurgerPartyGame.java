package com.agateau.burgerparty;

import com.agateau.burgerparty.model.Level;
import com.agateau.burgerparty.screens.GameScreen;
import com.agateau.burgerparty.screens.MenuScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

public class BurgerPartyGame extends Game {
	private Skin mSkin;
	private TextureAtlas mAtlas;
	private Array<Level> mLevels = new Array<Level>();
	private int mLevelIndex = 0;

	@Override
	public void create() {
		mAtlas = new TextureAtlas(Gdx.files.internal("burgerparty.atlas"));
		TextureAtlas skinAtlas = new TextureAtlas(Gdx.files.internal("skin/uiskin.atlas"));
		mSkin = new Skin(Gdx.files.internal("skin/uiskin.json"), skinAtlas);
			showMenu();

		Level level;
		level = new Level();
		level.minStackSize = 2;
		level.maxStackSize = 3;
		level.inventoryItems.add("steak");
		level.inventoryItems.add("tomato");
		level.inventoryItems.add("salad");
		level.customerCount = 4;
		level.duration = 120;
		mLevels.add(level);

		level = new Level();
		level.minStackSize = 2;
		level.maxStackSize = 3;
		level.inventoryItems.add("steak");
		level.inventoryItems.add("tomato");
		level.inventoryItems.add("salad");
		level.inventoryItems.add("onion");
		level.customerCount = 5;
		level.duration = 120;
		mLevels.add(level);

		level = new Level();
		level.minStackSize = 2;
		level.maxStackSize = 4;
		level.inventoryItems.add("steak");
		level.inventoryItems.add("tomato");
		level.inventoryItems.add("salad");
		level.inventoryItems.add("onion");
		level.customerCount = 6;
		level.duration = 120;
		mLevels.add(level);

		level = new Level();
		level.minStackSize = 2;
		level.maxStackSize = 5;
		level.inventoryItems.add("steak");
		level.inventoryItems.add("tomato");
		level.inventoryItems.add("salad");
		level.inventoryItems.add("onion");
		level.inventoryItems.add("cheese");
		level.customerCount = 7;
		level.duration = 120;
		mLevels.add(level);

		level = new Level();
		level.minStackSize = 3;
		level.maxStackSize = 6;
		level.inventoryItems.add("steak");
		level.inventoryItems.add("tomato");
		level.inventoryItems.add("salad");
		level.inventoryItems.add("onion");
		level.inventoryItems.add("cheese");
		level.customerCount = 8;
		level.duration = 120;
		mLevels.add(level);
	}

	public int getLevelIndex() {
		return mLevelIndex;
	}

	public int getLevelCount() {
		return mLevels.size;
	}

	public void startLevel(int index) {
		mLevelIndex = index;
		setScreen(new GameScreen(this, mLevels.get(mLevelIndex), mAtlas, mSkin));
	}

	public void showMenu() {
		setScreen(new MenuScreen(this, mSkin));
	}
}
