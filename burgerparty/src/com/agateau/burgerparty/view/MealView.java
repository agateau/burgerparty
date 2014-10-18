package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.Burger;
import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.model.MealExtra;
import com.agateau.burgerparty.model.MealItem;
import com.agateau.burgerparty.utils.AnimScriptLoader;
import com.agateau.burgerparty.utils.ResizeToFitChildren;
import com.agateau.burgerparty.utils.SoundAtlas;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class MealView extends Group implements ResizeToFitChildren {
    public static final float ADD_ACTION_DURATION = 0.2f;
    public static final float TRASH_ACTION_DURATION = 0.5f;

    private static final float PLATTER_BURGER_X = 70f;
    private static final float PLATTER_MEAL_Y = 15f;
    public static final float MEAL_ITEM_PADDING = 15f;

    private Image mPlatter = null;
    private BurgerView mBurgerView;
    private MealExtraView mMealExtraView;
    private final Config mConfig;

    public enum Config {
        WITH_PLATTER,
        WITH_ARROW,
        WITHOUT_ARROW
    }

    public MealView(Burger burger, MealExtra mealExtra, TextureAtlas atlas, SoundAtlas soundAtlas, AnimScriptLoader loader, Config config) {
        mConfig = config;
        if (mConfig == Config.WITH_PLATTER) {
            mPlatter = new Image(atlas.findRegion("platter"));
            addActor(mPlatter);
        }
        mMealExtraView = new MealExtraView(mealExtra, atlas, loader);
        addActor(mMealExtraView);
        mBurgerView = new BurgerView(burger, atlas, soundAtlas, loader);
        addActor(mBurgerView);

        if (mConfig == Config.WITH_PLATTER) {
            mBurgerView.setPosition(PLATTER_BURGER_X, PLATTER_MEAL_Y);
        } else if (mConfig == Config.WITH_ARROW) {
            mBurgerView.setPosition(NextBurgerItemArrow.OVERALL_WIDTH, 0);
            NextBurgerItemArrow arrow = new NextBurgerItemArrow(getBurgerView(), atlas);
            addActor(arrow);
        }
        updateGeometry();
    }

    public BurgerView getBurgerView() {
        return mBurgerView;
    }

    public MealExtraView getMealExtraView() {
        return mMealExtraView;
    }

    public void addItem(MealItem item) {
        if (item.getType() == MealItem.Type.BURGER) {
            addBurgerItem((BurgerItem)item);
        } else {
            addExtraItem(item);
        }
    }

    private void addBurgerItem(BurgerItem item) {
        mBurgerView.addItem(item);
    }

    private void addExtraItem(MealItem item) {
        mMealExtraView.addItem(item);
    }

    public void pop(MealItem.Type itemType) {
        if (itemType == MealItem.Type.BURGER) {
            mBurgerView.pop();
        } else {
            mMealExtraView.pop();
        }
    }

    public void updateGeometry() {
        mMealExtraView.setPosition(mBurgerView.getRight() + MEAL_ITEM_PADDING, mBurgerView.getY());
        setSize(
            (mConfig == Config.WITH_PLATTER) ? mPlatter.getWidth() : mMealExtraView.getRight(),
            Math.max(mBurgerView.getHeight(), mMealExtraView.getHeight())
        );
        UiUtils.notifyResizeToFitParent(this);
    }

    @Override
    public void onChildSizeChanged() {
        updateGeometry();
    }

    public static void addTrashActions(Actor actor) {
        float xOffset = (float)(Math.random() * 200 - 100);
        float rotation = xOffset;
        actor.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.moveBy(xOffset, 0, TRASH_ACTION_DURATION),
                    Actions.moveBy(0, -200, TRASH_ACTION_DURATION, Interpolation.pow2In),
                    Actions.scaleTo(0.5f, 0.5f, TRASH_ACTION_DURATION),
                    Actions.rotateBy(rotation, TRASH_ACTION_DURATION),
                    Actions.fadeOut(TRASH_ACTION_DURATION, Interpolation.pow5In)
                ),
                Actions.removeActor()
            )
        );
    }
}
