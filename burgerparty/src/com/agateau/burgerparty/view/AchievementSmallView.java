package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.utils.Achievement;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class AchievementSmallView extends AnchorGroup {
	public static final float PADDING = 12;

	public AchievementSmallView(Assets assets, Achievement achievement) {
		TextureAtlas atlas = assets.getTextureAtlas();
		// Create a new patch so we can remove the top border
		NinePatch patch = new NinePatch(atlas.createPatch("ui/achievement-bg"));
		patch.setTopHeight(0);
		Image bg = new Image(patch);
		bg.setFillParent(true);
		addActor(bg);

		TextureRegion iconRegion = atlas.findRegion("achievements/" + achievement.getId());
		if (iconRegion == null) {
			iconRegion = atlas.findRegion("achievements/generic");
		}

		Image icon = new Image(iconRegion);
		icon.setScale(0.5f);
		Label titleLabel = new Label(achievement.getTitle(), assets.getSkin(), "achievement-title");

		addRule(icon, Anchor.BOTTOM_LEFT, this, Anchor.BOTTOM_LEFT, PADDING, PADDING);
		addRule(titleLabel, Anchor.CENTER_LEFT, icon, Anchor.CENTER_RIGHT, PADDING / 2, 0);
		setSize(
			icon.getWidth() * icon.getScaleX() + PADDING / 2 + titleLabel.getPrefWidth() + 2 * PADDING,
			icon.getHeight() * icon.getScaleY() + 1.5f * PADDING);
		layout();
	}
}
