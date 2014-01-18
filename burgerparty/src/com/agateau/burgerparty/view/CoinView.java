package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
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
	private class CoinStack extends Group {
		public CoinStack() {
			final float coinWidth = mCoinRegion.getRegionWidth();
			final float coinHeight = mCoinRegion.getRegionHeight();

			for (int i = 0; i < mStarCost; ++i) {
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
					(mStarCost - 1) * COIN_THICKNESS + coinHeight));
		}

		public float setCoinCount(int count, float delay) {
			if (count == mCoinCount) {
				return 0;
			}
			assert(count >= mCoinCount);
			assert(count <= mCoinImages.size);
			for(int i = mCoinCount; i < count; ++i, delay += NEW_COIN_DURATION) {
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
			mCoinCount = count;
			if (count == mCoinImages.size) {
				mStarImage.addAction(
						Actions.delay(delay, Actions.fadeIn(NEW_COIN_DURATION))
				);
				delay += NEW_COIN_DURATION;
			}
			return delay;
		}

		Array<Image> mCoinImages = new Array<Image>();
		Image mStarImage;

		private int mCoinCount = 0;
	}

	public CoinView(Assets assets, int starCost) {
		mStarRegion = assets.getTextureAtlas().findRegion("ui/star-on");
		mCoinRegion = assets.getTextureAtlas().findRegion("ui/coin");
		mStarCost = starCost;
		int y = 0;
		for (int i = 1; i <= 3; ++i) {
			CoinStack stack = new CoinStack();
			stack.setY(y);
			y += stack.getHeight() + STACK_SPACING;
			mStacks.add(stack);
			addActor(stack);
		}
		CoinStack last = mStacks.get(2);
		setSize(last.getWidth(), last.getTop());
	}

	public void setCoinCount(int count) {
		float delay = 0;
		for (CoinStack stack: mStacks) {
			if (count >= mStarCost) {
				delay = stack.setCoinCount(mStarCost, delay);
				count -= mStarCost;
			} else {
				delay = stack.setCoinCount(count, delay);
				return;
			}
		}
	}

	private final Array<CoinStack> mStacks = new Array<CoinStack>();
	private final int mStarCost;
	private final TextureRegion mStarRegion, mCoinRegion;
}
