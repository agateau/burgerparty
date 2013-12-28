package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class WorldBaseButton extends Button {
	public static final int WIDTH = 140;
	public static final int HEIGHT = 300;

	static private class ShadedImage extends Image {
		public void setShader(ShaderProgram shader) {
			mShader = shader;
		}

		@Override
		public void draw(SpriteBatch batch, float parentAlpha) {
			if (mShader == null) {
				super.draw(batch, parentAlpha);
			} else {
				batch.setShader(mShader);
				super.draw(batch, parentAlpha);
				batch.setShader(null);
			}
		}

		private ShaderProgram mShader = null;
	}

	public WorldBaseButton(String text, String iconName, Assets assets) {
		super(assets.getSkin(), "world-button");
		mAssets = assets;
		TextureRegion region = assets.getTextureAtlas().findRegion(iconName);
		mBgImage.setDrawable(new TextureRegionDrawable(region));
		add(mBgImage);

		addActor(mGroup);
		mGroup.setFillParent(true);

		Label label = new Label(text, assets.getSkin(), "world-button-text");
		label.setAlignment(Align.center);
		mGroup.addRule(label, Anchor.CENTER, mGroup, Anchor.CENTER);

		setSize(WIDTH, HEIGHT);
	}

	public AnchorGroup getGroup() {
		return mGroup;
	}

	public void createLockOverlay() {
		setDisabled(true);
		mBgImage.setShader(mAssets.getDisabledShader());

		TextureRegion lockRegion = mAssets.getTextureAtlas().findRegion("ui/lock-key");
		Image image = new Image(lockRegion);
		mGroup.addRule(image, Anchor.CENTER, mGroup, Anchor.CENTER);
	}

	private final AnchorGroup mGroup = new AnchorGroup();
	private Assets mAssets;
	private ShadedImage mBgImage = new ShadedImage();
}
