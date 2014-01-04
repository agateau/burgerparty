package com.agateau.burgerparty.utils;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MaskedDrawableAtlas {
	public MaskedDrawableAtlas(TextureAtlas textureAtlas) {
		mTextureAtlas = textureAtlas;
	}

	public MaskedDrawable get(String name) {
		MaskedDrawable drawable = mMap.get(name);
		if (drawable == null) {
			TextureRegion region = mTextureAtlas.findRegion(name);
			if (region == null) {
				throw new RuntimeException("Could not find a texture region named " + region);
			}
			drawable = new MaskedDrawable(region);
			mMap.put(name, drawable);
		}
		return drawable;
	}

	private HashMap<String, MaskedDrawable> mMap = new HashMap<String, MaskedDrawable>();
	private final TextureAtlas mTextureAtlas;
}
