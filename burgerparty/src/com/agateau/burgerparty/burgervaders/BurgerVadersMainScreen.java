package com.agateau.burgerparty.burgervaders;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.agateau.burgerparty.utils.CollisionMask;
import com.agateau.burgerparty.utils.MaskedDrawableAtlas;
import com.agateau.burgerparty.utils.Signal1;
import com.agateau.burgerparty.utils.SpriteImage;
import com.agateau.burgerparty.utils.SpriteImagePool;
import com.agateau.burgerparty.utils.StageScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class BurgerVadersMainScreen extends StageScreen {
	private static final int BULLET_COUNT = 6;
	private static final int ENEMY_COUNT = 20;
	private static final int SCORE_ENEMY_HIT = 200;
	private static final float MAP_ROW_PER_SECOND = 0.5f;
	private static final int BONUS_PERIOD = 2;

	private static final int MAX_ENEMY_PER_LINE = 4;
	private static final int HARDEST_ROW = 40;
	public BurgerVadersMainScreen(BurgerVadersMiniGame miniGame) {
		super(miniGame.getAssets().getSkin());
		mMiniGame = miniGame;
		createBg();
		createBullets();
		createPlayer();
		createPools();
		createHud();
	}

	public void fire(float srcX, float srcY, float angle) {
		// Find an available bullet, and fire it
		for (Bullet bullet: mBullets) {
			if (!bullet.isVisible()) {
				mFireSound.play();
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
		getStage().act(delta);
		getStage().draw();
		fillNextRow();
		checkBulletHits();
		checkGameOver();
	}

	private void fillNextRow() {
		int row = MathUtils.floor(mTime * MAP_ROW_PER_SECOND);
		if (row == mRow) {
			return;
		}
		mRow = row;
		addEnemies();
		addBonuses();
	}

	private void addEnemies() {
		int enemyCount = MathUtils.random(0, MAX_ENEMY_PER_LINE * mRow / HARDEST_ROW) + 1;
		Gdx.app.log("addEnemies", "row=" + mRow + " enemyCount=" + enemyCount);
		if (enemyCount == 0) {
			return;
		}
		float step = getStage().getWidth() / enemyCount;
		for (int idx = 0; idx < enemyCount; ++idx) {
			SpriteImagePool<Enemy> pool = null;
			if (MathUtils.randomBoolean()) {
				pool = mEnemyPools.get(SaladEnemy.class);
			} else {
				pool = mEnemyPools.get(FriesEnemy.class);
			}
			Enemy enemy = pool.obtain();
			float width = enemy.getWidth();
			getStage().addActor(enemy);
			enemy.reset(step * idx + step / 2 + MathUtils.random(-width, width) - width / 2);
			addEnemy(enemy);
		}
	}

	private void addEnemy(Enemy enemy) {
		for (int idx = 0, n = mEnemies.size; idx < n; ++idx) {
			if (mEnemies.get(idx) == null) {
				mEnemies.set(idx, enemy);
				return;
			}
		}
		Gdx.app.log("Vaders.addEnemy", "No room, must add. Size was " + mEnemies.size);
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

	private void addBonuses() {
		if (mRow == 0 || mRow % BONUS_PERIOD != 0) {
			return;
		}
		Bonus bonus = mBonusPool.obtain();
		bonus.init(getStage());
		mBonuses.add(bonus);
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
				mMiniGame.showGameOverScreen();
				return;
			}
		}
	}

	@Override
	public void onBackPressed() {
		mMiniGame.showStartScreen();
	}

	private void createPlayer() {
		TextureRegion region = mMiniGame.getAssets().getTextureAtlas().findRegion("mealitems/0/ketchup-inventory");//"burgervaders/cannon");
		assert(region != null);
		mCannon = new Cannon(this, region);
		mCannon.setX((getStage().getWidth() - region.getRegionWidth()) / 2);
		mCannon.setY(0);
		getStage().addActor(mCannon);
		mFireSound = mMiniGame.getAssets().getSoundAtlas().findSound("splat");
	}

	private void createBg() {
		TextureRegion region = mMiniGame.getAssets().getTextureAtlas().findRegion("levels/2/background");
		assert(region != null);
		setBackgroundActor(new Image(region));
	}

	private void createPools() {
		TextureAtlas atlas = mMiniGame.getAssets().getTextureAtlas();
		mMaskedDrawableAtlas = new MaskedDrawableAtlas(atlas);
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

		mBonusPool = new Pool<Bonus>() {
			@Override
			protected Bonus newObject() {
				return new Bonus(mMaskedDrawableAtlas) {
					@Override
					public void mustBeRemoved() {
						mBonuses.removeValue(this, true);
						mBonusPool.free(this);
					}
				};
			}
		};
	}

	private void createBullets() {
		TextureRegion region = mMiniGame.getAssets().getTextureAtlas().findRegion("burgervaders/bullet");
		CollisionMask mask = new CollisionMask(region);
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
		mScoreLabel.setY(getStage().getHeight() - mScoreLabel.getPrefHeight());
	}

	private void updateHud() {
		mScoreLabel.setText(String.valueOf((mScore / 10) * 10));
	}

	private BurgerVadersMiniGame mMiniGame;
	private SpriteImage mCannon;
	private Array<Enemy> mEnemies = new Array<Enemy>(ENEMY_COUNT);
	private Array<Bullet> mBullets = new Array<Bullet>();
	private Array<Bonus> mBonuses = new Array<Bonus>();
	private int mScore = 0;
	private Label mScoreLabel;
	private Sound mFireSound;

	private Map<Class<?>, SpriteImagePool<Enemy>> mEnemyPools;
	private MaskedDrawableAtlas mMaskedDrawableAtlas;
	private Pool<Bonus> mBonusPool;
	private float mTime = 0;
	private int mRow = -1;

	private HashSet<Object> mHandlers = new HashSet<Object>();
}
