package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.MaskedDrawableAtlas;
import com.agateau.burgerparty.utils.SpriteImage;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Pool.Poolable;

public abstract class Bonus extends SpriteImage implements Poolable {
	private static final float PIXEL_PER_SECOND = 60;
	private static final float HIT_ANIMATION_DURATION = 0.5f;

	public Bonus(MaskedDrawableAtlas atlas) {
		super(atlas.get("ui/surprise"));
		setOriginX(getWidth() / 2);
		setOriginY(getHeight() / 2);
	}

	public void reset() {
		remove();
		mHit = false;
		mTime = 0;
		setScale(1, 1);
		setColor(1, 1, 1, 1);
	}

	public void init(Stage stage) {
		stage.addActor(this);
		float x = MathUtils.random(stage.getWidth() - getWidth());
		setPosition(x, stage.getHeight());
	}

	@Override
	public void act(float delta) {
		mTime += delta;
		if (mHit) {
			if (mTime < HIT_ANIMATION_DURATION) {
				float k = mTime / HIT_ANIMATION_DURATION;
				float scale = 1 + 4 * k;
				setScale(scale, scale);
				setColor(1, 1, 1, 1 - k);
			} else {
				mustBeRemoved();
			}
		} else {
			setY(getY() - delta * PIXEL_PER_SECOND);
			if (getTop() < 0) {
				mustBeRemoved();
			}
		}
	}

	public boolean hasBeenHit() {
		return mHit;
	}

	public void onHit() {
		mTime = 0;
		mHit = true;
	}

	public abstract void mustBeRemoved(); 

	private boolean mHit = false;
	private float mTime = 0;
}
