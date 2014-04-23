package com.agateau.burgerparty.model;

import com.agateau.burgerparty.AdSystem;
import com.agateau.burgerparty.utils.NLog;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.TimeUtils;

public class AdController {
    private static int START_COUNT_BEFORE_ADS = 8;
    private static long MINUTES_BETWEEN_ADS = 12;

    private final AdSystem mAdSystem;
    private final Preferences mPrefs;
    private static NLog log;

    public AdController(Preferences prefs, AdSystem adSystem) {
        mPrefs = prefs;
        mAdSystem = adSystem;
        if (log == null) {
            log = NLog.getRoot().create(getClass().getSimpleName());
        }
        mAdSystem.preloadAd();
    }

    public void onStartLevel(Runnable next) {
        if (mustShowAd()) {
            mAdSystem.showAd(next);
        } else {
            next.run();
        }
    }

    private boolean mustShowAd() {
        int startCount = mPrefs.getInteger("startCount", 0) + 1;
        log.i("mustShowAd: startCount=%d", startCount);
        mPrefs.putInteger("startCount", startCount);
        mPrefs.flush();
        if (startCount < START_COUNT_BEFORE_ADS) {
            return false;
        }

        long adDisplayTime = mPrefs.getLong("adDisplayTime", 0);
        long now = TimeUtils.millis();
        long delta = (now - adDisplayTime) / (60 * 1000);
        boolean hasAd = mAdSystem.isAdAvailable();
        log.i("mustShowAd: adDisplayTime=%d, now=%d, delta=%d, hasAd=%b", adDisplayTime, now, delta, hasAd);
        if (delta > MINUTES_BETWEEN_ADS && hasAd) {
            mPrefs.putLong("adDisplayTime", now);
            mPrefs.flush();
            return true;
        } else {
            return false;
        }
    }
}
