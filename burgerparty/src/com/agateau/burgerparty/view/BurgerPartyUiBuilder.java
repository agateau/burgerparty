package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.utils.UiBuilder;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.XmlReader;

public class BurgerPartyUiBuilder extends UiBuilder {
	public BurgerPartyUiBuilder(Assets assets) {
		super(assets.getTextureAtlas(), assets.getSkin());
		mAssets = assets;
	}

	protected ImageButton createImageButton(XmlReader.Element element) {
		ImageButton button = super.createImageButton(element);
		String soundName = element.getAttribute("sound", "");

		ChangeListener listener;
		if (soundName.isEmpty()) {
			listener = mAssets.getClickListener();
		} else {
			final Sound sound = mAssets.getSoundAtlas().findSound(soundName);
			listener = new ChangeListener() {
				public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
					sound.play();
				}
			};
		};
		button.addListener(listener);

		return button;
	}

	Assets mAssets;
}