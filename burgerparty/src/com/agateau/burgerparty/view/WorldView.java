package com.agateau.burgerparty.view;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.model.World;

import com.agateau.burgerparty.view.InventoryView;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class WorldView extends WidgetGroup {
	private BurgerPartyGame mGame;
	private World mWorld;
	private TextureAtlas mAtlas;
	private Skin mSkin;
	private InventoryView mInventoryView;
	private BurgerStackView mBurgerStackView;
	private BurgerStackView mTargetBurgerStackView;
	private Image mTrashActor;
	private Label mTimerDisplay;
	private Label mScoreLabel;
	private Actor mGameOverWindow;

	public WorldView(BurgerPartyGame game, World world, TextureAtlas atlas, Skin skin) {
		setFillParent(true);
		mGame = game;
		mWorld = world;
		mAtlas = atlas;
		mSkin = skin;

		setupInventoryView();
		setupTimerDisplay();
		setupScoreLabel();
		setupTrash();

		mBurgerStackView = new BurgerStackView(mWorld.getBurgerStack(), mAtlas);
		addActor(mBurgerStackView);
		
		mTargetBurgerStackView = new BurgerStackView(mWorld.getTargetBurgerStack(), mAtlas);
		addActor(mTargetBurgerStackView);
	}
	
	public InventoryView getInventoryView() {
		return mInventoryView;
	}

	@Override
	public void layout() {
		float width = getWidth();
		float height = getHeight();

		float inventoryWidth = width / 3;
		mInventoryView.setBounds(0, 0, inventoryWidth, height);

		float trashWidth = mTrashActor.getWidth();
		mTrashActor.setPosition(width - trashWidth, 0);

		float stackSize = width - inventoryWidth - trashWidth;
		mBurgerStackView.setScale(Math.min(stackSize / mBurgerStackView.getWidth(), 1));
		mBurgerStackView.setPosition(inventoryWidth + (stackSize - mBurgerStackView.getWidth() * mBurgerStackView.getScaleX()) / 2, 0);

		float targetSize = width / 6;
		mTargetBurgerStackView.setScale(Math.min(targetSize / mTargetBurgerStackView.getWidth(), 1));
		mTargetBurgerStackView.setPosition(width - targetSize, height - targetSize);

		mTimerDisplay.setBounds(0, height - mTimerDisplay.getPrefHeight(), width, mTimerDisplay.getPrefHeight());

		mScoreLabel.setBounds(0, height - mScoreLabel.getPrefHeight(), mScoreLabel.getPrefWidth(), mScoreLabel.getPrefHeight());
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		updateTimerDisplay();
		updateScoreLabel();
		if (mWorld.getRemainingSeconds() == 0 && mGameOverWindow == null) {
			showGameOverWindow();
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
		mTimerDisplay = new Label("", mSkin);
		mTimerDisplay.setAlignment(Align.center);
		addActor(mTimerDisplay);
	}

	private void setupScoreLabel() {
		mScoreLabel = new Label("", mSkin);
		mScoreLabel.setAlignment(Align.left);
		addActor(mScoreLabel);
	}

	private void setupTrash() {
		TextureRegion trash = mAtlas.findRegion("trash");
		mTrashActor = new Image(trash);
		mTrashActor.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				mWorld.getBurgerStack().clear();
			}
		});
		addActor(mTrashActor);
	}

	private void updateTimerDisplay() {
		int seconds = mWorld.getRemainingSeconds();
		String txt = String.valueOf(seconds);
		mTimerDisplay.setText(txt);
	}

	private void updateScoreLabel() {
		String txt = String.format("SCORE: %07d", mWorld.getScore());
		mScoreLabel.setText(txt);
	}

	private void showGameOverWindow() {
		mGameOverWindow = new GameOverWindow(mGame, mSkin);
		addActor(mGameOverWindow);
	}
}