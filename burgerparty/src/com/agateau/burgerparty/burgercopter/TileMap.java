package com.agateau.burgerparty.burgercopter;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

class TileMap {
	TileMap(int columnCount, int rowCount, int tileSize) {
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

	public int getTileSize() {
		return mTileSize;
	}

	public int getRowCount() {
		return mRowCount;
	}

	public int getColumnCount() {
		return mColumnCount;
	}

	public Array<TextureRegion> getColumn(int col) {
		return mColumns.get(col);
	}

	private int mTileSize;
	private int mRowCount;
	private int mColumnCount;
	private Array<Array<TextureRegion>> mColumns;
}