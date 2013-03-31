package com.agateau.burgerparty.model;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MealItemTest {
	@Test
	public void testEquals() {
		MealItem.addTestItem("foo");
		MealItem.addTestItem("bar");

		MealItem i1 = MealItem.get("foo");
		MealItem i2 = MealItem.get("foo");
		MealItem i3 = MealItem.get("bar");
		assertTrue(i1 == i2);
		assertTrue(i1 != i3);
	}
}
