package com.agateau.burgerparty.view;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class ComposableCustomer extends Customer {
	private TextureAtlas mAtlas;
	private String mDirName;

	static private class CustomerPart {
		Image image;
		float xCenter;
		float yOffset;
	}

	public ComposableCustomer(TextureAtlas atlas, String dirName, String bodyName, String topName, String faceName) {
		mAtlas = atlas;
		mDirName = dirName;
		CustomerPart body = getCustomerPart(bodyName, "");
		CustomerPart top = topName.isEmpty() ? null : getCustomerPart(topName, "");
		CustomerPart face = getCustomerPart(faceName, "happy");

		addActor(body.image);
		if (top != null) {
			xCenterImage(top, body.xCenter);
			addActor(top.image);
		}
		xCenterImage(face, body.xCenter);
		addActor(face.image);

		setWidth(body.image.getWidth());

		face.image.setY(face.yOffset);
		setHeight(body.image.getHeight());
	}
	
	private CustomerPart getCustomerPart(String name, String suffix) {
		String fullName = "customers/" + mDirName + "/" + name;
		if (!suffix.isEmpty()) {
			fullName += "-" + suffix;
		}
		CustomerPart part = new CustomerPart();
		part.image = new Image(mAtlas.findRegion(fullName));
		part.xCenter = getPartXCenter(fullName, part.image.getWidth() / 2);
		if (name.startsWith("face")) {
			if (mDirName.equals("ninjas")) {
				part.yOffset = 55;
			} else {
				part.yOffset = 69;
			}
		} else {
			part.yOffset = 0;
		}
		return part;
	}

	private static void xCenterImage(CustomerPart part, float xCenter) {
		part.image.setX(xCenter - part.xCenter);
	}

	private static float getPartXCenter(String name, float defaultValue) {
		// FIXME: Move custom xCenter to a configuration file
		if (name.equals("customers/girls/body-0")) {
			return 59;
		} else if (name.equals("customers/girls/body-1")) {
			return 53;
		} else if (name.equals("customers/ninjas/body-0")) {
			return 55;
		} else {
			return defaultValue;
		}
	}
}
