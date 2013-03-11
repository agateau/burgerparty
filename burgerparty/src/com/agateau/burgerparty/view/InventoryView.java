package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.model.Inventory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class InventoryView extends Actor {
	private Inventory mInventory;
	private TextureDict mTextureDict;
	
	private final int ColumnCount = 3;
	private final float Padding = 10;

	class AddBurgerItemEvent extends Event {
		public BurgerItem item;
		public AddBurgerItemEvent(BurgerItem item) {
			this.item = item;
		}
	}

	public InventoryView(Inventory inventory, TextureDict textureDict) {
		setBounds(0, 0, 800 / 3, 480);
		mInventory = inventory;
		mTextureDict = textureDict;
	
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				InventoryView view = (InventoryView)event.getTarget();
				int index = view.getIndexAt(x, y);
				BurgerItem item = view.mInventory.get(index);
				if (item == null) {
					return;
				}
				view.fire(new AddBurgerItemEvent(item));
			}
		});
	}
	
	@Override
	public void draw(SpriteBatch spriteBatch, float parentAlpha) {
		Texture bgTexture = mTextureDict.getByName("shelf");

		float cellSize = getWidth() / ColumnCount;

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
			if (posX > getWidth()) {
				posX = Padding;
				posY += cellSize;
			}
		}
	}
	
	public int getIndexAt(float posX, float posY) {
		int cellSize = (int)(getWidth() / ColumnCount);
		int x = (int)posX;
		int y = (int)posY;
		return (y / cellSize) * ColumnCount + x / cellSize;
	}
}
