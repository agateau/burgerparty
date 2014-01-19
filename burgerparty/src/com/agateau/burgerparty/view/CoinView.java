package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

public class CoinView extends Group {
	private static final float COIN_THICKNESS = 5;
	private static final float STACK_SPACING = -3;
	private static final float MIN_STAR_SPACING = 5;
	private static final float NEW_COIN_DURATION = 0.2f;
	private static final Color EXTRA_COLOR = new Color(1, 0.5f, 0.5f, 0);

	private class CoinStack extends Group {
		public CoinStack(int size) {
			mSize = size;
			final float coinWidth = mCoinRegion.getRegionWidth();
			final float coinHeight = mCoinRegion.getRegionHeight();

			for (int i = 0; i < mSize; ++i) {
				Image image = new Image(mCoinRegion);
				mCoinImages.add(image);
				image.setOrigin(coinWidth / 2, 0);
				image.setColor(1, 1, 1, 0);
				addActor(image);
			}
			mStarImage = new Image(mStarRegion);
			mStarImage.setScale(0.5f);
			mStarImage.setX(coinWidth - 4);
			mStarImage.setColor(1, 1, 1, 0);
			addActor(mStarImage);

			setWidth(mStarImage.getRight());
			setHeight(Math.max(
					mStarImage.getHeight() * mStarImage.getScaleY() + MIN_STAR_SPACING - STACK_SPACING,
					(mSize - 1) * COIN_THICKNESS + coinHeight));
		}

		public void setExtra(boolean extra) {
			for (Image image: mCoinImages) {
				image.setColor(EXTRA_COLOR);
			}
			mStarImage.remove();
			mStarImage = null;
		}

		public float setCoinCount(int count, float delay) {
			if (count == mCurrentCount) {
				return 0;
			}
			assert(count >= mCurrentCount);
			assert(count <= mSize);
			for(int i = mCurrentCount; i < count; ++i, delay += NEW_COIN_DURATION) {
				Image image = mCoinImages.get(i);

				float finalY = i * COIN_THICKNESS;
				image.setPosition(0, finalY + 60);
				image.rotate(MathUtils.random(-80, 80));
				image.addAction(
						Actions.delay(delay,
								Actions.parallel(
										Actions.fadeIn(NEW_COIN_DURATION / 3),
										Actions.rotateTo(0, NEW_COIN_DURATION),
										Actions.moveTo(0, finalY, NEW_COIN_DURATION, Interpolation.pow2In)
								)
						)
				);			}
			mCurrentCount = count;
			if (count == mSize && mStarImage != null) {
				mStarImage.addAction(
						Actions.delay(delay, Actions.fadeIn(NEW_COIN_DURATION))
				);
				delay += NEW_COIN_DURATION;
			}
			return delay;
		}

		public int getSize() {
			return mSize;
		}

		private final int mSize;
		private final Array<Image> mCoinImages = new Array<Image>();
		private Image mStarImage;

		private int mCurrentCount = 0;
	}

	public CoinView(Assets assets, int starCost, int maxCoinCount) {
		mStarRegion = assets.getTextureAtlas().findRegion("ui/star-on");
		mCoinRegion = assets.getTextureAtlas().findRegion("ui/coin");
		int y = 0;
		for (int i = 1; i <= 3; ++i) {
			CoinStack stack = new CoinStack(starCost);
			stack.setY(y);
			y += stack.getHeight() + STACK_SPACING;
			mStacks.add(stack);
			addActor(stack);
		}
		int remaining = maxCoinCount - starCost * 3;
		assert(remaining >= 0);
		if (remaining > 0) {
			CoinStack stack = new CoinStack(remaining);
			stack.setExtra(true);
			stack.setY(y);
			mStacks.add(stack);
			addActor(stack);
		}
		CoinStack last = mStacks.get(mStacks.size - 1);
		setSize(last.getWidth(), last.getTop());
	}

	public void setCoinCount(int count) {
		float delay = 0;
		for (CoinStack stack: mStacks) {
			int stackSize = stack.getSize();
			if (count >= stackSize) {
				delay = stack.setCoinCount(stackSize, delay);
				count -= stackSize;
			} else {
				delay = stack.setCoinCount(count, delay);
				return;
			}
		}
	}

	private final Array<CoinStack> mStacks = new Array<CoinStack>();
	private final TextureRegion mStarRegion, mCoinRegion;
}
