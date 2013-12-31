package com.agateau.burgerparty.burgerjeweled;

import java.util.HashSet;

import com.agateau.burgerparty.utils.MaskedDrawable;
import com.agateau.burgerparty.utils.Signal1;
import com.agateau.burgerparty.utils.SpriteImagePool;
import com.agateau.burgerparty.utils.StageScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

public class BurgerjeweledMainScreen extends StageScreen {
	private static final int SCORE_NORMAL = 200;
	private static final int SCORE_4 = 400;
	private static final int SCORE_5 = 800;
	private static final int BOARD_SIZE = 8;
	private static final float BOARD_CELL_WIDTH = 100;
	private static final float BOARD_CELL_HEIGHT = 60;

	public BurgerjeweledMainScreen(BurgerjeweledMiniGame miniGame) {
		super(miniGame.getAssets().getSkin());
		mMiniGame = miniGame;
		createPool();
		createPieceDrawables();
		//createBg();
		createBoard();
		createHud();
	}

	@Override
	public void render(float delta) {
		mTime += delta;
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		if (mGameOverDelay < 0) {
			getStage().act(delta);
			handleClicks();
			if (mCollapseNeeded) {
				collapse();
			} else {
				findMatches();
			}
			getStage().draw();
		} else {
			mGameOverDelay += delta;
			/*if (mGameOverDelay > 2) {
				mMiniGame.showStartScreen();
			}*/
			getStage().draw();
		}
	}

	private void handleClicks() {
		if (!Gdx.input.justTouched() || mCollapseNeeded) {
			return;
		}
		Vector2 v = new Vector2(Gdx.input.getX(), Gdx.input.getY());
		v = getStage().screenToStageCoordinates(v);
		int col = MathUtils.floor(v.x / BOARD_CELL_WIDTH);
		int row = MathUtils.floor(v.y / BOARD_CELL_HEIGHT);
		Gdx.app.log("Main", "v=" + v + " col=" + col + " row=" + row);

		if (mClickedCol == -1) {
			mClickedCol = col;
			mClickedRow = row;
			return;
		}
		int dc = Math.abs(mClickedCol - col);
		int dr = Math.abs(mClickedRow - row);
		if ((dr == 1 && dc == 0) || (dr == 0 && dc == 1)) {
			swapPieces(col, row);
		} else {
			mClickedCol = -1;
			mClickedRow = -1;
		}
	}

	private void swapPieces(int col2, int row2) {
		Gdx.app.log("swapPieces", "mClickedCol=" + mClickedCol + " mClickedRow=" + mClickedRow + " col2=" + col2 + " row2=" + row2);
		Piece piece1 = getPieceAt(mClickedCol, mClickedRow);
		Piece piece2 = getPieceAt(col2, row2);

		doSwap(mClickedCol, mClickedRow, col2, row2);
		findMatches();
		if (mCollapseNeeded) {
			piece1.moveTo(piece2.getX(), piece2.getY());
			piece2.moveTo(piece1.getX(), piece1.getY());
		} else {
			piece1.swapTo(piece2.getX(), piece2.getY());
			piece2.swapTo(piece1.getX(), piece1.getY());
			Gdx.app.log("swap", "cancel swap");
			doSwap(col2, row2, mClickedCol, mClickedRow);
		}
		mClickedCol = -1;
		mClickedRow = -1;
	}

	private void doSwap(int col1, int row1, int col2, int row2) {
		Piece piece1 = getPieceAt(col1, row1);
		Piece piece2 = getPieceAt(col2, row2);
		mBoard.get(col2).set(row2, piece1);
		mBoard.get(col1).set(row1, piece2);
	}

	private void checkGameOver() {
		/*
		for (Enemy enemy: mEnemies) {
			if (enemy == null) {
				continue;
			}
			if (enemy.getY() < 0) {
				Gdx.app.log("Vaders", "Game Over");
				gameOver();
				break;
			}
		}
		*/
	}

	@Override
	public void onBackPressed() {
		mMiniGame.showStartScreen();
	}

	private void createBg() {
		TextureRegion region = mMiniGame.getAssets().getTextureAtlas().findRegion("levels/3/background");
		assert(region != null);
		setBackgroundActor(new Image(region));
	}

	private void createPieceDrawables() {
		TextureAtlas atlas = mMiniGame.getAssets().getTextureAtlas();
		mPiecesDrawable.add(new MaskedDrawable(atlas.findRegion("mealitems/0/top-inventory")));
		mPiecesDrawable.add(new MaskedDrawable(atlas.findRegion("mealitems/0/salad-inventory")));
		mPiecesDrawable.add(new MaskedDrawable(atlas.findRegion("mealitems/0/steak-inventory")));
		mPiecesDrawable.add(new MaskedDrawable(atlas.findRegion("mealitems/0/tomato-inventory")));
	}

	private void createPool() {
		mPool = new SpriteImagePool<Piece>(Piece.class);
		mPool.removalRequested.connect(mHandlers, new Signal1.Handler<Piece>() {
			@Override
			public void handle(Piece piece) {
				for (Array<Piece> column: mBoard) {
					for (int row = 0; row < BOARD_SIZE; ++row) {
						if (column.get(row) == piece) {
							column.set(row, null);
							mCollapseNeeded = true;
							return;
						}
					}
				}
			}
		});
	}

