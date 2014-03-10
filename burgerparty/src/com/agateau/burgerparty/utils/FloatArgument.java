package com.agateau.burgerparty.utils;

import com.agateau.burgerparty.utils.AnimScript.Context;

class FloatArgument extends Argument {
	private FloatArgumentDefinition.Domain mDomain;
	private float mValue;

	public FloatArgument(FloatArgumentDefinition.Domain d, float v) {
		mDomain = d;
		mValue = v;
	}

	@Override
	public Class<?> getClassType() {
		return Float.TYPE;
	}

	@Override
	public Object computeValue(Context context) {
		if (mDomain == FloatArgumentDefinition.Domain.Width) {
			return mValue * context.width;
		}
		if (mDomain == FloatArgumentDefinition.Domain.Height) {
			return mValue * context.height;
		}
		if (mDomain == FloatArgumentDefinition.Domain.Duration) {
			return mValue * context.duration;
		}
		return mValue;
	}
}