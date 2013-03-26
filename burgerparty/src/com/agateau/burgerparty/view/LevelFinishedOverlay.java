package com.agateau.burgerparty.view;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.LevelFinishedSummary;
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
	public LevelFinishedOverlay(BurgerPartyGame game, LevelFinishedSummary summary, TextureAtlas atlas, Skin skin) {
		super(atlas);
		mGame = game;
		AnchorGroup group = new AnchorGroup();
		group.setSpacing(UiUtils.SPACING);
		group.setFillParent(true);
		addActor(group);

		Label mainLabel = new Label("", skin);

		Label summaryLabel = new Label(createSummaryText(summary), skin);

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
			mainLabel.setText("Congratulations, you finished level " + (levelIndex + 1) + "!");
			nextButton = new TextButton("Next Level", skin);
			UiUtils.setButtonSize(nextButton);
			nextButton.addListener(new ChangeListener() {
				public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
					mGame.startLevel(mGame.getLevelIndex() + 1);
				}
			});
		} else {
			mainLabel.setText("Congratulations, you finished the game!");
		}
		UiUtils.adjustToPrefSize(mainLabel);

		group.moveActor(summaryLabel, Anchor.BOTTOM_CENTER, this, Anchor.CENTER, 0, 1);
		group.moveActor(mainLabel, Anchor.BOTTOM_CENTER, summaryLabel, Anchor.TOP_CENTER, 0, 1);
		if (nextButton == null) {
			group.moveActor(menuButton, Anchor.TOP_CENTER, this, Anchor.CENTER, 0, 0);
		} else {
			group.moveActor(nextButton, Anchor.TOP_CENTER, this, Anchor.CENTER, 0, 0);
			group.moveActor(menuButton, Anchor.TOP_CENTER, nextButton, Anchor.BOTTOM_CENTER, 0, -1);
		}
	}

	String createSummaryText(LevelFinishedSummary summary) {
		String txt;
		txt = "- Trashed burgers: " + summary.trashedCount + ". Maximum allowed: " + summary.level.definition.maxThrashed + ". ";
		if (summary.trashedCount <= summary.level.definition.maxThrashed) {
			txt += "OK!";
		} else {
			txt += "Fail";
		}
		txt += "\n";

		txt += "- Time spent: " + summary.duration + " seconds.\n";
		txt += "\n";

		for (int idx = 0; idx < summary.stars; ++idx) {
			txt += "*";
		}
		return txt;
	}
}
