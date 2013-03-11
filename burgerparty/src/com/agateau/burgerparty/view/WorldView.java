package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.World;

import com.agateau.burgerparty.view.InventoryView;
import com.agateau.burgerparty.view.TextureDict;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;

public class WorldView {
	private World mWorld;
	private TextureDict mTextureDict;
	private InventoryView mInventoryView;
	private BurgerStackView mBurgerStackView;
	private OrthographicCamera mCam;
	private Sprite mTrashSprite;
	private SpriteBatch mSpriteBatch;	

	public WorldView(World world) {
		mWorld = world;
		mTextureDict = new TextureDict();
		mInventoryView = new InventoryView(mWorld.getInventory(), mTextureDict);
		mBurgerStackView = new BurgerStackView(mWorld.getBurgerStack(), mTextureDict);

		mCam = new OrthographicCamera();
		mCam.setToOrtho(false, 800, 480);
		mSpriteBatch = new SpriteBatch();
		
		Texture trash = mTextureDict.getByName("trash");
		mTrashSprite = new Sprite(trash);
		mTrashSprite.setY(480 - trash.getHeight());
	}

	public void render() {
		mCam.update();
		mSpriteBatch.setProjectionMatrix(mCam.combined);
		mSpriteBatch.begin();
		mInventoryView.drawSprites(mSpriteBatch);
		mBurgerStackView.drawSprites(mSpriteBatch);
		mTrashSprite.draw(mSpriteBatch);
		mSpriteBatch.end();
	}
	
	public InventoryView getInventoryView() {
		return mInventoryView;
	}
	
	public Sprite getTrashSprite() {
		return mTrashSprite;
	}
}