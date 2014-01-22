package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.utils.SoundAtlas;
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
	private static final float STAR_FINAL_SCALE = 0.5f;
	private static final float NEW_COIN_DURATION = 0.2f;
	private static final Color EXTRA_COLOR = new Color(1, 0.5f, 0.5f, 0);

	private class CoinStack extends Group {
		public CoinStack(int size, int index) {
			mSize = size;
			mIndex = index - 1;
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
			mStarImage.setScale(2f);
			mStarImage.setX(coinWidth - 4);
			mStarImage.setOrigin(mStarImage.getWidth() / 2, mStarImage.getHeight() / 2);
			mStarImage.setColor(1, 1, 1, 0);
			addActor(mStarImage);

			setWidth(mStarImage.getRight());
			setHeight(Math.max(
					mStarImage.getHeight() * STAR_FINAL_SCALE + MIN_STAR_SPACING - STACK_SPACING,
					(mSize - 1) * COIN_THICKNESS + coinHeight));
		}

		public void setExtra(boolean extra) {
			for (Image image: mCoinImages) {
				image.setColor(EXTRA_COLOR);
			}
			mStarImage.remove();
			mStarImage = null;
		}

		public float addCoin(float delay) {
			assert(mCoinCount < mSize);
			Image image = mCoinImages.get(mCoinCount);

			float finalY = mCoinCount * COIN_THICKNESS;
			image.setPosition(0, finalY + 60);
			image.rotate(MathUtils.random(-80, 80));
			image.addAction(
				Actions.delay(delay,
					Actions.sequence(
						Actions.parallel(
							Actions.fadeIn(NEW_COIN_DURATION / 3),
							Actions.rotateTo(0, NEW_COIN_DURATION),
							Actions.moveTo(0, finalY, NEW_COIN_DURATION, Interpolation.pow2In)
						),
						mSoundAtlas.createPlayAction("coin")
					)
				)
			);
			mCoinCount++;
			delay += NEW_COIN_DURATION;

			if (mCoinCount == mSize && mStarImage != null) {
				mStarImage.addAction(
					Actions.delay(delay,
						Actions.sequence(
								Actions.parallel(
								Actions.scaleTo(STAR_FINAL_SCALE, STAR_FINAL_SCALE, NEW_COIN_DURATION),
								Actions.fadeIn(NEW_COIN_DURATION)
							),
							mSoundAtlas.createPlayAction("star", 1 + mIndex * 0.05f)
						)
					)
				);
				delay += NEW_COIN_DURATION;
			}
			return delay;
		}

		public int getSize() {
			return mSize;
		}

		public int getCoinCount() {
			return mCoinCount;
		}

		private final int mSize;
		private final int mIndex;
		private final Array<Image> mCoinImages = new Array<Image>();
		private Image mStarImage;

		private int mCoinCount = 0;
	}

	public CoinView(Assets assets, int starCost, int maxCoinCount) {
		mStarRegion = assets.getTextureAtlas().findRegion("ui/star-on");
		mCoinRegion = assets.getTextureAtlas().findRegion("ui/coin");
		mSoundAtlas = assets.getSoundAtlas();
		int y = 0;
		for (int i = 1; i <= 3; ++i) {
			CoinStack stack = new CoinStack(starCost, i);
			stack.setY(y);
			y += stack.getHeight() + STACK_SPACING;
			mStacks.add(stack);
			addActor(stack);
		}
		int remaining = maxCoinCount - starCost * 3;
		assert(remaining >= 0);
		if (remaining > 0) {
			CoinStack stack = new CoinStack(remaining, 4);
			stack.setExtra(true);
			stack.setY(y);
			mStacks.add(stack);
			addActor(stack);
		}
		CoinStack last = mStacks.get(mStacks.size - 1);
		setSize(last.getWidth(), last.getTop());
	}

	public void setCoinCount(int count) {
		if (count == mCoinCount) {
			return;
		}
		assert(count > mCoinCount);
		int delta = count - mCoinCount;
		mCoinCount = count;
		float delay = 0;
		for (CoinStack stack: mStacks) {
			int stackSize = stack.getSize();
			while (stack.getCoinCount() < stackSize) {
				delay = stack.addCoin(delay);
				delta--;
				if (delta == 0) {
					return;
				}
			}
		}
	}

	private final Array<CoinStack> mStacks = new Array<CoinStack>();
	private final TextureRegion mStarRegion, mCoinRegion;
	private final SoundAtlas mSoundAtlas;
	private int mCoinCount = 0;
}
