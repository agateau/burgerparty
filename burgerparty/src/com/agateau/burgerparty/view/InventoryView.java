package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.model.Inventory;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class InventoryView extends Actor {
	private TextureRegion mBgRegion;
	private Inventory mInventory;
	private TextureAtlas mAtlas;
	
	private final int ColumnCount = 8;
	private final int RowCount = 2;
	private final float Padding = 10;

	class AddBurgerItemEvent extends Event {
		public BurgerItem item;
		public AddBurgerItemEvent(BurgerItem item) {
			this.item = item;
		}
	}
	
	public class Listener implements EventListener {
		public boolean handle(Event event) {
			if (!(event instanceof InventoryView.AddBurgerItemEvent)) {
				return false;
			}
			BurgerItem item = ((InventoryView.AddBurgerItemEvent)event).item;
			burgerItemClicked(item);
			return false;
		}
		
		public void burgerItemClicked(BurgerItem item) {
		}
	}

	public InventoryView(Inventory inventory, TextureAtlas atlas) {
		mInventory = inventory;
		mAtlas = atlas;
		mBgRegion = mAtlas.findRegion("shelf");
		setHeight(mBgRegion.getRegionHeight() * 2);
	
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
		spriteBatch.setColor(1, 1, 1, parentAlpha);

		float cellWidth = getWidth() / ColumnCount;
		float cellHeight = mBgRegion.getRegionHeight();

		int index = 0;
		Array<BurgerItem> items = mInventory.getItems();
		for (int row = 0; row < RowCount; ++row) {
			for (int col = 0; col < ColumnCount; ++col, ++index) {
				float posX = getX() + col * cellWidth;
				float posY = getY() + row * cellHeight;

				spriteBatch.draw(mBgRegion, posX, posY, cellWidth, cellHeight);
				if (index >= items.size) {
					continue;
				}
				TextureRegion region = mAtlas.findRegion("burgeritems/" + items.get(index).getName());
				float scale = (cellWidth - Padding * 2) / Math.max(region.getRegionWidth(), region.getRegionHeight());
				float width = region.getRegionWidth() * scale;
				float height = region.getRegionHeight() * scale;

				spriteBatch.draw(region, posX + Padding, posY + Padding * 2, width, height);
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
