package com.agateau.burgerparty.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.agateau.burgerparty.utils.AnimScript.Context;
import com.badlogic.gdx.scenes.scene2d.Action;

class BasicInstruction implements Instruction {
	public BasicInstruction(Object object, Method method, Argument[] args) {
		mObject = object;
		mMethod = method;
		mArgs = args;
	}

	/* (non-Javadoc)
	 * @see com.agateau.burgerparty.utils.Instruction#run(com.agateau.burgerparty.utils.AnimScript.Context)
	 */
	@Override
	public Action run(Context context) {
		Object[] objectArgs = new Object[mArgs.length];
		for(int idx=0; idx < mArgs.length; ++idx) {
			Argument arg = mArgs[idx];
			objectArgs[idx] = arg.computeValue(context);
		}
		try {
			return (Action)mMethod.invoke(mObject, objectArgs);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new RuntimeException();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	Object mObject;
	Method mMethod;
	Argument[] mArgs;
}
