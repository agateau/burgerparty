package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.MusicController;
import com.agateau.burgerparty.utils.NLog;
import com.agateau.burgerparty.utils.RefreshHelper;
import com.agateau.burgerparty.view.BurgerPartyUiBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.TimeUtils;

public class StartScreen extends BurgerPartyScreen {
	private static int START_COUNT_BEFORE_ADS = 8;
	private static long MINUTES_BETWEEN_ADS = 12;
	private static NLog log;

	public StartScreen(BurgerPartyGame game) {
		super(game);
		if (log == null) {
			log = NLog.getRoot().create(getClass().getSimpleName());
		}
		Image bgImage = new Image(getTextureAtlas().findRegion("ui/menu-bg"));
		setBackgroundActor(bgImage);
		setupWidgets();
		new RefreshHelper(getStage()) {
			@Override
			protected void refresh() {
				getGame().showStartScreen();
				dispose();
			}
		};
	}

	private void setupWidgets() {
		BurgerPartyUiBuilder builder = new BurgerPartyUiBuilder(getGame().getAssets());
		builder.build(FileUtils.assets("screens/start.gdxui"));
		AnchorGroup root = builder.getActor("root");
		getStage().addActor(root);
		root.setFillParent(true);

		builder.<ImageButton>getActor("startButton").addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				onStartClicked();
			}
		});
		builder.<ImageButton>getActor("aboutButton").addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				getGame().showAboutScreen();
			}
		});
		mMuteButton = builder.<ImageButton>getActor("muteButton");
		mMuteButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				MusicController controller = getGame().getMusicController();
				controller.setMuted(!controller.isMuted());
				updateMuteButton();
			}
		});
		updateMuteButton();
	}

	private void onStartClicked() {
		if (mustShowAd()) {
			getGame().getAdController().showAd(new Runnable() {
				@Override
				public void run() {
					log.i("startButton(runnable): showWorldListScreen");
					getGame().showWorldListScreen();
				}
			});
		} else {
			getGame().showWorldListScreen();
		}
	}

	private boolean mustShowAd() {
		Preferences prefs = getGame().getPreferences();
		int startCount = prefs.getInteger("startCount", 0) + 1;
		log.i("mustShowAd: startCount=%d", startCount);
		prefs.putInteger("startCount", startCount);
		prefs.flush();
		if (startCount < START_COUNT_BEFORE_ADS) {
			return false;
		}

		long adDisplayTime = prefs.getLong("adDisplayTime", 0);
		long now = TimeUtils.millis();
		long delta = (now - adDisplayTime) / (60 * 1000);
		boolean hasAd = getGame().getAdController().isAdAvailable();
		log.i("mustShowAd: adDisplayTime=%d, now=%d, delta=%d, hasAd=%b", adDisplayTime, now, delta, hasAd);
		if (delta > MINUTES_BETWEEN_ADS && hasAd) {
			prefs.putLong("adDisplayTime", now);
			prefs.flush();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onBackPressed() {
		Gdx.app.exit();
	}

	private void updateMuteButton() {
		boolean muted = getGame().getMusicController().isMuted();
		Drawable drawable = getGame().getAssets().getSkin().getDrawable(muted ? "ui/icon-sound-off" : "ui/icon-sound-on");
		mMuteButton.getImage().setDrawable(drawable);
	}

	private ImageButton mMuteButton;
}
