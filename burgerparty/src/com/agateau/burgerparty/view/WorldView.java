package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.World;

import com.agateau.burgerparty.view.InventoryView;
import com.agateau.burgerparty.view.TextureDict;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	private Image mTrashActor;

	public WorldView(World world) {
		setFillParent(true);
		mWorld = world;
		mTextureDict = new TextureDict();
		mInventoryView = new InventoryView(mWorld.getInventory(), mTextureDict);

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
	
	public void render() {
		SpriteBatch batch = getStage().getSpriteBatch();
		batch.begin();
		mInventoryView.drawSprites(batch);
		batch.end();
	}
	
	public InventoryView getInventoryView() {
		return mInventoryView;
	}
	
	public void layout() {
		mTrashActor.setY(getHeight() - mTrashActor.getHeight());
	}
}