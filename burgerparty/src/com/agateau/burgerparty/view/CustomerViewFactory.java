package com.agateau.burgerparty.view;

import java.io.IOException;

import com.agateau.burgerparty.model.Customer;
import com.agateau.burgerparty.utils.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.XmlReader;

/**
 * Knows all available customer types. Can create a CustomerView given a customer type with create().
 */
public class CustomerViewFactory {
	public static class Elements {
		public String dirName;
		public Array<String> bodies = new Array<String>();
		public Array<String> tops = new Array<String>();
		public Array<String> faces = new Array<String>(); // Does not include the "-$mood" suffix

		public Elements(String dirName) {
			this.dirName = dirName;
		} 
	}

	public static class CustomerPart {
		String name;
		float xCenter = 0;
		float yOffset = 0;
	
		CustomerPart(XmlReader.Element element) {
			name = element.getAttribute("name");
			xCenter = element.getFloatAttribute("xCenter", 0);
			yOffset = element.getFloatAttribute("yOffset", 0);
		}
	}

	public static class BodyPart extends CustomerPart {
		float yFace = 0;
	
		BodyPart(XmlReader.Element element) {
			super(element);
			yFace = element.getFloatAttribute("yFace", 0);
		}
	}

	public CustomerViewFactory(TextureAtlas atlas, FileHandle customerPartsHandle) {
		if (log == null) {
			log = NLog.createForClass(this);
		}
		initMap(customerPartsHandle);
		assert(mCustomerPartForPath.size > 0);

		mAtlas = atlas;
		for(TextureAtlas.AtlasRegion region: mAtlas.getRegions()) {
			String[] path = region.name.split("/", 3);
			if (!path[0].equals("customers")) {
				continue;
			}
			if (path.length < 3) {
				log.e("ctor: Skipping %s. Should not exist!", region.name);
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
					log.e("ctor: Skipping %s. Invalid face name!", region.name);
					continue;
				}
				if (tokens[2].equals("happy")) {
					elements.faces.add(tokens[0] + "-" + tokens[1]);
				}
			} else {
				log.e("ctor: Skipping %s. Unknown customer part!", region.name);
			}
		}
	}

	/**
	 * Creates a customer give a customer type
	 *
	 * @param customerType name of the category the customer should be made from
	 * @return a Customer instance
	 */
	public CustomerView create(Customer customer) {
		Elements elements = mElementsForType.get(customer.getType());
		return new CustomerView(customer, this, elements.dirName,
			getRandomString(elements.bodies),
			getRandomString(elements.tops),
			getRandomString(elements.faces));
	}

	public TextureAtlas getAtlas() {
		return mAtlas;
	}

	public Array<String> getTypes() {
		return mElementsForType.orderedKeys();
	}

	public Elements getElementsForType(String type) {
		return mElementsForType.get(type);
	}

	public CustomerPart getCustomerPart(String dirName, String name, String suffix) {
		String fullName = "customers/" + dirName + "/" + name;
		if (!suffix.isEmpty()) {
			fullName += "-" + suffix;
		}
		CustomerViewFactory.CustomerPart part = mCustomerPartForPath.get(fullName);
		if (part == null) {
			throw new RuntimeException("Failed to find customer part named " + fullName);
		}
		return part;
	}

	private void initMap(FileHandle handle) {
		mCustomerPartForPath.clear();
		XmlReader.Element root = null;
		try {
			XmlReader reader = new XmlReader();
			root = reader.parse(handle);
		} catch (IOException e) {
			Gdx.app.error("CustomerFactory.initMap", "Failed to load customer parts from " + handle.path() + ". Exception: " + e.toString());
			return;
		}
		for(int idx = 0; idx < root.getChildCount(); ++idx) {
			CustomerPart part;
			XmlReader.Element element = root.getChild(idx);
			if (element.getName().equals("body")) {
				part = new BodyPart(element);
			} else {
				part = new CustomerPart(element);
			}
			mCustomerPartForPath.put(part.name, part);
		}
	}

	private static String getRandomString(Array<String> array) {
		if (array.size > 0) {
			return array.get(MathUtils.random(array.size - 1));
		} else {
			return "";
		}
	}

	private static NLog log;
	private TextureAtlas mAtlas;
	private OrderedMap<String, Elements> mElementsForType = new OrderedMap<String, Elements>();
	private OrderedMap<String, CustomerPart> mCustomerPartForPath = new OrderedMap<String, CustomerPart>();
}
