package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.FadeToBlackAction;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.XmlReader;

class FlyingView extends AnchorGroup {
    private static final float ZOOM_DURATION_INTERVAL = 1.2f;
    private static final float SHADOW_ALPHA = 0.3f;

    private final NewWorldScreen mScreen;
    private final int mWorldIndex;

    private float mTime = 0;
    private float mDuration;

    private float mDotInterval;
    private TextureRegion mDotRegion;

    private Bezier<Vector2> mPath = new Bezier<Vector2>();
    private Vector2 mTmpV = new Vector2();
    private Image mPlane;
    private Image mPlaneShadow;
    private Image mBackground;

    private class FlyAction extends Action {
        private float mLastDotTime = 0;

        @Override
        public boolean act(float delta) {
            mTime += delta;
            if (mTime > mDuration) {
                mTime = mDuration;
                return true;
            }

            flyTo(mTime / mDuration);

            if (mTime - mLastDotTime > mDotInterval) {
                addDot();
                mLastDotTime = mTime;
            }
            return false;
        }

        private void addDot() {
            Image image = new Image(mDotRegion);
            mPath.valueAt(mTmpV, (mTime - mDotInterval) / mDuration);
            image.setPosition(
                    MathUtils.round(mTmpV.x - image.getWidth() / 2),
                    MathUtils.round(mTmpV.y - image.getHeight() / 2)
                    );
            image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);
            image.setColor(0, 0, 0, SHADOW_ALPHA);
            addActor(image);
            mPlane.toFront();
        }
    }

    private void flyTo(float k) {
        mPath.valueAt(mTmpV, k);
        updatePlane(mTmpV.x - mPlane.getWidth() / 2, mTmpV.y - mPlane.getHeight() / 2);
        float xBorder = getStage().getWidth() / 2;
        float yBorder = getStage().getHeight() / 2;
        float x = MathUtils.clamp(MathUtils.round(mPlane.getX()), xBorder, mBackground.getWidth() - xBorder);
        float y = MathUtils.clamp(MathUtils.round(mPlane.getY()), yBorder, mBackground.getHeight() - yBorder);
        setPosition(-x + xBorder, -y + yBorder);
    }

    private void updatePlane(float x, float y) {

        float scale = computePlaneScale();
        mPlane.setScale(scale);
        mPlaneShadow.setScale(scale * 0.6f);

        float oldX = mPlane.getX();
        float oldY = mPlane.getY();
        mPlane.setPosition(x, y + 20 * scale);
        mPlaneShadow.setPosition(x, y);
        float angle = MathUtils.atan2(mPlane.getY() - oldY, mPlane.getX() - oldX);

        mPlane.setRotation(MathUtils.radiansToDegrees * angle);
        mPlaneShadow.setRotation(MathUtils.radiansToDegrees * angle);
    }

    private float computePlaneScale() {
        float k = 1;
        if (mTime < ZOOM_DURATION_INTERVAL) {
            k = mTime / ZOOM_DURATION_INTERVAL;
        } else if (mTime > mDuration - ZOOM_DURATION_INTERVAL) {
            k = (mDuration - mTime) / ZOOM_DURATION_INTERVAL;
        }
        return Interpolation.pow2Out.apply(k);
    }

    public FlyingView(NewWorldScreen screen, XmlReader.Element rootElement, int worldIndex) {
        mScreen = screen;
        mWorldIndex = worldIndex;
        mDotRegion = mScreen.getTextureAtlas().findRegion("newworld/dot");
        createBackground();
        createPlane();
        loadXml(rootElement);
        createPin(mPath.points.get(0), mWorldIndex - 1);
        createPin(mPath.points.get(mPath.points.size - 1), mWorldIndex);
        createActions();
    }

    private void createActions() {
        TextureRegion whitePixel = mScreen.getTextureAtlas().findRegion("ui/white-pixel");
        addAction(
            Actions.sequence(
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        flyTo(0);
                    }
                }),
                Actions.delay(NewWorldScreen.ANIM_DURATION),
                new FlyAction(),
                new FadeToBlackAction(whitePixel, 1),
                Actions.delay(0.5f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        mScreen.goToNextView();
                    }
                })
            )
        );
        addAction(mScreen.getGame().getAssets().getSoundAtlas().createPlayAction("jet"));
    }

    private void createBackground() {
        mBackground = new Image(mScreen.getTextureAtlas().findRegion("levels/" + (mWorldIndex + 1) + "/newworld-map"));
        addActor(mBackground);
    }

    private void createPlane() {
        TextureRegion region = mScreen.getTextureAtlas().findRegion("newworld/plane");
        float orgX = region.getRegionWidth() / 2;
        float orgY = region.getRegionHeight() / 2;

        mPlane = new Image(region);
        mPlane.setOrigin(orgX, orgY);
        addActor(mPlane);

        mPlaneShadow = new Image(region);
        mPlaneShadow.setOrigin(orgX, orgY);
        mPlaneShadow.setColor(0, 0, 0, SHADOW_ALPHA);
        addActor(mPlaneShadow);
    }

    private void loadXml(XmlReader.Element rootElement) {
        mDuration = rootElement.getFloatAttribute("duration");
        mDotInterval = rootElement.getFloatAttribute("dotInterval");

        XmlReader.Element pointsElement = rootElement.getChildByName("points");
        Vector2[] points = new Vector2[pointsElement.getChildCount()];
        final float height = mBackground.getHeight();
        int idx = 0;
        for (XmlReader.Element pointElement: pointsElement.getChildrenByName("point")) {
            points[idx++] = new Vector2(pointElement.getFloatAttribute("x"), height - pointElement.getFloatAttribute("y"));
        }
        assert(points.length >= 2);
        mPath.set(points);
    }

    private void createPin(Vector2 pos, int worldIndex) {
        Image pin = new Image(mScreen.getTextureAtlas().findRegion("newworld/pin-" + (worldIndex + 1)));
        pin.setPosition(pos.x - 15, pos.y);
        addActor(pin);
    }
}