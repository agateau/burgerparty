package com.agateau.burgerparty.utils;

import java.io.StreamTokenizer;
import java.lang.reflect.Method;

class BasicInstructionDefinition implements InstructionDefinition {
	public BasicInstructionDefinition(Object instance, Method method, ArgumentDefinition<?>... argumentDefinitions) {
		mInstance = instance;
		mMethod = method;
		mArgumentDefinitions = argumentDefinitions;
	}

	public BasicInstructionDefinition(Method method, ArgumentDefinition<?>... argumentDefinitions) {
		mInstance = null;
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
			assert(def != null);
			args[idx] = def.parse(tokenizer);
		}
		return new BasicInstruction(mInstance, mMethod, args);
	}

	private Object mInstance;
	private Method mMethod;
	private ArgumentDefinition<?>[] mArgumentDefinitions;
}
