package com.agateau.burgerparty.view;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.XmlReader;

public class Customer extends WidgetGroup {
	public Customer(TextureAtlas atlas, String dirName, String bodyName, String topName, String faceName) {
		if (sMap.size == 0) {
			FileHandle handle = Gdx.files.internal("customerparts.xml");
			initMap(handle);
			assert(sMap.size > 0);
		}
		mAtlas = atlas;
		mDirName = dirName;

		// Body
		BodyPart body = (BodyPart)getCustomerPart(bodyName, "");
		Image bodyImage = getPartImage(body);
		addActor(bodyImage);

		// Top
		if (!topName.isEmpty()) {
			CustomerPart top = getCustomerPart(topName, "");
			Image topImage = getPartImage(top);
			addActor(topImage);
			xCenterImage(topImage, top, bodyImage, body);
			topImage.setY(top.yOffset);
		}

		// Face
		CustomerPart face = getCustomerPart(faceName, "happy");
		Image faceImage = getPartImage(face);
		addActor(faceImage);
		xCenterImage(faceImage, face, bodyImage, body);
		faceImage.setY(body.yFace + face.yOffset);

		setWidth(bodyImage.getWidth());
		setHeight(bodyImage.getHeight());
	}

	private Image getPartImage(CustomerPart part) {
		assert(part != null);
		TextureRegion region = mAtlas.findRegion(part.name);
		if (region == null) {
			throw new RuntimeException("No region named " + part.name);
		}
		return new Image(region);
	}

	private static void xCenterImage(Image image, CustomerPart imagePart, Image ref, CustomerPart refPart) {
		float imageCenter = imagePart.xCenter > 0 ? imagePart.xCenter : (image.getWidth() / 2);
		float refCenter = refPart.xCenter > 0 ? refPart.xCenter : (ref.getWidth() / 2);
		image.setX(refCenter - imageCenter);
	}
	
	private CustomerPart getCustomerPart(String name, String suffix) {
		String fullName = "customers/" + mDirName + "/" + name;
		if (!suffix.isEmpty()) {
			fullName += "-" + suffix;
		}
		CustomerPart part = sMap.get(fullName);
		if (part == null) {
			throw new RuntimeException("Failed to find customer part named " + fullName);
		}
		return part;
	}

	private TextureAtlas mAtlas;
	private String mDirName;

	private static class CustomerPart {
		String name;
		float xCenter = 0;
		float yOffset = 0;

		CustomerPart(XmlReader.Element element) {
			name = element.getAttribute("name");
			xCenter = element.getFloatAttribute("xCenter", 0);
			yOffset = element.getFloatAttribute("yOffset", 0);
		}
	}

	private static class BodyPart extends CustomerPart {
		float yFace = 0;

		BodyPart(XmlReader.Element element) {
			super(element);
			yFace = element.getFloatAttribute("yFace", 0);
		}
	}

	private static OrderedMap<String, CustomerPart> sMap = new OrderedMap<String, Customer.CustomerPart>();

	public static void initMap(FileHandle handle) {
		sMap.clear();
		XmlReader.Element root = null;
		try {
			XmlReader reader = new XmlReader();
			root = reader.parse(handle);
		} catch (IOException e) {
			Gdx.app.error("Customer.initMap", "Failed to load customer parts from " + handle.path() + ". Exception: " + e.toString());
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
			sMap.put(part.name, part);
		}
	}

}
