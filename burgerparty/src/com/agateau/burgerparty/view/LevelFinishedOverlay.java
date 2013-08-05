package com.agateau.burgerparty.view;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.model.LevelResult;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.HorizontalGroup;
import com.agateau.burgerparty.utils.RoundButton;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class LevelFinishedOverlay extends Overlay {
	public LevelFinishedOverlay(BurgerPartyGame game, LevelResult levelResult, TextureAtlas atlas, Skin skin) {
		super(atlas);
		mGame = game;
		mStarTextures.add(atlas.findRegion("ui/star-off"));
		mStarTextures.add(atlas.findRegion("ui/star-on"));

		AnchorGroup group = new AnchorGroup();
		group.setSpacing(UiUtils.SPACING);
		group.setFillParent(true);
		addActor(group);

		Label mainLabel = new Label("", skin);

		Actor resultActor = createDetailedResultActor(levelResult, skin);

		RoundButton nextButton = null;

		RoundButton selectLevelButton = Kernel.createRoundButton("ui/icon-levels");
		selectLevelButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mGame.selectLevel();
			}
		});

		RoundButton restartButton = Kernel.createRoundButton("ui/icon-restart");
		restartButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mGame.startLevel(mGame.getLevelWorldIndex(), mGame.getLevelIndex());
			}
		});

		// Top screen message
		int levelWorldIndex = mGame.getLevelWorldIndex();
		int levelIndex = mGame.getLevelIndex();
		LevelWorld levelWorld = mGame.getLevelWorld(levelWorldIndex);
		if (levelIndex < levelWorld.getLevelCount() - 1) {
			mainLabel.setText("Congratulations, you finished level " + (levelWorldIndex + 1) + "-" + (levelIndex + 1) + "!");
			nextButton = createNextButton("ui/icon-right");
		} else if (levelWorldIndex < mGame.getLevelWorldCount() - 1) {
			mainLabel.setText("Congratulations, you finished world " + (levelWorldIndex + 1) + "!");
			nextButton = createNextButton("ui/icon-right");
		} else {
			mainLabel.setText("Congratulations, you finished the game!");
		}
		UiUtils.adjustToPrefSize(mainLabel);

		// Layout
		group.addRule(resultActor, Anchor.BOTTOM_CENTER, this, Anchor.CENTER, 0, 0);
		group.addRule(mainLabel, Anchor.BOTTOM_CENTER, resultActor, Anchor.TOP_CENTER, 0, 1);
		if (nextButton != null) {
			group.addRule(nextButton, Anchor.TOP_CENTER, resultActor, Anchor.BOTTOM_CENTER, 0, -1);
		}
		group.addRule(restartButton, Anchor.BOTTOM_RIGHT, this, Anchor.BOTTOM_CENTER, -0.5f, 1);
		group.addRule(selectLevelButton, Anchor.BOTTOM_LEFT, this, Anchor.BOTTOM_CENTER, 0.5f, 1);
	}

	private Actor createDetailedResultActor(LevelResult result, Skin skin) {
		VerticalGroup group = new VerticalGroup();

		Label scoreLabel = new Label(String.valueOf(result.getScore()), skin, "lcd-font", "lcd-color");
		int stars = result.computeStars();

		HorizontalGroup starGroup = new HorizontalGroup();
		for (int i = 0; i < 3; ++i) {
			boolean on = i + 1 <= stars;
			Image image = new Image(mStarTextures.get(on ? 1 : 0));
			starGroup.addActor(image);
		}

		group.addActor(scoreLabel);
		group.addActor(starGroup);

		// TODO: Get rid of this once AnchorGroup is layout-friendly
		group.setWidth(group.getPrefWidth());
		group.setHeight(group.getPrefHeight());
		return group;
	}

	private RoundButton createNextButton(String name) {
		RoundButton button = Kernel.createRoundButton(name);
		button.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				goToNextLevel();
			}
		});
		return button;
	}

	private void goToNextLevel() {
		int levelWorldIndex = mGame.getLevelWorldIndex();
		int levelIndex = mGame.getLevelIndex();
		LevelWorld levelWorld = mGame.getLevelWorld(levelWorldIndex);
		if (levelIndex < levelWorld.getLevelCount() - 1) {
			levelIndex++;
		} else {
			levelWorldIndex++;
			levelIndex = 0;
		}
		mGame.startLevel(levelWorldIndex, levelIndex);
	}

	private BurgerPartyGame mGame;
	private Array<TextureRegion> mStarTextures = new Array<TextureRegion>();

}
