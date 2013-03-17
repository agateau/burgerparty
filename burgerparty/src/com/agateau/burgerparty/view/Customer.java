package com.agateau.burgerparty.view;

import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Customer extends Image {
	public Customer(TextureAtlas atlas) {
		String names[] = {"a", "b"};
		String name = names[MathUtils.random(0, 1)];
		TextureRegion region = atlas.findRegion("customers/" + name);
		setDrawable(new TextureRegionDrawable(region));
		UiUtils.adjustToPrefSize(this);
	}
}
