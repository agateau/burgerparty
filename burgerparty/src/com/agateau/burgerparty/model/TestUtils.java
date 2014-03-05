package com.agateau.burgerparty.model;

import com.agateau.burgerparty.utils.Signal0;
import com.badlogic.gdx.utils.XmlReader;

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
}
