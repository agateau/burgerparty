package com.agateau.burgerparty.utils;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class SoundAtlas {
	public SoundAtlas(String dir) {
		mDir = dir;
		if (!mDir.endsWith("/")) {
			mDir = mDir.concat("/");
		}
	}

	public Action createPlayAction(String name) {
		Sound sound = findSound(name);
		return Actions.run(new PlayRunnable(sound));
	}

	public Sound findSound(String name) {
		Sound sound = mSoundMap.get(name);
		if (sound == null) {
			String filename = mDir + name;
			sound = Gdx.audio.newSound(Gdx.files.internal(filename));
			mSoundMap.put(name, sound);
		}
		return sound;
	}

	private class PlayRunnable implements Runnable {
		public PlayRunnable(Sound sound) {
			mSound = sound;
		}

		@Override
		public void run() {
			mSound.play();
		}

		private Sound mSound;
	}

	Map<String, Sound> mSoundMap = new HashMap<String, Sound>();

	private String mDir;
}
