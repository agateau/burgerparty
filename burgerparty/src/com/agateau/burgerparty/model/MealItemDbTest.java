package com.agateau.burgerparty.model;

import static org.junit.Assert.*;

import org.junit.Test;

import com.badlogic.gdx.utils.XmlReader;

public class MealItemDbTest {

	@Test
	public void testGet() {
		XmlReader.Element root = parseXml(
			"<items>"
			+ "<generic>"
			+ "  <item name='foo' type='burger' row='1' column='3' height='12'/>"
			+ "</generic>"
			+ "<world index='3'>"
			+ "  <item name='foo' height='24'/>"
			+ "</world>"
			+ "</items>"
			);
		MealItemDb db = new MealItemDb();
		db.load(root);
		BurgerItem item;

		item = (BurgerItem)db.get(0, "foo");
		assertEquals(item.getName(), "foo");
		assertEquals(item.getWorldIndex(), -1); // Generic
		assertEquals(item.getRow(), 1);
		assertEquals(item.getColumn(), 3);
		assertEquals(item.getHeight(), 12);

		item = (BurgerItem)db.get(2, "foo"); // 2 == world[@index] - 1 
		assertEquals(item.getName(), "foo");
		assertEquals(item.getWorldIndex(), 2);
		assertEquals(item.getRow(), 1);
		assertEquals(item.getColumn(), 3);
		assertEquals(item.getHeight(), 24);
	}

	private static XmlReader.Element parseXml(String xml) {
		XmlReader reader = new XmlReader();
		return reader.parse(xml);
	}

}
