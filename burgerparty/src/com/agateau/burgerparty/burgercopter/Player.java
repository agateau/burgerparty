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

	private static final float JUMP_FUEL_CONSUME_PER_SECOND = 0.25f;
	private static final float JUMP_FUEL_REFILL_PER_SECOND = 0.5f;

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

	public float getJumpFuel() {
		return mJumpFuel;
	}

	private void act(float delta) {
		Actor actor = getActor();
		float y = actor.getY();
		if (Gdx.input.isTouched() && mJumpFuel > 0) {
			float maxY = actor.getStage().getHeight() - actor.getHeight();
			y = Math.min(y + PLAYER_DELTA_UP * delta, maxY);
			mJumpFuel = Math.max(mJumpFuel - JUMP_FUEL_CONSUME_PER_SECOND * delta, 0);
		} else {
			float newY = y - PLAYER_DELTA_DOWN * delta;
			float groundHeight1 = mGroundActor.getHeightAt(actor.getX());
			float groundHeight2 = mGroundActor.getHeightAt(actor.getRight());
			float minY = Math.max(groundHeight1, groundHeight2) + PLAYER_MIN_ALTITUDE;
			y = Math.max(newY, minY);
			if (Math.abs(y - minY) < 4.0) {
				mJumpFuel = Math.min(mJumpFuel + JUMP_FUEL_REFILL_PER_SECOND * delta, 1);
			}
		}
		getActor().setY(y);
	}

	private SpriteImage mImage;
	private TileActor mGroundActor;
	private float mJumpFuel = 1;
}