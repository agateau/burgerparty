package com.agateau.burgerparty.utils;

import java.io.StreamTokenizer;
import java.lang.reflect.Method;


class BasicInstructionDefinition implements InstructionDefinition {
	public BasicInstructionDefinition(Method method, ArgumentDefinition<?>... argumentDefinitions) {
		mMethod = method;
		mArgumentDefinitions = argumentDefinitions;
	}

	/* (non-Javadoc)
	 * @see com.agateau.burgerparty.utils.InstructionDefinition#parse(java.io.StreamTokenizer)
	 */
	@Override
	public Instruction parse(StreamTokenizer tokenizer) {
		Argument[] args = new Argument[mArgumentDefinitions.length];
		for (int idx = 0; idx < mArgumentDefinitions.length; ++idx) {
			ArgumentDefinition<?> def = mArgumentDefinitions[idx];
			args[idx] = def.parse(tokenizer);
		}
		return new BasicInstruction(mMethod, args);
	}

	private Method mMethod;
	private ArgumentDefinition<?>[] mArgumentDefinitions;
}