package com.agateau.burgerparty.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * An action to fade a stage to black.
 *
 * Requires a one pixel region to be provided through its constructor.
 *
 * @author aurelien
 */
public class FadeToBlackAction extends Action {
    private final float mDuration;
    private final Image mImage;
    private float mTime = 0;

    public FadeToBlackAction(TextureRegion onePixelRegion, float duration) {
        mDuration = duration;
        mImage = new Image(onePixelRegion);
    }

    @Override
    public boolean act(float delta) {
        if (mImage.getStage() == null) {
            init();
        }
        mTime += delta;
        boolean done = false;
        if (mTime > mDuration) {
            mTime = mDuration;
            done = true;
        }
        mImage.setColor(0, 0, 0, mTime / mDuration);
        return done;
    }

    private void init() {
        Stage stage = getActor().getStage();
        stage.addActor(mImage);
        float width = stage.getWidth();
        float height = stage.getHeight();
        Vector3 pos = stage.getCamera().position;
        mImage.setBounds(pos.x - width / 2, pos.y - height / 2, width, height);
    }
}