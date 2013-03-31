package com.agateau.burgerparty.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MealExtraTest {
	@Test
	public void testIsMissing() {
		MealItem.addTestItem("foo");
		MealItem.addTestItem("bar");
		MealItem.addTestItem("unused");

		MealExtra extra1 = new MealExtra();
		MealExtra extra2 = new MealExtra();
		
		extra1.addItem(MealItem.get("foo"));

		extra2.addItem(MealItem.get("foo"));
		extra2.addItem(MealItem.get("bar"));

		assertFalse(extra1.isMissing(extra2, MealItem.get("unused")));
		assertFalse(extra1.isMissing(extra2, MealItem.get("foo")));
		assertTrue(extra1.isMissing(extra2, MealItem.get("bar")));
	}
}
