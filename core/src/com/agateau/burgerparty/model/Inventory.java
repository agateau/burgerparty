package com.agateau.burgerparty.model;

import com.agateau.burgerparty.model.MealItem;

import com.badlogic.gdx.utils.Array;

public class Inventory {
    private Array<MealItem> mItems = new Array<MealItem>();

    public void setItems(Array<? extends MealItem> items) {
        mItems = new Array<MealItem>(items);
    }

    public void clear() {
        mItems.clear();
    }

    public Array<MealItem> getItems() {
        return mItems;
    }

    public void addItem(MealItem item) {
        mItems.add(item);
    }

    public MealItem get(int index) {
        if (index >=0 && index < mItems.size) {
            return mItems.get(index);
        } else {
            return null;
        }
    }
}
