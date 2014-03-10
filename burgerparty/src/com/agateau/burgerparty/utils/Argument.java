package com.agateau.burgerparty.utils;

import com.agateau.burgerparty.utils.AnimScript.Context;

abstract class Argument {
    public abstract Class<?> getClassType();
    public abstract Object computeValue(Context context);
}