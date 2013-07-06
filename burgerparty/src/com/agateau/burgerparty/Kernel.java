package com.agateau.burgerparty;

import com.agateau.burgerparty.utils.AnimScriptLoader;
import com.agateau.burgerparty.utils.SoundAtlas;

public class Kernel {
	public static AnimScriptLoader getAnimScriptLoader() {
		return sAnimScriptLoader;
	}

	public static SoundAtlas getSoundAtlas() {
		return sSoundAtlas;
	}

	private static AnimScriptLoader sAnimScriptLoader = new AnimScriptLoader();
	private static SoundAtlas sSoundAtlas = new SoundAtlas("sounds/");

}
