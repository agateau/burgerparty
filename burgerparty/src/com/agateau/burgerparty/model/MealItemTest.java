package com.agateau.burgerparty.model;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MealItemTest {
	@Test
	public void testEquals() {
		MealItemDb db = TestUtils.createMealItemDb(
				"<items>"
				+ "  <generic>"
				+ "    <item row='0' column='0' name='juice' type='drink'/>"
				+ "    <item row='0' column='1' name='fries' type='side-order'/>"
				+ "  </generic>"
				+ "</items>");
		MealItem juice = db.get("juice");

		assertTrue(juice == db.get("juice"));
		assertTrue(juice != db.get("side-order"));
	}

	@Test
	public void testIsAvailableInLevel() {
		MealItemDb db = TestUtils.createMealItemDb(
				"<items>"
				+ "  <generic>"
				+ "    <item row='0' column='0' name='start'   type='drink'/>"
				+ "    <item row='0' column='1' name='lvl_1_4' type='drink' world='1' level='4'/>"
				+ "    <item row='0' column='2' name='lvl_2_3' type='drink' world='2' level='3'/>"
				+ "  </generic>"
				+ "</items>");
		MealItem start = db.get("start");
		MealItem lvl_1_4 = db.get("lvl_1_4");
		MealItem lvl_2_3 = db.get("lvl_2_3");

		assertTrue(start.isAvailableInLevel(0, 0));
		assertTrue(start.isAvailableInLevel(0, 3));
		assertTrue(start.isAvailableInLevel(1, 0));
		assertTrue(start.isAvailableInLevel(1, 3));

		assertFalse(lvl_1_4.isAvailableInLevel(0, 0));
		assertTrue(lvl_1_4.isAvailableInLevel(0, 3));
		assertTrue(lvl_1_4.isAvailableInLevel(1, 0));
		assertTrue(lvl_1_4.isAvailableInLevel(1, 3));

		assertFalse(lvl_2_3.isAvailableInLevel(0, 0));
		assertFalse(lvl_2_3.isAvailableInLevel(0, 3));
		assertFalse(lvl_2_3.isAvailableInLevel(1, 0));
		assertTrue(lvl_2_3.isAvailableInLevel(1, 3));
	}
}
