package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.model.Achievement;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class AchievementView extends Label {

	public AchievementView(Assets assets, Achievement achievement) {
		super("", assets.getSkin(), "default");
		String text = achievement.getTitle() + "\n" + achievement.getDescription();
		setText(text);
		pack();
	}

}
