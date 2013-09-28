package com.agateau.burgerparty.view;

import java.util.HashSet;
import java.util.Stack;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.model.Inventory;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.model.MealItem;
import com.agateau.burgerparty.model.SandBoxWorld;
import com.agateau.burgerparty.screens.BurgerPartyScreen;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.Signal1;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

public class SandBoxGameView extends AbstractWorldView {
	public SandBoxGameView(BurgerPartyScreen screen, BurgerPartyGame game) {
		super(game.getLevelWorld(0).getDirName());
		mScreen = screen;
		mLevelWorldIndex = 0;
		mGame = game;

		setupWidgets();
		setupBottomLeftBar();
		setupInventory();
		setLevelWorldIndex(0);
		Gdx.app.postRunnable(new Runnable() {
			@Override
			public void run() {
				setupMealView();
			}
		});
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

		ImageButton worldButton = Kernel.createRoundButton("ui/icon-levels");
		worldButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				switchWorld();
			}
		});

		ImageButton deliverButton = Kernel.createRoundButton("ui/icon-right");
		deliverButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				deliver();
			}
		});

		addRule(backButton, Anchor.TOP_LEFT, this, Anchor.TOP_LEFT);
		addRule(worldButton, Anchor.CENTER_LEFT, backButton, Anchor.CENTER_RIGHT, 1, 0);
		addRule(deliverButton, Anchor.CENTER_RIGHT, this, Anchor.CENTER_RIGHT);
	}

	private void setupBottomLeftBar() {
		mSwitchInventoriesButton = Kernel.createHudButton("ui/inventory-burger");
		mSwitchInventoriesButton.setSize(48, 46);
		mSwitchInventoriesButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				switchInventories();
			}
		});

		ImageButton undoButton = Kernel.createHudButton("ui/undo");
		undoButton.setSize(48, 46);
		undoButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				undo();
			}
		});

		mBottomLeftBgImage.setFillParent(true);
		mBottomLeftBar.addActor(mBottomLeftBgImage);

		mBottomLeftBar.addRule(mSwitchInventoriesButton, Anchor.BOTTOM_LEFT, mBottomLeftBar, Anchor.BOTTOM_LEFT, 1, 1);
		mBottomLeftBar.addRule(undoButton, Anchor.BOTTOM_LEFT, mSwitchInventoriesButton, Anchor.TOP_LEFT, 1, 3);

		addRule(mBottomLeftBar, Anchor.BOTTOM_LEFT, mInventoryView, Anchor.TOP_LEFT);
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
				onAddItem(item);
			}
		});
	}

	private void setupMealView() {
		mWorld.getBurger().clear();
		mWorld.getMealExtra().clear();
		mUndoStack.clear();
		mMealView = new MealView(mWorld.getBurger(), mWorld.getMealExtra(), Kernel.getTextureAtlas(), true);
		slideInMealView(mMealView);
	}

	private void switchInventories() {
		Inventory inventory;
		String iconName;
		if (mInventoryView.getInventory() == mWorld.getBurgerInventory()) {
			inventory = mWorld.getMealExtraInventory();
			iconName = "ui/inventory-extra";
			scrollTo(0);
		} else {
			inventory = mWorld.getBurgerInventory();
			iconName = "ui/inventory-burger";
			scrollToBurgerTop();
		}
		mInventoryView.setInventory(inventory);
		Drawable drawable = Kernel.getSkin().getDrawable(iconName);
		mSwitchInventoriesButton.getStyle().imageUp = drawable;
	}

	private void deliver() {
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
		if (item.getType() == MealItem.Type.BURGER) {
			scrollToBurgerTop();
		}
	}

	private void switchWorld() {
		Array<LevelWorld> worlds = mGame.getLevelWorlds();
		WorldListOverlay overlay = new WorldListOverlay(mScreen, worlds, mLevelWorldIndex);
		overlay.currentIndexChanged.connect(mHandlers, new Signal1.Handler<Integer>() {
			@Override
			public void handle(Integer index) {
				setLevelWorldIndex(index);
			}
		});
		mScreen.setOverlay(overlay);
	}

	private void setLevelWorldIndex(int index) {
		mLevelWorldIndex = index;
		LevelWorld levelWorld = mGame.getLevelWorld(index);
		String dirName = levelWorld.getDirName();
		setWorldDirName(dirName);

		TextureRegion region = Kernel.getTextureAtlas().findRegion(dirName + "bottom-left-button-bar");
		mBottomLeftBgImage.setDrawable(new TextureRegionDrawable(region));
		mBottomLeftBar.setSize(region.getRegionWidth(), region.getRegionHeight());
	}

	private void onAddItem(MealItem item) {
		if (item.getType() == MealItem.Type.BURGER) {
			Array<BurgerItem> items = mMealView.getBurgerView().getItems();
			if (!mWorld.canAddBurgerItem(items, (BurgerItem)item)) {
				playError();
				return;
			}
		} else {
			Array<MealItem> items = mMealView.getMealExtraView().getItems();
			if (!mWorld.canAddMealExtraItem(items, item)) {
				playError();
				return;
			}
		}
		mMealView.addItem(item);
		if (item.getType() == MealItem.Type.BURGER) {
			scrollToBurgerTop();
		} else {
			scrollTo(0);
		}
		mUndoStack.push(item);
	}

	private void scrollToBurgerTop() {
		float viewTop = getActorTop(mMealView.getBurgerView());
		float offset = Math.max(0, getScrollOffset() + viewTop - getHeight());
		scrollTo(offset);
	}

	private void playError() {
		Kernel.getSoundAtlas().findSound("error").play();
	}

	private HashSet<Object> mHandlers = new HashSet<Object>();

	private int mLevelWorldIndex;
	private SandBoxWorld mWorld = new SandBoxWorld();
	private Stack<MealItem> mUndoStack = new Stack<MealItem>();
	private MealView mMealView;
	private final BurgerPartyGame mGame;
	private final BurgerPartyScreen mScreen;

	Image mBottomLeftBgImage = new Image();
	AnchorGroup mBottomLeftBar = new AnchorGroup();
	ImageButton mSwitchInventoriesButton;
}
