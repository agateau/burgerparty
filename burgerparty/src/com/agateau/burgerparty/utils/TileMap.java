package com.agateau.burgerparty.utils;

import com.badlogic.gdx.utils.Array;

public class TileMap {
	public TileMap(int columnCount, int rowCount, int tileSize) {
		this(columnCount, rowCount, tileSize, tileSize);
	}

	public TileMap(int columnCount, int rowCount, int tileWidth, int tileHeight) {
		mColumnCount = columnCount;
		mRowCount = rowCount;
		mTileWidth = tileWidth;
		mTileHeight = tileHeight;

		mColumns = new Array<Array <Tile>>(mColumnCount);
		for (int col = 0; col < mColumnCount; ++col) {
			Array<Tile> column = new Array<Tile>(mRowCount);
			for (int row = 0; row < mRowCount; ++row) {
				column.add(null);
			}
			mColumns.add(column);
		}
	}

	public int getTileWidth() {
		return mTileWidth;
	}

	public int getTileHeight() {
		return mTileHeight;
	}

	public int getRowCount() {
		return mRowCount;
	}

	public int getColumnCount() {
		return mColumnCount;
	}

	public Array<Tile> getColumn(int col) {
		return mColumns.get(col);
	}

	public void recycleColumn(int col) {
	}

	private int mTileWidth;
	private int mTileHeight;
	private int mRowCount;
	private int mColumnCount;
	private Array<Array<Tile>> mColumns;
}