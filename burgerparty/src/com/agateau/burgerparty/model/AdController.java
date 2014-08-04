package com.agateau.burgerparty.model;

import com.agateau.burgerparty.AdSystem;
import com.agateau.burgerparty.utils.NLog;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.TimeUtils;

public class AdController {
    private static int START_COUNT_BEFORE_ADS = 3;
    private static long MINUTES_BETWEEN_ADS = 3;

    private final AdSystem mAdSystem;
    private final Preferences mPrefs;

    private boolean mSkipNextAd = false;

    public AdController(Preferences prefs, AdSystem adSystem) {
        mPrefs = prefs;
        mAdSystem = adSystem;
        mAdSystem.preloadAd();
    }

    public void maybeShowAd(Runnable next) {
        if (mustShowAd()) {
            mAdSystem.showAd(next);
        } else {
            mSkipNextAd = false;
            next.run();
        }
    }

    public void onStartPlaying() {
        int startCount = mPrefs.getInteger("startCount", 0) + 1;
        mPrefs.putInteger("startCount", startCount);
        mPrefs.flush();
        // Skip next ad to make sure we don't show an ad before the first level has been played
        mSkipNextAd = true;
    }

    public void onLevelRestarted() {
        mSkipNextAd = true;
    }

    public void onLevelFailed() {
        mSkipNextAd = true;
    }

    private boolean mustShowAd() {
        if (mPrefs.getInteger("startCount", 0) < START_COUNT_BEFORE_ADS) {
            NLog.d("Not showing ad: startCount=%d < %d", mPrefs.getInteger("startCount", 0), START_COUNT_BEFORE_ADS);
            return false;
        }

        if (mSkipNextAd) {
            NLog.d("Not showing ad: skipped");
            return false;
        }

        long adDisplayTime = mPrefs.getLong("adDisplayTime", 0);
        long now = TimeUtils.millis();
        long delta = (now - adDisplayTime) / (60 * 1000);
        boolean hasAd = mAdSystem.isAdAvailable();
        NLog.i("adDisplayTime=%d, now=%d, delta=%dmn, hasAd=%b", adDisplayTime, now, delta, hasAd);
        if (delta >= MINUTES_BETWEEN_ADS && hasAd) {
            NLog.d("Showing ad");
            mPrefs.putLong("adDisplayTime", now);
            mPrefs.flush();
            return true;
        } else {
            NLog.d("Not showing ad");
            return false;
        }
    }
}
