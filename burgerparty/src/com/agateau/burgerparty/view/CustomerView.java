package com.agateau.burgerparty.view;

import java.util.HashSet;

import com.agateau.burgerparty.model.Customer;
import com.agateau.burgerparty.utils.Signal0;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class CustomerView extends WidgetGroup {
	private final HashSet<Object> mHandlers = new HashSet<Object>();

	private CustomerViewFactory mFactory;
	private String mDirName;
	private Customer mCustomer;
	private String mFaceName;
	private CustomerViewFactory.BodyPart mBodyPart = null;
	private Image mBodyImage = null;
	private Image mFaceImage = null;

	public CustomerView(Customer customer, CustomerViewFactory factory, String dirName, String bodyName, String topName, String faceName) {
		mCustomer = customer;
		mFactory = factory;
		mDirName = dirName;

		mCustomer.moodChanged.connect(mHandlers, new Signal0.Handler() {
			@Override
			public void handle() {
				updateFace();
			}
		});

		// Body
		mBodyPart = (CustomerViewFactory.BodyPart)mFactory.getCustomerPart(mDirName, bodyName, "");
		mBodyImage = getPartImage(mBodyPart);
		addActor(mBodyImage);

		// Top
		if (!topName.isEmpty()) {
			CustomerViewFactory.CustomerPart top = mFactory.getCustomerPart(mDirName, topName, "");
			Image topImage = getPartImage(top);
			addActor(topImage);
			xCenterImage(topImage, top, mBodyImage, mBodyPart);
			topImage.setY(top.yOffset);
		}

		// Face
		mFaceName = faceName;
		updateFace();

		setWidth(mBodyImage.getWidth());
		setHeight(Math.max(mBodyImage.getHeight(), mFaceImage.getTop()));
	}

	public float getPrefWidth() {
		return getWidth();
	}

	public float getPrefHeight() {
		return getHeight();
	}

	public Customer getCustomer() {
		return mCustomer;
	}

	private Image getPartImage(CustomerViewFactory.CustomerPart part) {
		assert(part != null);
		TextureRegion region = mFactory.getAtlas().findRegion(part.name);
		if (region == null) {
			throw new RuntimeException("No region named " + part.name);
		}
		return new Image(region);
	}

	private void updateFace() {
		if (mFaceImage != null) {
			mFaceImage.remove();
		}
		CustomerViewFactory.CustomerPart facePart = mFactory.getCustomerPart(mDirName, mFaceName, mCustomer.getMood().toString());
		mFaceImage = getPartImage(facePart);
		addActor(mFaceImage);
		xCenterImage(mFaceImage, facePart, mBodyImage, mBodyPart);
		mFaceImage.setY(mBodyPart.yFace + facePart.yOffset);
	}

	private static void xCenterImage(Image image, CustomerViewFactory.CustomerPart imagePart, Image ref, CustomerViewFactory.CustomerPart refPart) {
		float imageCenter = imagePart.xCenter > 0 ? imagePart.xCenter : (image.getWidth() / 2);
		float refCenter = refPart.xCenter > 0 ? refPart.xCenter : (ref.getWidth() / 2);
		image.setX(refCenter - imageCenter);
	}
}
