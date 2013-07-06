package com.agateau.burgerparty.utils;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class RoundButton extends ImageButton {
	public RoundButton(Skin skin, String name) {
		super(createStyle(skin, name));
	}

	private static ImageButton.ImageButtonStyle createStyle(Skin skin, String name) {
		ImageButton.ImageButtonStyle tmpl = skin.get("round-button", ImageButtonStyle.class);
		ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(tmpl);
		style.imageUp = skin.getDrawable(name);
		return style;
	}
}
