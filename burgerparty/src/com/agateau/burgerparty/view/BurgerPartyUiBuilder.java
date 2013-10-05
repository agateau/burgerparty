package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.utils.UiBuilder;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.utils.XmlReader;

class BurgerPartyUiBuilder extends UiBuilder {
	public BurgerPartyUiBuilder() {
		super(Kernel.getTextureAtlas(), Kernel.getSkin());
	}

	protected ImageButton createImageButton(XmlReader.Element element) {
		ImageButton button = super.createImageButton(element);
		button.addListener(Kernel.getClickListener());
		return button;
	}
}