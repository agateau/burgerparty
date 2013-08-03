package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.model.World;
import com.agateau.burgerparty.utils.AnimScript;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class ScoreFeedbackActor extends Label {
	private static final float FEEDBACK_ACTION_DURATION = 1.5f;
	public ScoreFeedbackActor(Actor parent, float mealXCenter, float mealY, World.ScoreType scoreType, int delta) {
		super("", Kernel.getSkin(), "score-feedback");
		setText("+" + delta + "!");
		initAnim();
		parent.getStage().addActor(this);
		Action act = sAnimScript.createAction(parent.getWidth(), parent.getHeight(), FEEDBACK_ACTION_DURATION);
		setX(mealXCenter - getPrefWidth() / 2);
		setY(mealY);
		addAction(
			Actions.sequence(
				act,
				Actions.removeActor()
			)
		);
	}

	private static void initAnim() {
		if (sAnimScript != null) {
			return;
		}
		sAnimScript = Kernel.getAnimScriptLoader().load(
			  "parallel\n"
			+ "    alpha 0\n"
			+ "    play meal-done.wav\n"
			+ "end\n"
			+ "parallel\n"
			+ "    alpha 1 0.1\n"
			+ "    moveBy 0 0.1 0.1\n"
			+ "end\n"
			+ "parallel\n"
			+ "    alpha 0 0.9\n"
			+ "    moveBy 0 0.4 0.9\n"
			+ "end\n"
		);
	}

	private static AnimScript sAnimScript = null;
}
