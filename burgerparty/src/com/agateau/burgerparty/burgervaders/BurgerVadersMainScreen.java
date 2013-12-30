package com.agateau.burgerparty.burgervaders;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.agateau.burgerparty.utils.Signal1;
import com.agateau.burgerparty.utils.SpriteImage;
import com.agateau.burgerparty.utils.StageScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

public class BurgerVadersMainScreen extends StageScreen {
	private static final int BULLET_COUNT = 6;
	private static final int ENEMY_COUNT = 20;
	private static final int SCORE_ENEMY_HIT = 200;
	private static final float MAP_ROW_PER_SECOND = 0.5f;
	public BurgerVadersMainScreen(BurgerVadersMiniGame miniGame) {
		super(miniGame.getAssets().getSkin());
		mMiniGame = miniGame;
		createBg();
		createBullets();
		createPlayer();
		createEnemyMap();
		createEnemyPools();
		createHud();
	}

	public void fire(float srcX, float srcY, float angle) {
		// Find an available bullet, and fire it
		for (Bullet bullet: mBullets) {
			if (!bullet.isVisible()) {
				bullet.start(srcX, srcY, angle);
				return;
			}
		}
	}

	@Override
	public void render(float delta) {
		mTime += delta;
		Gdx.gl.glClearColor(0.8f, 0.95f, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		if (mGameOverDelay < 0) {
			getStage().act(delta);
			getStage().draw();
			addEnemies();
			checkBulletHits();
			//checkWon();
			checkGameOver();
		} else {
			mGameOverDelay += delta;
			if (mGameOverDelay > 2) {
				mMiniGame.showStartScreen();
			}
			getStage().draw();
		}
	}

	private void addEnemies() {
		int row = MathUtils.ceil(mTime * MAP_ROW_PER_SECOND) % mEnemyMap.length;
		if (row == mRow) {
			return;
		}
		Gdx.app.log("addEnemies", "row=" + row);
		mRow = row;
		int step = Gdx.graphics.getWidth() / 10;
		for (int idx = 0, n = mEnemyMap[row].length; idx < n; ++idx) {
			char ch = mEnemyMap[row][idx];
			SpriteImagePool<Enemy> pool = null;
			if (ch == ' ') {
				continue;
			} else if (ch == 's') {
				pool = mEnemyPools.get(SaladEnemy.class);
			} else if (ch == 'f') {
				pool = mEnemyPools.get(FriesEnemy.class);
			} else {
				throw new RuntimeException("Unknown enemy type ch=" + ch);
			}
			Enemy enemy = pool.obtain();
			enemy.reset(step * idx);
			addEnemy(enemy);
		}
	}

	private void addEnemy(Enemy enemy) {
		getStage().addActor(enemy);
		for (int idx = 0, n = mEnemies.size; idx < n; ++idx) {
			if (mEnemies.get(idx) == null) {
				mEnemies.set(idx, enemy);
				return;
			}
		}
		Gdx.app.log("Vaders.addEnemy", "No room, must add");
		mEnemies.add(enemy);
	}
	private void removeEnemy(Enemy enemy) {
		enemy.remove();
		mEnemyPools.get(enemy.getClass()).free(enemy);
		for (int idx = 0, n = mEnemies.size; idx < n; ++idx) {
			if (mEnemies.get(idx) == enemy) {
				mEnemies.set(idx, null);
				return;
			}
		}
		Gdx.app.error("removeEnemy", "Could not find enemy " + enemy);
	}

	private void checkBulletHits() {
		for (Bullet bullet: mBullets) {
			if (!bullet.isVisible()) {
				continue;
			}
			for (int idx = 0, n = mEnemies.size; idx < n; ++idx) {
				Enemy enemy = mEnemies.get(idx);
				if (enemy == null) {
					continue;
				}
				if (enemy.isDying()) {
					continue;
				}
				if (SpriteImage.collide(bullet, enemy)) {
					enemy.onHit();
					bullet.setVisible(false);
					mScore += SCORE_ENEMY_HIT;
					updateHud();
				}
			}
		}
	}

	private void checkGameOver() {
		for (Enemy enemy: mEnemies) {
			if (enemy == null) {
				continue;
			}
			if (enemy.getY() < 0) {
				Gdx.app.log("Vaders", "Game Over");
				gameOver();
				break;
			}
		}
	}

	@Override
	public void onBackPressed() {
		mMiniGame.showStartScreen();
	}

	private void createPlayer() {
		TextureRegion region = mMiniGame.getAssets().getTextureAtlas().findRegion("burgervaders/cannon");
		assert(region != null);
		mCannon = new Cannon(this, region);
		mCannon.setX((Gdx.graphics.getWidth() - region.getRegionWidth()) / 2);
		mCannon.setY(0);
		getStage().addActor(mCannon);
	}

	private void createBg() {
		TextureRegion region = mMiniGame.getAssets().getTextureAtlas().findRegion("levels/2/background");
		assert(region != null);
		setBackgroundActor(new Image(region));
	}

	private void createEnemyMap() {
		char map[][] = {
				{ ' ', ' ', ' ', 's', ' ', 's', ' ', 's', ' ', ' ' },
				{ ' ', ' ', 'f', ' ', 's', ' ', ' ', ' ', 'f', ' ' },
				{ ' ', ' ', ' ', 'f', ' ', ' ', ' ', 's', ' ', ' ' },
				{ ' ', ' ', ' ', ' ', ' ', ' ', 's', ' ', ' ', ' ' },
				{ ' ', ' ', ' ', ' ', ' ', 's', ' ', ' ', ' ', ' ' },
				{ ' ', ' ', ' ', ' ', 's', ' ', ' ', ' ', ' ', ' ' },
				{ ' ', ' ', ' ', 'f', ' ', 'f', ' ', ' ', ' ', ' ' },
				{ ' ', ' ', 'f', ' ', ' ', ' ', 'f', ' ', ' ', ' ' },
				{ ' ', ' ', ' ', 'f', ' ', ' ', ' ', ' ', ' ', ' ' },
				{ ' ', ' ', ' ', ' ', ' ', ' ', 'f', ' ', ' ', ' ' },
				{ ' ', ' ', ' ', ' ', 'f', ' ', ' ', ' ', ' ', ' ' },
		};
		/*
				"    s s s   ",
				"   f  s  f  ",
				"    f   s   ",
				"       s    ",
				"      s     ",
				"     f f    ",
				"    f   f   ",
				"     f      ",
				"        f   ",
				"      f     ",
		};*/
		mEnemyMap = map;
	}

	private void createEnemyPools() {
		TextureAtlas atlas = mMiniGame.getAssets().getTextureAtlas();
		mEnemyPools = new HashMap<Class<?>, SpriteImagePool<Enemy>>();
		mEnemyPools.put(FriesEnemy.class,
				new SpriteImagePool<Enemy>(FriesEnemy.class, atlas.findRegion("mealitems/0/big-fries-inventory"))
				);
		mEnemyPools.put(SaladEnemy.class,
				new SpriteImagePool<Enemy>(SaladEnemy.class, atlas.findRegion("mealitems/0/salad-inventory"))
				);
		Signal1.Handler<Enemy> removalHandler = new Signal1.Handler<Enemy>() {
			@Override
			public void handle(Enemy enemy) {
				removeEnemy(enemy);
			}
		};
		mEnemyPools.get(FriesEnemy.class).removalRequested.connect(mHandlers, removalHandler);
		mEnemyPools.get(SaladEnemy.class).removalRequested.connect(mHandlers, removalHandler);
	}

	private void createBullets() {
		TextureRegion region = mMiniGame.getAssets().getTextureAtlas().findRegion("burgervaders/bullet");
		SpriteImage.CollisionMask mask = new SpriteImage.CollisionMask(region);
		assert(region != null);
		for (int i = 0; i < BULLET_COUNT; ++i) {
			Bullet bullet = new Bullet(region, mask);
			mBullets.add(bullet);
			getStage().addActor(bullet);
			bullet.setVisible(false);
		}
	}

	private void createHud() {
		mScoreLabel = new Label("0", mMiniGame.getAssets().getSkin(), "lock-star-text");
		getStage().addActor(mScoreLabel);
		mScoreLabel.setX(0);
		mScoreLabel.setY(Gdx.graphics.getHeight() - mScoreLabel.getPrefHeight());
	}

	private void updateHud() {
		mScoreLabel.setText(String.valueOf((mScore / 10) * 10));
	}

	private void gameOver() {
		mGameOverDelay = 0;
	}

	private float mGameOverDelay = -1;
	private BurgerVadersMiniGame mMiniGame;
	private SpriteImage mCannon;
	private Array<Enemy> mEnemies = new Array<Enemy>(ENEMY_COUNT);
	private Array<Bullet> mBullets = new Array<Bullet>();
	private int mScore = 0;
	private Label mScoreLabel;

	private Map<Class<?>, SpriteImagePool<Enemy>> mEnemyPools;
	private char mEnemyMap[][];
	private float mTime = 0;
	private int mRow = 0;

	private HashSet<Object> mHandlers = new HashSet<Object>();
}
