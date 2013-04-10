package com.agateau.burgerparty.view;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class ComposableCustomer extends Customer {
	private TextureAtlas mAtlas;
	private String mDirName;

	static private class CustomerPart {
		String name;
		float xCenter = 0;
		float yOffset = 0;
	}

	static private class BodyPart extends CustomerPart {
		float yFace = 0;
	}

	public ComposableCustomer(TextureAtlas atlas, String dirName, String bodyName, String topName, String faceName) {
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
		return new Image(mAtlas.findRegion(part.name));
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
		CustomerPart part;
		if (name.startsWith("body")) {
			BodyPart bodyPart = new BodyPart();
			if (mDirName.equals("ninjas")) {
				bodyPart.yFace = 55;
			} else {
				bodyPart.yFace = 69;
			}
			part = bodyPart;
		} else {
			part = new CustomerPart();
		}
		part.name = fullName;
		// FIXME: Move custom xCenter to a configuration file
		if (fullName.equals("customers/girls/body-0")) {
			part.xCenter = 59;
		} else if (fullName.equals("customers/girls/body-1")) {
			part.xCenter = 53;
		} else if (fullName.equals("customers/ninjas/body-0")) {
			part.xCenter = 55;
		}
		return part;
	}
}
