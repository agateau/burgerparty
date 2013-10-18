package com.agateau.burgerparty;

import com.agateau.burgerparty.utils.AnimScriptLoader;
import com.agateau.burgerparty.utils.RoundButton;
import com.agateau.burgerparty.utils.SoundAtlas;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

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

	public static ChangeListener getClickListener() {
		init();
		return sClickListener;
	}

	public static RoundButton createRoundButton(String name) {
		init();
		RoundButton button = new RoundButton(sSkin, name);
		button.setSound(sClickSound);
		return button;
	}

	public static ImageTextButton createTextButton(String text, String iconName) {
		init();
		ImageTextButton button = new ImageTextButton(text, sSkin, "image-text-button");
		button.getImage().setDrawable(sSkin.getDrawable(iconName));
		button.addListener(sClickListener);
		return button;
	}

	public static ImageButton createHudButton(String iconName) {
		init();
		Drawable drawable = sSkin.getDrawable(iconName);
		ImageButton button = new ImageButton(drawable);
		button.addListener(sClickListener);
		return button;
	}

	public static AssetManager getAssetManager() {
		assert(sAssetManager != null);
		return sAssetManager;
	}

	public static void preload() {
		sAssetManager = new AssetManager();
		Texture.setAssetManager(sAssetManager);
		sAssetManager.load("burgerparty.atlas", TextureAtlas.class);

		sSoundAtlas = new SoundAtlas(sAssetManager, "sounds/");
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
			"tick.wav",
			"time-bonus.wav",
			"trash.wav"
		};
		sSoundAtlas.preload(names);
	}

	private static void init() {
		if (sInited) {
			return;
		}
		sInited = true;
		assert(sAssetManager != null);
		if (sAssetManager.getQueuedAssets() > 0) {
			Gdx.app.error("Kernel", "Not all assets have been loaded yet, going to block (progress=" + sAssetManager.getProgress() + ")");
		}
		sTextureAtlas = sAssetManager.get("burgerparty.atlas");
		sSkin = new Skin(Gdx.files.internal("ui/skin.json"), sTextureAtlas);

		sSoundAtlas.finishLoad();

		sClickSound = sSoundAtlas.findSound("click");
		sClickListener = new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				sClickSound.play();
			}
		};
	}

	private static boolean sInited = false;
	private static AnimScriptLoader sAnimScriptLoader = new AnimScriptLoader();
	private static SoundAtlas sSoundAtlas;
	private static TextureAtlas sTextureAtlas;
	private static Skin sSkin;
	private static Sound sClickSound;
	private static ChangeListener sClickListener;
	private static AssetManager sAssetManager;
}
