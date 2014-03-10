package com.agateau.burgerparty;

import com.agateau.burgerparty.utils.NLog;
import com.badlogic.gdx.backends.android.AndroidApplication;

import com.heyzap.sdk.ads.HeyzapAds;
import com.heyzap.sdk.ads.HeyzapAds.OnStatusListener;
import com.heyzap.sdk.ads.InterstitialAd;

import android.os.Handler;
import android.os.Message;

public class AndroidAdController implements AdController {
    private static final int PRELOAD_MSG = 0;
    private static final int SHOW_MSG = 1;

    private class StatusListener implements OnStatusListener {
        @Override
        public void onShow(String tag) {
            log.i("onShow: tag=%s", tag);
        }

        @Override
        public void onClick(String tag) {
            log.i("onClick: tag=%s", tag);
        }

        @Override
        public void onHide(String tag) {
            log.i("onHide: tag=%s", tag);
            mApplication.postRunnable(mOnFinishedRunnable);
        }

        @Override
        public void onFailedToShow(String tag) {
            log.i("onFailedToSHow: tag=%s", tag);
            mApplication.postRunnable(mOnFinishedRunnable);
        }

        @Override
        public void onAvailable(String tag) {
            log.i("onAvailable: tag=%s", tag);
        }

        @Override
        public void onFailedToFetch(String tag) {
            log.i("onFailedToFetch: tag=%s", tag);
        }

        @Override
        public void onAudioStarted() {
            log.i("onAudioStarted");
        }

        @Override
        public void onAudioFinished() {
            log.i("onAudioFinished");
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
            mController.log.i("preload");
            InterstitialAd.fetch();
        }

        void show() {
            if (InterstitialAd.isAvailable()) {
                mController.log.i("show: ad is available");
                InterstitialAd.display(mController.mApplication);
            } else {
                mController.log.i("show: ad is not available");
            }
        }

        AndroidAdController mController;
    };

    private final NLog log;
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

    public AndroidAdController(AndroidApplication application) {
        log = NLog.createForClass(this);
        mApplication = application;
        mHandler = new AdHandler();
        mHandler.mController = this;

        HeyzapAds.start(application);
        InterstitialAd.fetch();

        StatusListener listener = new StatusListener();
        HeyzapAds.setOnStatusListener(listener);
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
