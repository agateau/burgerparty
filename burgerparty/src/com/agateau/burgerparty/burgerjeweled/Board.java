package com.agateau.burgerparty.burgerjeweled;

import com.badlogic.gdx.utils.Array;

public class Board {
	public Board() {
		for (int col = 0; col < Board.BOARD_SIZE; ++col) {
			Array<Piece> column = new Array<Piece>();
			mColumns.add(column);
			for (int row = 0; row < Board.BOARD_SIZE; ++row) {
				column.add(null);
			}
		}
	}

	public void setPiece(int col, int row, Piece piece) {
		assert(col != -1);
		assert(row != -1);
		mColumns.get(col).set(row, piece);
	}

	public Piece getPiece(int col, int row) {
		assert(col != -1);
		assert(row != -1);
		return mColumns.get(col).get(row);
	}

	public boolean removePiece(Piece piece) {
		for (Array<Piece> column: mColumns) {
			for (int row = 0; row < BOARD_SIZE; ++row) {
				if (column.get(row) == piece) {
					column.set(row, null);
					return true;
				}
			}
		}
		return false;
	}

	public Array<Piece> getColumn(int col) {
		return mColumns.get(col);
	}

	private Array<Array<Piece>> mColumns = new Array<Array<Piece>>();
	public static final int BOARD_SIZE = 8;
}
