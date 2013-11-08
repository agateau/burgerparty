package com.agateau.burgerparty.screens;

import java.util.HashSet;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.Signal1;
import com.agateau.burgerparty.view.BurgerPartyUiBuilder;
import com.agateau.burgerparty.view.WorldBaseButton;
import com.agateau.burgerparty.view.WorldListView;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.XmlReader;

public class WorldListScreen extends BurgerPartyScreen {
	public WorldListScreen(BurgerPartyGame game) {
		super(game);
		Image bgImage = new Image(getTextureAtlas().findRegion("ui/menu-bg"));
		setBackgroundActor(bgImage);
		setupWidgets();
	}

	@Override
	public void onBackPressed() {
		getGame().showStartScreen();
	}

	private class SandBoxButton extends WorldBaseButton {
		public SandBoxButton() {
			super("", "sandbox-preview", getGame().getAssets());
			addListener(new ChangeListener() {
				public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
					getGame().startSandBox();
				}
			});
		}
	}

	private class Builder extends BurgerPartyUiBuilder {
		public Builder(Assets assets) {
			super(assets);
		}

		@Override
		protected Actor createActorForElement(XmlReader.Element element) {
			if (element.getName().equals("WorldListView")) {
				WorldListView view = new WorldListView(getGame().getLevelWorlds(), -1, getGame().getAssets(), WorldListView.Details.SHOW_STARS);
				view.addActor(new SandBoxButton());
				return view;
			}
			return super.createActorForElement(element);
		}
	}

	private void setupWidgets() {
		BurgerPartyUiBuilder builder = new Builder(getGame().getAssets());
		builder.build(Gdx.files.internal("screens/worldlist.gdxui"));
		AnchorGroup root = builder.getActor("root");
		getStage().addActor(root);
		root.setFillParent(true);

		builder.<ImageButton>getActor("backButton").addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				onBackPressed();
			}
		});

		builder.<WorldListView>getActor("worldListView").currentIndexChanged.connect(mHandlers, new Signal1.Handler<Integer>() {
			@Override
			public void handle(Integer index) {
				getGame().showLevelListScreen(index);
			}
		});
	}

	private HashSet<Object> mHandlers = new HashSet<Object>();
}
