package com.agateau.burgerparty.tools;

import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.StageScreen;
import com.agateau.burgerparty.utils.TiledImage;
import com.agateau.burgerparty.utils.UiUtils;
import com.agateau.burgerparty.view.ComposableCustomer;
import com.agateau.burgerparty.view.ComposableCustomerFactory;
import com.agateau.burgerparty.view.Customer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;

public class CustomerEditorScreen extends StageScreen {

	public CustomerEditorScreen(CustomerEditorGame game, TextureAtlas atlas, Skin skin) {
		super(skin);
		mAtlas = atlas;
		mCustomerFactory = new ComposableCustomerFactory(atlas);
		TiledImage bgImage = new TiledImage(atlas.findRegion("ui/menu-bg"));
		setBackgroundActor(bgImage);
		setupWidgets(atlas, skin);
		fillCustomerTable();
	}

	private void setupWidgets(TextureAtlas atlas, Skin skin) {
		AnchorGroup group = new AnchorGroup();
		group.setSpacing(UiUtils.SPACING);
		getStage().addActor(group);
		group.setFillParent(true);

		Array<String> keys = mCustomerFactory.getTypes();
		keys.sort();

		mCustomerTypeList = new List(keys.toArray(), skin);
		group.addRule(mCustomerTypeList, Anchor.BOTTOM_LEFT, group, Anchor.BOTTOM_LEFT);
		group.addRule(new AnchorGroup.SizeRule(mCustomerTypeList, group, 0.2f, 1));

		mCustomerContainer = new HGroup();
		ScrollPane pane = new ScrollPane(mCustomerContainer);
		group.addRule(pane, Anchor.BOTTOM_LEFT, mCustomerTypeList, Anchor.BOTTOM_RIGHT, 1, 0);
		group.addRule(new AnchorGroup.SizeRule(pane, group, 0.8f, 1));

		mCustomerTypeList.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				fillCustomerTable();
			}
		});
	}

	private TextureAtlas mAtlas;
	private ComposableCustomerFactory mCustomerFactory;

	private List mCustomerTypeList;
	private HGroup mCustomerContainer;
	
	static class HGroup extends WidgetGroup {
		private boolean mSizeInvalid = true;
		private float mPrefWidth, mPrefHeight;
		private int mAlignment = Align.top;

		public void invalidate() {
			super.invalidate();
			mSizeInvalid = true;
		}

		private void computeSize() {
			mSizeInvalid = false;
			mPrefWidth = 0;
			mPrefHeight = 0;
			SnapshotArray<Actor> children = getChildren();
			for (int i = 0, n = children.size; i < n; i++) {
				Actor child = children.get(i);
				/*if (child instanceof Layout) {
					Layout layout = (Layout)child;
					mPrefWidth += layout.getPrefWidth();
					mPrefHeight = Math.max(mPrefHeight, layout.getPrefHeight());
				} else*/ {
					mPrefWidth += child.getWidth();
					mPrefHeight = Math.max(mPrefHeight, child.getHeight());
				}
			}
		}

		public void layout() {
			float groupHeight = getHeight();
			float x = 0;
			SnapshotArray<Actor> children = getChildren();
			for (int i = 0, n = children.size; i < n; i++) {
				Actor child = children.get(i);
				float width, height;
				/*if (child instanceof Layout) {
					Layout layout = (Layout)child;
					width = layout.getPrefWidth();
					height = layout.getPrefHeight();
				} else*/ {
					width = child.getWidth();
					height = child.getHeight();
				}
				Gdx.app.log("hgroup", "actor " + i + " width=" + width);
				float y;
				if ((mAlignment & Align.bottom) != 0) {
					y = 0;
				} else if ((mAlignment & Align.top) != 0) {
					y = groupHeight - height;
				} else {
					y = (groupHeight - height) / 2;
				}
				child.setBounds(x, y, width, height);
				x += width;
			}
		}

		public float getPrefWidth() {
			if (mSizeInvalid) {
				computeSize();
			}
			return mPrefWidth;
		}

		public float getPrefHeight() {
			if (mSizeInvalid) {
				computeSize();
			}
			return mPrefHeight;
		}
	}

	private void fillCustomerTable() {
		mCustomerContainer.clear();
		String type = mCustomerTypeList.getSelection();
		ComposableCustomerFactory.Elements elements = mCustomerFactory.getElementsForType(type);
		for (String body: elements.bodies) {
			for (String face: elements.faces) {
				if (elements.tops.size > 0) {
					for (String top: elements.tops) {
						addCustomer(type, body, top, face);
					}
				} else {
					addCustomer(type, body, "", face);
				}
			}
		}
		mCustomerContainer.setWidth(mCustomerContainer.getPrefWidth());
		mCustomerContainer.setHeight(mCustomerContainer.getPrefHeight());
		Gdx.app.log("size", "width:" + mCustomerContainer.getWidth());
	}

	private void addCustomer(String type, String body, String top, String face) {
		Customer customer = new ComposableCustomer(mAtlas, type, body, top, face);
		mCustomerContainer.addActor(customer);
	}
}
