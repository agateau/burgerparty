package com.agateau.burgerparty.model;

import static org.junit.Assert.*;

import org.junit.Test;

import com.badlogic.gdx.utils.Array;

public class SandBoxWorldTest {
	@Test
	public void testCanAddBurgerItem() {
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
		BurgerItem salad = db.getBurgerItem("salad");

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
		MealItemDb db = TestUtils.createMealItemDb(
				"<items>"
				+ "  <generic>"
				+ "    <item row='0' column='0' name='top'    type='burger' subType='top' bottom='bottom' height='12'/>"
				+ "    <item row='0' column='1' name='bottom' type='burger' subType='bottom' height='12'/>"
				+ "    <item row='0' column='2' name='tomato' type='burger' height='12'/>"
				+ "  </generic>"
				+ "</items>");
		BurgerItem top = db.getBurgerItem("top");
		BurgerItem bottom = db.getBurgerItem("bottom");
		BurgerItem tomato = db.getBurgerItem("tomato");

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
		MealItemDb db = TestUtils.createMealItemDb(
				"<items>"
				+ "  <generic>"
				+ "    <item row='0' column='0' name='juice' type='drink'/>"
				+ "    <item row='0' column='1' name='fries' type='side-order'/>"
				+ "  </generic>"
				+ "</items>");
		MealItem juice = db.get(-1, "juice");
		MealItem fries = db.get(-1, "fries");
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
