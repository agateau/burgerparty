package com.agateau.burgerparty.utils;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class SoundAtlas {
	public SoundAtlas(AssetManager manager, String dir) {
		mAssetManager = manager;
		mDir = dir;
		if (!mDir.endsWith("/")) {
			mDir = mDir.concat("/");
		}
	}

	public void preload(String[] names) {
		mPendingNames = names;
		for (int i=0, n=names.length; i < n; ++i) {
			String filename = mDir + names[i];
			mAssetManager.load(filename, Sound.class);
		}
	}

	public void finishLoad() {
		for (int i=0, n=mPendingNames.length; i < n; ++i) {
			String filename = mDir + mPendingNames[i];
			String name = mPendingNames[i].replaceFirst("\\.[a-z]+$", "");
			Gdx.app.log("SoundAtlas", "Getting " + filename + " as " + name);
			Sound sound = mAssetManager.get(filename);
			assert(sound != null);
			mSoundMap.put(name, sound);
		}
	}

	public Action createPlayAction(String name) {
		Sound sound = findSound(name);
		return Actions.run(new PlayRunnable(sound));
	}

	public boolean contains(String name) {
		return mSoundMap.containsKey(name);
	}

	public Sound findSound(String name) {
		Sound sound = mSoundMap.get(name);
		if (sound == null) {
			throw new RuntimeException("Could not find sound named '" + name + "'");
		}
		return sound;
	}

	private class PlayRunnable implements Runnable {
		public PlayRunnable(Sound sound) {
			assert(sound != null);
			mSound = sound;
		}

		@Override
		public void run() {
			mSound.play();
		}

		private Sound mSound;
	}

	Map<String, Sound> mSoundMap = new HashMap<String, Sound>();

	private AssetManager mAssetManager;
	private String mDir;
	private String[] mPendingNames;
}
