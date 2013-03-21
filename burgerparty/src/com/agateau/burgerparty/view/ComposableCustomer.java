package com.agateau.burgerparty.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class ComposableCustomer extends Customer {
	static final int FACE_OFFSET = 69;

	private TextureAtlas mAtlas;
	private String mDirName;

	static private class CustomerPart {
		Image image;
		float xCenter;
	}

	public ComposableCustomer(TextureAtlas atlas, String dirName, int bodyId, int topId, int faceId) {
		mAtlas = atlas;
		mDirName = dirName;
		CustomerPart body = getCustomerPart("body", bodyId, "");
		CustomerPart top = topId >= 0 ? getCustomerPart("top", topId, "") : null;
		CustomerPart face = getCustomerPart("face", faceId, "happy");

		addActor(body.image);
		if (top != null) {
			xCenterImage(top, body.xCenter);
			addActor(top.image);
		}
		xCenterImage(face, body.xCenter);
		addActor(face.image);

		setWidth(body.image.getWidth());

		face.image.setY(FACE_OFFSET);
		setHeight(body.image.getHeight());
	}
	
	private CustomerPart getCustomerPart(String prefix, int id, String suffix) {
		String name = "customers/" + mDirName + "/" + prefix + "-" + id;
		if (!suffix.isEmpty()) {
			name += "-" + suffix;
		}
		CustomerPart part = new CustomerPart();
		part.image = new Image(mAtlas.findRegion(name));
		part.xCenter = getPartXCenter(name, part.image.getWidth() / 2);
		Gdx.app.log("getCustomerPart", "name=" + name + " xCenter=" + part.xCenter + " default=" + (part.image.getWidth() / 2));
		return part;
	}

	private void xCenterImage(CustomerPart part, float xCenter) {
		part.image.setX(xCenter - part.xCenter);
	}

	private float getPartXCenter(String name, float defaultValue) {
		// FIXME: Move custom xCenter to a configuration file
		if (name.equals("customers/girls/body-0")) {
			return 59;
		} else if (name.equals("customers/girls/body-1")) {
			return 53;
		} else {
			return defaultValue;
		}
	}
}
