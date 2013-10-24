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
			setVolume(mVolume + FADE_STEP * mDirection);
			if (mVolume == 0 && mDirection == -1) {
				mMusic.stop();
				return;
			}
			if (mVolume == 1 && mDirection == 1) {
				return;
			}
			scheduleUpdate();
		}

		public void fade(int direction) {
			mDirection = direction;
			scheduleUpdate();
		}

		private void scheduleUpdate() {
			Timer.schedule(this, FADE_INTERVAL);
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

	public void setVolume(float volume) {
		mVolume = MathUtils.clamp(volume, 0, 1);
		mMusic.setVolume(mVolume);
	}

	private Music mMusic;
	private float mVolume = 1;
	private Fader mFader = new Fader();
}
