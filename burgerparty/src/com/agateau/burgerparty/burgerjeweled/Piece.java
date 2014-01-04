package com.agateau.burgerparty.burgerjeweled;

import com.agateau.burgerparty.utils.MaskedDrawable;
import com.agateau.burgerparty.utils.SpriteImage;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public abstract class Piece extends SpriteImage {
	static private final float DEATH_DURATION = 0.5f;
	static private final float FALL_DURATION = 0.2f;
	static private final float SWAP_DURATION = 0.2f;

	public void destroy() {
		mDying = true;
		addAction(Actions.sequence(
			Actions.parallel(
				Actions.scaleTo(0, 0, DEATH_DURATION),
				Actions.rotateBy(180, DEATH_DURATION)
			),
			Actions.run(new Runnable() {
				@Override
				public void run() {
					mustBeRemoved();
				}
			})
		));
	}

	public void fallTo(float dstY) {
		clearActions();
		addAction(Actions.moveTo(getX(), dstY, FALL_DURATION, Interpolation.pow2Out));
	}

	public void moveTo(float x, float y) {
		clearActions();
		addAction(Actions.moveTo(x, y, SWAP_DURATION));
	}
	public void swapTo(float x, float y) {
		clearActions();
		addAction(Actions.sequence(
			Actions.moveTo(x, y, SWAP_DURATION),
			Actions.moveTo(getX(), getY(), SWAP_DURATION)
			)
		);
	}

	public void reset(MaskedDrawable md, int id, float posX, float posY) {
		mTime = 0;
		mId = id;
		mDying = false;
		setMaskedDrawable(md);
		setOriginX(getWidth() / 2);
		setOriginY(getHeight() / 2);
		setScale(1);
		setRotation(0);
		setPosition(posX, getStage().getHeight() + posY);
		addAction(
			Actions.moveTo(posX, posY, FALL_DURATION, Interpolation.pow2Out)
			);
	}

	public boolean isDying() {
		return mDying;
	}

	public float getTime() {
		return mTime;
	}

	public int getId() {
		return mId;
	}

	public abstract void mustBeRemoved();

	private float mTime;
	private int mId;
	private boolean mDying = false;
}
