package com.agateau.burgerparty.burgercopter;

import com.agateau.burgerparty.utils.CollisionMask;
import com.agateau.burgerparty.utils.SpriteImage;
import com.agateau.burgerparty.utils.StageScreen;
import com.agateau.burgerparty.utils.TileActor;
import com.agateau.burgerparty.utils.TileMap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

public class BurgerCopterMainScreen extends StageScreen {
	static final float PIXEL_PER_SECOND = 180;
	static final int SCORE_PER_SECOND = 200;
	static final int TILE_SIZE = 32;
	static final int ENEMY_COUNT = 4;
	static final float PLAYER_DELTA = 2f * 60f;
	public BurgerCopterMainScreen(BurgerCopterMiniGame miniGame) {
		super(miniGame.getAssets().getSkin());
		mMiniGame = miniGame;
		createSky();
		createBg();
		createGround();
		createEnemies();
		createPlayer();
		createHud();
	}

	@Override
	public void onBackPressed() {
		mMiniGame.showStartScreen();
	}

	private FPSLogger mLogger = new FPSLogger();
	@Override
	public void render(float delta) {
		getStage().act(delta);
		mScore += SCORE_PER_SECOND * delta;
		updateHud();
		getStage().draw();
		mLogger.log();
	}

	private static class GravityAction extends Action {
		@Override
		public boolean act(float delta) {
			Actor actor = getActor();
			float y = actor.getY();
			if (Gdx.input.isTouched()) {
				float maxY = actor.getStage().getHeight() - actor.getHeight();
				y = Math.min(y + PLAYER_DELTA * delta, maxY);
			} else {
				y -= PLAYER_DELTA * delta;
			}
			getActor().setY(y);
			return false;
		}
	}

	private static class ScrollAction extends Action {
		public ScrollAction(float speed) {
			mSpeed = speed;
		}

		@Override
		public boolean act(float delta) {
			Actor actor = getActor();
			float x = actor.getX() - mSpeed * delta;
			if (x + actor.getWidth() > 0) {
				actor.setX(x);
			} else {
				actor.setX(actor.getStage().getWidth());
				return onFinished();
			}
			return false;
		}

		public boolean onFinished() {
			return false;
		}

		private float mSpeed;
	}

	private void createPlayer() {
		TextureRegion region = mMiniGame.getAssets().getTextureAtlas().findRegion("mealitems/0/top-inventory");
		assert(region != null);
		SpriteImage image = new SpriteImage(region);
		mPlayer = image;
		image.setX(10);
		image.setY(getStage().getHeight() * 3 / 4);
		image.addAction(new Action() {
			@Override
			public boolean act(float delta) {
				if (mGroundActor.collide(mPlayer)) {
					mMiniGame.showGameOverScreen();
				}
				for(SpriteImage enemy: mEnemies) {
					if (SpriteImage.collide(mPlayer, enemy)) {
						mMiniGame.showGameOverScreen();
					}
				}
				return false;
			}
		});
		image.addAction(new GravityAction());
		getStage().addActor(mPlayer);
	}

	private void createSky() {
		TextureRegion region = mMiniGame.getAssets().getTextureAtlas().findRegion("ui/white-pixel");
		Image bg = new Image(region);
		bg.setColor(0.8f, 0.95f, 1, 1);
		setBackgroundActor(bg);
	}

	private void createBg() {
		TextureRegion bg1Region = mMiniGame.getAssets().getTextureAtlas().findRegion("burgercopter/bg1");
		assert(bg1Region != null);
		TextureRegion bg2Region = mMiniGame.getAssets().getTextureAtlas().findRegion("burgercopter/bg2");
		assert(bg2Region != null);
		TileMap map = new TileMap(5, 1, bg1Region.getRegionWidth());

		for (int col = 0; col < map.getColumnCount(); ++col) {
			map.getColumn(col).set(0, MathUtils.randomBoolean() ? bg1Region : bg2Region);
		}

		TileActor actor;
		actor = new TileActor(map, PIXEL_PER_SECOND / 4);
		actor.setBounds(0, TILE_SIZE * 2, getStage().getWidth(), bg1Region.getRegionWidth());
		actor.setColor(1, 1, 1, 0.5f);
		getStage().addActor(actor);

		map = new TileMap(7, 1, bg1Region.getRegionWidth());
		for (int col = 0; col < map.getColumnCount(); ++col) {
			map.getColumn(col).set(0, MathUtils.randomBoolean() ? bg1Region : bg2Region);
		}

		actor = new TileActor(map, PIXEL_PER_SECOND / 2);
		actor.setBounds(0, TILE_SIZE - 1, getStage().getWidth(), map.getTileSize());
		getStage().addActor(actor);
	}

