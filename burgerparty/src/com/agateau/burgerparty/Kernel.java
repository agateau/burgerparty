package com.agateau.burgerparty;

import com.agateau.burgerparty.utils.AnimScriptLoader;
import com.agateau.burgerparty.utils.RoundButton;
import com.agateau.burgerparty.utils.SoundAtlas;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Kernel {
	public static AnimScriptLoader getAnimScriptLoader() {
		init();
		return sAnimScriptLoader;
	}

	public static SoundAtlas getSoundAtlas() {
		init();
		return sSoundAtlas;
	}

	public static TextureAtlas getTextureAtlas() {
		init();
		return sTextureAtlas;
	}

	public static Skin getSkin() {
		init();
		return sSkin;
	}

	public static RoundButton createRoundButton(String name) {
		init();
		RoundButton button = new RoundButton(sSkin, name);
		button.setSound(sSoundAtlas.findSound("click.wav"));
		return button;
	}

	private static void init() {
		if (sInited) {
			return;
		}
		sInited = true;
		sTextureAtlas = new TextureAtlas(Gdx.files.internal("burgerparty.atlas"));
		sSkin = new Skin(Gdx.files.internal("ui/skin.json"), sTextureAtlas);
	}

	private static boolean sInited = false;
	private static AnimScriptLoader sAnimScriptLoader = new AnimScriptLoader();
	private static SoundAtlas sSoundAtlas = new SoundAtlas("sounds/");
	private static TextureAtlas sTextureAtlas;
	private static Skin sSkin;
}
