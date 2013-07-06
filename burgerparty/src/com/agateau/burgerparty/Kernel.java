package com.agateau.burgerparty;

import com.agateau.burgerparty.utils.AnimScriptLoader;

public class Kernel {
	public static AnimScriptLoader getAnimScriptLoader() {
		return sAnimScriptLoader;
	}

	static AnimScriptLoader sAnimScriptLoader = new AnimScriptLoader();
}
