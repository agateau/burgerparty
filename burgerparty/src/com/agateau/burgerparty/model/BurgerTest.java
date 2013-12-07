package com.agateau.burgerparty.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class BurgerTest {
	@Test
	public void testCompare() {
		MealItemDb db = TestUtils.createMealItemDb(
				"<items>"
				+ "  <generic>"
				+ "    <item row='0' column='0' name='top'    type='burger' subType='top' bottom='bottom' height='12'/>"
				+ "    <item row='0' column='1' name='bottom' type='burger' subType='bottom' height='12'/>"
				+ "    <item row='0' column='2' name='tomato' type='burger' height='12'/>"
				+ "    <item row='0' column='3' name='salad'  type='burger' height='12'/>"
				+ "  </generic>"
				+ "</items>");
		BurgerItem top = db.getBurgerItem("top");
		BurgerItem bottom = db.getBurgerItem("bottom");
		BurgerItem tomato = db.getBurgerItem("tomato");

		Burger user = new Burger();
		Burger reference = new Burger();

		reference.addItem(bottom);
		reference.addItem(tomato);
		assertEquals(user.compareTo(reference), Burger.CompareResult.SUBSET);

		user.addItem(bottom);
		assertEquals(user.compareTo(reference), Burger.CompareResult.SUBSET);

		user.addItem(tomato);
		assertEquals(user.compareTo(reference), Burger.CompareResult.SAME);

		user.clear();
		user.addItem(top);
		assertEquals(user.compareTo(reference), Burger.CompareResult.DIFFERENT);
	}
}
