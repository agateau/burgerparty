package com.agateau.burgerparty.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

public class TextureDict {
	private TextureAtlas mAtlas;

	TextureDict() {
		mAtlas = new TextureAtlas(Gdx.files.internal("burgerparty.atlas"));
	}

	AtlasRegion getByName(String name) {
		return mAtlas.findRegion(name);
	}
}
