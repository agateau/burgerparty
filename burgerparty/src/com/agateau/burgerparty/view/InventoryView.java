package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.MealItem;
import com.agateau.burgerparty.model.Inventory;
import com.agateau.burgerparty.utils.Signal1;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class InventoryView extends Actor {
	public Signal1<MealItem> itemSelected = new Signal1<MealItem>();
	private TextureRegion mBgRegion;
	private Inventory mInventory;
	private TextureAtlas mAtlas;
	
	private static final int COLUMN_COUNT = 8;
	private static final int ROW_COUNT = 2;
	private static final float PADDING = 10;

	public InventoryView(Inventory inventory, TextureAtlas atlas) {
		mInventory = inventory;
		mAtlas = atlas;
		mBgRegion = mAtlas.findRegion("shelf");
		setHeight(mBgRegion.getRegionHeight() * 2);
	
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				int index = getIndexAt(x, y);
				MealItem item = mInventory.get(index);
				if (item == null) {
					return;
				}
				itemSelected.emit(item);
			}
		});
	}

	@Override
	public void draw(SpriteBatch spriteBatch, float parentAlpha) {
		spriteBatch.setColor(1, 1, 1, parentAlpha);

		float cellWidth = getWidth() / COLUMN_COUNT;
		float cellHeight = mBgRegion.getRegionHeight();

		int index = 0;
		Array<MealItem> items = mInventory.getItems();
		for (int row = 0; row < ROW_COUNT; ++row) {
			for (int col = 0; col < COLUMN_COUNT; ++col, ++index) {
				float posX = getX() + col * cellWidth;
				float posY = getY() + row * cellHeight;

				spriteBatch.draw(mBgRegion, posX, posY, cellWidth, cellHeight);
				if (index >= items.size) {
					// No more item to draw, continue drawing empty cells 
					continue;
				}
				String baseName = "burgeritems/" + items.get(index).getName();
				TextureRegion region = mAtlas.findRegion(baseName + "-inventory");
				if (region == null) {
					region = mAtlas.findRegion(baseName);
					assert(region != null);
				}
				float scale = (cellWidth - PADDING * 2) / Math.max(region.getRegionWidth(), region.getRegionHeight());
				float width = region.getRegionWidth() * scale;
				float height = region.getRegionHeight() * scale;

				spriteBatch.draw(region, posX + (cellWidth - width) / 2, posY + (cellHeight - height) / 2, width, height);
			}
		}
	}

	public int getIndexAt(float posX, float posY) {
		int cellSize = (int)(getWidth() / COLUMN_COUNT);
		int x = (int)posX;
		int y = (int)posY;
		return (y / cellSize) * COLUMN_COUNT + x / cellSize;
	}
}
