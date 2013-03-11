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

public class InventoryView extends Actor {
	private Inventory mInventory;
	private TextureAtlas mAtlas;
	
	private final int ColumnCount = 3;
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
		setBounds(0, 0, 800 / 3, 480);
		mInventory = inventory;
		mAtlas = atlas;
	
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
		TextureRegion bgTexture = mAtlas.findRegion("shelf");

		float cellSize = getWidth() / ColumnCount;

		float posX = Padding;
		float posY = 0;
		for(BurgerItem item: mInventory.getItems()) {
			TextureRegion texture = mAtlas.findRegion("burgeritems/" + item.getName());

			float scale = (cellSize - Padding * 2) / Math.max(texture.getRegionWidth(), texture.getRegionHeight());
			float width = texture.getRegionWidth() * scale;
			float height = texture.getRegionHeight() * scale;

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
