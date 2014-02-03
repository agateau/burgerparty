package com.agateau.burgerparty.burgercopter;

import java.util.HashSet;

import com.agateau.burgerparty.utils.MaskedDrawable;
import com.agateau.burgerparty.utils.MaskedDrawableAtlas;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal2;
import com.agateau.burgerparty.utils.SoundAtlas;
import com.agateau.burgerparty.utils.SpriteImage;
import com.agateau.burgerparty.utils.StageScreen;
import com.agateau.burgerparty.utils.Tile;
import com.agateau.burgerparty.utils.TileActor;
import com.agateau.burgerparty.utils.TileMap;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Timer;

public class BurgerCopterMainScreen extends StageScreen {
	static final float PIXEL_PER_SECOND = 180;
	static final float METERS_PER_SECOND = 4;
	static final int TILE_SIZE = 32;
	static final int GROUND_TILE_WIDTH = 64;
	static final int GROUND_TILE_HEIGHT = 64;
	static final int ENEMY_COUNT = 4;
	private static final float FUEL_MID_LEVEL = 0.4f;
	private static final float FUEL_LOW_LEVEL = 0.2f;
	public BurgerCopterMainScreen(BurgerCopterMiniGame miniGame) {
		mMiniGame = miniGame;
		loadSounds();
		createPools();
		createSky();
		createBg();
		createGround();
		createPlayer();
		createHud();
	}

	@Override
	public void dispose() {
		for (Disposable obj: mDisposables) {
			obj.dispose();
		}
	}

	@Override
	public void onBackPressed() {
		mMiniGame.showStartScreen();
	}

	@Override
	public void render(float delta) {
		getStage().act(delta);
		mMeters += METERS_PER_SECOND * delta;
		handleCollisions();
		updateHud(delta);
		getStage().draw();

		if (mGameOver) {
			mMiniGame.showGameOverScreen();
		}
	}

	private void handleCollisions() {
		for(SpriteImage enemy: mEnemies) {
			if (SpriteImage.collide(mPlayer.getActor(), enemy)) {
				mGameOver = true;
			}
		}
	}

	private void loadSounds() {
		SoundAtlas atlas = mMiniGame.getAssets().getSoundAtlas();
		mLowFuelSound = atlas.findSound("low-fuel");
	}
	private void createPlayer() {
		mPlayer = new Player(mMiniGame.getAssets(), mGroundActor);
		mPlayer.getActor().setPosition(10, getStage().getHeight() * 3 / 4);
		mPlayer.hitFatalGround.connect(mHandlers, new Signal0.Handler() {
			@Override
			public void handle() {
				mGameOver = true;
			}
		});
		getStage().addActor(mPlayer.getActor());
	}

	private void createSky() {
		TextureRegion region = mMiniGame.getAssets().getTextureAtlas().findRegion("burgercopter/sky-bg");
		Image bg = new Image(region);
		setBackgroundActor(bg);
	}

	private void createBg() {
		TextureRegion bg1Region = mMiniGame.getAssets().getTextureAtlas().findRegion("burgercopter/bg1");
		assert(bg1Region != null);
		Tile bg1 = new Tile(bg1Region, 0);
		TextureRegion bg2Region = mMiniGame.getAssets().getTextureAtlas().findRegion("burgercopter/bg2");
		assert(bg2Region != null);
		Tile bg2 = new Tile(bg2Region, 0);
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
		int columnCount = MathUtils.ceil(getStage().getWidth() / GROUND_TILE_WIDTH) * 2;
		GroundTileMap map = new GroundTileMap(mMiniGame.getAssets().getTextureAtlas(), columnCount, rowCount, GROUND_TILE_WIDTH, GROUND_TILE_HEIGHT);
		map.firstFill();

		mGroundActor = new TileActor(map, PIXEL_PER_SECOND);
		mGroundActor.setBounds(0, 0, getStage().getWidth(), GROUND_TILE_HEIGHT * rowCount);
		mDisposables.add(mGroundActor);
		getStage().addActor(mGroundActor);

		map.groundEnemyRequested.connect(mHandlers, new Signal2.Handler<Integer, Integer>() {
			@Override
			public void handle(Integer column, Integer row) {
				addEnemy(column, row, EnemyType.GROUND);
			}
		});
		map.flyingEnemyRequested.connect(mHandlers, new Signal2.Handler<Integer, Integer>() {
			@Override
			public void handle(Integer column, Integer row) {
				addEnemy(column, row, EnemyType.FLYING);
			}
		});
	}

