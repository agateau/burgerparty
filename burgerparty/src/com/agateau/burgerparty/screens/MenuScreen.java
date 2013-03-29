package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.UiUtils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MenuScreen extends BaseScreen {
	public MenuScreen(BurgerPartyGame game, Skin skin) {
		super(game, skin);
		setupWidgets(skin);
	}

	private void setupWidgets(Skin skin) {
		AnchorGroup group = new AnchorGroup();
		group.setSpacing(UiUtils.SPACING);
		getStage().addActor(group);
		group.setFillParent(true);

		Label titleLabel = new Label("Burger Party", skin);

		TextButton startButton = new TextButton("Start", skin);
		startButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				getGame().selectLevel();
			}
		});
		UiUtils.setButtonSize(startButton);

		group.addRule(titleLabel, Anchor.BOTTOM_CENTER, group, Anchor.CENTER, 0, 1);
		group.addRule(startButton, Anchor.TOP_CENTER, group, Anchor.CENTER);
	}
}
