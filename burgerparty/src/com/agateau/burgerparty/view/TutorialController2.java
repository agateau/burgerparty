package com.agateau.burgerparty.view;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.Burger;
import com.agateau.burgerparty.model.MealItem;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class TutorialController2 {
    private InventoryView mInventoryView;
    private Group mGroup;
    private Image mIndicator;
    private Image mPulsingCircle;
    private MealView mMealView;
    private Burger mTargetBurger;

    private static final float MOVE_DURATION = 0.75f;

    private static final float PULSING_SCALE_MIN = 0.8f;
    private static final float PULSING_DURATION = 0.6f;

    public TutorialController2(BurgerPartyGame game, Burger targetBurger, InventoryView inventoryView) {
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
    }

    public Actor getIndicator() {
        return mGroup;
    }

    public void setMealView(MealView view) {
        mMealView = view;
    }

    public void updateIndicator() {
        MealItem nextItem = findNextItem();
        if (nextItem == null) {
            mGroup.setVisible(false);
            return;
        }

        mGroup.setVisible(true);
        Vector2 pos = new Vector2();
        mInventoryView.getItemPosition(nextItem, pos);
        mInventoryView.localToAscendantCoordinates(mIndicator.getParent(), pos);
        pos.y -= mIndicator.getHeight();

        mPulsingCircle.setColor(1, 1, 1, 0);
        mPulsingCircle.setScale(1);
        Action action = Actions.sequence(
            Actions.moveTo(pos.x, pos.y, MOVE_DURATION, Interpolation.pow2),
            Actions.addAction(createPulseAction(), mPulsingCircle),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    addNextItem();
                }
            })
        );
        mGroup.addAction(action);
    }

    private void addNextItem() {
        MealItem item = findNextItem();
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
