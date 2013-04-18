package com.agateau.burgerparty.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.agateau.burgerparty.utils.AnimScript.Context;
import com.badlogic.gdx.scenes.scene2d.Action;

class Instruction {
	public Instruction(Method method, Argument[] args) {
		mMethod = method;
		mArgs = args;
	}

	public Action run(Context context) {
		Object[] objectArgs = new Object[mArgs.length];
		for(int idx=0; idx < mArgs.length; ++idx) {
			Argument arg = mArgs[idx];
			objectArgs[idx] = arg.computeValue(context);
		}
		try {
			return (Action)mMethod.invoke(null, objectArgs);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	Method mMethod;
	Argument[] mArgs;
}