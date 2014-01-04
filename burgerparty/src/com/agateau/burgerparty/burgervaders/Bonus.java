package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.MaskedDrawableAtlas;
import com.agateau.burgerparty.utils.SpriteImage;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Pool.Poolable;

public abstract class Bonus extends SpriteImage implements Poolable {
	private static final float PIXEL_PER_SECOND = 60;

	public Bonus(MaskedDrawableAtlas atlas) {
		super(atlas.get("ui/surprise"));
	}

	public void reset() {
		remove();
	}

	public void init(Stage stage) {
		stage.addActor(this);
		float x = MathUtils.random(stage.getWidth() - getWidth());
		setPosition(x, stage.getHeight());
	}

	@Override
	public void act(float delta) {
		setY(getY() - delta * PIXEL_PER_SECOND);
		if (getTop() < 0) {
			mustBeRemoved();
		}
	}

	public abstract void mustBeRemoved(); 
}
