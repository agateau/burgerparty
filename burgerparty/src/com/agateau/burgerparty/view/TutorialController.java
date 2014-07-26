package com.agateau.burgerparty.view;

import java.util.LinkedList;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.Burger;
import com.agateau.burgerparty.model.MealItem;
import com.agateau.burgerparty.model.World;
import com.agateau.burgerparty.utils.ConnectionManager;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class TutorialController {
    private ConnectionManager mMealConnections = new ConnectionManager();
    private World mWorld;
    private InventoryView mInventoryView;
    private Image mIndicator;
    private MoveToAction mMoveAction = null;

    private static final float MOVE_DURATION = 0.5f; 

    public TutorialController(BurgerPartyGame game, World world, InventoryView inventoryView) {
        mIndicator = new Image(game.getAssets().getTextureAtlas().findRegion("ui/icon-next-item"));
        mIndicator.setTouchable(Touchable.disabled);
        mWorld = world;
        mInventoryView = inventoryView;
        updateConnections();
    }

    public Actor getIndicator() {
        return mIndicator;
    }

    public void onNewMeal() {
        updateConnections();
        updateIndicator();
    }

    private void updateConnections() {
        mMealConnections.disconnectAll();
        mWorld.getBurger().itemAdded.connect(mMealConnections, new Signal1.Handler<MealItem>(){
            @Override
            public void handle(MealItem a1) {
                updateIndicator();
            }
        });
        mWorld.getBurger().trashed.connect(mMealConnections, new Signal0.Handler(){
            @Override
            public void handle() {
                updateIndicator();
            }
        });
        mWorld.getMealExtra().itemAdded.connect(mMealConnections, new Signal1.Handler<MealItem>(){
            @Override
            public void handle(MealItem a1) {
                updateIndicator();
            }
        });
        mWorld.getMealExtra().trashed.connect(mMealConnections, new Signal0.Handler(){
            @Override
            public void handle() {
                updateIndicator();
            }
        });
    }

    private void updateIndicator() {
        MealItem nextItem = findNextItem();
        if (nextItem == null) {
            mIndicator.setVisible(false);
            return;
        }

        mIndicator.setVisible(true);
        Vector2 pos = new Vector2();
        mInventoryView.getItemPosition(nextItem, pos);
        mInventoryView.localToAscendantCoordinates(mIndicator.getParent(), pos);

        if (mMoveAction != null) {
            mIndicator.removeAction(mMoveAction);
        }
        mMoveAction = Actions.moveTo(pos.x, pos.y, MOVE_DURATION, Interpolation.pow2);
        mIndicator.addAction(mMoveAction);
    }

    private MealItem findNextItem() {
        Burger burger = mWorld.getTargetBurger();
        int idx = burger.getArrowIndex();
        if (idx >= 0) {
            return burger.getItemAt(idx);
        }
        // No real order for meal extra, so we pick the first missing one
        LinkedList<MealItem> extra = mWorld.getMealExtra().getItems();
        LinkedList<MealItem> targetExtra = mWorld.getTargetMealExtra().getItems();
        for(MealItem item : targetExtra) {
            if (extra.indexOf(item) == -1) {
                return item;
            }
        }

        return null;
    }
}
