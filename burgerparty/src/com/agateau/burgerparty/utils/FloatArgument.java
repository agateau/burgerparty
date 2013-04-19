package com.agateau.burgerparty.utils;

import com.agateau.burgerparty.utils.AnimScript.Context;

class FloatArgument extends Argument {
	public FloatArgumentDefinition.Domain domain;
	public float value;

	public FloatArgument(FloatArgumentDefinition.Domain d, float v) {
		domain = d;
		value = v;
	}

	@Override
	public Class<?> getClassType() {
		return Float.TYPE;
	}

	@Override
	public Object computeValue(Context context) {
		if (domain == FloatArgumentDefinition.Domain.Width) {
			return value * context.width;
		}
		if (domain == FloatArgumentDefinition.Domain.Height) {
			return value * context.height;
		}
		if (domain == FloatArgumentDefinition.Domain.Duration) {
			return value * context.duration;
		}
		return value;
	}
}