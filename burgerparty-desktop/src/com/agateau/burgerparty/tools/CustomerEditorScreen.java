package com.agateau.burgerparty.tools;

import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.StageScreen;
import com.agateau.burgerparty.utils.TiledImage;
import com.agateau.burgerparty.utils.UiUtils;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class CustomerEditorScreen extends StageScreen {

	public CustomerEditorScreen(CustomerEditorGame game, TextureAtlas atlas, Skin skin) {
		super(skin);
		TiledImage bgImage = new TiledImage(atlas.findRegion("ui/menu-bg"));
		setBackgroundActor(bgImage);
		setupWidgets(atlas, skin);
	}

	private void setupWidgets(TextureAtlas atlas, Skin skin) {
		AnchorGroup group = new AnchorGroup();
		group.setSpacing(UiUtils.SPACING);
		getStage().addActor(group);
		group.setFillParent(true);
	}
}
