package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.utils.HorizontalGroup;
import com.agateau.burgerparty.utils.Signal1;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class WorldListView extends HorizontalGroup {
	public Signal1<Integer> currentIndexChanged = new Signal1<Integer>();

	public WorldListView(Array<LevelWorld> worlds, int currentIndex, Assets assets) {
		mAssets = assets;
		mCurrentIndex = currentIndex;
		setSpacing(UiUtils.SPACING);

		int idx = 0;
		for (LevelWorld world: worlds) {
			Actor levelButton = createWorldButton(world, idx);
			addActor(levelButton);
			++idx;
		}
	}

	public void addActor(Actor actor) {
		super.addActor(actor);
		setWidth(getPrefWidth());
	}

	private static class WorldButton extends WorldBaseButton {
		public WorldButton(String text, String dirName, Assets assets) {
			super(text, dirName + "preview", assets);
		}
		public int mIndex;
	}

	private Actor createWorldButton(LevelWorld world, int index) {
		String text = String.valueOf(index + 1);
		if (index == mCurrentIndex) {
			text = "> " + text + " <";
		}
		WorldListView.WorldButton button = new WorldButton(text, world.getDirName(), mAssets);
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