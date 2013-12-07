package com.agateau.burgerparty.model;

import static org.junit.Assert.assertTrue;

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
}
