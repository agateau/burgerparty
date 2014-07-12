package com.agateau.burgerparty.model;

import java.util.Collection;
import java.util.Vector;

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

    public Collection<BurgerItem> run(int count) {
        Vector<BurgerItem> lst = new Vector<BurgerItem>();
        lst.setSize(count);
        TopBottom topBottom = mTopBottomItems.get(MathUtils.random(mTopBottomItems.size - 1));
        lst.set(0, topBottom.bottom);
        lst.set(count - 1, topBottom.top);
        // Create a second stage for tall burgers
        if (count >= 7) {
            int separator = count / 2;
            lst.set(separator, topBottom.bottom);
            fillStage(lst, 1, separator);
            fillStage(lst, separator + 1, count - 1);
        } else {
            fillStage(lst, 1, count - 1);
        }
        return lst;
    }

    public void fillStage(Vector<BurgerItem> lst, int start, int end) {
        // Generate content, make sure items cannot appear two times consecutively
        Array<BurgerItem> items = new Array<BurgerItem>(mMiddleItems);
        BurgerItem lastItem = null;

        for (int n = start; n < end; ++n) {
            int index = MathUtils.random(items.size - 1);
            BurgerItem item = items.removeIndex(index);
            if (lastItem != null) {
                items.add(lastItem);
            }
            lastItem = item;
            lst.set(n, item);
        }
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