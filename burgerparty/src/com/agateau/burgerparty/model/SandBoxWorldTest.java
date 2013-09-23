package com.agateau.burgerparty.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class SandBoxWorldTest {
	@Test
	public void testCanAddItem() {
		BurgerItem top = BurgerItem.addTestItem("top", BurgerItem.SubType.TOP);
		BurgerItem bottom = BurgerItem.addTestItem("bottom", BurgerItem.SubType.BOTTOM);
		BurgerItem tomato = BurgerItem.addTestItem("tomato");
		BurgerItem salad = BurgerItem.addTestItem("salad");

		MealItem juice = MealItem.addTestItem(MealItem.Type.DRINK, "juice");

		SandBoxWorld world = new SandBoxWorld();
		assertTrue(world.canAddItem(bottom));
		assertTrue(world.canAddItem(juice));
		assertFalse(world.canAddItem(top));
		assertFalse(world.canAddItem(tomato));
		
		world.getBurger().addItem(top);
		world.getBurger().addItem(salad);
		assertTrue(world.canAddItem(bottom));
		assertTrue(world.canAddItem(juice));
		assertTrue(world.canAddItem(top));
		assertTrue(world.canAddItem(tomato));
	}

}
