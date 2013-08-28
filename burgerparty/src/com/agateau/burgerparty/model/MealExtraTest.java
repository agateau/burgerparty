package com.agateau.burgerparty.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MealExtraTest {
	private static MealItem[] arrayFromMealExtra(MealExtra extra) {
		return extra.getItems().toArray(new MealItem[0]);
	}

	@Test
	public void testAdd() {
		MealItem juice = MealItem.addTestItem(MealItem.Type.DRINK, "juice");
		MealItem fries = MealItem.addTestItem(MealItem.Type.SIDE_ORDER, "fries");
		MealItem potatoes = MealItem.addTestItem(MealItem.Type.SIDE_ORDER, "potatoes");

		MealExtra extra = new MealExtra();
		extra.addItem(fries);
		extra.addItem(juice);
		assertArrayEquals(new MealItem[]{fries, juice}, arrayFromMealExtra(extra));

		extra.addItem(fries);
		assertArrayEquals(new MealItem[]{fries, fries, juice}, arrayFromMealExtra(extra));

		extra.addItem(potatoes);
		assertArrayEquals(new MealItem[]{fries, fries, potatoes, juice}, arrayFromMealExtra(extra));
	}

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
		MealItem juice = MealItem.addTestItem(MealItem.Type.DRINK, "juice");
		MealItem fries = MealItem.addTestItem(MealItem.Type.SIDE_ORDER, "fries");
		MealItem potatoes = MealItem.addTestItem(MealItem.Type.SIDE_ORDER, "potatoes");
		MealItem soda = MealItem.addTestItem(MealItem.Type.DRINK, "soda");

		MealExtra reference = new MealExtra();
		reference.addItem(fries);
		reference.addItem(juice);
		reference.addItem(potatoes);

		{
			MealExtra user = new MealExtra();
			user.addItem(fries);
			user.addItem(juice);
			assertEquals(MealExtra.CompareResult.SUBSET, user.compareTo(reference));
		}

		{ // Add an item which is not in reference
			MealExtra user = new MealExtra();
			user.addItem(soda);
			assertEquals(MealExtra.CompareResult.DIFFERENT, user.compareTo(reference));
		}

		{ // Reference has one fries, but user has two
			MealExtra user = new MealExtra();
			user.addItem(fries);
			user.addItem(fries);
			assertEquals(MealExtra.CompareResult.DIFFERENT, user.compareTo(reference));
		}

		{ // Same as reference, but added in a different order
			MealExtra user = new MealExtra();
			user.addItem(juice);
			user.addItem(potatoes);
			user.addItem(fries);
			assertEquals(MealExtra.CompareResult.SAME, user.compareTo(reference));
		}
	}
}
