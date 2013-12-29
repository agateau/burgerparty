package com.agateau.burgerparty.burgercopter;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.utils.StageScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class BurgerCopterMainScreen extends StageScreen {
	private static final float PIXEL_PER_SECOND = 600;
	private static final int TILE_SIZE = 32;
	public BurgerCopterMainScreen(BurgerCopterMiniGame miniGame) {
		super(miniGame.getAssets().getSkin());
		mMiniGame = miniGame;
		createSky();
		//createBg();
		createGround();
		createPlayer();
	}

	@Override
	public void onBackPressed() {
		mMiniGame.showStartScreen();
	}

	private static class GroundActor extends Actor {
		public GroundActor(Assets assets) {
			final TextureRegion groundRegion = assets.getTextureAtlas().findRegion("burgercopter/ground");
			final TextureRegion buildingRegion = assets.getTextureAtlas().findRegion("burgercopter/building");

			mGroundColumnCount = (int)(Gdx.graphics.getWidth()) / TILE_SIZE + 1;
			mGroundRowCount = 8;
			mGroundColumns = new Array<Array <TextureRegion>>(mGroundColumnCount);
			for (int col = 0; col < mGroundColumnCount; ++col) {
				Array<TextureRegion> column = new Array<TextureRegion>(mGroundRowCount);
				column.add(groundRegion);
				int rows = MathUtils.random(1, mGroundRowCount);
				for (int row = 1; row < mGroundRowCount; ++row) {
					if (row <= rows) {
						column.add(buildingRegion);
					} else {
						column.add(null);
					}
				}
				mGroundColumns.add(column);
			}
		}

		@Override
		public void act(float delta) {
			mScrollOffset -= delta * PIXEL_PER_SECOND;
			if (mScrollOffset < -TILE_SIZE) {
				mScrollOffset = 0;
				mStartCol = (mStartCol + 1) % mGroundColumnCount;
			}
		}

		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			int snapScrollOffset = (int)mScrollOffset;
			for (int col = 0; col < mGroundColumnCount; ++col) {
				for (int row = 0; row < mGroundRowCount; ++row) {
					TextureRegion region = mGroundColumns.get((col + mStartCol) % mGroundColumnCount).get(row);
					if (region == null) {
						continue;
					}
					batch.draw(region, col * TILE_SIZE + snapScrollOffset, row * TILE_SIZE);
				}
			}
		}

		private int mGroundRowCount;
		private int mGroundColumnCount;
		private Array<Array<TextureRegion>> mGroundColumns;
		private float mScrollOffset = 0;
		private int mStartCol = 0;
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
		TextureRegion region = mMiniGame.getAssets().getTextureAtlas().findRegion("mealitems/0/top-inventory");
		assert(region != null);
		Image image = new Image(region);
		image.setX(10);
		image.setY(Gdx.graphics.getHeight() / 2);
		image.addAction(new Action() {
			@Override
			public boolean act(float delta) {
				Actor player = getActor();
				/*for (Actor wall: mWalls) {
					if (collide(player, wall)) {
						// gameover
						mMiniGame.showStartScreen();
					}
				}*/
				return false;
			}
		});
//		image.addAction(new FollowMouseAction());
		image.addAction(new GravityAction());
		mPlayer = image;
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
		Actor actor = new GroundActor(mMiniGame.getAssets());
		getStage().addActor(actor);
	}

	private static boolean collide(Actor a1, Actor a2) {
		if (!a1.isVisible()) {
			return false;
		}
		if (!a2.isVisible()) {
			return false;
		}
		float a1Left = a1.getX();
		float a1Right = a1.getRight();
		float a1Bottom = a1.getY();
		float a1Top = a1.getTop();
		float a2Left = a2.getX();
		float a2Right = a2.getRight();
		float a2Bottom = a2.getY();
		float a2Top = a2.getTop();
		if (a1Right < a2Left) {
			return false;
		}
		if (a2Right < a1Left) {
			return false;
		}
		if (a1Top < a2Bottom) {
			return false;
		}
		if (a2Top < a1Bottom) {
			return false;
		}
		return true;
	}

	private BurgerCopterMiniGame mMiniGame;
	private Actor mPlayer;
}
