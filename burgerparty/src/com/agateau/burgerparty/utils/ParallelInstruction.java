package com.agateau.burgerparty.utils;

import com.agateau.burgerparty.utils.AnimScript.Context;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.utils.Array;

public class ParallelInstruction implements Instruction {
    private Array<Instruction> mInstructions;

    public ParallelInstruction(Array<Instruction> instructions) {
        mInstructions = instructions;
    }

    @Override
    public Action run(Context context) {
        ParallelAction action = Actions.parallel();
        for (Instruction instruction: mInstructions) {
            action.addAction(instruction.run(context));
        }
        return action;
    }
}
