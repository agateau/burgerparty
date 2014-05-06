package com.agateau.burgerparty.model;

import com.agateau.burgerparty.AdSystem;
import com.agateau.burgerparty.utils.NLog;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.TimeUtils;

public class AdController {
    private static int START_COUNT_BEFORE_ADS = 8;
    private static long MINUTES_BETWEEN_ADS = 5;

    private final AdSystem mAdSystem;
    private final Preferences mPrefs;

    private boolean mFailedOnLastLevel = false;

    private static NLog log;

    public AdController(Preferences prefs, AdSystem adSystem) {
        mPrefs = prefs;
        mAdSystem = adSystem;
        if (log == null) {
            log = NLog.getRoot().create(getClass().getSimpleName());
        }
        mAdSystem.preloadAd();
    }

    public void maybeShowAd(Runnable next) {
        if (mustShowAd()) {
            mAdSystem.showAd(next);
        } else {
            mFailedOnLastLevel = false;
            next.run();
        }
    }

    public void onStartPlaying() {
        int startCount = mPrefs.getInteger("startCount", 0) + 1;
        mPrefs.putInteger("startCount", startCount);
        mPrefs.flush();
    }

    public void onLevelFailed() {
        mFailedOnLastLevel = true;
    }

    private boolean mustShowAd() {
        if (mPrefs.getInteger("startCount", 0) < START_COUNT_BEFORE_ADS) {
            log.d("Not showing ad: startCount=%d < %d", mPrefs.getInteger("startCount", 0), START_COUNT_BEFORE_ADS);
            return false;
        }

        if (mFailedOnLastLevel) {
            log.d("Not showing ad: failed on last level");
            return false;
        }

        long adDisplayTime = mPrefs.getLong("adDisplayTime", 0);
        long now = TimeUtils.millis();
        long delta = (now - adDisplayTime) / (60 * 1000);
        boolean hasAd = mAdSystem.isAdAvailable();
        log.i("mustShowAd: adDisplayTime=%d, now=%d, delta=%d, hasAd=%b", adDisplayTime, now, delta, hasAd);
        if (delta > MINUTES_BETWEEN_ADS && hasAd) {
            log.d("Showing ad");
            mPrefs.putLong("adDisplayTime", now);
            mPrefs.flush();
            return true;
        } else {
            log.d("Not showing ad");
            return false;
        }
    }
}
