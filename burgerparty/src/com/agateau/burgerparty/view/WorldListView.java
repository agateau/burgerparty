package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.model.Level;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.HorizontalGroup;
import com.agateau.burgerparty.utils.Signal1;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class WorldListView extends HorizontalGroup {
	public Signal1<Integer> currentIndexChanged = new Signal1<Integer>();

	public enum Details {
		SHOW_STARS,
		HIDE_STARS
	}

	public WorldListView(Array<LevelWorld> worlds, int currentIndex, Assets assets, Details details) {
		mAssets = assets;
		mCurrentIndex = currentIndex;
		setSpacing(UiUtils.SPACING);

		int idx = 0;
		for (LevelWorld world: worlds) {
			boolean locked = world.getLevel(0).score == Level.SCORE_LOCKED;
			Actor levelButton = createWorldButton(world, idx, locked, details);
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

	private Actor createWorldButton(LevelWorld world, int index, boolean locked, Details details) {
		String text = locked ? "" : String.valueOf(index + 1);
		if (index == mCurrentIndex) {
			text = "> " + text + " <";
		}
		WorldListView.WorldButton button = new WorldButton(text, world.getDirName(), mAssets);
		if (locked) {
			button.createLockOverlay();
		} else if (details == Details.SHOW_STARS) {
			createStarsActor(button.getGroup(), world);
		}
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

	private void createStarsActor(AnchorGroup group, LevelWorld world) {
		int wonStarCount = world.getWonStarCount();
		int totalStarCount = world.getTotalStarCount();

		Image image = new Image(mAssets.getTextureAtlas().findRegion("ui/star-on"));
		image.setScale(0.8f);

		String text = " " + wonStarCount + "/" + totalStarCount;
		Label label = new Label(text, mAssets.getSkin(), "world-button-star-text");

		group.addRule(image, Anchor.BOTTOM_LEFT, group, Anchor.BOTTOM_LEFT, 13, 14);
		group.addRule(label, Anchor.BOTTOM_LEFT, image, Anchor.BOTTOM_RIGHT, 0, -6);
	}

	private Assets mAssets;
	private int mCurrentIndex;
}