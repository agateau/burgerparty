package com.agateau.burgerparty.model;

import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.StageScreen;
import com.agateau.burgerparty.view.BurgerPartyUiBuilder;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MiniGameOverScreen extends StageScreen {
	private static final float BG_FADE_DURATION = 1;

	public MiniGameOverScreen(MiniGame miniGame, Pixmap bgPix) {
		super(miniGame.getAssets().getSkin());
		mMiniGame = miniGame;
		setupBg(bgPix);
		setupWidgets();
	}

	@Override
	public void onBackPressed() {
		mMiniGame.showStartScreen();
	}

	private void setupBg(Pixmap bgPix) {
		Texture texture = new Texture(bgPix);
		bgPix.dispose();
		Image image = new Image(texture);
		image.addAction(Actions.alpha(0.1f, BG_FADE_DURATION));
		setBackgroundActor(image);
	}

	private void setupWidgets() {
		BurgerPartyUiBuilder builder = new BurgerPartyUiBuilder(mMiniGame.getAssets());
		builder.build(FileUtils.assets("screens/minigameover.gdxui"));
		AnchorGroup root = builder.getActor("root");
		getStage().addActor(root);
		root.setFillParent(true);
		root.addAction(Actions.alpha(0));
		root.addAction(Actions.fadeIn(BG_FADE_DURATION));

		builder.<ImageButton>getActor("backButton").addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				onBackPressed();
			}
		});
		builder.<ImageButton>getActor("startButton").addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mMiniGame.showMainScreen();
			}
		});
	}

	private MiniGame mMiniGame;
}
