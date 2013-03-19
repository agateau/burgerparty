package com.agateau.burgerparty.view;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class SimpleCustomer extends Customer {
	public static final int CUSTOMER_COUNT = 6;
	public SimpleCustomer(TextureAtlas atlas, int id) {
		TextureRegion region = atlas.findRegion("customers/" + id);
		Image main = new Image(region);
		addActor(main);
		setWidth(main.getWidth());
		setHeight(main.getHeight());
	}
}