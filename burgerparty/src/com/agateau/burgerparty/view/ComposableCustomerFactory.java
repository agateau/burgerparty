package com.agateau.burgerparty.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;

/**
 * Knows all available customer types. Can create customer given a customer type with create().
 */
public class ComposableCustomerFactory {
	public static class Elements {
		public String dirName;
		public Array<String> bodies = new Array<String>();
		public Array<String> tops = new Array<String>();
		public Array<String> faces = new Array<String>();

		public Elements(String dirName) {
			this.dirName = dirName;
		} 
	}

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
			String customerType = path[1];
			Elements elements;
			elements = mElementsForType.get(customerType);
			if (elements == null) {
				elements = new Elements(customerType);
				mElementsForType.put(customerType, elements);
			}
			String name = path[2];
			if (name.startsWith("body-")) {
				elements.bodies.add(name);
			} else if (name.startsWith("top-")) {
				elements.tops.add(name);
			} else if (name.startsWith("face-")) {
				String[] tokens = name.split("-", 3);
				if (tokens.length != 3) {
					Gdx.app.log("ComposableCustomerFactory", "Skipping " + region.name + ". Invalid face name!");
					continue;
				}
				if (tokens[2].equals("happy")) {
					elements.faces.add(tokens[0] + "-" + tokens[1]);
				}
			} else {
				Gdx.app.log("ComposableCustomerFactory", "Skipping " + region.name + ". Unknown customer part!");
			}
		}
	}

	/**
	 * Creates a customer give a customer type
	 *
	 * @param customerType name of the category the customer should be made from
	 * @return a Customer instance
	 */
	public Customer create(String customerType) {
		Elements elements = mElementsForType.get(customerType);
		return new ComposableCustomer(mAtlas, elements.dirName,
			getRandomString(elements.bodies),
			getRandomString(elements.tops),
			getRandomString(elements.faces));
	}

	public Array<String> getTypes() {
		return mElementsForType.orderedKeys();
	}

	public Elements getElementsForType(String type) {
		return mElementsForType.get(type);
	}

	private static String getRandomString(Array<String> array) {
		if (array.size > 0) {
			return array.get(MathUtils.random(array.size - 1));
		} else {
			return "";
		}
	}

	private OrderedMap<String, Elements> mElementsForType = new OrderedMap<String, Elements>();
	private TextureAtlas mAtlas;
}
