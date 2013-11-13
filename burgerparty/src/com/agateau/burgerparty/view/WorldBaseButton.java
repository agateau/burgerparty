package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class WorldBaseButton extends ImageButton {
	public static final int WIDTH = 140;
	public static final int HEIGHT = 300;

	public WorldBaseButton(String text, String iconName, Assets assets) {
		super(assets.getSkin(), "world-button");
		mDisabledShader = assets.getDisabledShader();
		mLockRegion = assets.getTextureAtlas().findRegion("ui/lock");
		TextureRegion region = assets.getTextureAtlas().findRegion(iconName);
		getImage().setDrawable(new TextureRegionDrawable(region));

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

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		if (isDisabled()) {
			batch.setShader(mDisabledShader);
			super.draw(batch, parentAlpha);
			batch.setShader(null);
			float posX = getX() + (getWidth() - mLockRegion.getRegionWidth()) / 2;
			float posY = getY() + (getHeight() - mLockRegion.getRegionHeight()) / 2;
			batch.draw(mLockRegion, posX, posY);
		} else {
			super.draw(batch, parentAlpha);
		}
	}

	private final AnchorGroup mGroup = new AnchorGroup();
	private ShaderProgram mDisabledShader;
	private TextureRegion mLockRegion;
}