package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.StageScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class LoadingScreen extends StageScreen {
	public Signal0 ready = new Signal0();

	private AssetManager mAssetManager;
	private Texture mLoadingTexture = null;

	public LoadingScreen(AssetManager assetManager) {
		mAssetManager = assetManager;
		setBackgroundColor(Color.WHITE);
		setupWidgets();
	}

	private void setupWidgets() {
		mLoadingTexture = new Texture(Gdx.files.internal("loading.png"));
		AnchorGroup root = new AnchorGroup();
		getStage().addActor(root);
		root.setFillParent(true);

		Image image = new Image(mLoadingTexture);
		root.addRule(image, Anchor.CENTER, root, Anchor.CENTER);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		if (mAssetManager.update()) {
			ready.emit();
		}
		
	}

	@Override
	public void dispose() {
		mLoadingTexture.dispose();
		mLoadingTexture = null;
	}

	@Override
	public void onBackPressed() {
	}
}
