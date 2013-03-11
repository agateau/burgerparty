package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.model.World;

import com.agateau.burgerparty.view.InventoryView;
import com.agateau.burgerparty.view.TextureDict;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class WorldView extends WidgetGroup {
	private World mWorld;
	private TextureDict mTextureDict;
	private InventoryView mInventoryView;
	private BurgerStackView mBurgerStackView;
	private Image mTrashActor;

	public WorldView(World world) {
		setFillParent(true);
		mWorld = world;
		mTextureDict = new TextureDict();

		mInventoryView = new InventoryView(mWorld.getInventory(), mTextureDict);
		addActor(mInventoryView);
		mInventoryView.addListener(new EventListener() {
			public boolean handle(Event event) {
				if (!(event instanceof InventoryView.AddBurgerItemEvent)) {
					return false;
				}
				BurgerItem item = ((InventoryView.AddBurgerItemEvent)event).item;
				mWorld.getBurgerStack().addItem(item);
				return true;
			}
		});

		mBurgerStackView = new BurgerStackView(mWorld.getBurgerStack(), mTextureDict);
		addActor(mBurgerStackView);
		
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
		mTrashActor.setY(getHeight() - mTrashActor.getHeight());
	}
}