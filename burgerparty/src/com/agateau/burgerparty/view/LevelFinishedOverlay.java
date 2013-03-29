package com.agateau.burgerparty.view;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.LevelResult;
import com.agateau.burgerparty.model.ObjectiveResult;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.tablelayout.BaseTableLayout;

public class LevelFinishedOverlay extends Overlay {
	private BurgerPartyGame mGame;
	private Array<TextureRegion> mStars = new Array<TextureRegion>();
	public LevelFinishedOverlay(BurgerPartyGame game, LevelResult result, TextureAtlas atlas, Skin skin) {
		super(atlas);
		mGame = game;
		mStars.add(atlas.findRegion("star-empty"));
		mStars.add(atlas.findRegion("star"));

		AnchorGroup group = new AnchorGroup();
		group.setSpacing(UiUtils.SPACING);
		group.setFillParent(true);
		addActor(group);

		Label mainLabel = new Label("", skin);

		Actor resultActor = createDetailedResultActor(result, skin);

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

		group.addRule(resultActor, Anchor.BOTTOM_CENTER, this, Anchor.CENTER, 0, 0);
		group.addRule(mainLabel, Anchor.BOTTOM_CENTER, resultActor, Anchor.TOP_CENTER, 0, 1);
		if (nextButton == null) {
			group.addRule(menuButton, Anchor.TOP_CENTER, resultActor, Anchor.BOTTOM_CENTER, 0, -1);
		} else {
			group.addRule(nextButton, Anchor.TOP_CENTER, resultActor, Anchor.BOTTOM_CENTER, 0, -1);
			group.addRule(menuButton, Anchor.TOP_CENTER, nextButton, Anchor.BOTTOM_CENTER, 0, -1);
		}
	}

	private void addDetailToTable(Table table, String text, boolean on) {
		table.add(text)
			.align(BaseTableLayout.CENTER | BaseTableLayout.LEFT)
			.pad(UiUtils.SPACING);

		Image image = new Image(mStars.get(on ? 1 : 0));
		table.add(image);

		table.row();
	}

	private Actor createDetailedResultActor(LevelResult levelResult, Skin skin) {
		Table table = new Table(skin);

		addDetailToTable(table, "Finished Level", true);
		for(ObjectiveResult result: levelResult.getObjectiveResults()) {
			addDetailToTable(table, result.description, result.success);
		}
		table.setSize(table.getPrefWidth(), table.getPrefHeight());
		return table;
	}
}
