package com.agateau.burgerparty.model;

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
}
