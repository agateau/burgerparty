package com.agateau.burgerparty.tools;

import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.HorizontalGroup;
import com.agateau.burgerparty.utils.StageScreen;
import com.agateau.burgerparty.utils.TiledImage;
import com.agateau.burgerparty.utils.UiUtils;
import com.agateau.burgerparty.view.CustomerView;
import com.agateau.burgerparty.view.CustomerViewFactory;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class CustomerEditorScreen extends StageScreen {

	public CustomerEditorScreen(CustomerEditorGame game, TextureAtlas atlas, Skin skin) {
		super(skin);
		mGame = game;
		TiledImage bgImage = new TiledImage(atlas.findRegion("ui/menu-bg"));
		setBackgroundActor(bgImage);
		setupWidgets(atlas, skin);
		setupInput();
		fillCustomerContainer();
	}

	private void setupWidgets(TextureAtlas atlas, Skin skin) {
		AnchorGroup group = new AnchorGroup();
		group.setSpacing(UiUtils.SPACING);
		getStage().addActor(group);
		group.setFillParent(true);

		Array<String> keys = mGame.getCustomerFactory().getTypes();
		keys.sort();

		mCustomerTypeList = new List(keys.toArray(), skin);
		group.addRule(mCustomerTypeList, Anchor.BOTTOM_LEFT, group, Anchor.BOTTOM_LEFT);
		group.addRule(new AnchorGroup.SizeRule(mCustomerTypeList, group, 0.1f, 1));

		mCustomerContainer = new VerticalGroup();
		ScrollPane pane = new ScrollPane(mCustomerContainer);
		group.addRule(pane, Anchor.BOTTOM_LEFT, mCustomerTypeList, Anchor.BOTTOM_RIGHT, 1, 0);
		group.addRule(new AnchorGroup.SizeRule(pane, group, 0.9f, 1));

		mCustomerTypeList.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				fillCustomerContainer();
			}
		});

		TextButton reloadButton = new TextButton("Reload", skin);
		group.addRule(reloadButton, Anchor.BOTTOM_LEFT, group, Anchor.BOTTOM_LEFT);
		reloadButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				reload();
			}
		});
	}

	private void setupInput() {
		getStage().getRoot().addListener(new InputListener() {
			@Override
			public boolean keyUp(InputEvent event, int keycode) {
				if (keycode == Input.Keys.F5) {
					reload();
					return true;
				}
				return false;
			}
		});
	}

	private void reload() {
		mGame.loadPartsXml();
		fillCustomerContainer();
	}

	private CustomerEditorGame mGame;

	private List mCustomerTypeList;
	private VerticalGroup mCustomerContainer;
	
	private void fillCustomerContainer() {
		mCustomerContainer.clear();
		String type = mCustomerTypeList.getSelection();
		CustomerViewFactory.Elements elements = mGame.getCustomerFactory().getElementsForType(type);
		for (String body: elements.bodies) {
			HorizontalGroup hGroup = new HorizontalGroup();
			mCustomerContainer.addActor(hGroup);
			for (String face: elements.faces) {
				if (elements.tops.size > 0) {
					for (String top: elements.tops) {
						addCustomer(hGroup, type, body, top, face);
					}
				} else {
					addCustomer(hGroup, type, body, "", face);
				}
			}
		}
		mCustomerContainer.setWidth(mCustomerContainer.getPrefWidth());
		mCustomerContainer.setHeight(mCustomerContainer.getPrefHeight());
	}

	private void addCustomer(WidgetGroup parent, String type, String body, String top, String face) {
		CustomerView customerView = new CustomerView(mGame.getCustomerFactory(), type, body, top, face);
		float width = 0;
		for(Actor child: customerView.getChildren()) {
			width = Math.max(child.getRight(), width);
		}
		customerView.setWidth(width);
		parent.addActor(customerView);
	}
}
