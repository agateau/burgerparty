package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.model.World;

import com.agateau.burgerparty.view.InventoryView;
import com.agateau.burgerparty.view.TextureDict;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class WorldView extends WidgetGroup {
	private World mWorld;
	private TextureDict mTextureDict;
	private InventoryView mInventoryView;
	private BurgerStackView mBurgerStackView;
	private BurgerStackView mTargetBurgerStackView;
	private Image mTrashActor;

	public WorldView(World world) {
		setFillParent(true);
		mWorld = world;
		mTextureDict = new TextureDict();

		setupInventoryView();

		mBurgerStackView = new BurgerStackView(mWorld.getBurgerStack(), mTextureDict);
		addActor(mBurgerStackView);
		
		mTargetBurgerStackView = new BurgerStackView(mWorld.getTargetBurgerStack(), mTextureDict);
		addActor(mTargetBurgerStackView);
		
		Texture trash = mTextureDict.getByName("trash");
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
	}
	
	private void setupInventoryView() {
		mInventoryView = new InventoryView(mWorld.getInventory(), mTextureDict);
		addActor(mInventoryView);
		mInventoryView.addListener(mInventoryView.new Listener() {
			@Override
			public void burgerItemClicked(BurgerItem item) {
				mWorld.getBurgerStack().addItem(item);
				mWorld.checkStackStatus();
			}
		});
	}
}