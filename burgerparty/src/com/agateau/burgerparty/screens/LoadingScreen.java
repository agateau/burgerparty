package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.StageScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class LoadingScreen extends StageScreen {
    public Signal0 ready = new Signal0();

    private AssetManager mAssetManager;
    private Texture mLoadingTexture = null;
    private boolean mWaitForClick = false;

    public LoadingScreen(AssetManager assetManager) {
        mAssetManager = assetManager;
        setBackgroundColor(Color.WHITE);
        setupWidgets();
    }

    public void setWaitForClick(boolean value) {
        mWaitForClick = value;
    }

    private void setupWidgets() {
        mLoadingTexture = new Texture(Gdx.files.internal("loading.png"));
        mLoadingTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        AnchorGroup root = new AnchorGroup();
        getStage().addActor(root);
        root.setFillParent(true);

        Image image = new Image(mLoadingTexture);
        image.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                if (mAssetManager.update() && mWaitForClick) {
                    ready.emit();
                }
                return true;
            }
        });

        root.addRule(image, Anchor.CENTER, root, Anchor.CENTER);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (mAssetManager.update() && !mWaitForClick) {
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
