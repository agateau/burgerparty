package com.agateau.burgerparty.burgercopter;

import com.agateau.burgerparty.utils.CollisionMask;
import com.agateau.burgerparty.utils.SpriteImage;
import com.agateau.burgerparty.utils.StageScreen;
import com.agateau.burgerparty.utils.Tile;
import com.agateau.burgerparty.utils.TileActor;
import com.agateau.burgerparty.utils.TileMap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.StringBuilder;

public class BurgerCopterMainScreen extends StageScreen {
	static final float PIXEL_PER_SECOND = 180;
	static final int SCORE_PER_SECOND = 200;
	static final int TILE_SIZE = 32;
	static final int ENEMY_COUNT = 4;
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
		for(SpriteImage enemy: mEnemies) {
			if (SpriteImage.collide(mPlayer.getActor(), enemy)) {
				mMiniGame.showGameOverScreen();
			}
		}
		updateHud();
		getStage().draw();
		mLogger.log();
	}

	private void createPlayer() {
		mPlayer = new Player(mMiniGame.getAssets(), mGroundActor);
		mPlayer.getActor().setPosition(10, getStage().getHeight() * 3 / 4);
		getStage().addActor(mPlayer.getActor());
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
		Tile bg1 = new Tile(bg1Region);
		TextureRegion bg2Region = mMiniGame.getAssets().getTextureAtlas().findRegion("burgercopter/bg2");
		assert(bg2Region != null);
		Tile bg2 = new Tile(bg2Region);
		TileMap map = new TileMap(5, 1, bg1Region.getRegionWidth());

		for (int col = 0; col < map.getColumnCount(); ++col) {
			map.getColumn(col).set(0, MathUtils.randomBoolean() ? bg1 : bg2);
		}

		TileActor actor;
		actor = new TileActor(map, PIXEL_PER_SECOND / 4);
		actor.setBounds(0, TILE_SIZE * 2, getStage().getWidth(), bg1Region.getRegionWidth());
		actor.setColor(1, 1, 1, 0.5f);
		getStage().addActor(actor);
		mDisposables.add(actor);

		map = new TileMap(7, 1, bg1Region.getRegionWidth());
		for (int col = 0; col < map.getColumnCount(); ++col) {
			map.getColumn(col).set(0, MathUtils.randomBoolean() ? bg1 : bg2);
		}

		actor = new TileActor(map, PIXEL_PER_SECOND / 2);
		actor.setBounds(0, TILE_SIZE - 1, getStage().getWidth(), map.getTileHeight());
		getStage().addActor(actor);
		mDisposables.add(actor);
	}

	private void createGround() {
		int rowCount = 6;
		int tileWidth = 128;
		int tileHeight = 64;
		int columnCount = MathUtils.ceil(getStage().getWidth() / tileWidth) * 2;
		TileMap map = new GroundTileMap(mMiniGame.getAssets().getTextureAtlas(), columnCount, rowCount, tileWidth, tileHeight);
		mGroundActor = new TileActor(map, PIXEL_PER_SECOND);
		mGroundActor.setBounds(0, 0, getStage().getWidth(), tileHeight * rowCount);
		mDisposables.add(mGroundActor);
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
		mScoreLabel = new Label("0\n0", mMiniGame.getAssets().getSkin(), "lock-star-text");
		getStage().addActor(mScoreLabel);
		mScoreLabel.setX(0);
		mScoreLabel.setY(getStage().getHeight() - mScoreLabel.getPrefHeight());
	}

	private void updateHud() {
		float fuel = mPlayer.getJumpFuel();
		mHudStringBuilder.setLength(0);
		mHudStringBuilder.append(mScore).append("\n");
		for (float f = 0f; f < 1.0f; f += 0.05f) {
			mHudStringBuilder.append(f < fuel ? "|" : ".");
		}
		mScoreLabel.setText(mHudStringBuilder.toString());
	}

	private StringBuilder mHudStringBuilder = new StringBuilder();
	private BurgerCopterMiniGame mMiniGame;
	private Player mPlayer;
	private TileActor mGroundActor;
	private Array<SpriteImage> mEnemies = new Array<SpriteImage>();
	private int mScore = 0;
	private Label mScoreLabel;
	private Array<Disposable> mDisposables = new Array<Disposable>();
}
