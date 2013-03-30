package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.GridGroup;
import com.agateau.burgerparty.utils.TiledImage;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class LevelListScreen extends BaseScreen {
	static private int COL_COUNT = 3;
	private TextureRegion mStarOff;
	private TextureRegion mStarOn;
	private TextureRegion mLock;

	public LevelListScreen(BurgerPartyGame game, TextureAtlas atlas, Skin skin) {
		super(game, skin);
		TiledImage bgImage = new TiledImage(atlas.findRegion("ui/menu-bg"));
		setBackgroundActor(bgImage);

		mStarOff = atlas.findRegion("star-empty");
		mStarOn = atlas.findRegion("star");
		mLock = atlas.findRegion("lock");
		setupWidgets(skin);
	}

	private void setupWidgets(Skin skin) {
		AnchorGroup group = new AnchorGroup();
		group.setSpacing(UiUtils.SPACING);
		getStage().addActor(group);
		group.setFillParent(true);

		TextButton backButton = new TextButton("<- Back", skin);
		backButton.setSize(backButton.getPrefWidth(), UiUtils.BUTTON_HEIGHT);
		group.addRule(backButton, Anchor.BOTTOM_LEFT, group, Anchor.BOTTOM_LEFT, 1, 1);
		backButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				getGame().showMenu();
			}
		});

		GridGroup gridGroup = new GridGroup();
		gridGroup.setSpacing(UiUtils.SPACING);
		gridGroup.setColumnCount(COL_COUNT);
		gridGroup.setCellSize(150, 150);
		group.addRule(gridGroup, Anchor.TOP_CENTER, group, Anchor.TOP_CENTER, 0, -1);

		for (int idx=0; idx < getGame().getLevelCount(); idx++) {
			Actor levelButton = createLevelButton(idx, skin);
			gridGroup.addActor(levelButton);
		}
	}

	class LevelButton extends TextButton {
		public LevelButton(int idx, int score, Skin skin) {
			super(String.valueOf(idx + 1), skin);
			AnchorGroup group = new AnchorGroup();
			addActor(group);
			group.setFillParent(true);

			if (score >= 0) {
				Actor prev = null;
				for (int x = 1; x <= 3; ++x) {
					Image image = new Image(x > score ? mStarOff : mStarOn);
					if (prev == null) {
						group.addRule(image, Anchor.BOTTOM_LEFT, group, Anchor.BOTTOM_LEFT);
					} else {
						group.addRule(image, Anchor.BOTTOM_LEFT, prev, Anchor.BOTTOM_RIGHT);
					}
					prev = image;
				}
			} else {
				setDisabled(true);
				Image image = new Image(mLock);
				group.addRule(image, Anchor.BOTTOM_RIGHT, group, Anchor.BOTTOM_RIGHT);
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
