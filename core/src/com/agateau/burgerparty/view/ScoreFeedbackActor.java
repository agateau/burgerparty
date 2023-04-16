package com.agateau.burgerparty.view;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ScoreFeedbackActor extends Label {
    private static final float FEEDBACK_ACTION_DURATION = 1f;

    public ScoreFeedbackActor(Actor parent, float dy, String text, Skin skin, String style) {
        super("", skin, style);
        setText(text);
        parent.getStage().addActor(this);
        Action act = Actions.parallel(
                Actions.delay(FEEDBACK_ACTION_DURATION / 2,
                    Actions.fadeOut(FEEDBACK_ACTION_DURATION, Interpolation.pow2Out)
                ),
                Actions.moveBy(0, dy, FEEDBACK_ACTION_DURATION, Interpolation.pow2Out)
            );
        addAction(
            Actions.sequence(
                act,
                Actions.removeActor()
            )
        );
    }
}
