package com.agateau.burgerparty.utils;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;

public class AnimScript {
	static class Context {
		float width;
		float height;
		float duration;
	}

	public AnimScript(Array<Instruction> instructions) {
		mInstructions = instructions;
	}

	public Action createAction(float width, float height, float duration) {
		Context context = new Context();
		context.width = width;
		context.height = height;
		context.duration = duration;

		if (mInstructions.size == 1) {
			return mInstructions.get(0).run(context);
		}
		SequenceAction action = Actions.sequence();
		for (Instruction instruction: mInstructions) {
			action.addAction(instruction.run(context));
		}
		return action;
	}

	private Array<Instruction> mInstructions;
}
