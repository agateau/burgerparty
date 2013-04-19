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

	public FloatArgumentDefinition.Domain domain;

	FloatArgumentDefinition(FloatArgumentDefinition.Domain domain) {
		super(Float.TYPE, null);
		this.domain = domain;
	}

	FloatArgumentDefinition(FloatArgumentDefinition.Domain domain, float defaultValue) {
		super(Float.TYPE, defaultValue);
		this.domain = domain;
	}

	@Override
	public Argument parse(StreamTokenizer tokenizer) {
		try {
			if (this.defaultValue == null) {
				return readFloat(tokenizer, this.domain);
			} else {
				return readFloat(tokenizer, this.domain, defaultValue);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	static FloatArgument readFloat(StreamTokenizer tokenizer, FloatArgumentDefinition.Domain domain) throws IOException {
		tokenizer.nextToken();
		assert(tokenizer.ttype == StreamTokenizer.TT_NUMBER);
		return new FloatArgument(domain, (float)tokenizer.nval);
	}

	static FloatArgument readFloat(StreamTokenizer tokenizer, FloatArgumentDefinition.Domain domain, float defaultValue) throws IOException {
		float value;
		if (tokenizer.nextToken() == StreamTokenizer.TT_NUMBER) {
			value = (float)tokenizer.nval;
		} else {
			tokenizer.pushBack();
			value = defaultValue;
		}
		return new FloatArgument(domain, value);
	}
}