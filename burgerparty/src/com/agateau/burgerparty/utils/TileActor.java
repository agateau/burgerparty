package com.agateau.burgerparty.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class TileActor extends Actor implements Disposable {
	private TileMap mMap;
	private float mSpeed;
	private float mScrollOffset = 0;
	private int mStartCol = 0;
	private FrameBuffer mFrameBuffer = null;
	private Matrix4 mFrameBufferProjectionMatrix = null;

	public TileActor(TileMap map, float speed) {
		mMap = map;
		mSpeed = speed;
	}

	@Override
	public void dispose() {
		mFrameBuffer.dispose();
	}

	@Override
	public void act(float delta) {
		if (mFrameBuffer == null) {
			updateFrameBuffer();
		}
		mScrollOffset += mSpeed * delta;
		if (mScrollOffset > mMap.getTileWidth()) {
			mScrollOffset = 0;
			mMap.recycleColumn(mStartCol);
			mStartCol = (mStartCol + 1) % mMap.getColumnCount();
			updateFrameBuffer();
		}
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		Texture texture = mFrameBuffer.getColorBufferTexture();
		Color color = getColor();
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		batch.draw(texture, getX() - mScrollOffset, getY(), mFrameBuffer.getWidth(), mFrameBuffer.getHeight(), 0f, 0f, 1f, 1f);
	}

	private void updateFrameBuffer() {
		if (mFrameBuffer == null) {
			int tileWidth = mMap.getTileWidth();
			int w = MathUtils.ceil(getWidth() / tileWidth) * tileWidth + tileWidth;
			int h = (int)getHeight();
			mFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, w, h, false);
			mFrameBufferProjectionMatrix = new Matrix4();
			mFrameBufferProjectionMatrix.setToOrtho2D(0, 0, w, h);
		}
		mFrameBuffer.begin();

		SpriteBatch batch = getStage().getSpriteBatch();
		batch.disableBlending();
		batch.setColor(Color.WHITE);
		batch.begin();

		Gdx.gl.glClearColor(1, 1, 1, 0);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		batch.setProjectionMatrix(mFrameBufferProjectionMatrix);
		int tileWidth = mMap.getTileWidth();
		int tileHeight = mMap.getTileHeight();
		int colCount = mMap.getColumnCount();
		for (int col = mStartCol, x = 0; x < mFrameBuffer.getWidth(); ++col, x += tileWidth) {
			Array<Tile> column = mMap.getColumn(col % colCount);
			for (int row = 0; row < mMap.getRowCount(); ++row) {
				Tile tile = column.get(row);
				if (tile == null) {
					continue;
				}
				batch.draw(tile.region, x, row * tileHeight);
			}
		}
		batch.end();
		batch.enableBlending();

		mFrameBuffer.end();
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
			Array<Tile> column = mMap.getColumn(col);
			for (int row = minRow; row <= Math.min(maxRow, rowCount - 1); ++row) {
				Tile tile = column.get(row);
				if (tile != null) {
					return true;
				}
			}
		}
		return false;
	}

	public float getHeightAt(float x) {
		int col = colForX(x);
		float xInTile = (x + mScrollOffset) % mMap.getTileWidth();
		Array<Tile> column = mMap.getColumn(col);
		int row = column.size - 1;
		for (; row >= 0; --row) {
			Tile tile = column.get(row);
			if (tile != null) {
				return row * mMap.getTileHeight() + tile.getHeightAt(xInTile);
			}
		}
		return 0;
	}

	public int colForX(float x) {
		return (MathUtils.floor((x + mScrollOffset) / mMap.getTileWidth()) + mStartCol) % mMap.getColumnCount();
	}

	public int rowForY(float y) {
		return MathUtils.floor(y / mMap.getTileHeight());
	}

	public float xForCol(int col) {
		int colCount = mMap.getColumnCount();
		while (col < mStartCol) {
			col += colCount;
		}
		return (col - mStartCol) % colCount * mMap.getTileWidth() - mScrollOffset;
	}

	public float yForRow(int row) {
		return row * mMap.getTileHeight();
	}

	public TileMap getMap() {
		return mMap;
	}
}