package com.agateau.burgerparty.utils;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;

public class MusicController {
	private float FADE_STEP = 0.1f;
	private float FADE_INTERVAL = 0.1f;
	private class Fader extends Timer.Task {
		@Override
		public void run() {
			setVolume(mMusic.getVolume() + FADE_STEP * mDirection);
			float volume = mMusic.getVolume();
			if (volume == 0 && mDirection == -1) {
				mMusic.stop();
				return;
			}
			if (volume == 1 && mDirection == 1) {
				return;
			}
			scheduleUpdate();
		}

		public void fade(int direction) {
			mDirection = direction;
			scheduleUpdate();
		}

		private void scheduleUpdate() {
			if (!isScheduled()) {
				Timer.schedule(this, FADE_INTERVAL);
			}
		}

		int mDirection = 0;
	}

	public MusicController(Music music) {
		mMusic = music;
	}

	public void play() {
		if (mMusic.isPlaying()) {
			mFader.fade(1);
		} else {
			setVolume(1);
			mMusic.play();
		}
	}

	public void fadeOut() {
		if (mMusic.isPlaying()) {
			mFader.fade(-1);
		}
	}

	private void setVolume(float volume) {
		volume = MathUtils.clamp(volume, 0, 1);
		mMusic.setVolume(volume);
	}

	private Music mMusic;
	private Fader mFader = new Fader();
}
