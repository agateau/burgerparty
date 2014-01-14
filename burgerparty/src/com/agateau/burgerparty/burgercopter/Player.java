package com.agateau.burgerparty.burgercopter;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.SpriteImage;
import com.agateau.burgerparty.utils.Tile;
import com.agateau.burgerparty.utils.TileActor;
import com.agateau.burgerparty.utils.TileMap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class Player {
	private static final float PLAYER_DELTA_UP = 3f * 60f;
	private static final float PLAYER_DELTA_DOWN = 2f * 60f;
	private static final float HOVER_ALTITUDE = 2;

	private static final float FUEL_CONSUME_PER_SECOND = 0.25f;
	private static final float FUEL_REFILL_PER_SECOND = 0.5f;

	Signal0 hitFatalGround = new Signal0();

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
		if (Gdx.input.isTouched() && mFuel > 0) {
			handleFlying(delta);
		} else {
			handleGliding(delta);
		}
	}

	private void handleFlying(float delta) {
		Actor actor = getActor();
		float y = actor.getY();
		float maxY = actor.getStage().getHeight() - actor.getHeight();
		y = Math.min(y + PLAYER_DELTA_UP * delta, maxY);
		mFuel = Math.max(mFuel - FUEL_CONSUME_PER_SECOND * delta, 0);
		mIsFlying = true;

		actor.setY(y);
	}

	private void handleGliding(float delta) {
		Actor actor = getActor();
		float y = actor.getY() - PLAYER_DELTA_DOWN * delta;
		float groundHeight = 0;
		for (int x = 0; x < actor.getWidth(); x += 4) {
			groundHeight = Math.max(groundHeight, mGroundActor.getHeightAt(actor.getX() + x));
		}
		float minY = groundHeight;
		boolean fatalGround = fatalGroundAt(actor.getX()) && fatalGroundAt(actor.getRight());
		if (fatalGround) {
			minY -= 4;
		} else {
			minY += HOVER_ALTITUDE;
		}
		y = Math.max(y, minY);
		mIsFlying = Math.abs(y - minY) > 4.0f;
		if (!mIsFlying) {
			mFuel = Math.min(mFuel + FUEL_REFILL_PER_SECOND * delta, 1);
		}
		if (y == minY && fatalGround) {
			hitFatalGround.emit();
		}
		actor.setY(y);
	}

	private boolean fatalGroundAt(float x) {
		TileMap map = mGroundActor.getMap();
		int col = mGroundActor.colForX(x);
		Array<Tile> column = map.getColumn(col);
		for (int row = column.size - 1; row >= 0; --row) {
			Tile tile = column.get(row);
			if (tile != null) {
				return tile.typeId == GroundTileMap.FATAL_TYPE_ID;
			}
		}
		return true;
	}

	private SpriteImage mImage;
	private TileActor mGroundActor;
	private float mFuel = 1;
	private boolean mIsFlying = false;
}