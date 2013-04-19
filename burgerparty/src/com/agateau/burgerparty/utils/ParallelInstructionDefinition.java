package com.agateau.burgerparty.utils;

import java.io.IOException;
import java.io.StreamTokenizer;

import com.badlogic.gdx.utils.Array;

public class ParallelInstructionDefinition implements InstructionDefinition {
	public ParallelInstructionDefinition(AnimScriptLoader loader) {
		mLoader = loader;
	}

	@Override
	public Instruction parse(StreamTokenizer tokenizer) throws IOException {
		Array<Instruction> lst = mLoader.tokenize(tokenizer, "end");
		return new ParallelInstruction(lst);
	}

	private AnimScriptLoader mLoader;
}
