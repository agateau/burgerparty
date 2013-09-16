package com.agateau.burgerparty.view;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.model.Inventory;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.screens.SandBoxGameScreen;
import com.agateau.burgerparty.utils.Anchor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class SandBoxGameView extends AbstractWorldView {
	public SandBoxGameView(SandBoxGameScreen sandBoxGameScreen, BurgerPartyGame game, LevelWorld world) {
		super(world);
		mGame = game;

		setupWidgets();

		Array<String> list = new Array<String>();
		// FIXME
		list.addAll(new String[]{"bottom", "steak", "tomato", "salad", "cheese", "top"});
		mInventory = new Inventory(list);
		mInventoryView.setInventory(mInventory);
	}

	public void onBackPressed() {
	}

	private void setupWidgets() {
		ImageButton backButton = Kernel.createRoundButton("ui/icon-back");
		addRule(backButton, Anchor.TOP_LEFT, this, Anchor.TOP_LEFT);
		backButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mGame.showMenu();
			}
		});
	}

	private Inventory mInventory;
	private final BurgerPartyGame mGame;
}
