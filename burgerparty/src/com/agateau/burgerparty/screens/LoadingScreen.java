package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.utils.Signal0;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;

public class LoadingScreen implements Screen {
	private final static float PADDING = 36;
	private final static float ALPHA_STEP = 0.05f;
	private final static float ALPHA_INTERVAL = 0.01f;
	public Signal0 ready = new Signal0();

	private enum State {
		LOADING,
		FADING_OUT
	};

	public LoadingScreen(AssetManager assetManager) {
		super();
		mAssetManager = assetManager;
	}

	@Override
	public void render(float delta) {
		if (mLoadingTexture == null) {
			init();
		}
		if (mState == State.LOADING) {
			boolean done = mAssetManager.update();
			mAlpha = Math.min(mAssetManager.getProgress() * 2, 1);
			if (done) {
				mState = State.FADING_OUT;
				scheduleAlphaFadeout();
			}
		}
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mSpriteBatch.begin();
		mSpriteBatch.setColor(1, 1, 1, mAlpha);

		float screenWidth = Gdx.graphics.getWidth();
		float screenHeight = Gdx.graphics.getHeight();
		float width = mLoadingTexture.getWidth() - PADDING;
		float height = mLoadingTexture.getHeight() - PADDING;
		float ratio = Math.min(width / screenWidth, height / screenHeight);
		if (ratio < 1) {
			width *= ratio;
			height *= ratio;
		}
		mSpriteBatch.draw(mLoadingTexture, (screenWidth - width) / 2, (screenHeight - height) / 2, width, height);
		mSpriteBatch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		mLoadingTexture.dispose();
		mLoadingTexture = null;
	}

	private void init() {
		mLoadingTexture = new Texture(Gdx.files.internal("loading.png"));
	}

	private void scheduleAlphaFadeout() {
		Timer.Task task = new Timer.Task() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mAlpha -= ALPHA_STEP;
				if (mAlpha < 0) {
					ready.emit();
				} else {
					scheduleAlphaFadeout();
				}
			}
		};
		Timer.schedule(task, ALPHA_INTERVAL);
	}

	private SpriteBatch mSpriteBatch = new SpriteBatch();
	private AssetManager mAssetManager;
	private Texture mLoadingTexture = null;
	private float mAlpha = 0;

	State mState = State.LOADING;
}
