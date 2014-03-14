package com.agateau.burgerparty.model;

import com.agateau.burgerparty.utils.Signal0;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

public class TestUtils {
    public static XmlReader.Element parseXml(String xml) {
        XmlReader reader = new XmlReader();
        return reader.parse(xml);
    }

    public static MealItemDb createMealItemDb(String xml) {
        MealItemDb db = new MealItemDb();
        XmlReader.Element root = parseXml(xml);
        db.load(root);
        return db;
    }

    public static class SignalSpy0 implements Signal0.Handler {
        public int count = 0;
        @Override
        public void handle() {
            ++count;
        }
    }

    public static Element findChildElementById(Element root, String string) {
        for (int i = 0, n = root.getChildCount(); i < n; ++i) {
            Element child = root.getChild(i);
            if (child.getAttribute("id").equals(string)) {
                return child;
            }
        }
        return null;
    }
}
