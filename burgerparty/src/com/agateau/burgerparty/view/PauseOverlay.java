package com.agateau.burgerparty.view;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.Overlay;
import com.agateau.burgerparty.utils.RoundButton;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class PauseOverlay extends Overlay {
	private WorldView mWorldView;
	private BurgerPartyGame mGame;

	public PauseOverlay(WorldView worldView, BurgerPartyGame game, TextureAtlas atlas, Skin skin) {
		super(atlas);
		mWorldView = worldView;
		mGame = game;

		String txt = "Level: " + (game.getLevelWorldIndex() + 1) + "-" + (game.getLevelIndex() + 1) + "\n";
		int highScore = game.getHighScore(game.getLevelWorldIndex(), game.getLevelIndex());
		if (highScore > 0) {
			txt += "High score: " + highScore;
		} else {
			txt += "No high score yet";
		}
		Label levelLabel = new Label(txt, skin);
		levelLabel.setAlignment(Align.center, Align.center);

		Label pausedLabel = new Label("Paused", skin);

		RoundButton resumeButton = Kernel.createRoundButton("ui/icon-play");
		resumeButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mWorldView.resume();
			}
		});

		RoundButton restartButton = Kernel.createRoundButton("ui/icon-restart");
		restartButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mGame.startLevel(mGame.getLevelWorldIndex(), mGame.getLevelIndex());
			}
		});

		RoundButton selectLevelButton = Kernel.createRoundButton("ui/icon-levels");
		selectLevelButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mGame.selectLevel(mGame.getLevelWorldIndex());
			}
		});

		AnchorGroup group = new AnchorGroup();
		addActor(group);
		group.setFillParent(true);
		group.setSpacing(UiUtils.SPACING);

		group.addRule(levelLabel, Anchor.TOP_CENTER, this, Anchor.TOP_CENTER);
		group.addRule(resumeButton, Anchor.CENTER, this, Anchor.CENTER);
		group.addRule(pausedLabel, Anchor.BOTTOM_CENTER, resumeButton, Anchor.TOP_CENTER, 0, 1);
		group.addRule(restartButton, Anchor.BOTTOM_RIGHT, this, Anchor.BOTTOM_CENTER, -0.5f, 1);
		group.addRule(selectLevelButton, Anchor.BOTTOM_LEFT, this, Anchor.BOTTOM_CENTER, 0.5f, 1);
	}

	@Override
	public void onBackPressed() {
		Gdx.app.log("PauseOverlay", "onBackPressed");
		mWorldView.resume();
	}
}
