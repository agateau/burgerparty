package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.screens.BurgerPartyScreen;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.GridGroup;
import com.agateau.burgerparty.utils.Overlay;
import com.agateau.burgerparty.utils.Signal1;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class WorldListOverlay extends Overlay {
	public Signal1<Integer> currentIndexChanged = new Signal1<Integer>();
	
	private static final int COL_COUNT = 2;
	private static final int CELL_SIZE = 150;

	public WorldListOverlay(BurgerPartyScreen screen, Array<LevelWorld> worlds, int currentIndex) {
		super(screen.getGame().getAssets().getTextureAtlas());
		mScreen = screen;
		mWorlds = worlds;
		mCurrentIndex = currentIndex;

		setupWidgets();
	}

	@Override
	public void onBackPressed() {
		close();
	}

	private void close() {
		mScreen.setOverlay(null);
	}

	private static class WorldButton extends TextButton {
		public WorldButton(String text, Skin skin) {
			super(text, skin, "level-button");
		}
		public int mIndex;
	}

	private Actor createWorldButton(LevelWorld world, int index) {
		String text = String.valueOf(index + 1);
		if (index == mCurrentIndex) {
			text = "> " + text + " <";
		}
		WorldButton button = new WorldButton(text, mScreen.getSkin());
		button.mIndex = index;
		button.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mScreen.getGame().getAssets().getSoundAtlas().findSound("click").play();
				WorldButton button = (WorldButton)actor;
				currentIndexChanged.emit(button.mIndex);
				close();
			}
		});

		return button;
	}

	private void setupWidgets() {
		ImageButton backButton = Kernel.createRoundButton("ui/icon-back");
		backButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				close();
			}
		});

		GridGroup gridGroup = new GridGroup();
		gridGroup.setSpacing(UiUtils.SPACING);
		gridGroup.setColumnCount(COL_COUNT);
		gridGroup.setCellSize(CELL_SIZE, CELL_SIZE);

		int idx = 0;
		for (LevelWorld world: mWorlds) {
			Actor levelButton = createWorldButton(world, idx);
			gridGroup.addActor(levelButton);
			++idx;
		}

		AnchorGroup group = new AnchorGroup();
		addActor(group);
		group.setFillParent(true);
		group.setSpacing(UiUtils.SPACING);

		group.addRule(backButton, Anchor.BOTTOM_LEFT, group, Anchor.BOTTOM_LEFT, 1, 1);
		group.addRule(gridGroup, Anchor.TOP_CENTER, group, Anchor.TOP_CENTER, 0, -1);
	}

	private final BurgerPartyScreen mScreen;
	private final Array<LevelWorld> mWorlds;
	private int mCurrentIndex;
}
