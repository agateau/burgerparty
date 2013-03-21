package com.agateau.burgerparty.view;

import java.util.TreeMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class ComposableCustomerFactory {
	static class CustomerCategory {
		public String dirName;
		public int bodyCount;
		public int topCount;
		public int faceCount;

		public CustomerCategory(String dirName) {
			this.dirName = dirName;
			this.bodyCount = 0;
			this.topCount = 0;
			this.faceCount = 0;
		} 
	}

	private Array<CustomerCategory> mCategories = new Array<CustomerCategory>();
	private TextureAtlas mAtlas;

	public ComposableCustomerFactory(TextureAtlas atlas) {
		mAtlas = atlas;
		TreeMap<String, CustomerCategory> categoryMap = new TreeMap<String, CustomerCategory>();
		for(TextureAtlas.AtlasRegion region: mAtlas.getRegions()) {
			String[] tokens = region.name.split("/", 3);
			if (!tokens[0].equals("customers")) {
				continue;
			}
			if (tokens.length < 3) {
				Gdx.app.log("ComposableCustomerFactory", "Skipping " + region.name + ". Should not exist!");
				continue;
			}
			String categoryName = tokens[1];
			CustomerCategory category;
			category = categoryMap.get(categoryName);
			if (category == null) {
				category = new CustomerCategory(categoryName);
				mCategories.add(category);
				categoryMap.put(categoryName, category);
			}
			String name = tokens[2];
			if (name.startsWith("body-")) {
				category.bodyCount++;
			} else if (name.startsWith("top-")) {
				category.topCount++;
			} else if (name.startsWith("face-")) {
				category.faceCount++;
			} else {
				Gdx.app.log("ComposableCustomerFactory", "Skipping " + region.name + ". Unknown customer part!");
			}
		}
	}

	public Customer create() {
		CustomerCategory category = mCategories.get(MathUtils.random(mCategories.size - 1));
		return new ComposableCustomer(mAtlas, category.dirName,
			MathUtils.random(category.bodyCount - 1),
			category.topCount > 0 ? MathUtils.random(category.topCount - 1) : -1,
			MathUtils.random(category.faceCount - 1)
			);
	}
}
