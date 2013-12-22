package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.RefreshHelper;
import com.agateau.burgerparty.view.BurgerPartyUiBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class AboutScreen extends BurgerPartyScreen {
	private static final float PIXEL_PER_SECOND = 48;

	public AboutScreen(BurgerPartyGame game) {
		super(game);
		Image bgImage = new Image(getTextureAtlas().findRegion("ui/menu-bg"));
		setBackgroundActor(bgImage);
		setupWidgets();
		new RefreshHelper(getStage()) {
			@Override
			protected void refresh() {
				getGame().showAboutScreen();
				dispose();
			}
		};
	}

	private void setupWidgets() {
		BurgerPartyUiBuilder builder = new BurgerPartyUiBuilder(getGame().getAssets());
		builder.build(FileUtils.assets("screens/about.gdxui"));
		AnchorGroup root = builder.getActor("root");
		getStage().addActor(root);
		root.setFillParent(true);

		builder.<ImageButton>getActor("backButton").addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				onBackPressed();
			}
		});
		
		mScrollPane = builder.<ScrollPane>getActor("scrollPane");

		String aboutText = FileUtils.assets("about.txt").readString("utf-8");

		Label label = builder.<Label>getActor("bodyLabel");
		label.setText(aboutText);
		label.pack();
	}

	@Override
	public void onBackPressed() {
		getGame().showStartScreen();
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		if (Gdx.input.isTouched()) {
			return;
		}
		float maxY = mScrollPane.getWidget().getHeight();
		float y = mScrollPane.getScrollY();
		if (y < maxY) {
			mScrollPane.setScrollY(y + PIXEL_PER_SECOND * delta);
		}
	}

	private ScrollPane mScrollPane;
}
