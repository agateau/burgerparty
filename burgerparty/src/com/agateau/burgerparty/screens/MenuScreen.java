package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.RoundButton;
import com.agateau.burgerparty.utils.TiledImage;
import com.agateau.burgerparty.utils.UiUtils;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MenuScreen extends BaseScreen {

	public MenuScreen(BurgerPartyGame game, TextureAtlas atlas, Skin skin) {
		super(game, skin);
		TiledImage bgImage = new TiledImage(atlas.findRegion("ui/menu-bg"));
		setBackgroundActor(bgImage);
		setupWidgets(atlas, skin);
	}

	private void setupWidgets(TextureAtlas atlas, Skin skin) {
		AnchorGroup group = new AnchorGroup();
		group.setSpacing(UiUtils.SPACING);
		getStage().addActor(group);
		group.setFillParent(true);

		Image titleImage = new Image(atlas.findRegion("ui/title"));

		RoundButton startButton = new RoundButton(skin, "ui/icon-play");
		startButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				getGame().selectLevel();
			}
		});
		//UiUtils.setButtonSize(startButton);

		group.addRule(titleImage, Anchor.TOP_CENTER, group, Anchor.TOP_CENTER, 0, -1);
		group.addRule(startButton, Anchor.TOP_CENTER, titleImage, Anchor.BOTTOM_CENTER);
	}
}
