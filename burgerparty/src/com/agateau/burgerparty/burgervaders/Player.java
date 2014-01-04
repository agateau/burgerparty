package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.SpriteImage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

public class Player extends Group {
	private static final float GUN_OFFSET = 0.2f;
	private static final int MAX_GUN_COUNT = 7;
	private class Gun extends SpriteImage {
		public Gun(TextureRegion region, float angleOffset) {
			super(region);
			mAngleOffset = angleOffset;
			setOriginX(getWidth() / 2);
			setOriginY(0);
		}

		public void fire(float srcX, float srcY, float angle) {
			angle += mAngleOffset;
			mMainScreen.fire(srcX, srcY, angle);
			setRotation(MathUtils.radiansToDegrees * angle - 90);
		}
		
		private float mAngleOffset;
	}

	public Player(BurgerVadersMainScreen mainScreen, TextureRegion region) {
		mRegion = region;
		createGun(0);
		mMainScreen = mainScreen;
	}

	public void act(float delta) {
		super.act(delta);
		if (!Gdx.input.justTouched()) {
			return;
		}

		Vector2 v = new Vector2(Gdx.input.getX(), Gdx.input.getY());
		v = getStage().screenToStageCoordinates(v);
		float srcX = getX();
		float srcY = getY();

		float angle = MathUtils.atan2((v.y - srcY), (v.x - srcX));

		for(Gun gun: mGuns) {
			gun.fire(srcX, srcY, angle);
		}
	}

	public void addGun() {
		if (mGuns.size < MAX_GUN_COUNT) {
			float offset = ((mGuns.size + 1) / 2) * GUN_OFFSET;
			createGun(-offset);
			createGun(+offset);
		}
	}

	private void createGun(float angleOffset) {
		Gun gun = new Gun(mRegion, angleOffset);
		gun.setX(-gun.getWidth() / 2);
		addActor(gun);
		if (mGuns.size > 0) {
			float angle = mGuns.get(0).getRotation() + MathUtils.radiansToDegrees * angleOffset;
			gun.setRotation(angle);
			gun.setScale(0);
			gun.addAction(Actions.scaleTo(1, 1, 0.5f));
		}
		int zIndex = mGuns.size;
		for (int idx = mGuns.size - 1; idx >= 0; --idx) {
			mGuns.get(idx).setZIndex(zIndex);
		}
		mGuns.add(gun);
	}

	private TextureRegion mRegion;
	private Array<Gun> mGuns = new Array<Gun>();
	private BurgerVadersMainScreen mMainScreen;
}
