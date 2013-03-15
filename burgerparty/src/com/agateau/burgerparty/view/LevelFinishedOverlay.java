package com.agateau.burgerparty.view;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.World;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class LevelFinishedOverlay extends Overlay {
	private BurgerPartyGame mGame;
	public LevelFinishedOverlay(BurgerPartyGame game, World world, TextureAtlas atlas, Skin skin) {
		super(atlas);
		mGame = game;
		AnchorGroup group = new AnchorGroup();
		group.setSpacing(20);
		group.setFillParent(true);
		addActor(group);

		Label label = new Label("", skin);

		TextButton nextButton = null;

		TextButton menuButton = new TextButton("Return to menu", skin);
		UiUtils.setButtonSize(menuButton);
		menuButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mGame.showMenu();
			}
		});

		int levelIndex = mGame.getLevelIndex(); 
		if (levelIndex < mGame.getLevelCount() - 1) {
			label.setText("Congratulations, you finished level " + (levelIndex + 1) + "!");
			nextButton = new TextButton("Next Level", skin);
			UiUtils.setButtonSize(nextButton);
			nextButton.addListener(new ChangeListener() {
				public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
					mGame.startLevel(mGame.getLevelIndex() + 1);
				}
			});
		} else {
			label.setText("Congratulations, you finished the game!");
		}
		UiUtils.adjustToPrefSize(label);

		group.moveActor(label, Anchor.BOTTOM_CENTER, this, Anchor.CENTER, 0, 1);
		if (nextButton == null) {
			group.moveActor(menuButton, Anchor.TOP_CENTER, this, Anchor.CENTER, 0, 0);
		} else {
			group.moveActor(nextButton, Anchor.TOP_CENTER, this, Anchor.CENTER, 0, 0);
			group.moveActor(menuButton, Anchor.TOP_CENTER, nextButton, Anchor.BOTTOM_CENTER, 0, -1);
		}
	}
}
