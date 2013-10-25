package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.UiUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class StartScreen extends BurgerPartyScreen {

	public StartScreen(BurgerPartyGame game) {
		super(game);
		Image bgImage = new Image(getTextureAtlas().findRegion("ui/menu-bg"));
		setBackgroundActor(bgImage);
		setupWidgets();
	}

	private void setupWidgets() {
		AnchorGroup group = new AnchorGroup();
		group.setSpacing(UiUtils.SPACING);
		getStage().addActor(group);
		group.setFillParent(true);

		Image titleImage = new Image(getTextureAtlas().findRegion("ui/title"));

		ImageTextButton normalStartButton = Kernel.createTextButton(getGame().getAssets(), "Start", "ui/icon-play");
		normalStartButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				getGame().showLevelListScreen(0);
			}
		});

		ImageTextButton sandBoxStartButton = Kernel.createTextButton(getGame().getAssets(), "Sand Box", "ui/icon-play");
		sandBoxStartButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				getGame().startSandBox();
			}
		});

		group.addRule(titleImage, Anchor.TOP_CENTER, group, Anchor.TOP_CENTER, 0, -0.5f);
		group.addRule(normalStartButton, Anchor.TOP_CENTER, titleImage, Anchor.BOTTOM_CENTER, 0, 0.5f);
		group.addRule(sandBoxStartButton, Anchor.TOP_CENTER, normalStartButton, Anchor.BOTTOM_CENTER, 0, -0.5f);
	}

	@Override
	public void onBackPressed() {
		Gdx.app.exit();
	}
}
