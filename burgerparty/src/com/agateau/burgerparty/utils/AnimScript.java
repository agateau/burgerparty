package com.agateau.burgerparty.utils;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class AnimScript {
	static class Context {
		float width;
		float height;
		float duration;
	}

	public void createActions(Actor actor, float width, float height, float duration) {
		Context context = new Context();
		context.width = width;
		context.height = height;
		context.duration = duration;

		for (Instruction instruction: mInstructions) {
			Action action = instruction.run(context);
			assert(action != null);
			actor.addAction(action);
		}
	}

	public void addInstruction(Instruction i) {
		mInstructions.add(i);
	}

	private Array<Instruction> mInstructions = new Array<Instruction>();
}
