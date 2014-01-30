package com.agateau.burgerparty;

import com.agateau.burgerparty.utils.NLog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

public class AndroidAdController implements AdController {
	private static final int PRELOAD_MSG = 0;
	private static final int SHOW_MSG = 1;
	private static String INTERSTITIAL_ID = "ca-app-pub-1401038189223985/1605506750";

	private class LogAdListener extends AdListener {
		@Override
		public void onAdClosed() {
			log.i("onAdClosed");
			preloadAd();
		}
		@Override
		public void onAdFailedToLoad(int errorCode) {
			log.i("onAdFailedToLoad: errorCode=%d", errorCode);
		}
		@Override
		public void onAdLeftApplication() {
			log.i("onAdLeftApplication");
		}
		@Override
		public void onAdLoaded() {
			log.i("onAdLoaded");
		}
		@Override
		public void onAdOpened() {
			log.i("onAdOpened");
		}
	}

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
			log.i("preloadAd");
			AdRequest.Builder builder = new AdRequest.Builder();
			builder.tagForChildDirectedTreatment(true);
			builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
			builder.addTestDevice("A5A48A10EF5E078F1B701AF026C0298C"); // NEXUS-S
			mInterstitial.loadAd(builder.build());
		}

		void show() {
			if (mInterstitial.isLoaded()) {
				log.i("showAd: showing");
				mInterstitial.show();
			} else {
				log.e("showAd: not loaded");
			}
		}

		Activity mActivity;
		InterstitialAd mInterstitial;
		NLog log;
	};

	private NLog log;
	private final AdHandler mHandler;

	public AndroidAdController(Activity activity) {
		log = NLog.createForClass(this);
		mHandler = new AdHandler();
		mHandler.mActivity = activity;
		mHandler.log = log;
		mHandler.mInterstitial = new InterstitialAd(mHandler.mActivity);
		mHandler.mInterstitial.setAdListener(new LogAdListener());
		mHandler.mInterstitial.setAdUnitId(INTERSTITIAL_ID);
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
