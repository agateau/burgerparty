package com.agateau.burgerparty.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class BurgerTest {
	@Test
	public void testCompare() {
		BurgerItem.addTestItem("foo");
		BurgerItem.addTestItem("bar");
		BurgerItem.addTestItem("baz");

		Burger user = new Burger();
		Burger reference = new Burger();

		reference.addItem(BurgerItem.get("bar"));
		reference.addItem(BurgerItem.get("baz"));
		assertEquals(user.compareTo(reference), Burger.CompareResult.SUBSET);

		user.addItem(BurgerItem.get("bar"));
		assertEquals(user.compareTo(reference), Burger.CompareResult.SUBSET);

		user.addItem(BurgerItem.get("baz"));
		assertEquals(user.compareTo(reference), Burger.CompareResult.SAME);		

		user.clear();
		user.addItem(BurgerItem.get("foo"));
		assertEquals(user.compareTo(reference), Burger.CompareResult.DIFFERENT);		
	}
}
