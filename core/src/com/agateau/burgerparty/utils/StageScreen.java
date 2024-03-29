package com.agateau.burgerparty.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * A Screen with a stage filling its surface
 */
public abstract class StageScreen implements Screen {
    // FIXME: Ugly hard-coded sizes
    private static final float STAGE_WIDTH = 800;
    private static final float STAGE_HEIGHT = 480;

    private Viewport mViewport = new FitViewport(STAGE_WIDTH, STAGE_HEIGHT);
    private Stage mStage = new Stage(mViewport);
    private Actor mBgActor = null;
    private Group mOverlayLayer = new Group();
    private Group mNotificationLayer = new Group();
    private Overlay mOverlay = null;
    private Color mBackgroundColor = Color.BLACK;

    public StageScreen() {
        mStage.getRoot().addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK || keycode == Input.Keys.BACKSPACE) {
                    if (mOverlay == null) {
                        onBackPressed();
                    } else {
                        mOverlay.onBackPressed();
                    }
                    return true;
                }
                return false;
            }
        });

        mOverlayLayer.setTouchable(Touchable.childrenOnly);
        mStage.addActor(mOverlayLayer);

        mNotificationLayer.setTouchable(Touchable.childrenOnly);
        mStage.addActor(mNotificationLayer);
    }

    public Stage getStage() {
        return mStage;
    }

    public void setBackgroundColor(Color color) {
        mBackgroundColor = color.cpy();
    }

    public void setBackgroundActor(Actor actor) {
        mBgActor = actor;
        if (mBgActor != null) {
            mStage.addActor(mBgActor);
            resizeBackgroundActor();
        }
    }

    public Overlay getOverlay() {
        return mOverlay;
    }

    public void setOverlay(Overlay overlay) {
        if (mOverlay != null) {
            mOverlay.aboutToBeRemoved();
            mOverlay.remove();
            mOverlay = null;
        }
        if (overlay != null) {
            mOverlay = overlay;
            mOverlayLayer.addActor(mOverlay);
            updateLayersZOrder();
        }
    }

    public void addNotificationActor(Actor actor) {
        mNotificationLayer.addActor(actor);
        updateLayersZOrder();
    }

    /**
     * Must be reimplemented to handle pressing the "back" button
     */
    public abstract void onBackPressed();

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(mBackgroundColor.r, mBackgroundColor.g, mBackgroundColor.b, mBackgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mStage.act(delta);
        mStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        mViewport.update(width, height);
        mOverlayLayer.setSize(mStage.getWidth(), mStage.getHeight());
        mNotificationLayer.setSize(mStage.getWidth(), mStage.getHeight());
        resizeBackgroundActor();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(mStage);
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        setOverlay(null);
    }

    private void resizeBackgroundActor() {
        if (mBgActor != null) {
            mBgActor.setBounds(0, 0, mStage.getWidth(), mStage.getHeight());
        }
    }

    private void updateLayersZOrder() {
        mOverlayLayer.toFront();
        mNotificationLayer.toFront();
    }
}
