package com.agateau.burgerparty;

import com.agateau.burgerparty.utils.NLog;
import com.badlogic.gdx.backends.android.AndroidApplication;

import com.heyzap.sdk.ads.HeyzapAds;
import com.heyzap.sdk.ads.HeyzapAds.OnStatusListener;
import com.heyzap.sdk.ads.InterstitialAd;

import android.os.Handler;
import android.os.Message;

public class HeyzapAdSystem implements AdSystem {
    private static final String PUBLISHER_ID = "8e60c0ef3a2232935daf2006bb5367f6";
    private static final int PRELOAD_MSG = 0;
    private static final int SHOW_MSG = 1;

    private class StatusListener implements OnStatusListener {
        @Override
        public void onShow(String tag) {
            NLog.i("tag=%s", tag);
        }

        @Override
        public void onClick(String tag) {
            NLog.i("tag=%s", tag);
        }

        @Override
        public void onHide(String tag) {
            NLog.i("onHide: tag=%s", tag);
            mApplication.postRunnable(mOnFinishedRunnable);
        }

        @Override
        public void onFailedToShow(String tag) {
            NLog.i("onFailedToSHow: tag=%s", tag);
            mApplication.postRunnable(mOnFinishedRunnable);
        }

        @Override
        public void onAvailable(String tag) {
            NLog.i("onAvailable: tag=%s", tag);
        }

        @Override
        public void onFailedToFetch(String tag) {
            NLog.i("onFailedToFetch: tag=%s", tag);
        }

        @Override
        public void onAudioStarted() {
            NLog.i("onAudioStarted");
        }

        @Override
        public void onAudioFinished() {
            NLog.i("onAudioFinished");
        }
    };

    private static class AdHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case PRELOAD_MSG:
                preload();
                break;
            case SHOW_MSG:
                show();
                break;
            }
        }

        void preload() {
            NLog.i("preload");
            InterstitialAd.fetch();
        }

        void show() {
            if (InterstitialAd.isAvailable()) {
                NLog.i("show: ad is available");
                InterstitialAd.display(mController.mApplication);
            } else {
                NLog.i("show: ad is not available");
            }
        }

        HeyzapAdSystem mController;
    };

    private final AndroidApplication mApplication;
    private final AdHandler mHandler;

    // Called when user is back to the game
    private final Runnable mOnFinishedRunnable = new Runnable() {
        @Override
        public void run() {
            preloadAd();
            mAfter.run();
        }
    };
    private Runnable mAfter;

    public HeyzapAdSystem(AndroidApplication application) {
        mApplication = application;
        mHandler = new AdHandler();
        mHandler.mController = this;

        HeyzapAds.start(PUBLISHER_ID, application);
        InterstitialAd.fetch();

        StatusListener listener = new StatusListener();
        InterstitialAd.setOnStatusListener(listener);
    }

    @Override
    public void preloadAd() {
        mHandler.sendEmptyMessage(PRELOAD_MSG);
    }

    @Override
    public boolean isAdAvailable() {
        return InterstitialAd.isAvailable();
    }

    @Override
    public void showAd(Runnable after) {
        mAfter = after;
        mHandler.sendEmptyMessage(SHOW_MSG);
    }
}
