package com.agateau.burgerparty;

import com.agateau.burgerparty.utils.NLog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class AndroidAdController implements AdController {
	private static final int SHOW_ADS = 1;
	private static final int HIDE_ADS = 0;
	private static String BANNER_ID = "ca-app-pub-1401038189223985/7100310357";

	private NLog log;

	private class LogAdListener extends AdListener {
		@Override
		public void onAdClosed() {
			log.i("ad closed");
		}
		@Override
		public void onAdFailedToLoad(int errorCode) {
			log.i("ad failed to load. error: %d", errorCode);
		}
		@Override
		public void onAdLeftApplication() {
			log.i("ad left application");
		}
		@Override
		public void onAdLoaded() {
			log.i("ad loaded");
		}
		@Override
		public void onAdOpened() {
			log.i("ad opened");
		}
	}

	private static class AdHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case SHOW_ADS:
				mBannerView.setVisibility(View.VISIBLE);
				break;
			case HIDE_ADS:
				mBannerView.setVisibility(View.GONE);
				break;
			}
		}

		AdView mBannerView;
	};
	private final AdHandler mHandler;

	public AndroidAdController(Activity activity) {
		log = NLog.createForClass(this);
		mHandler = new AdHandler();
		AdView adView = new AdView(activity);
		adView.setAdUnitId(BANNER_ID);
		adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
		adView.setAdListener(new LogAdListener());
		adView.setVisibility(View.GONE);
		mHandler.mBannerView = adView;

		AdRequest adRequest = new AdRequest.Builder()
			.tagForChildDirectedTreatment(true)
			.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
			.addTestDevice("A5A48A10EF5E078F1B701AF026C0298C") // NEXUS-S
			.build();
		adView.loadAd(adRequest);
	}

	public View getBannerView() {
		return mHandler.mBannerView;
	}

	@Override
	public void showFullScreenAd() {
	}

	@Override
	public void showBanner(float x, float y) {
		mHandler.sendEmptyMessage(SHOW_ADS);
	}

	@Override
	public void hideBanner() {
		mHandler.sendEmptyMessage(HIDE_ADS);
	}
}