	private enum EnemyType {
		GROUND,
		FLYING,
	}

	private class Enemy extends SpriteImage {
		public Enemy(MaskedDrawableAtlas atlas) {
			mAtlas = atlas;
		}

		@Override
		public void act(float delta) {
			float oldRight = getRight();
			updatePosition();
			if (getRight() > oldRight) {
				// We wrapped around, recycle ourself
				remove();
				mEnemies.removeValue(this, true);
				mGroundEnemyPool.free(this);
			}
		}
		public void init(int col, int row, EnemyType type) {
			mCol = col;
			mRow = row;
			MaskedDrawable md = null;
			switch (type) {
			case GROUND:
				md = mAtlas.get("burgercopter/spikes");
				break;
			case FLYING:
				md = mAtlas.get("mealitems/0/fish-inventory");
				break;
			}
			setMaskedDrawable(md);
			updatePosition();
		}
		private void updatePosition() {
			float x = mGroundActor.xForCol(mCol);
			setPosition(
					x + (GROUND_TILE_WIDTH - getWidth()) / 2,
					mGroundActor.yForRow(mRow));
		}

		private int mCol;
		private int mRow;
		private MaskedDrawableAtlas mAtlas;
	}

	private void addEnemy(int col, int row, EnemyType type) {
		Enemy enemy = mGroundEnemyPool.obtain();
		getStage().addActor(enemy);
		enemy.init(col, row, type);
		mEnemies.add(enemy);
	}

	private void createPools() {
		TextureAtlas atlas = mMiniGame.getAssets().getTextureAtlas();
		mMaskedDrawableAtlas = new MaskedDrawableAtlas(atlas);
		mGroundEnemyPool = new Pool<Enemy>() {
			@Override
			protected Enemy newObject() {
				return new Enemy(mMaskedDrawableAtlas);
			}
		};
	}

	private void createHud() {
		mMeterLabel = new Label("0", mMiniGame.getAssets().getSkin(), "timer");
		getStage().addActor(mMeterLabel);
		mMeterLabel.setX(0);
		mMeterLabel.setY(getStage().getHeight() - mMeterLabel.getPrefHeight() + 10);

		mFuelBar = new Gauge(mMiniGame.getAssets().getTextureAtlas().findRegion("ui/white-pixel"), 20);
		getStage().addActor(mFuelBar);
		mFuelBar.setX(2);
		mFuelBar.setY(getStage().getHeight() - mFuelBar.getPrefHeight() - 2);
		mSecondBipTask = new Timer.Task() {
			@Override
			public void run() {
				mLowFuelSound.play();
			}
		};
	}

	private void updateHud(float delta) {
		mMeterLabel.setText((int)mMeters + "m");
		mMeterLabel.setX(getStage().getWidth() - mMeterLabel.getPrefWidth());

		float fuel = mPlayer.getFuel();
		mFuelBar.setValue(fuel);

		mFuelBar.setColor(Color.WHITE);
		if (fuel < FUEL_MID_LEVEL) {
			float oldTime = mFuelPulseTime;
			if (mFuelPulseTime < 0) {
				mFuelPulseTime = 0;
			} else {
				mFuelPulseTime += delta;
				if (mFuelPulseTime > 1) {
					oldTime = -1;
					mFuelPulseTime = 0;
				}
			}
			if (mPlayer.isFlying()) {
				if (mFuelPulseTime >= 0f && oldTime < 0f) {
					mLowFuelSound.play();
					Timer.schedule(mSecondBipTask, 0.2f);
				}
			}
			Color pulseColor = fuel < FUEL_LOW_LEVEL ? Color.RED : Color.ORANGE;
			mFuelBar.getColor().lerp(pulseColor, 1.0f - mFuelPulseTime);
		} else {
			mFuelPulseTime = -1;
		}
	}

	private MaskedDrawableAtlas mMaskedDrawableAtlas;
	private Pool<Enemy> mGroundEnemyPool;

	private BurgerCopterMiniGame mMiniGame;
	private Player mPlayer;
	private TileActor mGroundActor;
	private Array<SpriteImage> mEnemies = new Array<SpriteImage>();
	private float mMeters = 0;
	private float mFuelPulseTime = -1;
	private Label mMeterLabel;
	private Gauge mFuelBar;
	private Array<Disposable> mDisposables = new Array<Disposable>();

	private Sound mLowFuelSound;
	private Timer.Task mSecondBipTask;

	private boolean mGameOver = false;

	private HashSet<Object> mHandlers = new HashSet<Object>();
}
