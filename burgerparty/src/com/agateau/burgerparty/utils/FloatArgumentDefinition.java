package com.agateau.burgerparty.utils;

import java.io.IOException;
import java.io.StreamTokenizer;

class FloatArgumentDefinition extends ArgumentDefinition<Float> {
	enum Domain {
		Width,
		Height,
		Duration,
		Scalar
	}

	FloatArgumentDefinition(FloatArgumentDefinition.Domain domain) {
		super(Float.TYPE, null);
		mDomain = domain;
	}

	FloatArgumentDefinition(FloatArgumentDefinition.Domain domain, float defaultValue) {
		super(Float.TYPE, defaultValue);
		mDomain = domain;
	}

	@Override
	public Argument parse(StreamTokenizer tokenizer) {
		try {
			tokenizer.nextToken();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		float value;
		if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
			value = (float)tokenizer.nval;
		} else {
			assert(this.defaultValue != null);
			tokenizer.pushBack();
			value = this.defaultValue;
		}
		return new FloatArgument(mDomain, value);
	}

	private FloatArgumentDefinition.Domain mDomain;
}