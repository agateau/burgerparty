package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.utils.Signal0;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class LoadingScreen implements Screen {
	private final static float PADDING = 36;
	public Signal0 ready = new Signal0();

	public LoadingScreen(AssetManager assetManager) {
		super();
		mAssetManager = assetManager;
	}

	@Override
	public void render(float delta) {
		if (mLoadingTexture == null) {
			init();
		}
		boolean done = mAssetManager.update();
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mSpriteBatch.begin();
		mSpriteBatch.setColor(1, 1, 1, Math.min(mAssetManager.getProgress() * 2, 1));

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

		if (done) {
			ready.emit();
		}
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

	private SpriteBatch mSpriteBatch = new SpriteBatch();
	private AssetManager mAssetManager;
	private Texture mLoadingTexture = null;
}
