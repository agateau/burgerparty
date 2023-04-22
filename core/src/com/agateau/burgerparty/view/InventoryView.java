package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.Inventory;
import com.agateau.burgerparty.model.MealItem;
import com.agateau.burgerparty.utils.Signal1;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class InventoryView extends Group {
    public Signal1<MealItem> itemSelected = new Signal1<MealItem>();
    private TextureRegion mBgRegion;
    private Inventory mInventory = null;
    private TextureAtlas mAtlas;
    private Image mClickHighlight;

    private static final int COLUMN_COUNT = 8;
    private static final int ROW_COUNT = 2;
    private static final float HIGHLIGHT_MARGIN = 2;
    private static final float HIGHLIGHT_DURATION = 0.4f;
    private static final float HIGHLIGHT_ALPHA = 0.4f;
    public InventoryView(TextureAtlas atlas) {
        mAtlas = atlas;
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onClicked(x, y);
            }
        });
        mClickHighlight = new Image(atlas.findRegion("ui/white-pixel"));
        mClickHighlight.setColor(0, 0, 0, 0);
        addActor(mClickHighlight);
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

    public void getItemPosition(MealItem item, Vector2 pos) {
        final float cellWidth = getWidth() / COLUMN_COUNT;
        final float cellHeight = getHeight() / ROW_COUNT;
        pos.x = item.getColumn() * cellWidth + cellWidth / 2;
        pos.y = item.getRow() * cellHeight + cellHeight / 2;
    }

    @Override
    public void draw(Batch spriteBatch, float parentAlpha) {
        spriteBatch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);

        final float cellWidth = getWidth() / COLUMN_COUNT;
        final float cellHeight = getHeight() / ROW_COUNT;

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
        super.draw(spriteBatch, parentAlpha);
    }

    private void onClicked(float posX, float posY) {
        float cellWidth = getWidth() / COLUMN_COUNT;
        float cellHeight = getHeight() / ROW_COUNT;
        int column = (int)(posX / cellWidth);
        int row = (int)(posY / cellHeight);
        for (MealItem item: mInventory.getItems()) {
            if (item.getRow() == row && item.getColumn() == column) {
                showHighlight(column, row);
                itemSelected.emit(item);
            }
        }
    }

    private void showHighlight(int column, int row) {
        float cellWidth = getWidth() / COLUMN_COUNT;
        float cellHeight = getHeight() / ROW_COUNT;
        mClickHighlight.setBounds(
            column * cellWidth + HIGHLIGHT_MARGIN,
            row * cellHeight + HIGHLIGHT_MARGIN,
            cellWidth - 2 * HIGHLIGHT_MARGIN,
            cellHeight - 2 * HIGHLIGHT_MARGIN
        );
        mClickHighlight.addAction(
            Actions.sequence(
                Actions.alpha(HIGHLIGHT_ALPHA, HIGHLIGHT_DURATION * 0.25f),
                Actions.alpha(0, HIGHLIGHT_DURATION * 0.75f)
            )
        );
    }
}
