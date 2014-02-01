package com.agateau.burgerparty.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * A Screen with a stage filling its surface
 */
public abstract class StageScreen implements Screen {
	// FIXME: Ugly hard-coded sizes
	private static final float STAGE_WIDTH = 800;
	private static final float STAGE_HEIGHT = 480;
	private Stage mStage = new Stage(STAGE_WIDTH, STAGE_HEIGHT, true);
	private Actor mBgActor = null;

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
			mStage.getRoot().addActor(mOverlay);
		}
	}

	/**
	 * Must be reimplemented to handle pressing the "back" button
	 */
	public abstract void onBackPressed();

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(mBackgroundColor.r, mBackgroundColor.g, mBackgroundColor.b, mBackgroundColor.a);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		mStage.act(delta);
		mStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		mStage.setViewport(STAGE_WIDTH, STAGE_HEIGHT, true);
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

	private Overlay mOverlay = null;
	private Color mBackgroundColor = Color.BLACK;
}
