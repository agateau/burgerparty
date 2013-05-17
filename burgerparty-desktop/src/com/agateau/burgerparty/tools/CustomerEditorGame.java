package com.agateau.burgerparty.tools;

import com.agateau.burgerparty.view.CustomerFactory;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class CustomerEditorGame extends Game {
	CustomerEditorGame(String partsXmlName) {
		mPartsXmlName = partsXmlName;
	}

	@Override
	public void create() {
		mAtlas = new TextureAtlas(Gdx.files.internal("burgerparty.atlas"));
		mSkin = new Skin(Gdx.files.internal("ui/skin.json"), mAtlas);
		loadPartsXml();
		showCustomerEditorScreen();
	}

	private void showCustomerEditorScreen() {
		setScreen(new CustomerEditorScreen(this, mAtlas, mSkin));
	}

	public CustomerFactory getCustomerFactory() {
		return mCustomerFactory;
	}

	public void loadPartsXml() {
		System.out.println("Loading " + mPartsXmlName);
		FileHandle handle = Gdx.files.absolute(mPartsXmlName);
		mCustomerFactory = new CustomerFactory(mAtlas, handle);
	}

	private Skin mSkin;
	private TextureAtlas mAtlas;
	private String mPartsXmlName;
	private CustomerFactory mCustomerFactory;
}
