package com.agateau.burgerparty.view;

import com.agateau.burgerparty.model.Customer;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class CustomerView extends WidgetGroup {
	public CustomerView(Customer customer, CustomerViewFactory factory, String dirName, String bodyName, String topName, String faceName) {
		mCustomer = customer;
		mFactory = factory;
		mDirName = dirName;

		// Body
		CustomerViewFactory.BodyPart body = (CustomerViewFactory.BodyPart)mFactory.getCustomerPart(mDirName, bodyName, "");
		Image bodyImage = getPartImage(body);
		addActor(bodyImage);

		// Top
		if (!topName.isEmpty()) {
			CustomerViewFactory.CustomerPart top = mFactory.getCustomerPart(mDirName, topName, "");
			Image topImage = getPartImage(top);
			addActor(topImage);
			xCenterImage(topImage, top, bodyImage, body);
			topImage.setY(top.yOffset);
		}

		// Face
		CustomerViewFactory.CustomerPart face = mFactory.getCustomerPart(mDirName, faceName, "happy");
		Image faceImage = getPartImage(face);
		addActor(faceImage);
		xCenterImage(faceImage, face, bodyImage, body);
		faceImage.setY(body.yFace + face.yOffset);

		setWidth(bodyImage.getWidth());
		setHeight(Math.max(bodyImage.getHeight(), faceImage.getTop()));
	}

	private Image getPartImage(CustomerViewFactory.CustomerPart part) {
		assert(part != null);
		TextureRegion region = mFactory.getAtlas().findRegion(part.name);
		if (region == null) {
			throw new RuntimeException("No region named " + part.name);
		}
		return new Image(region);
	}

	private static void xCenterImage(Image image, CustomerViewFactory.CustomerPart imagePart, Image ref, CustomerViewFactory.CustomerPart refPart) {
		float imageCenter = imagePart.xCenter > 0 ? imagePart.xCenter : (image.getWidth() / 2);
		float refCenter = refPart.xCenter > 0 ? refPart.xCenter : (ref.getWidth() / 2);
		image.setX(refCenter - imageCenter);
	}
	
	private CustomerViewFactory mFactory;
	private String mDirName;
	private Customer mCustomer;
}
