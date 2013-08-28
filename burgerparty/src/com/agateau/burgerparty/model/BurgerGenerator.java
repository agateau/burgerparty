package com.agateau.burgerparty.model;

import java.util.LinkedList;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

class BurgerGenerator {
	public BurgerGenerator(Array<String> names) {
		for (String name: names) {
			BurgerItem item = BurgerItem.get(name);
			switch (item.getSubType()) {
			case MIDDLE:
				mMiddleItems.add(item);
				break;
			case BOTTOM:
				// Skip, assume there are matching tops
				break;
			case TOP:
				BurgerItem bottomItem = BurgerItem.get(item.getBottomName());
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
		for (; count >= 0; count--) {
			int index = MathUtils.random(items.size - 1);
			BurgerItem item = items.removeIndex(index);
			if (lastItem != null) {
				items.add(lastItem);
			}
			lastItem = item;
			lst.add(item);
		}

		lst.add(topBottom.top);
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

	private Array<TopBottom> mTopBottomItems = new Array<TopBottom>();
	private Array<BurgerItem> mMiddleItems = new Array<BurgerItem>();
}