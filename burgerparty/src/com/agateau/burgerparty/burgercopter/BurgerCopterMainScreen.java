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
import com.badlogic.gdx.utils.Disposable;

public class BurgerCopterMainScreen extends StageScreen {
	static final float PIXEL_PER_SECOND = 180;
	static final int SCORE_PER_SECOND = 200;
	static final int TILE_SIZE = 32;
	static final int ENEMY_COUNT = 4;
	static final float PLAYER_DELTA = 2f * 60f;
	static final float PLAYER_MIN_ALTITUDE = 2;
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
	public void dispose() {
		Gdx.app.log("BurgerCopterMiniGame.dispose" ,"");
		for (Disposable obj: mDisposables) {
			obj.dispose();
		}
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

	private class GravityAction extends Action {
		@Override
		public boolean act(float delta) {
			Actor actor = getActor();
			float y = actor.getY();
			if (Gdx.input.isTouched()) {
				float maxY = actor.getStage().getHeight() - actor.getHeight();
				y = Math.min(y + PLAYER_DELTA * delta, maxY);
			} else {
				float newY = PLAYER_DELTA * delta;
				float groundHeight1 = mGroundActor.getHeightAt(actor.getX());
				float groundHeight2 = mGroundActor.getHeightAt(actor.getRight());
				y = Math.max(newY, Math.max(groundHeight1, groundHeight2) + PLAYER_MIN_ALTITUDE);
			}
			getActor().setY(y);
			return false;
		}
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
		mDisposables.add(actor);

		map = new TileMap(7, 1, bg1Region.getRegionWidth());
		for (int col = 0; col < map.getColumnCount(); ++col) {
			map.getColumn(col).set(0, MathUtils.randomBoolean() ? bg1Region : bg2Region);
		}

		actor = new TileActor(map, PIXEL_PER_SECOND / 2);
		actor.setBounds(0, TILE_SIZE - 1, getStage().getWidth(), map.getTileHeight());
		getStage().addActor(actor);
		mDisposables.add(actor);
	}

	private void createGround() {
		int columnCount = 200;
		int rowCount = 6;
		int tileWidth = 128;
		int tileHeight = 64;
		TileMap map = new TileMap(columnCount, rowCount, tileWidth, tileHeight);
		mGroundActor = new TileActor(map, PIXEL_PER_SECOND);
		mGroundActor.setBounds(0, 0, getStage().getWidth(), tileHeight * rowCount);
		mDisposables.add(mGroundActor);

		TextureAtlas atlas = mMiniGame.getAssets().getTextureAtlas();
		final TextureRegion groundRegion = atlas.findRegion("burgercopter/ground");
		final TextureRegion stoneRegion = atlas.findRegion("burgercopter/stone");
		final TextureRegion stoneUpARegion = atlas.findRegion("burgercopter/stone-up-a");
		final TextureRegion stoneUpBRegion = atlas.findRegion("burgercopter/stone-up-b");
		final TextureRegion stoneDownARegion = atlas.findRegion("burgercopter/stone-down-a");
		final TextureRegion stoneDownBRegion = atlas.findRegion("burgercopter/stone-down-b");

		int groundLevel = 0;
		for (int col = 0; col < columnCount;) {
			// Raise or lower ground
			int newGroundLevel = MathUtils.clamp(groundLevel + MathUtils.random(-1, 1), 0, rowCount - 2);
			if (newGroundLevel > groundLevel) {
				Array<TextureRegion> column = map.getColumn(col++);
				int row;
				for (row = 0; row < newGroundLevel - 1; ++row) {
					column.set(row, groundRegion);
				}
				column.set(row++, stoneUpARegion);
				column.set(row++, stoneUpBRegion);
			} else if (newGroundLevel < groundLevel) {
				Array<TextureRegion> column = map.getColumn(col++);
				int row;
				for (row = 0; row < groundLevel - 1; ++row) {
					column.set(row, groundRegion);
				}
				column.set(row++, stoneDownARegion);
				column.set(row, stoneDownBRegion);
			}
			groundLevel = newGroundLevel;
			// Add some space
			int end = Math.min(columnCount, col + MathUtils.random(1, 4));
			for (; col < end; ++col) {
				Array<TextureRegion> column = map.getColumn(col);
				int row;
				for (row = 0; row < groundLevel; ++row) {
					column.set(row, groundRegion);
				}
				column.set(row, stoneRegion);
			}
		}

		getStage().addActor(mGroundActor);
	}

	private static class EnemyAction extends Action {
		public EnemyAction(float speed) {
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
				onFinished();
			}
			return false;
		}

		private void onFinished() {
			float x = getActor().getStage().getWidth();
			float y = MathUtils.random(240, 480);
			actor.setX(x);
			actor.setY(y);
		}

		private float mSpeed;
	}

	private void createEnemies() {
		final TextureRegion region = mMiniGame.getAssets().getTextureAtlas().findRegion("mealitems/0/cheese-inventory");//"mealitems/0/fish-inventory");
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
	private Array<Disposable> mDisposables = new Array<Disposable>();
}
