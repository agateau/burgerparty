package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.World;
import com.agateau.burgerparty.utils.AnimScript;
import com.agateau.burgerparty.utils.AnimScriptLoader;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ScoreFeedbackActor extends Label {
	private static final float FEEDBACK_ACTION_DURATION = 1.5f;
	public ScoreFeedbackActor(Actor parent, float mealXCenter, float mealY, World.Score score, Skin skin, AnimScriptLoader loader) {
		super("", skin, "score-feedback");
		String text = score.message;
		if (!text.isEmpty()) {
			text += "\n";
		}
		text += "+" + score.deltaScore;
		setText(text);
		initAnim(loader);
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

	private static void initAnim(AnimScriptLoader loader) {
		if (sAnimScript != null) {
			return;
		}
		sAnimScript = loader.load(
			  "parallel\n"
			+ "    alpha 0\n"
			+ "    play meal-done\n"
			+ "end\n"
			+ "parallel\n"
			+ "    alpha 1 0.05 pow2Out\n"
			+ "    moveBy 0 0.01 0.1\n"
			+ "end\n"
			+ "parallel\n"
			+ "    alpha 0 0.9\n"
			+ "    moveBy 0 0.3 0.9\n"
			+ "end\n"
		);
	}

	private static AnimScript sAnimScript = null;
}
