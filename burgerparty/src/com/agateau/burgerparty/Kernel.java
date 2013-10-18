package com.agateau.burgerparty;

import com.agateau.burgerparty.utils.RoundButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;

public class Kernel {
	public static RoundButton createRoundButton(Assets assets, String name) {
		RoundButton button = new RoundButton(assets.getSkin(), name);
		button.setSound(assets.getClickSound());
		return button;
	}

	public static ImageTextButton createTextButton(Assets assets, String text, String iconName) {
		ImageTextButton button = new ImageTextButton(text, assets.getSkin(), "image-text-button");
		button.getImage().setDrawable(assets.getSkin().getDrawable(iconName));
		button.addListener(assets.getClickListener());
		return button;
	}

	public static void preload() {
		assert(sAssets == null);
		sAssets = new Assets();
	}

	public static Assets getAssets() {
		assert(sAssets != null);
		return sAssets;
	}

	private static Assets sAssets = null;
}