	private void createGround() {
		int columnCount = 200;
		int rowCount = 6;
		TileMap map = new TileMap(columnCount, rowCount, TILE_SIZE);
		mGroundActor = new TileActor(map, PIXEL_PER_SECOND);
		mGroundActor.setBounds(0, 0, getStage().getWidth(), TILE_SIZE * rowCount);

		TextureAtlas atlas = mMiniGame.getAssets().getTextureAtlas();
		final TextureRegion groundRegion = atlas.findRegion("burgercopter/ground");
		final TextureRegion groundTopRegion = atlas.findRegion("burgercopter/ground-top");
		final TextureRegion groundUpARegion = atlas.findRegion("burgercopter/ground-up-a");
		final TextureRegion groundUpBRegion = atlas.findRegion("burgercopter/ground-up-b");
		final TextureRegion groundDownARegion = atlas.findRegion("burgercopter/ground-down-a");
		final TextureRegion groundDownBRegion = atlas.findRegion("burgercopter/ground-down-b");
		final TextureRegion buildingRegion = atlas.findRegion("burgercopter/building");
		final TextureRegion buildingTopRegion = atlas.findRegion("burgercopter/building-top");

		int groundLevel = 0;
		for (int col = 0; col < columnCount;) {
			// Building
			int end = Math.min(columnCount, col + MathUtils.random(1, 6));
			for (; col < end; ++col) {
				Array<TextureRegion> column = map.getColumn(col);
				int row;
				for (row = 0; row < groundLevel; ++row) {
					column.set(row, groundRegion);
				}
				column.set(row++, groundTopRegion);
				if (row < rowCount - 2) {
					int floors = MathUtils.random(row, rowCount - 2);
					for (; row < floors; ++row) {
						column.set(row, buildingRegion);
					}
					column.set(row, buildingTopRegion);
				}
			}
			if (col == columnCount) {
				break;
			}
			// Raise or lower ground
			int newGroundLevel = MathUtils.clamp(groundLevel + MathUtils.random(-1, 1), 0, rowCount - 2);
			if (newGroundLevel > groundLevel) {
				Array<TextureRegion> column = map.getColumn(col++);
				int row;
				for (row = 0; row < groundLevel; ++row) {
					column.set(row, groundRegion);
				}
				column.set(row++, groundUpARegion);
				column.set(row, groundUpBRegion);
			} else if (newGroundLevel < groundLevel) {
				Array<TextureRegion> column = map.getColumn(col++);
				int row;
				for (row = 0; row < newGroundLevel; ++row) {
					column.set(row, groundRegion);
				}
				column.set(row++, groundDownARegion);
				column.set(row, groundDownBRegion);
			}
			groundLevel = newGroundLevel;
			// Add some space
			end = Math.min(columnCount, col + MathUtils.random(1, 4));
			for (; col < end; ++col) {
				Array<TextureRegion> column = map.getColumn(col);
				int row;
				for (row = 0; row < groundLevel; ++row) {
					column.set(row, groundRegion);
				}
				column.set(row++, groundTopRegion);
			}
		}

		getStage().addActor(mGroundActor);
	}

	private static class EnemyAction extends ScrollAction {
		public EnemyAction(float speed) {
			super(speed);
		}

		@Override
		public boolean onFinished() {
			float x = getActor().getStage().getWidth();
			float y = MathUtils.random(240, 480);
			actor.setX(x);
			actor.setY(y);
			return false;
		}
	}
	private void createEnemies() {
		final TextureRegion region = mMiniGame.getAssets().getTextureAtlas().findRegion("mealitems/0/fish-inventory");
		assert(region != null);
		CollisionMask mask = new CollisionMask(region);
		float screenWidth = getStage().getWidth();
		for (int idx = 0; idx < ENEMY_COUNT; ++idx) {
			float x = screenWidth * (1 + (float)idx / ENEMY_COUNT);
			float y = MathUtils.random(240, 480);
			SpriteImage actor = new SpriteImage(region, mask);
			actor.setX(x);
			actor.setY(y);
			actor.addAction(new EnemyAction(PIXEL_PER_SECOND * 2));
			getStage().addActor(actor);
			mEnemies.add(actor);
		}
	}

	private void createHud() {
		mScoreLabel = new Label("0", mMiniGame.getAssets().getSkin(), "lock-star-text");
		getStage().addActor(mScoreLabel);
		mScoreLabel.setX(0);
		mScoreLabel.setY(getStage().getHeight() - mScoreLabel.getPrefHeight());
	}

	private void updateHud() {
		mScoreLabel.setText(String.valueOf((mScore / 10) * 10));
	}

	private BurgerCopterMiniGame mMiniGame;
	private SpriteImage mPlayer;
	private TileActor mGroundActor;
	private Array<SpriteImage> mEnemies = new Array<SpriteImage>();
	private int mScore = 0;
	private Label mScoreLabel;
}
