package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.model.Level;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.GridGroup;
import com.agateau.burgerparty.utils.HorizontalGroup;
import com.agateau.burgerparty.utils.RefreshHelper;
import com.agateau.burgerparty.utils.UiUtils;
import com.agateau.burgerparty.view.BurgerPartyUiBuilder;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.XmlReader;

public class LevelListScreen extends BurgerPartyScreen {
	private static final int COL_COUNT = 3;
	private static final float CELL_SIZE = 130;

	private static final float SURPRISE_ROTATE_ANGLE = 5f;
	private static final float SURPRISE_ROTATE_DURATION = 0.8f;

	public LevelListScreen(BurgerPartyGame game, int worldIndex) {
		super(game);
		TextureAtlas atlas = getTextureAtlas();
		Image bgImage = new Image(atlas.findRegion("ui/menu-bg"));
		setBackgroundActor(bgImage);

		mLevelWorld = game.getUniverse().get(worldIndex);

		mStarOff = atlas.findRegion("ui/star-off");
		mStarOn = atlas.findRegion("ui/star-on");
		mLock = atlas.findRegion("ui/lock-key");
		mSurpriseRegion = atlas.findRegion("ui/surprise");
		setupWidgets();

		new RefreshHelper(getStage()) {
			@Override
			protected void refresh() {
				getGame().showLevelListScreen(mLevelWorld.getIndex());
				dispose();
			}
		};
	}

	private class Builder extends BurgerPartyUiBuilder {
		public Builder(Assets assets) {
			super(assets);
		}

		@Override
		protected Actor createActorForElement(XmlReader.Element element) {
			if (element.getName().equals("LevelGrid")) {
				return createLevelButtonGridGroup();
			} else if (element.getName().equals("LevelBaseButton")) {
				return new LevelBaseButton(getGame().getAssets());
			} else {
				return super.createActorForElement(element);
			}
		}
	}

	private void setupWidgets() {
		BurgerPartyUiBuilder builder = new Builder(getGame().getAssets());
		builder.build(FileUtils.assets("screens/levellist.gdxui"));
		AnchorGroup root = builder.getActor("root");
		getStage().addActor(root);
		root.setFillParent(true);

		builder.<ImageButton>getActor("backButton").addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				onBackPressed();
			}
		});

		TextButton miniGameButton = builder.<TextButton>getActor("miniGameButton");
		Label miniGameLockLabel = builder.<Label>getActor("miniGameLockLabel");
		Image miniGameButtonImage = builder.<Image>getActor("miniGameButtonImage");
		miniGameButtonImage.setTouchable(Touchable.disabled);
		if (getGame().getUniverse().getStarCount() > mLevelWorld.getMiniGameStarCount()) {
			Image lockImage = builder.<Image>getActor("miniGameLockImage");
			lockImage.setVisible(false);
			miniGameLockLabel.setVisible(false);
			miniGameButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					getGame().startMiniGame(mLevelWorld.getIndex());
				}
			});
		} else {
			miniGameButtonImage.setVisible(false);
			miniGameButton.setDisabled(true);
			miniGameLockLabel.setText(String.valueOf(mLevelWorld.getMiniGameStarCount()));
		}
	}

	private GridGroup createLevelButtonGridGroup() {
		GridGroup gridGroup = new GridGroup();
		gridGroup.setSpacing(UiUtils.SPACING);
		gridGroup.setColumnCount(COL_COUNT);
		gridGroup.setCellSize(CELL_SIZE, CELL_SIZE);

		for (Level level: mLevelWorld.getLevels()) {
			gridGroup.addActor(createLevelButton(level));
		}
		return gridGroup;
	}

	private static class LevelBaseButton extends TextButton {
		public LevelBaseButton(Assets assets) {
			super("", assets.getSkin(), "level-button");
			mAssets = assets;
		}
		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			if (isDisabled()) {
				batch.setShader(mAssets.getDisabledShader());
				super.draw(batch, parentAlpha);
				batch.setShader(null);
			} else {
				super.draw(batch, parentAlpha);
			}
		}
		protected Assets mAssets;
	}

	private class LevelButton extends LevelBaseButton {
		public LevelButton(Assets assets, int levelWorldIndex, int levelIndex) {
			super(assets);
			this.levelWorldIndex = levelWorldIndex;
			this.levelIndex = levelIndex;

			mGroup = new AnchorGroup();
			addActor(mGroup);
			mGroup.setFillParent(true);
		}

		public void createStars(int stars) {
			setText(String.valueOf(levelWorldIndex + 1) + "-" + String.valueOf(levelIndex + 1));
			HorizontalGroup starGroup = new HorizontalGroup();
			starGroup.setSpacing(4);
			for (int n = 1; n <= 3; ++n) {
				Image image = new Image(n > stars ? mStarOff : mStarOn);
				starGroup.addActor(image);
			}
			starGroup.setScale(0.8f);
			starGroup.pack();
			mGroup.addRule(starGroup, Anchor.BOTTOM_CENTER, mGroup, Anchor.BOTTOM_CENTER, 0, 8);
		}

		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			super.draw(batch, parentAlpha);
			if (isDisabled()) {
				float posX = getX() + (getWidth() - mLock.getRegionWidth()) / 2;
				float posY = getY() + (getHeight() - mLock.getRegionHeight()) / 2;
				batch.draw(mLock, posX, posY);
			}
		}

		private AnchorGroup mGroup;

		public int levelWorldIndex;
		public int levelIndex;
	}

	private Actor createLevelButton(Level level) {
		LevelButton button = new LevelButton(getGame().getAssets(), mLevelWorld.getIndex(), level.getIndex());
		button.setSize(CELL_SIZE, CELL_SIZE);

		AnchorGroup group = new AnchorGroup();
		group.addRule(button, Anchor.TOP_LEFT, group, Anchor.TOP_LEFT);
		if (level.isLocked()) {
			button.setDisabled(true);
		} else {
			button.createStars(level.getStars());
		}
		if (level.hasBrandNewItem()) {
			createSurpriseImage(group);
		}

		button.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				getGame().getAssets().getSoundAtlas().findSound("click").play();
				LevelButton button = (LevelButton)actor;
				getGame().startLevel(button.levelWorldIndex, button.levelIndex);
			}
		});
		return group;
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

	@Override
	public void onBackPressed() {
		getGame().showWorldListScreen();
	}

	private LevelWorld mLevelWorld;
	private TextureRegion mStarOff;
	private TextureRegion mStarOn;
	private TextureRegion mLock;
	private TextureRegion mSurpriseRegion;
}
