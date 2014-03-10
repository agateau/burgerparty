package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.utils.SpriteImage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Player extends Group {
    private static final float GUN_OFFSET = 0.2f;
    private static final int MAX_GUN_COUNT = 5;
    private static final float MAX_MULTIGUN_DURATION = 15f;

    private class Gun extends SpriteImage implements Poolable {
        public Gun() {
            super(mRegion);
        }

        public void init(float angleOffset) {
            mAngleOffset = angleOffset;
            setOriginX(getWidth() / 2);
            setOriginY(0);
        }

        public void fire(float srcX, float srcY, float angle) {
            angle += mAngleOffset;
            mMainScreen.fire(srcX, srcY, angle);
            setRotation(MathUtils.radiansToDegrees * angle - 90);
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            if (getParent() == null) {
                mGunPool.free(this);
            }
        }

        @Override
        public void reset() {
            setScale(1);
        }

        private float mAngleOffset;
    }

    public Player(BurgerVadersMainScreen mainScreen, Assets assets) {
        mRegion = assets.getTextureAtlas().findRegion("mealitems/0/ketchup-inventory");
        assert(mRegion!= null);
        mFireSound = assets.getSoundAtlas().findSound("invaders-shot");
        mGunPool = new Pool<Gun>() {
            @Override
            protected Gun newObject() {
                return new Gun();
            }
        };
        createGun(0);
        mMainScreen = mainScreen;
    }

    public void act(float delta) {
        super.act(delta);
        handleMultiGunTimeout(delta);
        handleTouch();
    }

    private void handleMultiGunTimeout(float delta) {
        if (mGuns.size > 1) {
            mMultiGunTime += delta;
            if (mMultiGunTime > MAX_MULTIGUN_DURATION) {
                removeGun();
                removeGun();
                mMultiGunTime = 0;
            }
        }
    }

    private void removeGun() {
        Gun gun = mGuns.pop();
        gun.addAction(Actions.sequence(
                          Actions.scaleTo(0, 0, 0.5f),
                          Actions.removeActor()
                      ));
    }

    private void handleTouch() {
        if (!Gdx.input.justTouched()) {
            return;
        }

        Vector2 v = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        v = getStage().screenToStageCoordinates(v);
        float srcX = getX();
        float srcY = getY();

        float angle = MathUtils.atan2((v.y - srcY), (v.x - srcX));

        for (Gun gun: mGuns) {
            gun.fire(srcX, srcY, angle);
        }
        mFireSound.play();
    }

    public void addGun() {
        if (mGuns.size < MAX_GUN_COUNT) {
            mMultiGunTime = 0;
            float offset = ((mGuns.size + 1) / 2) * GUN_OFFSET;
            createGun(-offset);
            createGun(+offset);
        }
    }

    private void createGun(float angleOffset) {
        Gun gun = mGunPool.obtain();
        gun.init(angleOffset);
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
    private Sound mFireSound;
    private Array<Gun> mGuns = new Array<Gun>();
    private Pool<Gun> mGunPool;
    private BurgerVadersMainScreen mMainScreen;
    private float mMultiGunTime = 0;
}
