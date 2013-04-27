package com.agateau.burgerparty.view;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class SoundAtlas {
	public Action createPlayAction(String name) {
		Sound sound = findSound(name);
		return Actions.run(new PlayRunnable(sound));
	}

	public Sound findSound(String name) {
		Sound sound = mSoundMap.get(name);
		if (sound == null) {
			String filename = "sounds/" + name;
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
}
