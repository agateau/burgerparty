package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.SpriteImage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Pool.Poolable;

public abstract class Enemy extends WidgetGroup implements Poolable {
	static private final float DEATH_DURATION = 0.5f;

	public void act(float delta) {
		super.act(delta);
		mTime += delta;
		if (mDying) {
			float k = mTime / DEATH_DURATION;
			float scale = 1.0f - k;
			if (scale < 0) {
				mustBeRemoved();
				return;
			}
			setScale(scale);
			setRotation(MathUtils.sin(k * 6) * 60);
			setColor(1, 1 - k, 1 - k, 1);
		} else {
			doAct(delta);
		}
	}

	public abstract void doAct(float delta);

	public void reset() {
		mTime = 0;
		mDying = false;
		setScale(1);
		setRotation(0);
		setColor(1, 1, 1, 1);
	}

	public void init(float posX) {
		setPosition(posX, getStage().getHeight());
	}

	public void onHit() {
		mDying = true;
		mTime = 0;
	}

	public abstract void mustBeRemoved();

	public boolean isDying() {
		return mDying;
	}

	public float getTime() {
		return mTime;
	}

	public boolean collide(SpriteImage other) {
		if (!SpriteImage.boundCollide(this, other)) {
			return false;
		}
		for(Actor actor: getChildren()) {
			SpriteImage image = (SpriteImage)actor;
			if (image == null) {
				continue;
			}
			float dx = other.getX() - (getX() + image.getX());
			float dy = other.getY() - (getY() + image.getY());
			if (image.getCollisionMask().collide(other.getCollisionMask(), (int)dx, (int)dy)) {
				return true;
			}
		}
		return false;
	}

	protected void updateSize() {
		float w = 0, h = 0;
		for(Actor actor: getChildren()) {
			w = Math.max(actor.getRight(), w);
			h = Math.max(actor.getTop(), h);
		}
		setWidth(w);
		setHeight(h);
		setOriginX(w / 2);
		setOriginY(h / 2);
		Gdx.app.log("updateSize", "w=" + w + " h=" + h);
	}

	private float mTime;
	private boolean mDying = false;
}
