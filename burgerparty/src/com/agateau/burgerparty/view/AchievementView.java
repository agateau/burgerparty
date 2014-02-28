package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.utils.Achievement;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class AchievementView extends AnchorGroup {
	private static final float WIDTH = 600;

	public AchievementView(Assets assets, Achievement achievement) {
		setSpacing(4);
		Image bg = new Image(assets.getTextureAtlas().createPatch("ui/achievement-bg"));
		bg.setWidth(WIDTH);
		addActor(bg);
		setSize(bg.getWidth(), bg.getHeight());

		// FIXME: Get icon name from Achievement class
		Image icon = new Image(assets.getTextureAtlas().findRegion("ui/achievements/burger-master"));
		Label titleLabel = new Label(achievement.getTitle(), assets.getSkin(), "achievement-title");
		Label descriptionLabel = new Label(achievement.getDescription(), assets.getSkin(), "achievement-description");

		addRule(icon, Anchor.CENTER_LEFT, this, Anchor.CENTER_LEFT, 1, 0);
		addRule(titleLabel, Anchor.BOTTOM_LEFT, icon, Anchor.CENTER_RIGHT, 1, -2.5f);
		addRule(descriptionLabel, Anchor.TOP_LEFT, icon, Anchor.CENTER_RIGHT, 1, 0.5f);

		if (achievement.isUnlocked()) {
			Image unlocked = new Image(assets.getTextureAtlas().findRegion("ui/achievement-unlocked"));
			addRule(unlocked, Anchor.CENTER_RIGHT, this, Anchor.CENTER_RIGHT, -1, 0);
		}
	}

}
