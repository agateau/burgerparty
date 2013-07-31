package com.agateau.burgerparty.tools;

import com.agateau.burgerparty.model.Customer;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.HorizontalGroup;
import com.agateau.burgerparty.utils.StageScreen;
import com.agateau.burgerparty.utils.TiledImage;
import com.agateau.burgerparty.utils.UiUtils;
import com.agateau.burgerparty.view.CustomerView;
import com.agateau.burgerparty.view.CustomerViewFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class CustomerEditorScreen extends StageScreen {

	public CustomerEditorScreen(CustomerEditorGame game, TextureAtlas atlas, Skin skin) {
		super(skin);
		mGame = game;
		mSkin = skin;

		TiledImage bgImage = new TiledImage(atlas.findRegion("ui/menu-bg"));
		setBackgroundActor(bgImage);
		setupWidgets();
		setupInput();
		fillCustomerContainer();
	}

	private void setupWidgets() {
		AnchorGroup group = new AnchorGroup();
		group.setSpacing(UiUtils.SPACING);
		getStage().addActor(group);
		group.setFillParent(true);

		Array<String> keys = mGame.getCustomerFactory().getTypes();
		keys.sort();

		mMoodList = new List(getMoodStrings(), mSkin);
		group.addRule(mMoodList, Anchor.BOTTOM_LEFT, group, Anchor.BOTTOM_LEFT);
		group.addRule(new AnchorGroup.SizeRule(mMoodList, group, 0.1f, 0.3f));

		mCustomerTypeList = new List(keys.toArray(), mSkin);
		group.addRule(new AnchorGroup.SizeRule(mCustomerTypeList, group, 0.1f, 0.6f));
		group.addRule(mCustomerTypeList, Anchor.TOP_LEFT, group, Anchor.TOP_LEFT); //, 0, 1);

		mCustomerContainer = new VerticalGroup();
		ScrollPane pane = new ScrollPane(mCustomerContainer);
		group.addRule(new AnchorGroup.SizeRule(pane, group, 0.9f, 1));
		group.addRule(pane, Anchor.TOP_LEFT, mCustomerTypeList, Anchor.TOP_RIGHT, 1, 0);

		// Connections
		mCustomerTypeList.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				reload();
			}
		});

		mMoodList.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
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

	private static Array<String> sortedArray(Array<String> array) {
		array.sort();
		return array;
	}

	private void fillCustomerContainer() {
		mCustomerContainer.clear();
		mCustomers.clear();
		String type = mCustomerTypeList.getSelection();
		CustomerViewFactory.Elements elements = mGame.getCustomerFactory().getElementsForType(type);
		Customer.Mood mood = getSelectedMood();

		for (String body: sortedArray(elements.bodies)) {
			HorizontalGroup hGroup = new HorizontalGroup();
			mCustomerContainer.addActor(hGroup);
			for (String face: sortedArray(elements.faces)) {
				if (elements.tops.size > 0) {
					for (String top: sortedArray(elements.tops)) {
						addCustomer(hGroup, type, body, top, face, mood);
					}
				} else {
					addCustomer(hGroup, type, body, "", face, mood);
				}
			}

			Label lbl = new Label(body, mSkin);
			mCustomerContainer.addActor(lbl);
		}
		mCustomerContainer.setWidth(mCustomerContainer.getPrefWidth());
		mCustomerContainer.setHeight(mCustomerContainer.getPrefHeight());
	}

	private void addCustomer(WidgetGroup parent, String type, String body, String top, String face, Customer.Mood mood) {
		Customer customer = new Customer(type);
		customer.setMood(mood);
		mCustomers.add(customer);

		CustomerView customerView = new CustomerView(customer, mGame.getCustomerFactory(), type, body, top, face);

		Label lbl = new Label(top + "\n" + face, mSkin);

		VerticalGroup group = new VerticalGroup();
		group.addActor(customerView);
		group.addActor(lbl);

		parent.addActor(group);
	}

	private Customer.Mood getSelectedMood() {
		return Customer.Mood.fromString(mMoodList.getSelection());
	}

	private static String[] getMoodStrings() {
		Customer.Mood[] moods = Customer.Mood.moods;
		String[] moodStrings = new String[moods.length];
		for (int i=0; i < moods.length; ++i) {
			moodStrings[i] = moods[i].toString();
		}
		return moodStrings;
	}

	private CustomerEditorGame mGame;
	private Skin mSkin;

	private List mCustomerTypeList;
	private VerticalGroup mCustomerContainer;
	private List mMoodList;
	private Array<Customer> mCustomers = new Array<Customer>();
}
