package com.agateau.burgerparty.view;

import java.util.HashSet;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.model.Burger;
import com.agateau.burgerparty.model.Inventory;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.model.MealExtra;
import com.agateau.burgerparty.model.MealItem;
import com.agateau.burgerparty.screens.SandBoxGameScreen;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.Signal1;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class SandBoxGameView extends AbstractWorldView {
	public SandBoxGameView(SandBoxGameScreen sandBoxGameScreen, BurgerPartyGame game, LevelWorld world) {
		super(world);
		mGame = game;

		setupWidgets();
		setupInventory();
		setupMealView();
	}

	public void onBackPressed() {
	}

	private void setupWidgets() {
		ImageButton backButton = Kernel.createRoundButton("ui/icon-back");
		backButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mGame.showMenu();
			}
		});

		ImageButton switchInventoriesButton = Kernel.createRoundButton("ui/icon-back");
		switchInventoriesButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				switchInventories();
			}
		});

		addRule(backButton, Anchor.TOP_LEFT, this, Anchor.TOP_LEFT);
		addRule(switchInventoriesButton, Anchor.CENTER_LEFT, this, Anchor.CENTER_LEFT);
	}

	private void setupInventory() {
		Array<String> list = new Array<String>();
		// FIXME
		list.addAll(new String[]{"bottom", "steak", "tomato", "salad", "cheese", "top"});
		mBurgerInventory = new Inventory(list);

		list.clear();
		list.addAll(new String[]{"soda" ,"juice", "big-fries", "small-fries"});
		mMealExtraInventory = new Inventory(list);

		mInventoryView.setInventory(mBurgerInventory);

		mInventoryView.itemSelected.connect(mHandlers, new Signal1.Handler<MealItem>() {
			@Override
			public void handle(MealItem item) {
				mMealView.addItem(item);
			}
		});
	}

	private void setupMealView() {
		mBurger = new Burger();
		mMealExtra = new MealExtra();
		mMealView = new MealView(mBurger, mMealExtra, Kernel.getTextureAtlas(), true);

		addRule(mMealView, Anchor.BOTTOM_CENTER, mWorkbench, Anchor.BOTTOM_CENTER, 0, 0);
	}

	private void switchInventories() {
		if (mInventoryView.getInventory() == mBurgerInventory) {
			mInventoryView.setInventory(mMealExtraInventory);
		} else {
			mInventoryView.setInventory(mBurgerInventory);
		}
	}

	private HashSet<Object> mHandlers = new HashSet<Object>();

	private Inventory mBurgerInventory;
	private Inventory mMealExtraInventory;
	private Burger mBurger;
	private MealExtra mMealExtra;
	private MealView mMealView;
	private final BurgerPartyGame mGame;
}
