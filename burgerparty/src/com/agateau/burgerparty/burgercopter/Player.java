package com.agateau.burgerparty.burgercopter;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.utils.SpriteImage;
import com.agateau.burgerparty.utils.TileActor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Player {
	private static final float PLAYER_DELTA_UP = 3f * 60f;
	private static final float PLAYER_DELTA_DOWN = 2f * 60f;
	private static final float PLAYER_MIN_ALTITUDE = 2;

	private static final float FUEL_CONSUME_PER_SECOND = 0.25f;
	private static final float FUEL_REFILL_PER_SECOND = 0.5f;

	public Player(Assets assets, TileActor groundActor) {
		assert(groundActor != null);
		mGroundActor = groundActor;

		TextureRegion region = assets.getTextureAtlas().findRegion("mealitems/0/top-inventory");
		assert(region != null);

		final Player player = this;
		mImage = new SpriteImage(region) {
			@Override
			public void act(float delta) {
				player.act(delta);
			}
		};
	}

	public SpriteImage getActor() {
		return mImage;
	}

	public float getFuel() {
		return mFuel;
	}

	public boolean isFlying() {
		return mIsFlying;
	}

	private void act(float delta) {
		Actor actor = getActor();
		float y = actor.getY();
		if (Gdx.input.isTouched() && mFuel > 0) {
			float maxY = actor.getStage().getHeight() - actor.getHeight();
			y = Math.min(y + PLAYER_DELTA_UP * delta, maxY);
			mFuel = Math.max(mFuel - FUEL_CONSUME_PER_SECOND * delta, 0);
			mIsFlying = true;
		} else {
			float newY = y - PLAYER_DELTA_DOWN * delta;
			float groundHeight = 0;
			for (int x = 0; x < actor.getWidth(); x += 4) {
				groundHeight = Math.max(groundHeight, mGroundActor.getHeightAt(actor.getX() + x));
			}
			float minY = groundHeight + PLAYER_MIN_ALTITUDE;
			y = Math.max(newY, minY);
			mIsFlying = Math.abs(y - minY) > 4.0f;
			if (!mIsFlying) {
				mFuel = Math.min(mFuel + FUEL_REFILL_PER_SECOND * delta, 1);
			}
		}
		getActor().setY(y);
	}

	private SpriteImage mImage;
	private TileActor mGroundActor;
	private float mFuel = 1;
	private boolean mIsFlying = false;
}