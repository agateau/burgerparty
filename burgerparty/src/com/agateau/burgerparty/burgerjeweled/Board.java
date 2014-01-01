package com.agateau.burgerparty.burgerjeweled;

import com.badlogic.gdx.utils.Array;

public class Board {
	public static final int MATCH_COUNT = 3;
	public Board() {
		for (int col = 0; col < Board.BOARD_SIZE; ++col) {
			Array<Piece> column = new Array<Piece>();
			mColumns.add(column);
			for (int row = 0; row < Board.BOARD_SIZE; ++row) {
				column.add(null);
			}
		}
	}

	public void initFrom(Board other) {
		for (int col = 0; col < Board.BOARD_SIZE; ++col) {
			for (int row = 0; row < Board.BOARD_SIZE; ++row) {
				setPiece(col, row, other.getPiece(col, row));
			}
		}
	}

	public boolean hasMatchesAt(int atCol, int atRow) {
		int id = getPiece(atCol, atRow).getId();
		int count = 1;
		count += findSame(id, atCol + 1, atRow, 1, 0);
		if (count >= MATCH_COUNT) {
			return true;
		}
		count += findSame(id, atCol - 1, atRow, -1, 0);
		if (count >= MATCH_COUNT) {
			return true;
		}
		count = 1;
		count += findSame(id, atCol, atRow + 1, 0, 1);
		if (count >= MATCH_COUNT) {
			return true;
		}
		count += findSame(id, atCol, atRow - 1, 0, -1);
		if (count >= MATCH_COUNT) {
			return true;
		}
		return false;
	}

	private int findSame(int id, int col, int row, int dc, int dr) {
		int count = 0;
		for (;col >= 0 && row >= 0 && col < BOARD_SIZE && row < BOARD_SIZE; col += dc, row += dr) {
			if (getPiece(col, row).getId() == id) {
				++count;
			} else {
				break;
			}
		}
		return count;
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

	public boolean hasDyingPieces() {
		for (Array<Piece> column: mColumns) {
			for (Piece piece: column) {
				if (piece != null && piece.isDying()) {
					return true;
				}
			}
		}
		return false;
	}

	public void swap(int col1, int row1, int col2, int row2) {
		Piece piece1 = getPiece(col1, row1);
		Piece piece2 = getPiece(col2, row2);
		setPiece(col2, row2, piece1);
		setPiece(col1, row1, piece2);
	}

	private Array<Array<Piece>> mColumns = new Array<Array<Piece>>();
	public static final int BOARD_SIZE = 8;
}
