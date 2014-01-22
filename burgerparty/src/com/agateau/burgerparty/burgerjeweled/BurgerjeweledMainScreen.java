package com.agateau.burgerparty.burgerjeweled;

import com.agateau.burgerparty.model.MiniGame;
import com.agateau.burgerparty.utils.MaskedDrawable;
import com.agateau.burgerparty.utils.StageScreen;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class BurgerjeweledMainScreen extends StageScreen {
	private static final int SCORE_NORMAL = 200;
	private static final float TIME_BONUS = 10;
	private static final float START_TIME = 60;
	private static final float BOARD_CELL_WIDTH = 100;
	private static final float BOARD_CELL_HEIGHT = 60;
	private static final float BONUS_ANIM_DURATION = 1.2f;

	private class OurInputListener extends InputListener {
		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			if (mBoard.hasDyingPieces()) {
				return false;
			}
			int col = MathUtils.floor(x / BOARD_CELL_WIDTH);
			int row = MathUtils.floor(y / BOARD_CELL_HEIGHT);
			mTouchedCol = col;
			mTouchedRow = row;
			return true;
		}

		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			int col = MathUtils.floor(x / BOARD_CELL_WIDTH);
			int row = MathUtils.floor(y / BOARD_CELL_HEIGHT);
			if (col == mTouchedCol && row != mTouchedRow) {
				// vertical swipe
				mFirstPieceCol = mTouchedCol;
				mFirstPieceRow = mTouchedRow;
				swapPieces(col, mTouchedRow + (row > mTouchedRow ? 1 : -1));
				return;
			}
			if (col != mTouchedCol && row == mTouchedRow) {
				// horizontal swipe
				mFirstPieceCol = mTouchedCol;
				mFirstPieceRow = mTouchedRow;
				swapPieces(mTouchedCol + (col > mTouchedCol ? 1 : -1), row);
				return;
			}
			if (mFirstPieceCol == -1) {
				// first piece clicked
				mFirstPieceCol = col;
				mFirstPieceRow = row;
			} else {
				// second piece clicked
				swapPieces(col, row);
			}
		}

		private int mTouchedCol = -1;
		private int mTouchedRow = -1;
	}

	public BurgerjeweledMainScreen(MiniGame miniGame) {
		mMiniGame = miniGame;
		setupBg();
		createPools();
		createPieceDrawables();
		resetBoard();
		createHud();
		getStage().addListener(new OurInputListener());
	}

	@Override
	public void render(float delta) {
		mTime -= delta;
		if (mTime < 0) {
			mTime = 0;
		}
		getStage().act(delta);
		if (!mBoard.hasDyingPieces() && !mCollapseNeeded) {
			findMatches();
		}
		if (!mBoard.hasDyingPieces() && mCollapseNeeded) {
			collapse();
		}
		checkGameOver();
		updateHud();
		getStage().draw();
	}

	private void swapPieces(int col2, int row2) {
		assert(!mBoard.hasDyingPieces());
		assert(mFirstPieceCol != -1);
		assert(mFirstPieceRow != -1);
		int dc = Math.abs(mFirstPieceCol - col2);
		int dr = Math.abs(mFirstPieceRow - row2);
		boolean adjacentSwap = (dc == 1 && dr == 0) || (dc == 0 && dr == 1);
		if (!adjacentSwap) {
			mFirstPieceCol = -1;
			mFirstPieceRow = -1;
			return;
		}
		Piece piece1 = mBoard.getPiece(mFirstPieceCol, mFirstPieceRow);
		Piece piece2 = mBoard.getPiece(col2, row2);

		mPendingBoard.initFrom(mBoard);
		mPendingBoard.swap(mFirstPieceCol, mFirstPieceRow, col2, row2);
		if (mPendingBoard.hasMatchesAt(mFirstPieceCol, mFirstPieceRow) || mPendingBoard.hasMatchesAt(col2, row2)) {
			Board tmp = mPendingBoard;
			mPendingBoard = mBoard;
			mBoard = tmp;
			piece1.moveTo(piece2.getX(), piece2.getY());
			piece2.moveTo(piece1.getX(), piece1.getY());
		} else {
			piece1.swapTo(piece2.getX(), piece2.getY());
			piece2.swapTo(piece1.getX(), piece1.getY());
		}
		mFirstPieceCol = -1;
		mFirstPieceRow = -1;
	}

	private void checkGameOver() {
		if (mTime <= 0) {
			mMiniGame.showGameOverScreen();
		}
	}

	@Override
	public void onBackPressed() {
		mMiniGame.showStartScreen();
	}

	private void setupBg() {
		TextureRegion region = mMiniGame.getAssets().getTextureAtlas().findRegion("ui/white-pixel");
		Image bg = new Image(region);
		bg.setColor(0.2f, 0.2f, 0.2f, 1);
		setBackgroundActor(bg);
	}

	private void createPieceDrawables() {
		TextureAtlas atlas = mMiniGame.getAssets().getTextureAtlas();
		mPiecesDrawable.add(new MaskedDrawable(atlas.findRegion("mealitems/0/top-inventory")));
		mPiecesDrawable.add(new MaskedDrawable(atlas.findRegion("mealitems/0/salad-inventory")));
		mPiecesDrawable.add(new MaskedDrawable(atlas.findRegion("mealitems/0/steak-inventory")));
		mPiecesDrawable.add(new MaskedDrawable(atlas.findRegion("mealitems/0/tomato-inventory")));
		mPiecesDrawable.add(new MaskedDrawable(atlas.findRegion("mealitems/0/fish-inventory")));
		mPiecesDrawable.add(new MaskedDrawable(atlas.findRegion("mealitems/0/cheese-inventory")));
		mPiecesDrawable.add(new MaskedDrawable(atlas.findRegion("mealitems/3/toast-inventory")));
	}

	private void createPools() {
		mPiecePool = new Pool<Piece>() {
			@Override
			public Piece newObject() {
				final Piece piece = new Piece() {
					@Override
					public void mustBeRemoved() {
						if (mBoard.removePiece(this)) {
							mPiecePool.free(this);
						}
					}
				};
				return piece;
			}
		};
		mBonusPool = new Pool<Label>() {
			@Override
			protected Label newObject() {
				return new Label("", mMiniGame.getAssets().getSkin(), "score-feedback");
			}
		};
	}

	private void resetBoard() {
		for (int row = 0; row < Board.BOARD_SIZE; ++row) {
			for (int col = 0; col < Board.BOARD_SIZE; ++col) {
				resetPiece(col, row);
			}
		}
	}

	private void resetPiece(int col, int row) {
		Piece piece = mPiecePool.obtain();
		getStage().addActor(piece);
		int id = MathUtils.random(mPiecesDrawable.size - 1);
		float posX = col * BOARD_CELL_WIDTH;
		float posY = row * BOARD_CELL_HEIGHT; 
		piece.reset(mPiecesDrawable.get(id), id, posX, posY);
		mBoard.setPiece(col, row, piece);
	}

	private void findMatches(int sliceStep, int cellStep) {
		int firstCellId = mBoard.getCellId(0, 0);
		for (int slice = 0; slice < Board.BOARD_SIZE; ++slice, firstCellId += sliceStep) {
			int sameCount = 1;
			int lastId = -1;
			int cellId = firstCellId;
			for (int step = 0; step < Board.BOARD_SIZE; ++step, cellId += cellStep) {
				Piece piece = mBoard.getPiece(cellId);
				assert(piece != null);
				int id = piece.getId();
				if (id == lastId) {
					++sameCount;
				} else {
					lastId = id;
					if (sameCount >= Board.MATCH_COUNT) {
						markPieces(cellId - sameCount * cellStep, cellId, cellStep);
					}
					sameCount = 1;
				}
			}
			if (sameCount >= Board.MATCH_COUNT) {
				markPieces(cellId - sameCount * cellStep, cellId, cellStep);
			}
		}
	}

	private void markPieces(int from, int to, int step) {
		for (int cellId = from; cellId != to; cellId += step) {
			mBoard.getPiece(cellId).destroy();
		}
	}

	private void findMatches() {
		findMatches(1, Board.CELL_ROW_STEP);
		findMatches(Board.CELL_ROW_STEP, 1);
		
		int count = 0;
		for (int row = 0; row < Board.BOARD_SIZE; ++row) {
			for (int col = 0; col < Board.BOARD_SIZE; ++col) {
				Piece piece = mBoard.getPiece(col, row);
				if (piece.isDying()) {
					++count; 
				}
			}
		}
		if (count == 0) {
			return;
		}
		mCollapseNeeded = true;
		int score = SCORE_NORMAL * count;
		mScore += score;
		addBonus("+" + score);
		if (count > Board.MATCH_COUNT) {
			int bonus = (int)TIME_BONUS * (count - Board.MATCH_COUNT);
			mTime = Math.min(START_TIME, mTime + bonus);
			addBonus("+" + bonus + "s");
		}
	}

	private void addBonus(String name) {
		final Label label = mBonusPool.obtain();
		getStage().addActor(label);
		label.setColor(1, 1, 1, 1);
		label.setText(name);
		label.invalidate();
		label.setX((getStage(). getWidth() - label.getPrefWidth()) / 2);
		float height = label.getPrefHeight();
		float y = (getStage().getHeight() - height) / 2;
		if (mLastBonusLabel != null && mLastBonusLabel.getStage() != null) {
			y = Math.min(y, mLastBonusLabel.getY() - height * 0.75f);
		}
		label.setY(y);
		label.addAction(Actions.sequence(
			Actions.parallel(
				Actions.moveBy(0, height, BONUS_ANIM_DURATION),
				Actions.alpha(0, BONUS_ANIM_DURATION, Interpolation.pow3In)
			),
			Actions.run(new Runnable() {
				@Override
				public void run() {
					label.remove();
					mLastBonusLabel = null;
					mBonusPool.free(label);
				}
			})
			)
		);
		mLastBonusLabel = label;
	}

	private void collapse() {
		if (mBoard.hasDyingPieces()) {
			return;
		}
		mCollapseNeeded = false;
		for (int col = 0; col < Board.BOARD_SIZE; ++col) {
			collapseColumn(col);
		}
	}

	private void collapseColumn(int col) {
		Array<Piece> column = mBoard.getColumn(col);
		int fallSize = 0;
		int dstRow = 0;
		for (int row = 0; row < Board.BOARD_SIZE; ++row) {
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
		for (int row = Board.BOARD_SIZE - fallSize; row < Board.BOARD_SIZE; ++row) {
			resetPiece(col, row);
		}
	}

	private void createHud() {
		mScoreLabel = new Label("0\n0", mMiniGame.getAssets().getSkin(), "score");
		getStage().addActor(mScoreLabel);
		mScoreLabel.setX(0);
		mScoreLabel.setY(getStage().getHeight() - mScoreLabel.getPrefHeight());
	}

	private void updateHud() {
		mScoreLabel.setText("Score: "+ mScore + "\nTime: " + (int)mTime);
		// Keep hud above pieces
		mScoreLabel.setZIndex(1000);
	}

	// "final" fields
	private MiniGame mMiniGame;
	private Label mScoreLabel;

	private Pool<Piece> mPiecePool;
	private Pool<Label> mBonusPool;
	private Board mBoard = new Board();
	private Board mPendingBoard = new Board();

	private Array<MaskedDrawable> mPiecesDrawable = new Array<MaskedDrawable>();

	// mutable fields
	private Label mLastBonusLabel;
	private float mTime = START_TIME;
	private int mScore = 0;
	private int mFirstPieceRow = -1;
	private int mFirstPieceCol = -1;
	private boolean mCollapseNeeded = false;
}
