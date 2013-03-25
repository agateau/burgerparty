package com.agateau.burgerparty;

import com.agateau.burgerparty.model.Level;
import com.agateau.burgerparty.screens.GameScreen;
import com.agateau.burgerparty.screens.LevelListScreen;
import com.agateau.burgerparty.screens.MenuScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

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
		loadLevels();
	}

	private void loadLevels() {
		Json json = new Json();
		for (int n=1;; n++) {
			String name = "levels/" + n + ".json";
			FileHandle levelFile = Gdx.files.internal(name);
			if (!levelFile.exists()) {
				break;
			}
			Gdx.app.log("loadLevels", "levelFile=" + levelFile);
			Level level = new Level();
			level.definition = json.fromJson(Level.Definition.class, levelFile);
			mLevels.add(level);
		}
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

	public void selectLevel() {
		setScreen(new LevelListScreen(this, mSkin));
	}
}
