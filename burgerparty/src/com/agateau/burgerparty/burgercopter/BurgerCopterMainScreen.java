package com.agateau.burgerparty.burgercopter;

import com.agateau.burgerparty.utils.StageScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class BurgerCopterMainScreen extends StageScreen {
	static final float PIXEL_PER_SECOND = 180;
	static final int TILE_SIZE = 32;
	static final int ENEMY_COUNT = 4;
	public BurgerCopterMainScreen(BurgerCopterMiniGame miniGame) {
		super(miniGame.getAssets().getSkin());
		mMiniGame = miniGame;
		//createSky();
		//createBg();
		createGround();
		createEnemies();
		createPlayer();
	}

	@Override
	public void onBackPressed() {
		mMiniGame.showStartScreen();
	}

	private FPSLogger mLogger = new FPSLogger();
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.8f, 0.95f, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		if (mGameOverDelay < 0) {
			getStage().act(delta);
		} else {
			mGameOverDelay += delta;
			if (mGameOverDelay > 2) {
				mMiniGame.showStartScreen();
			}
		}
		getStage().draw();
		mLogger.log();
	}

	private static class FollowMouseAction extends Action {
		@Override
		public boolean act(float delta) {
			getActor().setY(Gdx.graphics.getHeight() - Gdx.input.getY());
			return false;
		}
	}

	private static class GravityAction extends Action {
		@Override
		public boolean act(float delta) {
			float y = getActor().getY();
			if (Gdx.input.isTouched()) {
				y += 1;
			} else {
				y -= 1;
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
				actor.setX(Gdx.graphics.getWidth());
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
		TextureRegion region = mMiniGame.getAssets().getTextureAtlas().findRegion("mealitems/2/soda-inventory");
		assert(region != null);
		SpriteImage image = new SpriteImage(region);
		mPlayer = image;
		image.setX(10);
		image.setY(Gdx.graphics.getHeight() * 3 / 4);
		image.addAction(new Action() {
			@Override
			public boolean act(float delta) {
				if (mGroundActor.collide(mPlayer)) {
					gameOver();
				}
				for(SpriteImage enemy: mEnemies) {
					if (SpriteImage.collide(mPlayer, enemy)) {
						gameOver();
					}
				}
				return false;
			}
		});
//		image.addAction(new FollowMouseAction());
		image.addAction(new GravityAction());
		getStage().addActor(mPlayer);
	}

	private void createSky() {
		TextureRegion region = mMiniGame.getAssets().getTextureAtlas().findRegion("burgercopter/sky");
		assert(region != null);
		setBackgroundActor(new Image(region));
	}

	private void createBg() {
		TextureRegion region = mMiniGame.getAssets().getTextureAtlas().findRegion("burgercopter/bg1");
		assert(region != null);
		float width = region.getRegionWidth();
		for (float x = 0; x <= Gdx.graphics.getWidth(); x += width) {
			Image image = new Image(region);
			image.setX(x);
			image.setY(TILE_SIZE);
			image.addAction(new ScrollAction(PIXEL_PER_SECOND / 4));
			getStage().addActor(image);
		}
	}

	private void createGround() {
		int columnCount = (int)(Gdx.graphics.getWidth()) / TILE_SIZE + 1;
		int rowCount = 8;
		mGroundActor = new TileActor(columnCount, rowCount, TILE_SIZE);

		final TextureRegion groundRegion = mMiniGame.getAssets().getTextureAtlas().findRegion("burgercopter/ground");
		final TextureRegion buildingRegion = mMiniGame.getAssets().getTextureAtlas().findRegion("burgercopter/building");

		for (int col = 0; col < columnCount; ++col) {
			Array<TextureRegion> column = mGroundActor.getColumn(col);
			int rows = MathUtils.random(1, rowCount);
			column.set(0, groundRegion);
			for (int row = 1; row < rows; ++row) {
				column.set(row, buildingRegion);
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
			int x = Gdx.graphics.getWidth();
			int y = MathUtils.random(240, 480);
			actor.setX(x);
			actor.setY(y);
			return false;
		}
	}
	private void createEnemies() {
		final TextureRegion region = mMiniGame.getAssets().getTextureAtlas().findRegion("mealitems/0/fish-inventory");
		assert(region != null);
		SpriteImage.CollisionMask mask = new SpriteImage.CollisionMask(region);
		float screenWidth = Gdx.graphics.getWidth();
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

	private void gameOver() {
		mGameOverDelay = 0;
	}

	private float mGameOverDelay = -1;
	private BurgerCopterMiniGame mMiniGame;
	private SpriteImage mPlayer;
	private TileActor mGroundActor;
	private Array<SpriteImage> mEnemies = new Array<SpriteImage>();
}
