package com.agateau.burgerparty.utils;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class RoundButton extends ImageButton {
	public RoundButton(Skin skin, String name) {
		super(createStyle(skin, name));
		addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				if (mSound != null) {
					mSound.play();
				}
			}
		});
	}

	private static ImageButton.ImageButtonStyle createStyle(Skin skin, String name) {
		ImageButton.ImageButtonStyle tmpl = skin.get("round-button", ImageButtonStyle.class);
		ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(tmpl);
		style.imageUp = skin.getDrawable(name);
		return style;
	}

	public void setSound(Sound sound) {
		mSound = sound;
	}

	private Sound mSound = null;
}
