package com.agateau.burgerparty.view;


import java.util.Set;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.model.LevelResult;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.HorizontalGroup;
import com.agateau.burgerparty.utils.Overlay;
import com.agateau.burgerparty.utils.RunQueue;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;

public class LevelFinishedOverlay extends Overlay {
	private static final int EXTRA_TIME_SCORE = 100;
	private static final float EXTRA_TIME_UPDATE_INTERVAL = 0.01f;

	private static final float STAR_ANIM_DURATION = 0.3f;

	class ConsumeSecondsTask extends RunQueue.Task {
		public ConsumeSecondsTask(int secs) {
			mRemainingSeconds = secs;
			mSound = mGame.getAssets().getSoundAtlas().findSound("time-bonus");
		}
		@Override
		public void run() {
			consumeRemainingSeconds();
		}
		public void fastForward() {
			mScore += mRemainingSeconds * EXTRA_TIME_SCORE;
			mRemainingSeconds = 0;
			mScoreLabel.setText(String.valueOf(mScore));
		}
		private void consumeRemainingSeconds() {
			if (mRemainingSeconds == 0) {
				done();
				return;
			}
			mSound.play(0.1f);
			mScore += EXTRA_TIME_SCORE;
			mScoreLabel.setText(String.valueOf(mScore));
			--mRemainingSeconds;
			Timer.schedule(this, EXTRA_TIME_UPDATE_INTERVAL);
		}
		private int mRemainingSeconds;
		private Sound mSound;
	}

	class LightUpStarTask extends RunQueue.Task {
		public LightUpStarTask(Overlay parent, int index) {
			mImage = new Image(mStarTextures.get(1));
			mReferenceImage = mStarImages.get(index);
			parent.addActor(mImage);
			mImage.setVisible(false);
			mImage.setOrigin(mImage.getWidth() / 2, mImage.getHeight() / 2);
		}
		@Override
		public void run() {
			mImage.setVisible(true);
			Vector2 pos = mReferenceImage.localToAscendantCoordinates(getParent(), new Vector2(0, 0));
			mImage.setPosition(pos.x, pos.y);
			mImage.setColor(1, 1, 1, 0);
			mImage.setScale(10);
			mImage.setRotation(-72);
			mImage.addAction(
				Actions.sequence(
					Actions.parallel(
						Actions.moveBy(0, 30),
						Actions.moveBy(0, -30, STAR_ANIM_DURATION),
						Actions.scaleTo(1, 1, STAR_ANIM_DURATION, Interpolation.pow3In),
						Actions.rotateTo(0, STAR_ANIM_DURATION),
						Actions.alpha(1, STAR_ANIM_DURATION, Interpolation.pow5In)
					),
					mGame.getAssets().getSoundAtlas().createPlayAction("star"),
					Actions.run(createDoneRunnable())
				)
			);
			Drawable texture = mStarTextures.get(1);
			mImage.setDrawable(texture);
		}
		private Image mImage;
		private Image mReferenceImage;
	}

	class HighScoreTask extends RunQueue.Task {
		public HighScoreTask(Overlay parent) {
			mLabel = new Label("New High Score!", mGame.getAssets().getSkin(), "score-feedback");
			parent.addActor(mLabel);
			mLabel.setVisible(false);
		}
		@Override
		public void run() {
			mLabel.setVisible(true);
			float screenWidth = mLabel.getParent().getWidth();
			float finalX = mScoreLabel.getRight() + UiUtils.SPACING;
			float finalY = mScoreLabel.getY() - 2;
			mLabel.setPosition(screenWidth, finalY);
			mLabel.addAction(
				Actions.sequence(
					Actions.moveTo(finalX, finalY, 1, Interpolation.bounceOut),
					Actions.run(createDoneRunnable())
				)
			);
		}
		Label mLabel;
	}

	class PerfectTask extends RunQueue.Task {
		public PerfectTask(Overlay parent) {
			mLabel = new Label("PERFECT!", mGame.getAssets().getSkin(), "score-feedback");
			parent.addActor(mLabel);
			mLabel.setVisible(false);
		}
		@Override
		public void run() {
			mLabel.setVisible(true);
			float finalX = mScoreLabel.getX() - mLabel.getWidth() - UiUtils.SPACING;
			float finalY = mScoreLabel.getY() - 2;
			mLabel.setPosition(-mLabel.getWidth(), finalY);
			mLabel.addAction(
				Actions.sequence(
					Actions.moveTo(finalX, finalY, 1, Interpolation.bounceOut),
					Actions.run(createDoneRunnable())
				)
			);
		}
		Label mLabel;
	}

	public LevelFinishedOverlay(BurgerPartyGame game, LevelResult levelResult, TextureAtlas atlas, Skin skin) {
		super(atlas);
		mGame = game;
		int previousScore = levelResult.getLevel().getScore();
		mScore = levelResult.getScore();
		boolean perfect = levelResult.getCoinCount() == levelResult.getMaximumCoinCount();
		int remainingSeconds = levelResult.getRemainingSeconds();
		int finalScore = mScore + EXTRA_TIME_SCORE * remainingSeconds;
		int starCount = Math.min(levelResult.getCoinCount() / levelResult.getStarCost(), 3);

		// Store final score *now*
		Set<String> unlockedThings = mGame.getUniverse().updateLevel(mGame.getLevelWorldIndex(), mGame.getLevelIndex(), finalScore, starCount, perfect);
		for (String thing: unlockedThings) {
			Gdx.app.log("LevelFinishedOverlay", "Unlocked " + thing);
		}

		mStarTextures.add(new TextureRegionDrawable(atlas.findRegion("ui/star-off")));
		mStarTextures.add(new TextureRegionDrawable(atlas.findRegion("ui/star-on")));
		setupWidgets(skin);

		mRunQueue.add(new ConsumeSecondsTask(remainingSeconds));
		for (int i = 0; i < starCount; ++i) {
			mRunQueue.add(new LightUpStarTask(this, i));
		}
		if (finalScore > previousScore) {
			mRunQueue.add(new HighScoreTask(this));
		}
		if (perfect) {
			mRunQueue.add(new PerfectTask(this));
		}
		mRunQueue.start();
		mGame.getAssets().getSoundAtlas().findSound("finished").play();
	}

