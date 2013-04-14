package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.MealItem;
import com.agateau.burgerparty.model.Inventory;
import com.agateau.burgerparty.utils.Signal1;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Array;

public class InventoryView extends Actor {
	public Signal1<MealItem> itemSelected = new Signal1<MealItem>();
	private TiledDrawable mBgDrawable;
	private Inventory mInventory;
	private TextureAtlas mAtlas;
	
	private static final int COLUMN_COUNT = 8;
	private static final int ROW_COUNT = 2;

	public InventoryView(Inventory inventory, String levelWorldDirName, TextureAtlas atlas) {
		mInventory = inventory;
		mAtlas = atlas;
		TextureRegion region = mAtlas.findRegion(levelWorldDirName + "shelf");
		mBgDrawable = new TiledDrawable(region);
		setHeight(region.getRegionHeight() * 2);

		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				onClicked(x, y);
			}
		});
	}

	public void setInventory(Inventory inventory) {
		mInventory = inventory;
	}

	@Override
	public void draw(SpriteBatch spriteBatch, float parentAlpha) {
		spriteBatch.setColor(1, 1, 1, parentAlpha);

		float cellWidth = getWidth() / COLUMN_COUNT;
		float cellHeight = mBgDrawable.getRegion().getRegionHeight();

		int index = 0;
		Array<MealItem> items = mInventory.getItems();
		mBgDrawable.draw(spriteBatch, 0, 0, getWidth(), getHeight());

		for (int row = 0; row < ROW_COUNT; ++row) {
			for (int col = 0; col < COLUMN_COUNT && index < items.size; ++col, ++index) {
				float posX = getX() + col * cellWidth;
				float posY = getY() + row * cellHeight;

				String baseName = "mealitems/" + items.get(index).getName();
				TextureRegion region = mAtlas.findRegion(baseName + "-inventory");
				if (region == null) {
					region = mAtlas.findRegion(baseName);
				}
				assert(region != null);

				spriteBatch.draw(region,
					MathUtils.ceil(posX + (cellWidth - region.getRegionWidth()) / 2),
					MathUtils.ceil(posY + (cellHeight - region.getRegionHeight()) / 2));
			}
		}
	}

	public int getIndexAt(float posX, float posY) {
		int cellSize = (int)(getWidth() / COLUMN_COUNT);
		int x = (int)posX;
		int y = (int)posY;
		return (y / cellSize) * COLUMN_COUNT + x / cellSize;
	}

	private void onClicked(float x, float y) {
		int index = getIndexAt(x, y);
		MealItem item = mInventory.get(index);
		if (item == null) {
			return;
		}
		itemSelected.emit(item);
	}
}
