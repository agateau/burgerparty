package com.agateau.burgerparty.view;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class PauseOverlay extends Overlay {
	private WorldView mWorldView;
	private BurgerPartyGame mGame;

	public PauseOverlay(WorldView worldView, BurgerPartyGame game, TextureAtlas atlas, Skin skin) {
		super(atlas);
		mWorldView = worldView;
		mGame = game;

		Label label = new Label("Paused", skin);

		TextButton resumeButton = new TextButton("Resume", skin);
		resumeButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mWorldView.resume();
			}
		});

		TextButton restartButton = new TextButton("Restart", skin);
		restartButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mGame.startLevel(mGame.getLevelGroupIndex(), mGame.getLevelIndex());
			}
		});

		TextButton selectLevelButton = new TextButton("Levels", skin);
		selectLevelButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mGame.selectLevel();
			}
		});

		UiUtils.setButtonSize(resumeButton);
		UiUtils.setButtonSize(restartButton);
		UiUtils.setButtonSize(selectLevelButton);

		AnchorGroup group = new AnchorGroup();
		addActor(group);
		group.setFillParent(true);
		group.setSpacing(UiUtils.SPACING);

		group.addRule(label, Anchor.BOTTOM_CENTER, this, Anchor.CENTER, 0, 2);
		group.addRule(resumeButton, Anchor.TOP_CENTER, this, Anchor.CENTER);
		group.addRule(restartButton, Anchor.TOP_CENTER, resumeButton, Anchor.BOTTOM_CENTER, 0, -1);
		group.addRule(selectLevelButton, Anchor.TOP_CENTER, restartButton, Anchor.BOTTOM_CENTER, 0, -1);
	}
}
