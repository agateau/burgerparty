package com.agateau.burgerparty;

import com.agateau.burgerparty.utils.RoundButton;

public class Kernel {
	public static RoundButton createRoundButton(Assets assets, String name) {
		RoundButton button = new RoundButton(assets.getSkin(), name);
		button.setSound(assets.getClickSound());
		return button;
	}
}
