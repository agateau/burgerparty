package com.agateau.burgerparty.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MealExtraTest {
	@Test
	public void testIsMissing() {
		MealItem.addTestItem(MealItem.Type.SIDE_ORDER, "foo");
		MealItem.addTestItem(MealItem.Type.SIDE_ORDER, "bar");
		MealItem.addTestItem(MealItem.Type.SIDE_ORDER, "unused");

		MealExtra extra1 = new MealExtra();
		MealExtra extra2 = new MealExtra();
		
		extra1.addItem(MealItem.get("foo"));

		extra2.addItem(MealItem.get("foo"));
		extra2.addItem(MealItem.get("bar"));

		assertFalse(extra1.isMissing(extra2, MealItem.get("unused")));
		assertFalse(extra1.isMissing(extra2, MealItem.get("foo")));
		assertTrue(extra1.isMissing(extra2, MealItem.get("bar")));
	}

	@Test
	public void testEquals() {
		MealItem.addTestItem(MealItem.Type.SIDE_ORDER, "foo");
		MealItem.addTestItem(MealItem.Type.SIDE_ORDER, "bar");
		MealItem.addTestItem(MealItem.Type.SIDE_ORDER, "unused");

		MealExtra extra1 = new MealExtra();
		MealExtra extra2 = new MealExtra();

		assertTrue(extra1.equals(extra2));

		extra1.addItem(MealItem.get("foo"));
		assertFalse(extra1.equals(extra2));

		extra2.addItem(MealItem.get("bar"));
		assertFalse(extra1.equals(extra2));

		extra1.addItem(MealItem.get("bar"));
		extra2.addItem(MealItem.get("foo"));
		assertTrue(extra1.equals(extra2));
	}

	@Test
	public void testCompare() {
		MealItem.addTestItem(MealItem.Type.SIDE_ORDER, "foo");
		MealItem.addTestItem(MealItem.Type.SIDE_ORDER, "bar");
		MealItem.addTestItem(MealItem.Type.SIDE_ORDER, "baz");

		MealExtra user = new MealExtra();
		MealExtra reference = new MealExtra();

		reference.addItem(MealItem.get("bar"));
		reference.addItem(MealItem.get("baz"));
		assertEquals(user.compareTo(reference), MealExtra.CompareResult.SUBSET);

		user.addItem(MealItem.get("bar"));
		assertEquals(user.compareTo(reference), MealExtra.CompareResult.SUBSET);

		user.addItem(MealItem.get("baz"));
		assertEquals(user.compareTo(reference), MealExtra.CompareResult.SAME);		

		user.clear();
		user.addItem(MealItem.get("foo"));
		assertEquals(user.compareTo(reference), MealExtra.CompareResult.DIFFERENT);		
	}
}
