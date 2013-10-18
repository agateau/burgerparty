package com.agateau.burgerparty;

import com.agateau.burgerparty.utils.AnimScriptLoader;
import com.agateau.burgerparty.utils.SoundAtlas;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class Assets {
	public Assets() {
		mAssetManager = new AssetManager();
		Texture.setAssetManager(mAssetManager);
		mAssetManager.load("burgerparty.atlas", TextureAtlas.class);

		mSoundAtlas = new SoundAtlas(mAssetManager, "sounds/");
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
		mSoundAtlas.preload(names);
	}

	public void finishLoad() {
		if (mAssetManager.getQueuedAssets() > 0) {
			Gdx.app.error("Kernel", "Not all assets have been loaded yet, going to block (progress=" + mAssetManager.getProgress() + ")");
		}
		mTextureAtlas = mAssetManager.get("burgerparty.atlas");
		mSkin = new Skin(Gdx.files.internal("ui/skin.json"), mTextureAtlas);

		mSoundAtlas.finishLoad();

		mClickSound = mSoundAtlas.findSound("click");
		mClickListener = new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mClickSound.play();
			}
		};
	}

	public TextureAtlas getTextureAtlas() {
		return mTextureAtlas;
	}

	public Skin getSkin() {
		return mSkin;
	}

	public SoundAtlas getSoundAtlas() {
		return mSoundAtlas;
	}

	public ChangeListener getClickListener() {
		return mClickListener;
	}

	public AnimScriptLoader getAnimScriptLoader() {
		return mAnimScriptLoader;
	}

	AnimScriptLoader mAnimScriptLoader = new AnimScriptLoader();
	SoundAtlas mSoundAtlas;
	TextureAtlas mTextureAtlas;
	Skin mSkin;
	Sound mClickSound;
	ChangeListener mClickListener;
	AssetManager mAssetManager;
}