package com.agateau.burgerparty.model;

import static org.junit.Assert.*;

import org.junit.Test;

import com.badlogic.gdx.utils.Array;

public class SandBoxWorldTest {
	@Test
	public void testCanAddBurgerItem() {
		BurgerItem top = BurgerItem.addTestItem("top", BurgerItem.SubType.TOP);
		BurgerItem bottom = BurgerItem.addTestItem("bottom", BurgerItem.SubType.BOTTOM);
		BurgerItem tomato = BurgerItem.addTestItem("tomato");
		BurgerItem salad = BurgerItem.addTestItem("salad");

		SandBoxWorld world = new SandBoxWorld();
		Array<BurgerItem> burger = new Array<BurgerItem>();

		assertTrue(world.canAddBurgerItem(burger, bottom));
		assertFalse(world.canAddBurgerItem(burger, top));
		assertFalse(world.canAddBurgerItem(burger, tomato));

		burger.add(top);
		burger.add(salad);
		assertTrue(world.canAddBurgerItem(burger, bottom));
		assertTrue(world.canAddBurgerItem(burger, top));
		assertTrue(world.canAddBurgerItem(burger, tomato));
	}

	@Test
	public void testCanAddBurgerItem_MaxBurgerItems() {
		BurgerItem top = BurgerItem.addTestItem("top", BurgerItem.SubType.TOP);
		BurgerItem bottom = BurgerItem.addTestItem("bottom", BurgerItem.SubType.BOTTOM);
		BurgerItem tomato = BurgerItem.addTestItem("tomato");

		SandBoxWorld world = new SandBoxWorld();
		world.setMaxBurgerItems(3);

		Array<BurgerItem> burger = new Array<BurgerItem>();

		burger.add(bottom);
		assertTrue(world.canAddBurgerItem(burger, tomato));

		burger.add(tomato);

		// Only top should be allowed now
		assertTrue(world.canAddBurgerItem(burger, top));
		assertFalse(world.canAddBurgerItem(burger, tomato));

		burger.add(top);

		// Nothing should be allowed now
		assertFalse(world.canAddBurgerItem(burger, top));
		assertFalse(world.canAddBurgerItem(burger, tomato));
	}


	@Test
	public void testCanAddMealItem() {
		MealItem juice = MealItem.addTestItem(MealItem.Type.DRINK, "juice");
		MealItem fries = MealItem.addTestItem(MealItem.Type.SIDE_ORDER, "fries");
		SandBoxWorld world = new SandBoxWorld();
		Array<MealItem> extra = new Array<MealItem>();

		assertTrue(world.canAddMealExtraItem(extra, juice));
		extra.add(juice);

		// an extra can hold up to 2 items, so should still be good
		assertTrue(world.canAddMealExtraItem(extra, fries));
		extra.add(juice);

		// now it should be full
		assertFalse(world.canAddMealExtraItem(extra, fries));
	}
}
