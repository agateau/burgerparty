package com.agateau.burgerparty;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;

public class Main {
	public static void main(String[] args) {
		packTextures();
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "burgerparty";
		cfg.useGL20 = true;
		cfg.width = 800;
		cfg.height = 480;
		new LwjglApplication(new BurgerPartyGame(), cfg);
	}

	private static void packTextures() {
		Settings settings = new Settings();
		TexturePacker2.process(settings, "../burgerparty/assets", "../burgerparty-android/assets", "burgerparty");
	}
}
