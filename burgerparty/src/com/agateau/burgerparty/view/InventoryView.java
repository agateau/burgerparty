package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.model.Inventory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class InventoryView extends Actor {
	private Inventory mInventory;
	private TextureDict mTextureDict;
	
	private float mWidth;
	
	private final int ColumnCount = 3;
	private final float Padding = 10;

	public InventoryView(Inventory inventory, TextureDict textureDict) {
		mWidth = 800 / 3;
		mInventory = inventory;
		mTextureDict = textureDict;
	}
	
	@Override
	public void draw(SpriteBatch spriteBatch, float parentAlpha) {
		Texture bgTexture = mTextureDict.getByName("shelf");

		float cellSize = mWidth / ColumnCount;

		float posX = Padding;
		float posY = 0;
		for(BurgerItem item: mInventory.getItems()) {
			Texture texture = mTextureDict.getByName(item.getName());

			float scale = (cellSize - Padding * 2) / Math.max(texture.getWidth(), texture.getHeight());
			float width = texture.getWidth() * scale;
			float height = texture.getHeight() * scale;

			spriteBatch.draw(bgTexture, posX - Padding, posY - Padding, cellSize, cellSize);
			spriteBatch.draw(texture, posX, posY + 4, width, height);
			
			posX += cellSize;
			if (posX > mWidth) {
				posX = Padding;
				posY += cellSize;
			}
		}
	}
	
	
	public int getIndexAt(float posX, float posY) {
		int cellSize = (int)(mWidth / ColumnCount);
		int x = (int)posX;
		int y = (int)posY;
		return (y / cellSize) * ColumnCount + x / cellSize;
	}
}
