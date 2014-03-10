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

public class InventoryView extends Actor {
    public Signal1<MealItem> itemSelected = new Signal1<MealItem>();
    private TextureRegion mBgRegion;
    private Inventory mInventory = null;
    private TextureAtlas mAtlas;

    private static final int COLUMN_COUNT = 8;
    private static final int ROW_COUNT = 2;

    public InventoryView(TextureAtlas atlas) {
        mAtlas = atlas;
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onClicked(x, y);
            }
        });
    }

    public void setWorldDirName(String levelWorldDirName) {
        mBgRegion = mAtlas.findRegion(levelWorldDirName + "shelf");
        setHeight(mBgRegion.getRegionHeight() * ROW_COUNT);
    }

    public Inventory getInventory() {
        return mInventory;
    }

    public void setInventory(Inventory inventory) {
        mInventory = inventory;
    }

    @Override
    public void draw(SpriteBatch spriteBatch, float parentAlpha) {
        spriteBatch.setColor(1, 1, 1, parentAlpha);

        float cellWidth = getWidth() / COLUMN_COUNT;
        float cellHeight = getHeight() / ROW_COUNT;

        for (int row = 0; row < ROW_COUNT; ++row) {
            for (int col = 0; col < COLUMN_COUNT; ++col) {
                spriteBatch.draw(mBgRegion, col * cellWidth, row * cellHeight, cellWidth, cellHeight);
            }
        }

        for (MealItem item: mInventory.getItems()) {
            float posX = getX() + item.getColumn() * cellWidth;
            float posY = getY() + item.getRow() * cellHeight;

            String baseName = "mealitems/" + item.getPath();
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

    private void onClicked(float posX, float posY) {
        float cellWidth = getWidth() / COLUMN_COUNT;
        float cellHeight = getHeight() / ROW_COUNT;
        int column = (int)(posX / cellWidth);
        int row = (int)(posY / cellHeight);
        for (MealItem item: mInventory.getItems()) {
            if (item.getRow() == row && item.getColumn() == column) {
                itemSelected.emit(item);
            }
        }
    }
}
