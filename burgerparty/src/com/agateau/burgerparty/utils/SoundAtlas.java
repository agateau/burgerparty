package com.agateau.burgerparty.utils;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class SoundAtlas {
    private Map<String, Sound> mSoundMap = new HashMap<String, Sound>();

    private final AssetManager mAssetManager;
    private final MusicController mMusicController;
    private String mDir;
    private String[] mPendingNames;

    private class AtlasSound implements Sound {
        public AtlasSound(Sound sound) {
            mSound = sound;
        }

        @Override
        public long play() {
            if (mMusicController.isMuted()) {
                return -1;
            }
            return mSound.play();
        }

        @Override
        public long play(float volume) {
            if (mMusicController.isMuted()) {
                return -1;
            }
            return mSound.play(volume);
        }

        @Override
        public long play(float volume, float pitch, float pan) {
            if (mMusicController.isMuted()) {
                return -1;
            }
            return mSound.play(volume, pitch, pan);
        }

        @Override
        public long loop() {
            if (mMusicController.isMuted()) {
                return -1;
            }
            return mSound.loop();
        }

        @Override
        public long loop(float volume) {
            if (mMusicController.isMuted()) {
                return -1;
            }
            return mSound.loop(volume);
        }

        @Override
        public long loop(float volume, float pitch, float pan) {
            if (mMusicController.isMuted()) {
                return -1;
            }
            return mSound.loop(volume, pitch, pan);
        }

        @Override
        public void stop() {
            if (mMusicController.isMuted()) {
                return;
            }
            mSound.stop();
        }

        @Override
        public void pause() {
            if (mMusicController.isMuted()) {
                return;
            }
            mSound.pause();
        }

        @Override
        public void resume() {
            if (mMusicController.isMuted()) {
                return;
            }
            mSound.resume();
        }

        @Override
        public void dispose() {
            if (mMusicController.isMuted()) {
                return;
            }
            mSound.dispose();
        }

        @Override
        public void stop(long soundId) {
            if (mMusicController.isMuted()) {
                return;
            }
            mSound.stop(soundId);
        }

        @Override
        public void pause(long soundId) {
            if (mMusicController.isMuted()) {
                return;
            }
            mSound.pause(soundId);
        }

        @Override
        public void resume(long soundId) {
            if (mMusicController.isMuted()) {
                return;
            }
            mSound.resume(soundId);
        }

        @Override
        public void setLooping(long soundId, boolean looping) {
            if (mMusicController.isMuted()) {
                return;
            }
            mSound.setLooping(soundId, looping);
        }

        @Override
        public void setPitch(long soundId, float pitch) {
            if (mMusicController.isMuted()) {
                return;
            }
            mSound.setPitch(soundId, pitch);
        }

        @Override
        public void setVolume(long soundId, float volume) {
            if (mMusicController.isMuted()) {
                return;
            }
            mSound.setVolume(soundId, volume);
        }

        @Override
        public void setPan(long soundId, float pan, float volume) {
            if (mMusicController.isMuted()) {
                return;
            }
            mSound.setPan(soundId, pan, volume);
        }

        @Override
        public void setPriority(long soundId, int priority) {
            if (mMusicController.isMuted()) {
                return;
            }
            mSound.setPriority(soundId, priority);
        }

        Sound mSound;
    }

    public SoundAtlas(AssetManager manager, String dir, MusicController musicController) {
        mAssetManager = manager;
        mMusicController = musicController;
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
            Sound sound = mAssetManager.get(filename);
            assert(sound != null);
            mSoundMap.put(name, new AtlasSound(sound));
        }
        NLog.i("Loaded %d sounds", mPendingNames.length);
    }

    public Action createPlayAction(String name) {
        Sound sound = findSound(name);
        return Actions.run(new PlayRunnable(sound));
    }

    public Action createPlayAction(String name, float pitch) {
        Sound sound = findSound(name);
        return Actions.run(new PlayRunnable(sound, pitch));
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
            this(sound, 1);
        }

        public PlayRunnable(Sound sound, float pitch) {
            assert(sound != null);
            mSound = sound;
            mPitch = pitch;
        }

        @Override
        public void run() {
            mSound.play(1, mPitch, 0 /* pan */);
        }

        private final Sound mSound;
        private final float mPitch;
    }
}
