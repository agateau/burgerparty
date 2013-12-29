package com.agateau.burgerparty.burgercopter;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

class TileActor extends Actor {
	public TileActor(int columnCount, int rowCount, int tileSize) {
		mColumnCount = columnCount;
		mRowCount = rowCount;
		mTileSize = tileSize;

		mColumns = new Array<Array <TextureRegion>>(mColumnCount);
		for (int col = 0; col < mColumnCount; ++col) {
			Array<TextureRegion> column = new Array<TextureRegion>(mRowCount);
			for (int row = 0; row < mRowCount; ++row) {
				column.add(null);
			}
			mColumns.add(column);
		}
	}

	@Override
	public void act(float delta) {
		mScrollOffset -= delta * BurgerCopterMainScreen.PIXEL_PER_SECOND;
		if (mScrollOffset < -mTileSize) {
			mScrollOffset = 0;
			mStartCol = (mStartCol + 1) % mColumnCount;
		}
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		int snapScrollOffset = (int)mScrollOffset;
		for (int col = 0; col < mColumnCount; ++col) {
			for (int row = 0; row < mRowCount; ++row) {
				TextureRegion region = mColumns.get((col + mStartCol) % mColumnCount).get(row);
				if (region == null) {
					continue;
				}
				batch.draw(region, col * mTileSize + snapScrollOffset, row * mTileSize);
			}
		}
	}

	public boolean collide(Actor actor) {
		int minCol = colForX(actor.getX());
		int maxCol = colForX(actor.getRight());
		int minRow = rowForY(actor.getY());
		int maxRow = rowForY(actor.getTop());
		if (minRow >= mRowCount) {
			return false;
		}
		for (int row = minRow; row <= Math.min(maxRow, mRowCount - 1); ++row) {
			for (int col = minCol ; col <= maxCol; col = (col + 1) % mColumnCount) {
				TextureRegion region = mColumns.get(col).get(row);
				if (region != null) {
					return true;
				}
			}
		}
		return false;
	}

	public Array<TextureRegion> getColumn(int idx) {
		return mColumns.get(idx);
	}

	private int colForX(float x) {
		return (((int)x) / mTileSize + mStartCol) % mColumnCount;
	}

	private int rowForY(float y) {
		return ((int)y) / mTileSize;
	}

	private int mTileSize;
	private int mRowCount;
	private int mColumnCount;
	private Array<Array<TextureRegion>> mColumns;
	private float mScrollOffset = 0;
	private int mStartCol = 0;
}