package com.agateau.burgerparty;

import com.agateau.burgerparty.utils.AnimScriptLoader;
import com.agateau.burgerparty.utils.RoundButton;
import com.agateau.burgerparty.utils.SoundAtlas;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

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
		button.setSound(sSoundAtlas.findSound("click"));
		return button;
	}

	public static ImageTextButton createTextButton(String text, String iconName) {
		init();
		ImageTextButton button = new ImageTextButton(text, sSkin, "image-text-button");
		button.getImage().setDrawable(Kernel.getSkin().getDrawable(iconName));
		button.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				sSoundAtlas.findSound("click").play();
			}
		});

		return button;
	}

	private static void init() {
		if (sInited) {
			return;
		}
		sInited = true;
		sTextureAtlas = new TextureAtlas(Gdx.files.internal("burgerparty.atlas"));
		sSkin = new Skin(Gdx.files.internal("ui/skin.json"), sTextureAtlas);

		String[] names = {
			"add-item.wav",
			"add-item-bottom.wav",
			"add-item-cheese.wav",
			"add-item-coconut.wav",
			"add-item-onion.wav",
			"add-item-salad.wav",
			"add-item-steak.wav",
			"add-item-tomato.wav",
			"click.wav",
			"error.wav",
			"finished.wav",
			"meal-done.wav",
			"sauce.wav",
			"splat.wav",
			"star.wav",
			"time-bonus.wav"
		};
		sSoundAtlas.load(names);
	}

	private static boolean sInited = false;
	private static AnimScriptLoader sAnimScriptLoader = new AnimScriptLoader();
	private static SoundAtlas sSoundAtlas = new SoundAtlas("sounds/");
	private static TextureAtlas sTextureAtlas;
	private static Skin sSkin;
}
