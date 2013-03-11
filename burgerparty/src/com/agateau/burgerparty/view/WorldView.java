package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.model.World;

import com.agateau.burgerparty.view.InventoryView;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class WorldView extends WidgetGroup {
	private World mWorld;
	private TextureAtlas mAtlas;
	private InventoryView mInventoryView;
	private BurgerStackView mBurgerStackView;
	private BurgerStackView mTargetBurgerStackView;
	private Image mTrashActor;
	private Label mTimerDisplay;
	private Actor mGameOverWindow;

	public WorldView(World world) {
		setFillParent(true);
		mWorld = world;
		mAtlas = new TextureAtlas(Gdx.files.internal("burgerparty.atlas"));

		setupInventoryView();
		setupTimerDisplay();

		mBurgerStackView = new BurgerStackView(mWorld.getBurgerStack(), mAtlas);
		addActor(mBurgerStackView);
		
		mTargetBurgerStackView = new BurgerStackView(mWorld.getTargetBurgerStack(), mAtlas);
		addActor(mTargetBurgerStackView);
		
		TextureRegion trash = mAtlas.findRegion("trash");
		mTrashActor = new Image(trash);
		mTrashActor.setX(0);
		mTrashActor.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				mWorld.getBurgerStack().clear();
			}
		});
		addActor(mTrashActor);
	}
	
	public InventoryView getInventoryView() {
		return mInventoryView;
	}

	@Override
	public void layout() {
		float width = getWidth();
		float height = getHeight();
		mTrashActor.setY(height - mTrashActor.getHeight());
		
		float inventoryWidth = width / 3;
		mInventoryView.setBounds(0, 0, inventoryWidth, height);
		
		float stackSize = width - inventoryWidth;
		mBurgerStackView.setBounds(inventoryWidth, 0, stackSize, height);
		
		float targetSize = width / 8;
		mTargetBurgerStackView.setBounds(width - targetSize, height - targetSize, targetSize, targetSize);

		mTimerDisplay.setBounds(0, height - mTimerDisplay.getPrefHeight(), width, mTimerDisplay.getPrefHeight());
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		updateTimerDisplay();
		if (mWorld.getRemainingSeconds() == 0 && mGameOverWindow == null) {
			showGameOverWindow();
		} else if (mWorld.getRemainingSeconds() > 0 && mGameOverWindow != null) {
			hideGameOverWindow();
		}
	}

	private void setupInventoryView() {
		mInventoryView = new InventoryView(mWorld.getInventory(), mAtlas);
		addActor(mInventoryView);
		mInventoryView.addListener(mInventoryView.new Listener() {
			@Override
			public void burgerItemClicked(BurgerItem item) {
				mWorld.getBurgerStack().addItem(item);
				mWorld.checkStackStatus();
			}
		});
	}

	private void setupTimerDisplay() {
		Label.LabelStyle style = new Label.LabelStyle();
		style.font = new BitmapFont();
		style.fontColor = Color.WHITE;
		mTimerDisplay = new Label("", style);
		mTimerDisplay.setAlignment(Align.center);
		addActor(mTimerDisplay);
	}

	private void updateTimerDisplay() {
		int seconds = mWorld.getRemainingSeconds();
		String txt = String.valueOf(seconds);
		mTimerDisplay.setText(txt);
	}

	private void showGameOverWindow() {
		mGameOverWindow = new GameOverWindow(mWorld);
		addActor(mGameOverWindow);
	}

	private void hideGameOverWindow() {
		mGameOverWindow.remove();
		mGameOverWindow = null;
	}
}