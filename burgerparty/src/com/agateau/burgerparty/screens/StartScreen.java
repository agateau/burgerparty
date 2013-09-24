package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.RoundButton;
import com.agateau.burgerparty.utils.UiUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class StartScreen extends BurgerPartyScreen {

	public StartScreen(BurgerPartyGame game, TextureAtlas atlas, Skin skin) {
		super(game, skin);
		Image bgImage = new Image(atlas.findRegion("ui/menu-bg"));
		setBackgroundActor(bgImage);
		setupWidgets(atlas, skin);
	}

	private void setupWidgets(TextureAtlas atlas, Skin skin) {
		AnchorGroup group = new AnchorGroup();
		group.setSpacing(UiUtils.SPACING);
		getStage().addActor(group);
		group.setFillParent(true);

		Image titleImage = new Image(atlas.findRegion("ui/title"));

		RoundButton normalStartButton = Kernel.createRoundButton("ui/icon-play");
		normalStartButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				getGame().selectLevel(0);
			}
		});

		RoundButton sandBoxStartButton = Kernel.createRoundButton("ui/icon-play");
		sandBoxStartButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				getGame().startSandBox();
			}
		});

		group.addRule(titleImage, Anchor.CENTER, group, Anchor.CENTER, 0, 2);
		group.addRule(normalStartButton, Anchor.TOP_CENTER, titleImage, Anchor.BOTTOM_CENTER, -2, 0);
		group.addRule(sandBoxStartButton, Anchor.TOP_CENTER, titleImage, Anchor.BOTTOM_CENTER, 2, 0);
	}

	@Override
	public void onBackPressed() {
		Gdx.app.exit();
	}
}
