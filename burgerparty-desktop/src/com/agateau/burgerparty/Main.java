package com.agateau.burgerparty;

import com.agateau.burgerparty.utils.NLog;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		NLog.init(new NLog.GdxPrinter("BurgerParty"));
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "burgerparty";
		cfg.useGL20 = true;

		boolean fullscreen = false;
		if (fullscreen) {
			DisplayMode mode = LwjglApplicationConfiguration.getDesktopDisplayMode();
			cfg.width = mode.width;
			cfg.height = mode.height;
			cfg.fullscreen = true;
			cfg.vSyncEnabled = true;
		} else {
			cfg.width = 800;
			cfg.height = 480;
		}

		new LwjglApplication(new BurgerPartyGame(), cfg);
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}
}
