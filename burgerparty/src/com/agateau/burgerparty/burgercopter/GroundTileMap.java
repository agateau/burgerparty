package com.agateau.burgerparty.burgercopter;

import com.agateau.burgerparty.utils.MaskedTile;
import com.agateau.burgerparty.utils.Signal2;
import com.agateau.burgerparty.utils.Tile;
import com.agateau.burgerparty.utils.TileMap;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class GroundTileMap extends TileMap {
	private static final int MAX_FILL_SIZE = 6;

	public Signal2<Integer, Integer> groundEnemyRequested = new Signal2<Integer, Integer>();
	public Signal2<Integer, Integer> flyingEnemyRequested = new Signal2<Integer, Integer>();

	public GroundTileMap(TextureAtlas atlas, int columnCount, int rowCount, int tileWidth, int tileHeight) {
		super(columnCount, rowCount, tileWidth, tileHeight);
		mGround = new Tile(atlas.findRegion("burgercopter/ground"));
		mStone = new Tile(atlas.findRegion("burgercopter/stone"));
		mStoneUpA = new Tile(atlas.findRegion("burgercopter/stone-up-a"));
		mStoneUpB = new MaskedTile(atlas.findRegion("burgercopter/stone-up-b"));
		mStoneDownA = new Tile(atlas.findRegion("burgercopter/stone-down-a"));
		mStoneDownB = new MaskedTile(atlas.findRegion("burgercopter/stone-down-b"));

		mFirstFreeCol = 0;
		mLastFreeCol = columnCount - 1;
		mGroundLevel = 1;
		while (fillColumns()) {}
	}

	@Override
	public void recycleColumn(int col) {
		mLastFreeCol = col;
		fillColumns();
	}

	private int deltaCol(int from, int to) {
		if (from > to) {
			to += getColumnCount();
		}
		assert(to >= from);
		return to - from;
	}

	private boolean fillColumns() {
		if (deltaCol(mFirstFreeCol, mLastFreeCol) < MAX_FILL_SIZE) {
			return false;
		}
		int newGroundLevel = MathUtils.clamp(mGroundLevel + MathUtils.random(-4, 4), 1, getRowCount() - 1);
		int fillSize;
		if (newGroundLevel == mGroundLevel) {
			fillSize = MathUtils.random(MAX_FILL_SIZE / 2, MAX_FILL_SIZE);
			fillFlat(fillSize);
		} else if (newGroundLevel > mGroundLevel) {
			fillUp(newGroundLevel - mGroundLevel);
		} else if (newGroundLevel < mGroundLevel) {
			fillDown(mGroundLevel - newGroundLevel);
		}
		assert(mGroundLevel == newGroundLevel);
		return true;
	}

	private Array<Tile> advanceNextCol() {
		Array<Tile> column = getColumn(mFirstFreeCol);
		mFirstFreeCol = (mFirstFreeCol + 1) % getColumnCount();
		return column;
	}

	private void fillFlat(int size) {
		for (int idx = 0; idx < size; ++idx) {
			if (mFirstFreeCol % 4 == 0) {
				groundEnemyRequested.emit(mFirstFreeCol, mGroundLevel);
			}
			Array<Tile> column = advanceNextCol();
			fillColumn(column, mStone);
		}
	}

	private void fillUp(int size) {
		++mGroundLevel;
		for (int idx = 0; idx < size; ++idx, ++mGroundLevel) {
			Array<Tile> column = advanceNextCol();
			fillColumn(column, mStoneUpA, mStoneUpB);
		}
		--mGroundLevel;
	}

	private void fillDown(int size) {
		for (int idx = 0; idx < size; ++idx, --mGroundLevel) {
			Array<Tile> column = advanceNextCol();
			fillColumn(column, mStoneDownA, mStoneDownB);
		}
	}

	private void fillColumn(Array<Tile> column, Tile top) {
		int row;
		for (row = 0; row < mGroundLevel - 1; ++row) {
			column.set(row, mGround);
		}
		column.set(row++, top);
		fillVoid(column, row);
	}

	private void fillColumn(Array<Tile> column, Tile top1, Tile top2) {
		int row;
		for (row = 0; row < mGroundLevel - 2; ++row) {
			column.set(row, mGround);
		}
		column.set(row++, top1);
		column.set(row++, top2);
		fillVoid(column, row);
	}

	private void fillVoid(Array<Tile> column, int row) {
		int rowCount = getRowCount();
		if (rowCount - row > 3 && MathUtils.randomBoolean(0.6f)) {
			int col = mFirstFreeCol - 1;
			if (col < 0) {
				col = getColumnCount() - 1;
			}
			flyingEnemyRequested.emit(col, MathUtils.random(row + 1, rowCount - 1));
		}
		for (; row < rowCount; ++row) {
			column.set(row, null);
		}
	}

	private final Tile mGround;
	private final Tile mStone;
	private final Tile mStoneUpA;
	private final Tile mStoneUpB;
	private final Tile mStoneDownA;
	private final Tile mStoneDownB;

	private int mGroundLevel;

	private int mFirstFreeCol;
	private int mLastFreeCol;
}
