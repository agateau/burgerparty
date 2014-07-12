package com.agateau.burgerparty.view;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ScoreFeedbackActor extends Label {
    private static final float FEEDBACK_ACTION_DURATION = 1f;

    public ScoreFeedbackActor(Actor parent, float mealXCenter, float mealY, String text, Skin skin) {
        super("", skin, "score-feedback");
        setText(text);
        parent.getStage().addActor(this);
        Action act = Actions.parallel(
                Actions.delay(FEEDBACK_ACTION_DURATION / 2,
                    Actions.fadeOut(FEEDBACK_ACTION_DURATION, Interpolation.pow2Out)
                ),
                Actions.moveBy(0, getPrefHeight() / 2, FEEDBACK_ACTION_DURATION, Interpolation.pow2Out)
            );
        setX(mealXCenter - getPrefWidth() / 2);
        setY(mealY);
        addAction(
            Actions.sequence(
                act,
                Actions.removeActor()
            )
        );
    }
}
