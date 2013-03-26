package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.GridGroup;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class LevelListScreen extends BaseScreen {
	static private int COL_COUNT = 3;

	public LevelListScreen(BurgerPartyGame game, Skin skin) {
		super(game, skin);
		setupWidgets(skin);
	}

	private void setupWidgets(Skin skin) {
		AnchorGroup group = new AnchorGroup();
		group.setSpacing(UiUtils.SPACING);
		getStage().addActor(group);
		group.setFillParent(true);

		TextButton backButton = new TextButton("<- Back", skin);
		backButton.setSize(backButton.getPrefWidth(), UiUtils.BUTTON_HEIGHT);
		group.moveActor(backButton, Anchor.BOTTOM_LEFT, group, Anchor.BOTTOM_LEFT, 1, 1);
		backButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				getGame().showMenu();
			}
		});

		GridGroup gridGroup = new GridGroup();
		gridGroup.setSpacing(UiUtils.SPACING);
		gridGroup.setColumnCount(COL_COUNT);
		gridGroup.setCellSize(150, 150);
		group.moveActor(gridGroup, Anchor.TOP_CENTER, group, Anchor.TOP_CENTER, 0, -1);

		for (int idx=0; idx < getGame().getLevelCount(); idx++) {
			Actor levelButton = createLevelButton(idx, skin);
			gridGroup.addActor(levelButton);
		}
	}

	class LevelButton extends TextButton {
		public LevelButton(int idx, int score, Skin skin) {
			super("", skin);
			if (score >= 0) {
				setText(String.format("LVL %d: %d", idx + 1, score));
			} else {
				setText(String.format("[LVL %d]", idx + 1));
				setDisabled(true);
			}
			this.idx = idx;
		}
		public int idx;
	}
	private Actor createLevelButton(int idx, Skin skin) {
		int score = getGame().getLevelStars(idx);
		LevelButton button = new LevelButton(idx, score, skin);
		button.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				LevelButton button = (LevelButton)actor;
				getGame().startLevel(button.idx);
			}
		});

		return button;
	}
}