	private void createBoard() {
		for (int col = 0; col < BOARD_SIZE; ++col) {
			Array<Piece> column = new Array<Piece>();
			mBoard.add(column);
			for (int row = 0; row < BOARD_SIZE; ++row) {
				column.add(null);
				resetPiece(col, row);
			}
		}
	}

	private void resetPiece(int col, int row) {
		Piece piece = mPool.obtain();
		getStage().addActor(piece);
		int id = MathUtils.random(mPiecesDrawable.size - 1);
		float posX = col * BOARD_CELL_WIDTH;
		float posY = row * BOARD_CELL_HEIGHT; 
		piece.reset(mPiecesDrawable.get(id), id, posX, posY);
		mBoard.get(col).set(row, piece);
	}

	private void findMatches() {
		findVerticalMatches();
		findHorizontalMatches();
	}

	private void findVerticalMatches() {
		for (int col = 0; col < BOARD_SIZE; ++col) {
			Array<Piece> column = mBoard.get(col);
//			Gdx.app.log("findVerticalMatches", "col=" + col);
			int sameCount = 1;
			int lastId = -1;
			for (int row = 0; row < BOARD_SIZE; ++row) {
				Piece piece = column.get(row);
				if (piece == null) {
					return;
				}
				int id = piece.getId();
//				Gdx.app.log("findVerticalMatches", "row=" + row + " id=" + id + " lastId=" + lastId + " sameCount=" + sameCount);
				if (id == lastId) {
					++sameCount;
				} else {
					lastId = id;
					if (sameCount >= 3) {
						deleteVerticalPieces(column, row - sameCount, sameCount);
					}
					sameCount = 1;
				}
			}
			if (sameCount >= 3) {
				deleteVerticalPieces(column, BOARD_SIZE - sameCount, sameCount);
			}
		}
		//mGameOverDelay = 1;
	}

	private void deleteVerticalPieces(Array<Piece> column, int from, int size) {
		Gdx.app.log("deletePieces", "from=" + from + " size="+ size);
		for (int row = from; row < from + size; ++row) {
			column.get(row).destroy();
		}
		mCollapseNeeded = true;
	}

	private void findHorizontalMatches() {
		for (int row = 0; row < BOARD_SIZE; ++row) {
			int sameCount = 1;
			int lastId = -1;
			for (int col = 0; col < BOARD_SIZE; ++col) {
				Piece piece = getPieceAt(col, row);
				if (piece == null) {
					return;
				}
				int id = piece.getId();
				if (id == lastId) {
					++sameCount;
				} else {
					lastId = id;
					if (sameCount >= 3) {
						deleteHorizontalPieces(row, col - sameCount, sameCount);
					}
					sameCount = 1;
				}
			}
			if (sameCount >= 3) {
				deleteHorizontalPieces(row, BOARD_SIZE - sameCount, sameCount);
			}
		}
	}

	private void deleteHorizontalPieces(int row, int fromCol, int size) {
		Gdx.app.log("deletePieces", "row=" + row + "col=" + fromCol + " size="+ size);
		for (int col = fromCol; col < fromCol + size; ++col) {
			getPieceAt(col, row).destroy();
		}
		mCollapseNeeded = true;
	}

	private void collapse() {
		mCollapseNeeded = false;
		for (int col = 0; col < BOARD_SIZE; ++col) {
			collapseColumn(col);
		}
		findMatches();
	}

	private void collapseColumn(int col) {
		Array<Piece> column = mBoard.get(col);
		int fallSize = 0;
		int dstRow = 0;
		for (int row = 0; row < BOARD_SIZE; ++row) {
			Piece piece = column.get(row);
			if (piece == null) {
				++fallSize;
			} else {
				if (fallSize > 0) {
					piece.fallTo(dstRow * BOARD_CELL_HEIGHT);
				}
				column.set(dstRow, piece);
				++dstRow;
			}
		}
		for (int row = BOARD_SIZE - fallSize; row < BOARD_SIZE; ++row) {
			resetPiece(col, row);
		}
	}

	private Piece getPieceAt(int col, int row) {
		assert(col != -1);
		assert(row != -1);
		return mBoard.get(col).get(row);
	}

	private void createHud() {
		mScoreLabel = new Label("0", mMiniGame.getAssets().getSkin(), "lock-star-text");
		getStage().addActor(mScoreLabel);
		mScoreLabel.setX(0);
		mScoreLabel.setY(getStage().getHeight() - mScoreLabel.getPrefHeight());
	}

	private void updateHud() {
		mScoreLabel.setText(String.valueOf((mScore / 10) * 10));
	}

	private void gameOver() {
		mGameOverDelay = 0;
	}

	private float mGameOverDelay = -1;
	private BurgerjeweledMiniGame mMiniGame;
	private int mScore = 0;
	private Label mScoreLabel;
	private boolean mCollapseNeeded = false;

	private SpriteImagePool<Piece> mPool;
	private float mTime = 0;

	private int mClickedRow = -1;
	private int mClickedCol = -1;

	private Array<Array<Piece>> mBoard = new Array<Array<Piece>>();

	private Array<MaskedDrawable> mPiecesDrawable = new Array<MaskedDrawable>();
	private HashSet<Object> mHandlers = new HashSet<Object>();
}