	private void setupWidgets(Skin skin) {
		AnchorGroup group = new AnchorGroup();
		group.setSpacing(UiUtils.SPACING);
		group.setFillParent(true);
		addActor(group);

		// Score label
		mScoreLabel = new Label(String.valueOf(mScore), skin);

		// Stars
		Actor starsActor = createStarsActor(skin);

		// Select level button
		ImageButton selectLevelButton = Kernel.createRoundButton(mGame.getAssets(), "ui/icon-levels");
		selectLevelButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mGame.showLevelListScreen(mGame.getLevelWorldIndex());
			}
		});

		// Restart button
		ImageButton restartButton = Kernel.createRoundButton(mGame.getAssets(), "ui/icon-restart");
		restartButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mGame.startLevel(mGame.getLevelWorldIndex(), mGame.getLevelIndex());
			}
		});

		// Main label and next button
		Label mainLabel = new Label("", skin);
		ImageButton nextButton = null;
		int levelWorldIndex = mGame.getLevelWorldIndex();
		int levelIndex = mGame.getLevelIndex();
		LevelWorld levelWorld = mGame.getUniverse().get(levelWorldIndex);
		if (levelIndex < levelWorld.getLevelCount() - 1) {
			mainLabel.setText("Congratulations, you finished level " + (levelWorldIndex + 1) + "-" + (levelIndex + 1) + "!");
			nextButton = createNextButton("ui/icon-right");
		} else if (levelWorldIndex < mGame.getUniverse().getWorlds().size - 1) {
			mainLabel.setText("Congratulations, you finished world " + (levelWorldIndex + 1) + "!");
			nextButton = createNextButton("ui/icon-right");
		} else {
			mainLabel.setText("Congratulations, you finished the game!");
		}
		UiUtils.adjustToPrefSize(mainLabel);

		// Layout
		group.addRule(mainLabel, Anchor.TOP_CENTER, this, Anchor.TOP_CENTER, 0, -1);
		group.addRule(mScoreLabel, Anchor.TOP_CENTER, mainLabel, Anchor.BOTTOM_CENTER, 0, -1);
		group.addRule(starsActor, Anchor.TOP_CENTER, mScoreLabel, Anchor.BOTTOM_CENTER, 0, -1);
		if (nextButton != null) {
			group.addRule(nextButton, Anchor.TOP_CENTER, starsActor, Anchor.BOTTOM_CENTER, 0, -2);
		}
		group.addRule(restartButton, Anchor.BOTTOM_RIGHT, this, Anchor.BOTTOM_CENTER, -0.5f, 1);
		group.addRule(selectLevelButton, Anchor.BOTTOM_LEFT, this, Anchor.BOTTOM_CENTER, 0.5f, 1);
	}

	private Actor createStarsActor(Skin skin) {
		HorizontalGroup starGroup = new HorizontalGroup();

		Drawable texture = mStarTextures.get(0);
		for (int i = 0; i < 3; ++i) {
			Image image = new Image(texture);
			mStarImages.add(image);
			starGroup.addActor(image);
		}
		starGroup.setWidth(starGroup.getPrefWidth());
		starGroup.setHeight(starGroup.getPrefHeight());

		return starGroup;
	}

	private ImageButton createNextButton(String name) {
		ImageButton button = Kernel.createRoundButton(mGame.getAssets(), name);
		button.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				goToNextLevel();
			}
		});
		return button;
	}

	private void goToNextLevel() {
		if (mRunQueue.isEmpty()) {
			doGoToNextLevel();
		} else {
			mRunQueue.add(new RunQueue.Task() {
				@Override
				public void run() {
					doGoToNextLevel();
					done();
				}
			});
			mRunQueue.fastForward();
		}
	}

	private void doGoToNextLevel() {
		int levelWorldIndex = mGame.getLevelWorldIndex();
		int levelIndex = mGame.getLevelIndex();
		LevelWorld levelWorld = mGame.getUniverse().get(levelWorldIndex);
		if (levelIndex < levelWorld.getLevelCount() - 1) {
			levelIndex++;
		} else {
			levelWorldIndex++;
			levelIndex = 0;
		}
		mGame.startLevel(levelWorldIndex, levelIndex);
	}

	@Override
	public void onBackPressed() {
		mGame.showLevelListScreen(mGame.getLevelWorldIndex());
	}

	private RunQueue mRunQueue = new RunQueue();
	private BurgerPartyGame mGame;
	private int mScore;
	private Array<TextureRegionDrawable> mStarTextures = new Array<TextureRegionDrawable>();
	private Label mScoreLabel;
	private Array<Image> mStarImages = new Array<Image>();
}
