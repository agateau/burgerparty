package com.agateau.burgerparty.utils;

import java.io.IOException;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.badlogic.gdx.utils.XmlWriter;

public class StringListGameStat extends GameStat {
    private Array<String> mList = new Array<String>();

    public StringListGameStat(String id) {
        super(id);
    }

    @Override
    public void reset() {
        mList.clear();
    }

    @Override
    public void load(Element element) {
        mList.clear();
        for (XmlReader.Element item: element.getChildrenByName("item")) {
            mList.add(item.getText());
        }
    }

    @Override
    public void save(XmlWriter root) throws IOException {
        for (String value: mList) {
            root.element("item", value);
        }
    }

    public boolean contains(String dateString) {
        return mList.contains(dateString, false);
    }

    public void add(String dateString) {
        mList.add(dateString);
        changed.emit();
    }

    public int getCount() {
        return mList.size;
    }
}
