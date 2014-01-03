package com.agateau.burgerparty.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class TileActor extends Actor {
	public TileActor(TileMap map, float speed) {
		mMap = map;
		mSpeed = speed;
	}

	@Override
	public void act(float delta) {
		mScrollOffset -= mSpeed * delta;
		if (mScrollOffset < -mMap.getTileSize()) {
			mScrollOffset = 0;
			mStartCol = (mStartCol + 1) % mMap.getColumnCount();
		}
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		Color color = getColor();
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

		int snapScrollOffset = (int)mScrollOffset;
		int tileSize = mMap.getTileSize();
		int colCount = mMap.getColumnCount();
		for (int col = mStartCol, x = snapScrollOffset; x < getWidth(); ++col, x += tileSize) {
			Array<TextureRegion> column = mMap.getColumn(col % colCount);
			for (int row = 0; row < mMap.getRowCount(); ++row) {
				TextureRegion region = column.get(row);
				if (region == null) {
					continue;
				}
				batch.draw(region, getX() + x, getY() + row * tileSize);
			}
		}
	}

	public boolean collide(Actor actor) {
		int minCol = colForX(actor.getX());
		int maxCol = colForX(actor.getRight());
		int minRow = rowForY(actor.getY());
		int maxRow = rowForY(actor.getTop());
		int rowCount = mMap.getRowCount();
		int colCount = mMap.getColumnCount();
		if (minRow >= rowCount) {
			return false;
		}
		for (int col = minCol; col <= maxCol; col = (col + 1) % colCount) {
			Array<TextureRegion> column = mMap.getColumn(col);
			for (int row = minRow; row <= Math.min(maxRow, rowCount - 1); ++row) {
				TextureRegion region = column.get(row);
				if (region != null) {
					return true;
				}
			}
		}
		return false;
	}

	private int colForX(float x) {
		return (MathUtils.floor(x / mMap.getTileSize()) + mStartCol) % mMap.getColumnCount();
	}

	private int rowForY(float y) {
		return MathUtils.floor(y / mMap.getTileSize());
	}

	private TileMap mMap;
	private float mSpeed;
	private float mScrollOffset = 0;
	private int mStartCol = 0;
}