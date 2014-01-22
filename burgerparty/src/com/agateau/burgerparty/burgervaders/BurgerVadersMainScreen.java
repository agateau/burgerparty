package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.CollisionMask;
import com.agateau.burgerparty.utils.MaskedDrawableAtlas;
import com.agateau.burgerparty.utils.SoundAtlas;
import com.agateau.burgerparty.utils.SpriteImage;
import com.agateau.burgerparty.utils.StageScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class BurgerVadersMainScreen extends StageScreen {
	private static final int BULLET_COUNT = 50;
	private static final int ENEMY_COUNT = 20;
	private static final int SCORE_ENEMY_HIT = 200;
	private static final float MAP_ROW_PER_SECOND = 0.5f;
	private static final int BONUS_PERIOD = 8;

	private static final int MAX_ENEMY_PER_LINE = 4;
	private static final int HARDEST_ROW = 40;
	public BurgerVadersMainScreen(BurgerVadersMiniGame miniGame) {
		mMiniGame = miniGame;
		loadSounds();
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
				bullet.start(srcX, srcY, angle);
				return;
			}
		}
	}

	@Override
	public void render(float delta) {
		mTime += delta;
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
		float gutter = getStage().getWidth() / 6;
		float range = (getStage().getWidth() - gutter * 2) / enemyCount;

		for (int idx = 0; idx < enemyCount; ++idx) {
			Pool<Enemy> pool = mEnemyPools.get(MathUtils.random(mEnemyPools.size - 1));
			Enemy enemy = pool.obtain();
			float width = enemy.getWidth();
			getStage().addActor(enemy);
			enemy.init(gutter + range * idx + MathUtils.random(width / 2, range - width / 2));
			addEnemy(enemy);
		}
	}

	public void addEnemy(Enemy enemy) {
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
				if (enemy.collide(bullet)) {
					enemy.onHit();
					mEnemyHitSound.play();
					bullet.setVisible(false);
					mScore += SCORE_ENEMY_HIT;
					updateHud();
				}
			}
			for (Bonus bonus: mBonuses) {
				if (bonus.hasBeenHit()) {
					continue;
				}
				if (SpriteImage.collide(bullet, bonus)) {
					bonus.onHit();
					mBonusSound.play();
					bullet.setVisible(false);
					mPlayer.addGun();
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
				Gdx.app.log("BurgerVadersMainScreen", "enemy hit the bottom: " + enemy);
				mMiniGame.showGameOverScreen();
				return;
			}
		}
	}

	@Override
	public void onBackPressed() {
		mMiniGame.showStartScreen();
	}

	private void loadSounds() {
		SoundAtlas atlas = mMiniGame.getAssets().getSoundAtlas();
		mBonusSound = atlas.findSound("meal-done");
		mEnemyHitSound = atlas.findSound("invaders-hit");
	}

	private void createPlayer() {
		mPlayer = new Player(this, mMiniGame.getAssets());
		mPlayer.setX(getStage().getWidth() / 2);
		mPlayer.setY(0);
		getStage().addActor(mPlayer);
	}

	private void createBg() {
		TextureRegion region = mMiniGame.getAssets().getTextureAtlas().findRegion("levels/2/background");
		assert(region != null);
		setBackgroundActor(new Image(region));
	}

	private void createPools() {
		TextureAtlas atlas = mMiniGame.getAssets().getTextureAtlas();
		mMaskedDrawableAtlas = new MaskedDrawableAtlas(atlas);
		mEnemyPools = new Array<Pool<Enemy>>();

		Pool<Enemy> friesPool = new Pool<Enemy>() {
			@Override
			public Enemy newObject() {
				final Pool<Enemy> pool = this;
				return new FriesEnemy(mMaskedDrawableAtlas) {
					@Override
					public void mustBeRemoved() {
						removeEnemy(this);
						pool.free(this);
					}
				};
			}
		};

		Pool<Enemy> saladPool = new Pool<Enemy>() {
			@Override
			public Enemy newObject() {
				final Pool<Enemy> pool = this;
				return new SaladEnemy(mMaskedDrawableAtlas) {
					@Override
					public void mustBeRemoved() {
						removeEnemy(this);
						pool.free(this);
					}
				};
			}
		};

		final Pool<BurgerItemEnemy> burgerItemPool = new Pool<BurgerItemEnemy>() {
			@Override
			public BurgerItemEnemy newObject() {
				final Pool<BurgerItemEnemy> pool = this;
				return new BurgerItemEnemy() {
					@Override
					public void mustBeRemoved() {
						removeEnemy(this);
						pool.free(this);
					}
				};
			}
		};

		final BurgerVadersMainScreen screen = this;
		Pool<Enemy> multiBurgerPool = new Pool<Enemy>() {
			@Override
			public Enemy newObject() {
				final Pool<Enemy> pool = this;
				return new MultiBurgerEnemy(mMaskedDrawableAtlas, burgerItemPool, screen) {
					@Override
					public void mustBeRemoved() {
						removeEnemy(this);
						pool.free(this);
					}
				};
			}
		};

		Pool<Enemy> burgerPool = new Pool<Enemy>() {
			@Override
			public Enemy newObject() {
				final Pool<Enemy> pool = this;
				return new BurgerEnemy(mMaskedDrawableAtlas) {
					@Override
					public void mustBeRemoved() {
						removeEnemy(this);
						pool.free(this);
					}
				};
			}
		};

		mEnemyPools.add(friesPool);
		mEnemyPools.add(saladPool);
		mEnemyPools.add(multiBurgerPool);
		mEnemyPools.add(burgerPool);

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
	private Player mPlayer;
	private Array<Enemy> mEnemies = new Array<Enemy>(ENEMY_COUNT);
	private Array<Bullet> mBullets = new Array<Bullet>();
	private Array<Bonus> mBonuses = new Array<Bonus>();
	private int mScore = 0;
	private Label mScoreLabel;
	private Sound mBonusSound;
	private Sound mEnemyHitSound;

	private MaskedDrawableAtlas mMaskedDrawableAtlas;
	private Array<Pool<Enemy>> mEnemyPools;
	private Pool<Bonus> mBonusPool;
	private float mTime = 0;
	private int mRow = -1;
}
