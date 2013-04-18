package com.agateau.burgerparty.utils;

import java.io.StreamTokenizer;
import java.lang.reflect.Method;


class InstructionDefinition {
	public InstructionDefinition(Method method, ArgumentDefinition<?>... argumentDefinitions) {
		mMethod = method;
		mArgumentDefinitions = argumentDefinitions;
	}

	public Instruction parse(StreamTokenizer tokenizer) {
		Argument[] args = new Argument[mArgumentDefinitions.length];
		for (int idx = 0; idx < mArgumentDefinitions.length; ++idx) {
			ArgumentDefinition<?> def = mArgumentDefinitions[idx];
			args[idx] = def.parse(tokenizer);
		}
		return new Instruction(mMethod, args);
	}

	private Method mMethod;
	private ArgumentDefinition<?>[] mArgumentDefinitions;
}