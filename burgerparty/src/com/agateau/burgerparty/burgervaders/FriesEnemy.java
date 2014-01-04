package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.MaskedDrawableAtlas;
import com.agateau.burgerparty.utils.SpriteImage;

public abstract class FriesEnemy extends Enemy {
	private static final float PIXEL_PER_SECOND = 90;

	public FriesEnemy(MaskedDrawableAtlas atlas) {
		SpriteImage img = new SpriteImage(atlas.get("mealitems/0/big-fries-inventory"));
		addActor(img);
		updateSize();
	}

	@Override
	public void doAct(float delta) {
		setY(getY() - PIXEL_PER_SECOND * delta);
	}
}
