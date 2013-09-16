package com.agateau.burgerparty.view;

import java.util.HashSet;
import java.util.Stack;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.model.MealItem;
import com.agateau.burgerparty.model.SandBoxWorld;
import com.agateau.burgerparty.screens.SandBoxGameScreen;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.Signal1;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class SandBoxGameView extends AbstractWorldView {
	public SandBoxGameView(SandBoxGameScreen sandBoxGameScreen, BurgerPartyGame game, LevelWorld world) {
		super(world);
		mGame = game;

		setupWidgets();
		setupInventory();
		setupMealView();
	}

	public void onBackPressed() {
		mGame.showMenu();
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

		ImageButton deliverButton = Kernel.createRoundButton("ui/icon-right");
		deliverButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				deliver();
			}
		});

		ImageButton undoButton = Kernel.createRoundButton("ui/icon-right");
		undoButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				undo();
			}
		});

		addRule(backButton, Anchor.TOP_LEFT, this, Anchor.TOP_LEFT);
		addRule(switchInventoriesButton, Anchor.CENTER_LEFT, this, Anchor.CENTER_LEFT);
		addRule(undoButton, Anchor.BOTTOM_LEFT, switchInventoriesButton, Anchor.TOP_LEFT, 0, 1);
		addRule(deliverButton, Anchor.CENTER_RIGHT, this, Anchor.CENTER_RIGHT);
	}

	private void setupInventory() {
		for (String name: mGame.getKnownItems()) {
			MealItem item = MealItem.get(name);
			if (item.getType() == MealItem.Type.BURGER) {
				mWorld.getBurgerInventory().addItem(name);
			} else {
				mWorld.getMealExtraInventory().addItem(name);
			}
		}

		mInventoryView.setInventory(mWorld.getBurgerInventory());

		mInventoryView.itemSelected.connect(mHandlers, new Signal1.Handler<MealItem>() {
			@Override
			public void handle(MealItem item) {
				mMealView.addItem(item);
				mUndoStack.push(item);
			}
		});
	}

	private void setupMealView() {
		mWorld.getBurger().clear();
		mWorld.getMealExtra().clear();
		mUndoStack.clear();
		mMealView = new MealView(mWorld.getBurger(), mWorld.getMealExtra(), Kernel.getTextureAtlas(), true);

		addRule(mMealView, Anchor.BOTTOM_CENTER, mWorkbench, Anchor.BOTTOM_CENTER, 0, 0);
	}

	private void switchInventories() {
		if (mInventoryView.getInventory() == mWorld.getBurgerInventory()) {
			mInventoryView.setInventory(mWorld.getMealExtraInventory());
		} else {
			mInventoryView.setInventory(mWorld.getBurgerInventory());
		}
	}

	private void deliver() {
		removeRulesForActor(mMealView);
		mMealView.addAction(
			Actions.sequence(
				Actions.moveTo(getWidth(), mMealView.getY(), 0.4f, Interpolation.pow2In),
				Actions.run(new Runnable() {
					@Override
					public void run() {
						setupMealView();
					}
				}),
				Actions.removeActor()
			)
		);
	}

	private void undo() {
		if (mUndoStack.isEmpty()) {
			return;
		}
		MealItem item = mUndoStack.pop();
		mMealView.pop(item.getType());
	}

	private HashSet<Object> mHandlers = new HashSet<Object>();

	private SandBoxWorld mWorld = new SandBoxWorld();
	private Stack<MealItem> mUndoStack = new Stack<MealItem>();
	private MealView mMealView;
	private final BurgerPartyGame mGame;
}
