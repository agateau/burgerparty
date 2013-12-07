package com.agateau.burgerparty.model;

import static org.junit.Assert.*;

import org.junit.Test;

import com.badlogic.gdx.utils.Array;
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
		assertEquals(item.getWorldIndex(), MealItem.WORLD_INDEX_GENERIC);
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

	@Test
	public void testGetItemsForLevel() {
		MealItemDb db = TestUtils.createMealItemDb(
				"<items>"
				+ "<generic>"
				+ "  <item name='salad' type='burger' row='1' column='1' height='12'/>"
				+ "  <item name='onion' type='burger' row='1' column='1' height='12' world='2' level='1'/>"
				+ "</generic>"
				+ "<world index='2'>"
				+ "  <item name='salad' height='24'/>"
				+ "</world>"
				+ "</items>"
			);

		Array<MealItem> lst;

		lst = db.getItemsForLevel(0, 0);
		assertEquals(1, lst.size);
		assertEquals("0/salad", lst.get(0).getPath());

		lst = db.getItemsForLevel(1, 0);
		assertEquals(2, lst.size);
		assertEquals("2/salad", lst.get(0).getPath());
		assertEquals("0/onion", lst.get(1).getPath());
	}

	private static XmlReader.Element parseXml(String xml) {
		XmlReader reader = new XmlReader();
		return reader.parse(xml);
	}
}
