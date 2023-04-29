package com.agateau.burgerparty.utils;

import com.agateau.burgerparty.utils.AnimScript.Context;

public class BasicArgument extends Argument {
    private Class<?> mClassType;
    private Object mValue;

    BasicArgument(Class<?> classType, Object value) {
        mClassType = classType;
        mValue = value;
    }

    @Override
    public Class<?> getClassType() {
        return mClassType;
    }

    @Override
    public Object computeValue(Context context) {
        return mValue;
    }
}
