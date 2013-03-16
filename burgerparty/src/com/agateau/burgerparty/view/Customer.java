package com.agateau.burgerparty.view;

import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Customer extends Image {
	public Customer(TextureAtlas atlas) {
		TextureRegion region = atlas.findRegion("customer");
		setDrawable(new TextureRegionDrawable(region));
		UiUtils.adjustToPrefSize(this);
	}
}
