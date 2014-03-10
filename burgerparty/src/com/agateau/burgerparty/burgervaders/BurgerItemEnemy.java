package com.agateau.burgerparty.burgervaders;

import com.agateau.burgerparty.utils.MaskedDrawable;
import com.agateau.burgerparty.utils.SpriteImage;

public abstract class BurgerItemEnemy extends Enemy {
    private static final float PIXEL_PER_SECOND = 90;

    public BurgerItemEnemy() {
        addActor(mSpriteImage);
    }

    public void init(MaskedDrawable md) {
        mSpriteImage.setMaskedDrawable(md);
        updateSize();
    }

    @Override
    public void doAct(float delta) {
        setY(getY() - PIXEL_PER_SECOND * delta);
    }

    private SpriteImage mSpriteImage = new SpriteImage();
}
