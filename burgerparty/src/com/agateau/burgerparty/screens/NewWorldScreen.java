package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.RefreshHelper;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.XmlReader;

public class NewWorldScreen extends BurgerPartyScreen {
    private static final float ZOOM_DURATION_INTERVAL = 1.2f;
    private static final float SHADOW_ALPHA = 0.3f;
    private int mWorldIndex;
    private float mDuration;

    private float mDotInterval;
    private TextureRegion mDotRegion;

    private Bezier<Vector2> mPath = new Bezier<Vector2>();
    private Vector2 mTmpV = new Vector2();
    private Image mPlane;
    private Image mPlaneShadow;
    private Image mBackground;

    private static class FadeToBlackAction extends Action {
        private final TextureRegion mRegion;
        private final float mDuration;
        private final Image mImage;
        private float mTime = 0;

        public FadeToBlackAction(TextureRegion region, float duration) {
            mDuration = duration;
            mRegion = region;
            mImage = new Image(mRegion);
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

    private class FlyAction extends Action {
        private float mTime = 0;
        private float mLastDotTime = 0;

        @Override
        public boolean act(float delta) {
            mTime += delta;
            if (mTime > mDuration) {
                mTime = mDuration;
                return true;
            }

            mPath.valueAt(mTmpV, mTime / mDuration);
            updatePlane(mTmpV.x - mPlane.getWidth() / 2, mTmpV.y - mPlane.getHeight() / 2);

            if (mTime - mLastDotTime > mDotInterval) {
                addDot();
                mLastDotTime = mTime;
            }
            Camera camera = getStage().getCamera();
            float xBorder = getStage().getWidth() / 2;
            float yBorder = getStage().getHeight() / 2;
            camera.position.x = MathUtils.clamp(MathUtils.round(mPlane.getX()), xBorder, mBackground.getWidth() - xBorder);
            camera.position.y = MathUtils.clamp(MathUtils.round(mPlane.getY()), yBorder, mBackground.getHeight() - yBorder);
            return false;
        }

        private void updatePlane(float x, float y) {

            float scale = computeScale();
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

        private float computeScale() {
            float k = 1;
            if (mTime < ZOOM_DURATION_INTERVAL) {
                k = mTime / ZOOM_DURATION_INTERVAL;
            } else if (mTime > mDuration - ZOOM_DURATION_INTERVAL) {
                k = (mDuration - mTime) / ZOOM_DURATION_INTERVAL;
            }
            return Interpolation.pow2Out.apply(k);
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
            getStage().addActor(image);
            mPlane.toFront();
        }
    }

    public NewWorldScreen(BurgerPartyGame game, int worldIndex) {
        super(game);
        mWorldIndex = worldIndex;

        mDotRegion = getTextureAtlas().findRegion("newworld/dot");
        createBackground();
        createPlane();
        loadXml();
        createRefreshHelper();
        createPin(mPath.points.get(0), worldIndex - 1);
        createPin(mPath.points.get(mPath.points.size - 1), worldIndex);
        createActions();
        game.getAssets().getSoundAtlas().findSound("jet").play();
    }

    private void createActions() {
        TextureRegion whitePixel = getTextureAtlas().findRegion("ui/white-pixel");
        getStage().getRoot().addAction(
            Actions.sequence(
                new FlyAction(),
                new FadeToBlackAction(whitePixel, 1),
                Actions.delay(0.5f),
        Actions.run(new Runnable() {
            @Override
            public void run() {
                startNextLevel();
            }
        }
                           )
            )
        );
    }

    @Override
    public void onBackPressed() {
        startNextLevel();
    }

    private void createBackground() {
        mBackground = new Image(getTextureAtlas().findRegion("levels/" + (mWorldIndex + 1) + "/newworld-map"));
        getStage().addActor(mBackground);

    }

    private void createPlane() {
        TextureRegion region = getTextureAtlas().findRegion("newworld/plane");
        float orgX = region.getRegionWidth() / 2;
        float orgY = region.getRegionHeight() / 2;

        mPlane = new Image(region);
        mPlane.setOrigin(orgX, orgY);
        getStage().addActor(mPlane);

        mPlaneShadow = new Image(region);
        mPlaneShadow.setOrigin(orgX, orgY);
        mPlaneShadow.setColor(0, 0, 0, SHADOW_ALPHA);
        getStage().addActor(mPlaneShadow);
    }

    private void loadXml() {
        XmlReader.Element rootElement = FileUtils.parseXml(FileUtils.assets("levels/" + (mWorldIndex + 1) + "/newworld.xml"));
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
        Image pin = new Image(getTextureAtlas().findRegion("newworld/pin-" + (worldIndex + 1)));
        pin.setPosition(pos.x - 15, pos.y);
        getStage().addActor(pin);
    }

    private void createRefreshHelper() {
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                getGame().showNewWorldScreen(mWorldIndex);
            }
        };
    }

    private void startNextLevel() {
        getGame().startLevel(mWorldIndex, 0);
    }
}
