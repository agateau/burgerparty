package com.agateau.burgerparty.view;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class TextureDict {
	private HashMap<String, Texture> mMap;

	TextureDict() {
		mMap = new HashMap<String, Texture>();
		loadTexture("top");
		loadTexture("bottom");
		loadTexture("salad");
		loadTexture("tomato");
		loadTexture("steak");
		loadTexture("cheese");
		loadTexture("shelf");
		loadTexture("trash");
	}

	private void loadTexture(String name) {
		Texture texture = new Texture(Gdx.files.internal(name + ".png"));
		mMap.put(name, texture);
	}

	Texture getByName(String name) {
		return mMap.get(name);
	}
}
