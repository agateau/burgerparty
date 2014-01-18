package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public class CoinView extends Group {
	private static final float COIN_HEIGHT = 5;
	private static final float COIN_OFF_ALPHA = 0.186f;
	private static final float STACK_SPACING = -3;
	private static final float MIN_STAR_SPACING = 5;
	private class CoinStack extends Group {
		public CoinStack() {
			for (int i = 0; i < mStarCost; ++i) {
				Image image = new Image(mCoin);
				mCoinImages.add(image);
				image.setY(i * COIN_HEIGHT);
				image.setColor(1, 1, 1, COIN_OFF_ALPHA);
				addActor(image);
			}

			final float coinWidth = mCoin.getRegion().getRegionWidth();
			final float coinHeight = mCoin.getRegion().getRegionHeight();
			mStarImage = new Image(mStarOff);
			mStarImage.setScale(0.5f);
			mStarImage.setX(coinWidth - 4);
			addActor(mStarImage);

			setWidth(mStarImage.getRight());
			setHeight(Math.max(
					mStarImage.getHeight() + MIN_STAR_SPACING - STACK_SPACING,
					(mStarCost - 1) * COIN_HEIGHT + coinHeight));
		}

		public void setCoinCount(int count) {
			for(int i = 0, n = mCoinImages.size; i < n; ++i) {
				float alpha = i < count ? 1 : COIN_OFF_ALPHA;
				mCoinImages.get(i).setColor(1, 1, 1, alpha);
			}
			if (count == mCoinImages.size) {
				mStarImage.setDrawable(mStarOn);
			}
		}

		Array<Image> mCoinImages = new Array<Image>();
		Image mStarImage;
	}

	public CoinView(Assets assets, int starCost) {
		mStarOn = new TextureRegionDrawable(assets.getTextureAtlas().findRegion("ui/star-on"));
		mStarOff = new TextureRegionDrawable(assets.getTextureAtlas().findRegion("ui/star-off"));
		mCoin = new TextureRegionDrawable(assets.getTextureAtlas().findRegion("ui/coin"));
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
		for (CoinStack stack: mStacks) {
			if (count >= mStarCost) {
				stack.setCoinCount(mStarCost);
				count -= mStarCost;
			} else {
				stack.setCoinCount(count);
				return;
			}
		}
	}

	private final Array<CoinStack> mStacks = new Array<CoinStack>();
	private final int mStarCost;
	private final TextureRegionDrawable mStarOn, mStarOff, mCoin;
}
