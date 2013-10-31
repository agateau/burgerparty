package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.model.Level;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.GridGroup;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class LevelListScreen extends BurgerPartyScreen {
	private static final int COL_COUNT = 4;
	private static final float CELL_SIZE = 130;

	private static final float SURPRISE_ROTATE_ANGLE = 5f;
	private static final float SURPRISE_ROTATE_DURATION = 0.8f;

	public LevelListScreen(BurgerPartyGame game, int worldIndex) {
		super(game);
		TextureAtlas atlas = getTextureAtlas();
		Image bgImage = new Image(atlas.findRegion("ui/menu-bg"));
		setBackgroundActor(bgImage);

		mStarOff = atlas.findRegion("ui/star-off");
		mStarOn = atlas.findRegion("ui/star-on");
		mLock = atlas.findRegion("ui/lock");
		mSurpriseRegion = atlas.findRegion("ui/surprise");
		setupWidgets(getSkin(), worldIndex);
	}

	private void setupWidgets(Skin skin, int worldIndex) {
		mAnchorGroup.setSpacing(UiUtils.SPACING);
		getStage().addActor(mAnchorGroup);
		mAnchorGroup.setFillParent(true);

		ImageButton backButton = Kernel.createRoundButton(getGame().getAssets(), "ui/icon-back");
		mAnchorGroup.addRule(backButton, Anchor.BOTTOM_LEFT, mAnchorGroup, Anchor.BOTTOM_LEFT, 1, 1);
		backButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				onBackPressed();
			}
		});

		GridGroup gridGroup = createLevelButtonGridGroup(worldIndex, skin);
		mAnchorGroup.addRule(gridGroup, Anchor.TOP_CENTER, mAnchorGroup, Anchor.TOP_CENTER, 0, -1);
	}

	private GridGroup createLevelButtonGridGroup(int levelWorldIndex, Skin skin) {
		GridGroup gridGroup = new GridGroup();
		gridGroup.setSpacing(UiUtils.SPACING);
		gridGroup.setColumnCount(COL_COUNT);
		gridGroup.setCellSize(CELL_SIZE, CELL_SIZE);

		LevelWorld levelWorld = getGame().getLevelWorld(levelWorldIndex);
		for (int idx=0; idx < levelWorld.getLevelCount(); idx++) {
			Actor levelButton = createLevelButton(levelWorldIndex, idx, skin);
			gridGroup.addActor(levelButton);
		}
		return gridGroup;
	}

	class LevelButton extends TextButton {
		public LevelButton(int levelWorldIndex, int levelIndex, int stars, boolean locked, boolean surprise, Skin skin) {
			super("", skin, "level-button");
			this.levelWorldIndex = levelWorldIndex;
			this.levelIndex = levelIndex;

			AnchorGroup group = new AnchorGroup();
			addActor(group);
			group.setFillParent(true);
			group.setSpacing(6);

			if (locked) {
				setDisabled(true);
				Image image = new Image(mLock);
				group.addRule(image, Anchor.CENTER, group, Anchor.CENTER);
			} else {
				setText(String.valueOf(levelWorldIndex + 1) + "-" + String.valueOf(levelIndex + 1));
				Table table = new Table();
				for (int x = 1; x <= 3; ++x) {
					Image image = new Image(x > stars ? mStarOff : mStarOn);
					table.add(image);
				}
				group.addRule(table, Anchor.BOTTOM_CENTER, group, Anchor.BOTTOM_CENTER, 0, 2);
				table.setSize(mStarOff.getRegionWidth() * 3, mStarOff.getRegionHeight());
			}

			if (surprise) {
				createSurpriseImage(group);
			}
		}

		private void createSurpriseImage(AnchorGroup group) {
			Image image = new Image(mSurpriseRegion);
			image.setOrigin(mSurpriseRegion.getRegionWidth() / 2, mSurpriseRegion.getRegionHeight() / 2);
			group.addRule(image, Anchor.BOTTOM_RIGHT, group, Anchor.BOTTOM_RIGHT, -2f, 2f);
			float variation = MathUtils.random(0.9f, 1.1f);
			image.addAction(
				Actions.forever(
					Actions.sequence(
						Actions.delay(MathUtils.random(1f, 5f)),
						Actions.rotateTo(SURPRISE_ROTATE_ANGLE, SURPRISE_ROTATE_DURATION * variation / 2, Interpolation.sine),
						Actions.rotateTo(-SURPRISE_ROTATE_ANGLE, SURPRISE_ROTATE_DURATION * variation, Interpolation.sine),
						Actions.rotateTo(0, SURPRISE_ROTATE_DURATION * variation / 2, Interpolation.sine)
					)
				)
			);
		}

		public int levelWorldIndex;
		public int levelIndex;
	}

	private Actor createLevelButton(int levelWorldIndex, int levelIndex, Skin skin) {
		LevelWorld world = getGame().getLevelWorld(levelWorldIndex);
		Level level = world.getLevel(levelIndex);
		int stars = level.getStars();
		boolean locked = level.score == Level.SCORE_LOCKED;
		boolean surprise = level.hasBrandNewItem();
		LevelButton button = new LevelButton(levelWorldIndex, levelIndex, stars, locked, surprise, skin);
		button.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				getGame().getAssets().getSoundAtlas().findSound("click").play();
				LevelButton button = (LevelButton)actor;
				getGame().startLevel(button.levelWorldIndex, button.levelIndex);
			}
		});

		return button;
	}

	@Override
	public void onBackPressed() {
		getGame().showWorldListScreen();
	}

	private TextureRegion mStarOff;
	private TextureRegion mStarOn;
	private TextureRegion mLock;
	private TextureRegion mSurpriseRegion;

	private AnchorGroup mAnchorGroup = new AnchorGroup();
}
