package com.agateau.burgerparty.utils;

import com.agateau.burgerparty.utils.AnimScript.Context;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;

public class RepeatInstruction implements Instruction {
	public RepeatInstruction(Array<Instruction> instructions, int count) {
		mInstructions = instructions;
		mCount = count;
	}

	@Override
	public Action run(Context context) {
		Action action;
		if (mInstructions.size > 1) {
			SequenceAction seq = Actions.sequence();
			for (Instruction instruction: mInstructions) {
				seq.addAction(instruction.run(context));
			}
			action = seq;
		} else {
			action = mInstructions.get(0).run(context);
		}
		return Actions.repeat(mCount == 0 ? RepeatAction.FOREVER : mCount, action);
	}

	Array<Instruction> mInstructions;
	int mCount;
}
