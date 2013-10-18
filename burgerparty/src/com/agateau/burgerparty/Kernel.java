package com.agateau.burgerparty;

import com.agateau.burgerparty.utils.AnimScriptLoader;
import com.agateau.burgerparty.utils.RoundButton;
import com.agateau.burgerparty.utils.SoundAtlas;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class Kernel {
	public static AnimScriptLoader getAnimScriptLoader() {
		init();
		return sAssets.mAnimScriptLoader;
	}

	public static SoundAtlas getSoundAtlas() {
		init();
		return sAssets.mSoundAtlas;
	}

	public static Skin getSkin() {
		init();
		return sAssets.mSkin;
	}

	public static ChangeListener getClickListener() {
		init();
		return sAssets.mClickListener;
	}

	public static RoundButton createRoundButton(String name) {
		init();
		RoundButton button = new RoundButton(sAssets.mSkin, name);
		button.setSound(sAssets.mClickSound);
		return button;
	}

	public static ImageTextButton createTextButton(String text, String iconName) {
		init();
		ImageTextButton button = new ImageTextButton(text, sAssets.mSkin, "image-text-button");
		button.getImage().setDrawable(sAssets.mSkin.getDrawable(iconName));
		button.addListener(sAssets.mClickListener);
		return button;
	}

	public static ImageButton createHudButton(String iconName) {
		init();
		Drawable drawable = sAssets.mSkin.getDrawable(iconName);
		ImageButton button = new ImageButton(drawable);
		button.addListener(sAssets.mClickListener);
		return button;
	}

	public static AssetManager getAssetManager() {
		assert(sAssets.mAssetManager != null);
		return sAssets.mAssetManager;
	}

	public static void preload() {
		assert(sAssets == null);
		sAssets = new Assets();
	}

	public static Assets getAssets() {
		assert(sAssets != null);
		return sAssets;
	}

	private static void init() {
		assert(sAssets != null);
	}

	private static Assets sAssets = null;
}
