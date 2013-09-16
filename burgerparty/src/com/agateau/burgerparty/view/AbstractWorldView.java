package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AbstractWorldView extends AnchorGroup {
	public AbstractWorldView(LevelWorld world) {
		setFillParent(true);
		setSpacing(UiUtils.SPACING);
		mBackgroundRegion = Kernel.getTextureAtlas().findRegion(world.getDirName() + "background");
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.setColor(1, 1, 1, parentAlpha);
		batch.draw(mBackgroundRegion, 0, 0, getWidth(), getHeight());
		super.draw(batch, parentAlpha);
	}

	private TextureRegion mBackgroundRegion;
}
