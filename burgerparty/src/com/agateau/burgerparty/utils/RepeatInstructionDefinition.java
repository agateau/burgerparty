package com.agateau.burgerparty.utils;

import java.io.IOException;
import java.io.StreamTokenizer;

import com.badlogic.gdx.utils.Array;

public class RepeatInstructionDefinition implements InstructionDefinition {
	private AnimScriptLoader mLoader;

	public RepeatInstructionDefinition(AnimScriptLoader loader) {
		mLoader = loader;
	}

	@Override
	public Instruction parse(StreamTokenizer tokenizer) throws IOException {
		int count = parseCount(tokenizer);
		Array<Instruction> lst = mLoader.tokenize(tokenizer, "end");
		return new RepeatInstruction(lst, count);
	}

	private int parseCount(StreamTokenizer tokenizer) throws IOException {
		tokenizer.nextToken();
		if (tokenizer.ttype == StreamTokenizer.TT_EOL) {
			return 0;
		}
		if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
			return (int)tokenizer.nval;
		}
		throw new RuntimeException("Error in repeat instruction: '" + tokenizer.sval + "' is not a valid repeat count");
	}
}
