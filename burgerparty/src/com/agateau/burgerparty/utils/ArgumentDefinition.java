package com.agateau.burgerparty.utils;

import java.io.StreamTokenizer;

public abstract class ArgumentDefinition<T> {
	public final Class<?> javaType;
	public final T defaultValue;

	ArgumentDefinition(Class<?> javaType, T defaultValue) {
		this.javaType = javaType;
		this.defaultValue = defaultValue;
	}

	public abstract Argument parse(StreamTokenizer tokenizer);
}