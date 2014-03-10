package com.agateau.burgerparty.view;

import java.util.HashSet;

import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.screens.BurgerPartyScreen;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.Overlay;
import com.agateau.burgerparty.utils.Signal1;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class WorldListOverlay extends Overlay {
	public Signal1<Integer> currentIndexChanged = new Signal1<Integer>();

	private final HashSet<Object> mHandlers = new HashSet<Object>();
	private final BurgerPartyScreen mScreen;
	private final Array<LevelWorld> mWorlds;
	private int mCurrentIndex;

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

	private void setupWidgets() {
		ImageButton backButton = Kernel.createRoundButton(mScreen.getGame().getAssets(), "ui/icon-back");
		backButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				close();
			}
		});

		WorldListView worldListView = new WorldListView(mWorlds, mCurrentIndex, mScreen.getGame().getAssets(), WorldListView.Details.HIDE_STARS);
		worldListView.currentIndexChanged.connect(mHandlers, new Signal1.Handler<Integer>() {
			@Override
			public void handle(Integer index) {
				close();
				currentIndexChanged.emit(index);
			}
		});

		AnchorGroup group = new AnchorGroup();
		addActor(group);
		group.setFillParent(true);
		group.setSpacing(UiUtils.SPACING);

		group.addRule(backButton, Anchor.BOTTOM_LEFT, group, Anchor.BOTTOM_LEFT, 1, 1);
		group.addRule(worldListView, Anchor.TOP_CENTER, group, Anchor.TOP_CENTER, 0, -1);
	}
}
