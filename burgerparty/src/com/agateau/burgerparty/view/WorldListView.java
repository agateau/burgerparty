package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.utils.GridGroup;
import com.agateau.burgerparty.utils.Signal1;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class WorldListView extends GridGroup {
	private static final int COL_COUNT = 2;
	private static final int CELL_SIZE = 150;

	public Signal1<Integer> currentIndexChanged = new Signal1<Integer>();

	public WorldListView(Array<LevelWorld> worlds, int currentIndex, Assets assets) {
		mAssets = assets;
		mCurrentIndex = currentIndex;
		setSpacing(UiUtils.SPACING);
		setColumnCount(COL_COUNT);
		setCellSize(CELL_SIZE, CELL_SIZE);

		int idx = 0;
		for (LevelWorld world: worlds) {
			Actor levelButton = createWorldButton(world, idx);
			addActor(levelButton);
			++idx;
		}
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
		WorldListView.WorldButton button = new WorldButton(text, mAssets.getSkin());
		button.mIndex = index;
		button.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mAssets.getSoundAtlas().findSound("click").play();
				WorldListView.WorldButton button = (WorldListView.WorldButton)actor;
				currentIndexChanged.emit(button.mIndex);
			}
		});

		return button;
	}

	private Assets mAssets;
	private int mCurrentIndex;
}