package com.agateau.burgerparty.utils;

import java.io.IOException;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.badlogic.gdx.utils.XmlWriter;

public abstract class ListGameStat<T> extends GameStat {
    private Array<T> mList = new Array<T>();

    @Override
    public void reset() {
        mList.clear();
    }

    @Override
    public void load(Element element) {
        mList.clear();
        for (XmlReader.Element child: element.getChildrenByName("item")) {
            mList.add(itemForString(child.getText()));
        }
    }

    @Override
    public void save(XmlWriter root) throws IOException {
        for (T value: mList) {
            root.element("item", stringForItem(value));
        }
    }

    protected abstract T itemForString(String string);

    protected abstract String stringForItem(T value);

    public boolean contains(T value) {
        return mList.contains(value, false);
    }

    public void add(T value) {
        mList.add(value);
        changed.emit();
    }

    public int getCount() {
        return mList.size;
    }
}
