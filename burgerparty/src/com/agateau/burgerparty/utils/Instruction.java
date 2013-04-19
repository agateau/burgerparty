package com.agateau.burgerparty.utils;

import com.agateau.burgerparty.utils.AnimScript.Context;
import com.badlogic.gdx.scenes.scene2d.Action;

interface Instruction {

	public abstract Action run(Context context);

}