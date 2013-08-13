package com.agateau.burgerparty.view;

import java.util.LinkedList;

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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;

public class LevelFinishedOverlay extends Overlay {
	private static final int EXTRA_TIME_SCORE = 100;
	private static final float EXTRA_TIME_UPDATE_INTERVAL = 0.01f;

	private static class RunQueue {
		public static class Task extends Timer.Task {
			void setQueue(RunQueue queue) {
				mQueue = queue;
			}
			public void done() {
				mQueue.processNext();
			}
			@Override
			public void run() {
				done();
			}
			private RunQueue mQueue;
		}

		public void add(Task task) {
			task.setQueue(this);
			mList.add(task);
		}

		public void start() {
			processNext();
		}

		void processNext() {
			if (mList.isEmpty()) {
				return;
			}
			Task task = mList.remove();
			Timer.post(task);
		}

		private LinkedList<Task> mList = new LinkedList<Task>();
	}

	class ConsumeSecondsTask extends RunQueue.Task {
		public ConsumeSecondsTask(int secs) {
			mRemainingSeconds = secs;
		}
		@Override
		public void run() {
			consumeRemainingSeconds();
		}
		public void finishNow() {
			mScore += mRemainingSeconds * EXTRA_TIME_SCORE;
			mRemainingSeconds = 0;
			mScoreLabel.setText(String.valueOf(mScore));
			done();
		}
		private void consumeRemainingSeconds() {
			if (mRemainingSeconds == 0) {
				done();
				return;
			}
			mScore += EXTRA_TIME_SCORE;
			mScoreLabel.setText(String.valueOf(mScore));
			--mRemainingSeconds;
			Timer.schedule(this, EXTRA_TIME_UPDATE_INTERVAL);
		}
		int mRemainingSeconds;
	}

	class LightUpStarTask extends RunQueue.Task {
		public LightUpStarTask(int index) {
			mImage = mStarImages.get(index);
		}
		@Override
		public void run() {
			Drawable texture = mStarTextures.get(1);
			mImage.setDrawable(texture);
			done();
		}
		Image mImage;
	}

	class HighScoreTask extends RunQueue.Task {
		public HighScoreTask(Overlay parent) {
			mLabel = new Label("New High Score!", Kernel.getSkin());
			parent.addActor(mLabel);
			mLabel.setVisible(false);
		}
		@Override
		public void run() {
			mLabel.setVisible(true);
			mLabel.setPosition(200.f, 200.f);
			done();
		}
		Label mLabel;
	}

	public LevelFinishedOverlay(BurgerPartyGame game, LevelResult levelResult, TextureAtlas atlas, Skin skin) {
		super(atlas);
		mGame = game;
		int previousScore = levelResult.getLevel().score;
		mScore = levelResult.getScore();
		int remainingSeconds = levelResult.getRemainingSeconds();
		int finalScore = mScore + EXTRA_TIME_SCORE * remainingSeconds;

		// Store final score *now*
		mGame.onCurrentLevelFinished(finalScore);

		mStarTextures.add(new TextureRegionDrawable(atlas.findRegion("ui/star-off")));
		mStarTextures.add(new TextureRegionDrawable(atlas.findRegion("ui/star-on")));
		setupWidgets(skin);

		mConsumeSecondsTask = new ConsumeSecondsTask(remainingSeconds);
		mRunQueue.add(mConsumeSecondsTask);
		int starCount = levelResult.getLevel().getStarsFor(finalScore);
		for (int i = 0; i < starCount; ++i) {
			mRunQueue.add(new LightUpStarTask(i));
		}
		if (finalScore > previousScore) {
			mRunQueue.add(new HighScoreTask(this));
		}
		mRunQueue.start();
	}

	private void setupWidgets(Skin skin) {
		AnchorGroup group = new AnchorGroup();
		group.setSpacing(UiUtils.SPACING);
		group.setFillParent(true);
		addActor(group);

		Label mainLabel = new Label("", skin);

		Actor resultActor = createDetailedResultActor(skin);

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

	private Actor createDetailedResultActor(Skin skin) {
		VerticalGroup group = new VerticalGroup();

		mScoreLabel = new Label(String.valueOf(mScore), skin, "lcd-font", "lcd-color");
		HorizontalGroup starGroup = new HorizontalGroup();

		Drawable texture = mStarTextures.get(0);
		for (int i = 0; i < 3; ++i) {
			Image image = new Image(texture);
			mStarImages.add(image);
			starGroup.addActor(image);
		}

		group.addActor(mScoreLabel);
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
		if (mConsumeSecondsTask.mRemainingSeconds > 0) {
			mConsumeSecondsTask.finishNow();
			Timer.schedule(new Timer.Task() {
				@Override
				public void run() {
					doGoToNextLevel();
				}
			}, 0.5f);
		} else {
			doGoToNextLevel();
		}
	}

	private void doGoToNextLevel() {
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

	private ConsumeSecondsTask mConsumeSecondsTask;
	private RunQueue mRunQueue = new RunQueue();
	private BurgerPartyGame mGame;
	private int mScore;
	private Array<TextureRegionDrawable> mStarTextures = new Array<TextureRegionDrawable>();
	private Label mScoreLabel;
	private Array<Image> mStarImages = new Array<Image>();
}
