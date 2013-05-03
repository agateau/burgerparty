package com.agateau.burgerparty.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;

public class ComposableCustomerFactory {
	static class CustomerCategory {
		public String dirName;
		public Array<String> bodies = new Array<String>();
		public Array<String> tops = new Array<String>();
		public Array<String> faces = new Array<String>();

		public CustomerCategory(String dirName) {
			this.dirName = dirName;
		} 
	}

	private OrderedMap<String, CustomerCategory> mCategories = new OrderedMap<String, CustomerCategory>();
	private TextureAtlas mAtlas;

	public ComposableCustomerFactory(TextureAtlas atlas) {
		mAtlas = atlas;
		for(TextureAtlas.AtlasRegion region: mAtlas.getRegions()) {
			String[] path = region.name.split("/", 3);
			if (!path[0].equals("customers")) {
				continue;
			}
			if (path.length < 3) {
				Gdx.app.log("ComposableCustomerFactory", "Skipping " + region.name + ". Should not exist!");
				continue;
			}
			String categoryName = path[1];
			CustomerCategory category;
			category = mCategories.get(categoryName);
			if (category == null) {
				category = new CustomerCategory(categoryName);
				mCategories.put(categoryName, category);
			}
			String name = path[2];
			if (name.startsWith("body-")) {
				category.bodies.add(name);
			} else if (name.startsWith("top-")) {
				category.tops.add(name);
			} else if (name.startsWith("face-")) {
				String[] tokens = name.split("-", 3);
				if (tokens.length != 3) {
					Gdx.app.log("ComposableCustomerFactory", "Skipping " + region.name + ". Invalid face name!");
					continue;
				}
				if (tokens[2].equals("happy")) {
					category.faces.add(tokens[0] + "-" + tokens[1]);
				}
			} else {
				Gdx.app.log("ComposableCustomerFactory", "Skipping " + region.name + ". Unknown customer part!");
			}
		}
	}

	static String getRandomString(Array<String> array) {
		if (array.size > 0) {
			return array.get(MathUtils.random(array.size - 1));
		} else {
			return "";
		}
	}

	public Customer create(String categoryName) {
		CustomerCategory category = mCategories.get(categoryName);
		return new ComposableCustomer(mAtlas, category.dirName,
			getRandomString(category.bodies),
			getRandomString(category.tops),
			getRandomString(category.faces));
	}
}
