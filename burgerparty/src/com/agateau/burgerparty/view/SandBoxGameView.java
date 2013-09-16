package com.agateau.burgerparty.view;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.Inventory;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.screens.SandBoxGameScreen;
import com.badlogic.gdx.utils.Array;

public class SandBoxGameView extends AbstractWorldView {
	public SandBoxGameView(SandBoxGameScreen sandBoxGameScreen, BurgerPartyGame game, LevelWorld world) {
		super(world);
		Array<String> list = new Array<String>();
		// FIXME
		list.addAll(new String[]{"bottom", "steak", "tomato", "salad", "cheese", "top"});
		mInventory = new Inventory(list);
		mInventoryView.setInventory(mInventory);
	}

	public void onBackPressed() {
	}

	private Inventory mInventory;
}
