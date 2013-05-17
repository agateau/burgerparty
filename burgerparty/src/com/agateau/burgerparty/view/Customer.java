package com.agateau.burgerparty.view;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class Customer extends WidgetGroup {
	public Customer(TextureAtlas atlas, String dirName, String bodyName, String topName, String faceName) {
		mAtlas = atlas;
		mDirName = dirName;

		// Body
		CustomerFactory.BodyPart body = (CustomerFactory.BodyPart)getCustomerPart(bodyName, "");
		Image bodyImage = getPartImage(body);
		addActor(bodyImage);

		// Top
		if (!topName.isEmpty()) {
			CustomerFactory.CustomerPart top = getCustomerPart(topName, "");
			Image topImage = getPartImage(top);
			addActor(topImage);
			xCenterImage(topImage, top, bodyImage, body);
			topImage.setY(top.yOffset);
		}

		// Face
		CustomerFactory.CustomerPart face = getCustomerPart(faceName, "happy");
		Image faceImage = getPartImage(face);
		addActor(faceImage);
		xCenterImage(faceImage, face, bodyImage, body);
		faceImage.setY(body.yFace + face.yOffset);

		setWidth(bodyImage.getWidth());
		setHeight(bodyImage.getHeight());
	}

	private Image getPartImage(CustomerFactory.CustomerPart part) {
		assert(part != null);
		TextureRegion region = mAtlas.findRegion(part.name);
		if (region == null) {
			throw new RuntimeException("No region named " + part.name);
		}
		return new Image(region);
	}

	private static void xCenterImage(Image image, CustomerFactory.CustomerPart imagePart, Image ref, CustomerFactory.CustomerPart refPart) {
		float imageCenter = imagePart.xCenter > 0 ? imagePart.xCenter : (image.getWidth() / 2);
		float refCenter = refPart.xCenter > 0 ? refPart.xCenter : (ref.getWidth() / 2);
		image.setX(refCenter - imageCenter);
	}
	
	private CustomerFactory.CustomerPart getCustomerPart(String name, String suffix) {
		String fullName = "customers/" + mDirName + "/" + name;
		if (!suffix.isEmpty()) {
			fullName += "-" + suffix;
		}
		CustomerFactory.CustomerPart part = CustomerFactory.sMap.get(fullName);
		if (part == null) {
			throw new RuntimeException("Failed to find customer part named " + fullName);
		}
		return part;
	}

	private TextureAtlas mAtlas;
	private String mDirName;
}
