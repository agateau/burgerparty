package com.agateau.burgerparty;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;

public class PackerMain {
	public static void main(String[] args) {
		packTextures();
	}

	private static void packTextures() {
		Settings settings = new Settings();
		settings.filterMag = TextureFilter.Linear;
		TexturePacker2.process(settings, "../burgerparty/assets", "../burgerparty-android/assets", "burgerparty");
		System.out.println("Done");
	}
}
