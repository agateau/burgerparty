package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.utils.Achievement;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class AchievementView extends AnchorGroup {
	private static final float WIDTH = 600;

	public AchievementView(Assets assets, Achievement achievement) {
		TextureAtlas atlas = assets.getTextureAtlas();
		Image bg = new Image(atlas.createPatch("ui/achievement-bg"));
		bg.setWidth(WIDTH);
		addActor(bg);
		setSize(bg.getWidth(), bg.getHeight());

		TextureRegion iconRegion = atlas.findRegion("achievements/" + achievement.getId());
		if (iconRegion == null) {
			iconRegion = atlas.findRegion("achievements/generic");
		}

		TextureRegion statusRegion = atlas.findRegion("ui/achievement-" + (achievement.isUnlocked() ? "unlocked" : "locked"));

		Image icon = new Image(iconRegion);
		Label titleLabel = new Label(achievement.getTitle(), assets.getSkin(), "achievement-title");
		Label descriptionLabel = new Label(achievement.getDescription(), assets.getSkin(), "achievement-description");

		Image status = new Image(statusRegion);

		addRule(icon, Anchor.CENTER_LEFT, this, Anchor.CENTER_LEFT, 6, 0);
		addRule(titleLabel, Anchor.BOTTOM_LEFT, icon, Anchor.CENTER_RIGHT, 4, -10f);
		addRule(descriptionLabel, Anchor.TOP_LEFT, icon, Anchor.CENTER_RIGHT, 4, 2f);
		addRule(status, Anchor.CENTER_RIGHT, this, Anchor.CENTER_RIGHT, -12, 0);
	}

}
