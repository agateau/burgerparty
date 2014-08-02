package com.agateau.burgerparty.view;

import java.util.HashSet;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.Burger;
import com.agateau.burgerparty.model.MealItem;
import com.agateau.burgerparty.utils.NLog;
import com.agateau.burgerparty.utils.Signal1;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class TutorialController2 {
    private final HashSet<Object> mHandlers = new HashSet<Object>();
    private InventoryView mInventoryView;
    private Group mGroup;
    private Image mIndicator;
    private Image mPulsingCircle;
    private Action mMoveAction = null;
    private MealView mMealView;
    private Burger mTargetBurger;

    private static final float MOVE_DURATION = 0.5f;

    private static final float PULSING_SCALE_MIN = 0.8f;
    private static final float PULSING_DURATION = 0.6f;

    public TutorialController2(BurgerPartyGame game, MealView mealView, Burger targetBurger, InventoryView inventoryView) {
        mMealView = mealView;
        mTargetBurger = targetBurger;
        mInventoryView = inventoryView;

        mGroup = new Group();
        mGroup.setTouchable(Touchable.disabled);
        mIndicator = new Image(game.getAssets().getTextureAtlas().findRegion("ui/finger"));

        mPulsingCircle = new Image(game.getAssets().getTextureAtlas().findRegion("ui/finger-circle"));
        mPulsingCircle.setOrigin(mPulsingCircle.getWidth() / 2, mPulsingCircle.getHeight() / 2);

        mGroup.addActor(mPulsingCircle);
        mGroup.addActor(mIndicator);
        mPulsingCircle.setPosition(-13, mIndicator.getHeight() - 28);
        updateConnections();
    }

    public Actor getIndicator() {
        return mGroup;
    }

    private void updateConnections() {
        mMealView.getBurgerView().getBurger().itemAdded.connect(mHandlers, new Signal1.Handler<MealItem>(){
            @Override
            public void handle(MealItem a1) {
                NLog.i("Calling updateIndicator");
                updateIndicator();
            }
        });
    }

    public void updateIndicator() {
        MealItem nextItem = findNextItem();
        NLog.i("nextItem=%s", nextItem);
        if (nextItem == null) {
            mGroup.setVisible(false);
            return;
        }

        mGroup.setVisible(true);
        Vector2 pos = new Vector2();
        mInventoryView.getItemPosition(nextItem, pos);
        mInventoryView.localToAscendantCoordinates(mIndicator.getParent(), pos);
        pos.y -= mIndicator.getHeight();

        if (mMoveAction != null) {
            mGroup.removeAction(mMoveAction);
        }
        mPulsingCircle.setColor(1, 1, 1, 0);
        mPulsingCircle.setScale(1);
        mMoveAction = Actions.sequence(
            Actions.moveTo(pos.x, pos.y, MOVE_DURATION, Interpolation.pow2),
            Actions.addAction(createPulseAction(), mPulsingCircle),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    addNextItem();
                }
            })
        );
        mGroup.addAction(mMoveAction);
    }

    private void addNextItem() {
        MealItem item = findNextItem();
        NLog.i(item);
        mMealView.addItem(item);
        if (item.getName().equals("top")) {
            mTargetBurger.hideArrow();
        } else {
            mTargetBurger.moveUpArrow();
        }
    }

    private MealItem findNextItem() {
        int idx = mTargetBurger.getArrowIndex();
        if (idx >= 0) {
            return mTargetBurger.getItemAt(idx);
        }
        return null;
    }

    private static Action createPulseAction() {
        return Actions.parallel(
            Actions.alpha(1),
            Actions.scaleTo(PULSING_SCALE_MIN, PULSING_SCALE_MIN, PULSING_DURATION, Interpolation.pow2),
            Actions.alpha(0, PULSING_DURATION)
        );
    }
}
