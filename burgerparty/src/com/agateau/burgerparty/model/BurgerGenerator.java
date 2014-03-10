package com.agateau.burgerparty.model;

import java.util.LinkedList;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

class BurgerGenerator {
	private Array<TopBottom> mTopBottomItems = new Array<TopBottom>();
	private Array<BurgerItem> mMiddleItems = new Array<BurgerItem>();

	public BurgerGenerator(int worldIndex, Array<BurgerItem> items) {
		for (BurgerItem item: items) {
			switch (item.getSubType()) {
			case MIDDLE:
				mMiddleItems.add(item);
				break;
			case BOTTOM:
				// Skip, assume there are matching tops
				break;
			case TOP:
				BurgerItem bottomItem = (BurgerItem)MealItemDb.getInstance().get(worldIndex, item.getBottomName());
				assert bottomItem != null;
				mTopBottomItems.add(new TopBottom(item, bottomItem));
				break;
			case TOP_BOTTOM:
				mTopBottomItems.add(new TopBottom(item, item));
				break;
			}
		}
	}

	public LinkedList<BurgerItem> run(int count) {
		LinkedList<BurgerItem> lst = new LinkedList<BurgerItem>();

		TopBottom topBottom = mTopBottomItems.get(MathUtils.random(mTopBottomItems.size - 1));
		lst.add(topBottom.bottom);

		// Generate content, make sure items cannot appear two times consecutively
		Array<BurgerItem> items = new Array<BurgerItem>(mMiddleItems);
		BurgerItem lastItem = null;

		// Subtract 2 because we add top and bottom items out of the loop
		for (int n = count - 2; n >= 0; n--) {
			int index = MathUtils.random(items.size - 1);
			BurgerItem item = items.removeIndex(index);
			if (lastItem != null) {
				items.add(lastItem);
			}
			lastItem = item;
			lst.add(item);
		}

		lst.add(topBottom.top);

		// Replace item in the middle with an intermediate stage for tall burgers
		if (count >= 7) {
			lst.set(count / 2, topBottom.bottom);
		}

		return lst;
	}

	private static class TopBottom {
		public BurgerItem top;
		public BurgerItem bottom;
		public TopBottom(BurgerItem t, BurgerItem b) {
			top = t;
			bottom = b;
		}
	}
}