package com.agateau.burgerparty;

import com.agateau.burgerparty.utils.NLog;

import com.heyzap.sdk.ads.HeyzapAds;
import com.heyzap.sdk.ads.HeyzapAds.OnStatusListener;
import com.heyzap.sdk.ads.InterstitialAd;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

public class AndroidAdController implements AdController {
	private static final int PRELOAD_MSG = 0;
	private static final int SHOW_MSG = 1;

	private class StatusListener implements OnStatusListener {
		@Override
		public void onShow(String tag) {
			log.i("onShow: tag={}", tag);
		}

		@Override
		public void onClick(String tag) {
			log.i("onClick: tag={}", tag);
		}

		@Override
		public void onHide(String tag) {
			log.i("onHide: tag={}", tag);
		}

		@Override
		public void onFailedToShow(String tag) {
			log.i("onFailedToSHow: tag={}", tag);
		}

		@Override
		public void onAvailable(String tag) {
			log.i("onAvailable: tag={}", tag);
		}

		@Override
		public void onFailedToFetch(String tag) {
			log.i("onFailedToFetch: tag={}", tag);
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
			switch(msg.what) {
			case PRELOAD_MSG:
				preload();
				break;
			case SHOW_MSG:
				show();
				break;
			}
		}

		void preload() {
			log.i("preload");
			InterstitialAd.fetch();
		}

		void show() {
			if (InterstitialAd.isAvailable()) {
				log.i("show: ad is available");
				InterstitialAd.display(mActivity);
			} else {
				log.i("show: ad is not available");
			}
		}

		Activity mActivity;
		NLog log;
	};

	private NLog log;
	private final AdHandler mHandler;

	public AndroidAdController(Activity activity) {
		log = NLog.createForClass(this);
		HeyzapAds.start(activity);
		InterstitialAd.fetch();
		HeyzapAds.setOnStatusListener(new StatusListener());
		mHandler = new AdHandler();
		mHandler.mActivity = activity;
		mHandler.log = log;
	}

	@Override
	public void preloadAd() {
		mHandler.sendEmptyMessage(PRELOAD_MSG);
	}

	@Override
	public void showAd() {
		mHandler.sendEmptyMessage(SHOW_MSG);
	}
}
