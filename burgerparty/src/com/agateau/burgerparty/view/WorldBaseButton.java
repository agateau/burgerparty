package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class WorldBaseButton extends ImageButton {
	public static final int WIDTH = 140;
	public static final int HEIGHT = 300;

	public WorldBaseButton(String text, String iconName, Assets assets) {
		super(assets.getSkin(), "world-button");
		TextureRegion region = assets.getTextureAtlas().findRegion(iconName);
		getImage().setDrawable(new TextureRegionDrawable(region));

		AnchorGroup group = new AnchorGroup();
		addActor(group);
		group.setFillParent(true);

		Label label = new Label(text, assets.getSkin(), "world-button-text");
		group.addRule(label, Anchor.CENTER, group, Anchor.CENTER);

		setSize(WIDTH, HEIGHT);
	}
}