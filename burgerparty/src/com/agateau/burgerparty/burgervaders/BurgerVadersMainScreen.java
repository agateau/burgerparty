package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.SpriteImage;
import com.agateau.burgerparty.utils.StageScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

public class BurgerVadersMainScreen extends StageScreen {
	private static final int BULLET_COUNT = 6;
	private static final int ENEMY_COUNT = 4;
	private static final int SCORE_ENEMY_HIT = 200;
	public BurgerVadersMainScreen(BurgerVadersMiniGame miniGame) {
		super(miniGame.getAssets().getSkin());
		mMiniGame = miniGame;
		createBg();
		createEnemies();
		createBullets();
		createPlayer();
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
		Gdx.gl.glClearColor(0.8f, 0.95f, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		if (mGameOverDelay < 0) {
			getStage().act(delta);
			getStage().draw();
			addEnemies();
			checkBulletHits();
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
		for (Enemy enemy: mEnemies) {
			if (!enemy.isVisible()) {
				enemy.start(0);
				return;
			}
		}
	}

	private void checkBulletHits() {
		for (Bullet bullet: mBullets) {
			if (!bullet.isVisible()) {
				continue;
			}
			for (Enemy enemy: mEnemies) {
				if (SpriteImage.collide(bullet, enemy)) {
					enemy.setVisible(false);
					bullet.setVisible(false);
					mScore += SCORE_ENEMY_HIT;
					updateHud();
				}
			}
		}
	}

	private void checkGameOver() {
		for (Enemy enemy: mEnemies) {
			if (!enemy.isVisible()) {
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

	private void createEnemies() {
		EnemyType types[] = {
			new FriesEnemyType(mMiniGame.getAssets().getTextureAtlas()),
			new SaladEnemyType(mMiniGame.getAssets().getTextureAtlas()),
		};
		for (int i = 0; i < ENEMY_COUNT; ++i) {
			Enemy enemy = new Enemy(types[MathUtils.random(types.length - 1)]);
			mEnemies.add(enemy);
			getStage().addActor(enemy);
			enemy.start(i * 160);
		}
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
	private Array<Enemy> mEnemies = new Array<Enemy>();
	private Array<Bullet> mBullets = new Array<Bullet>();
	private int mScore = 0;
	private Label mScoreLabel;
}
