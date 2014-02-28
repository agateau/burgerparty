package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.Achievement;
import com.agateau.burgerparty.utils.AchievementManager;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.NLog;
import com.agateau.burgerparty.utils.RefreshHelper;
import com.agateau.burgerparty.view.AchievementView;
import com.agateau.burgerparty.view.BurgerPartyUiBuilder;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class AchievementsScreen extends BurgerPartyScreen {
	private static NLog log;

	public AchievementsScreen(BurgerPartyGame game) {
		super(game);
		if (log == null) {
			log = NLog.getRoot().create(getClass().getSimpleName());
		}
		Image bgImage = new Image(getTextureAtlas().findRegion("ui/menu-bg"));
		setBackgroundActor(bgImage);
		setupWidgets();
		new RefreshHelper(getStage()) {
			@Override
			protected void refresh() {
				getGame().showStartScreen();
				dispose();
			}
		};
	}

	@Override
	public void onBackPressed() {
		getGame().showStartScreen();
	}

	private void setupWidgets() {
		BurgerPartyUiBuilder builder = new BurgerPartyUiBuilder(getGame().getAssets());
		builder.build(FileUtils.assets("screens/achievements.gdxui"));
		AnchorGroup root = builder.getActor("root");
		getStage().addActor(root);
		root.setFillParent(true);

		builder.<ImageButton>getActor("backButton").addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				onBackPressed();
			}
		});

		ScrollPane pane = builder.<ScrollPane>getActor("scrollPane");
		VerticalGroup group = new VerticalGroup();
		group.setSpacing(20);
		pane.setWidget(group);
		createAchievementViews(group);
	}

	private void createAchievementViews(VerticalGroup parent) {
		AchievementManager manager = getGame().getGameStats().manager;
		for (Achievement achievement: manager.getAchievements()) {
			AchievementView view = new AchievementView(getGame().getAssets(), achievement);
			parent.addActor(view);
		}
	}
}
