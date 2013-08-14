package com.agateau.burgerparty.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

/**
 * A Screen with a stage filling its surface and a reference to a skin
 */
public class StageScreen implements Screen {
	// FIXME: Ugly hard-coded sizes
	private Stage mStage = new Stage(800, 480, true);
	private Skin mSkin;
	private Widget mBgActor = null;

	public StageScreen(Skin skin) {
		mSkin = skin;
		Gdx.input.setInputProcessor(mStage);
	}

	public Skin getSkin() {
		return mSkin;
	}

	public Stage getStage() {
		return mStage;
	}

	public void setBackgroundActor(Widget actor) {
		mBgActor = actor;
		if (mBgActor != null) {
			mStage.addActor(mBgActor);
			resizeBackgroundActor();
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		mStage.act(delta);
		mStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		resizeBackgroundActor();
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	private void resizeBackgroundActor() {
		if (mBgActor != null) {
			mBgActor.setBounds(0, 0, mStage.getWidth(), mStage.getHeight());
			mBgActor.layout();
		}
	}
}
