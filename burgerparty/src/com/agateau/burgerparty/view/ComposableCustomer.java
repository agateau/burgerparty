package com.agateau.burgerparty.view;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class ComposableCustomer extends Customer {
	public static final int BODY_COUNT = 3;
	public static final int TOP_COUNT = 5;
	public static final int MOUTH_COUNT = 2;
	public static final int EYES_COUNT = 3;
	public static final int HAIR_COUNT = 3;

	static final int BODY_TO_HAIR_OFFSET = 23;

	static final int MOUTH_OFFSET = 69;
	static final int EYES_OFFSET = 90;

	static class Ids {
		public int bodyId;
		public int topId;
		public int mouthId;
		public int eyesId;
		public int hairId;
	}

	public ComposableCustomer(TextureAtlas atlas, Ids ids) {
		Image body = getCustomerPartImage(atlas, "body", ids.bodyId);
		Image top = getCustomerPartImage(atlas, "top", ids.topId);
		Image mouth = getCustomerPartImage(atlas, "mouth", ids.mouthId);
		Image eyes = getCustomerPartImage(atlas, "eyes", ids.eyesId);
		Image hair = getCustomerPartImage(atlas, "hair", ids.hairId);

		addActor(body);
		addActor(top);
		addActor(hair);
		addActor(mouth);
		addActor(eyes);

		setWidth(Math.max(body.getWidth(), hair.getWidth()));
		setHeight(body.getHeight() + BODY_TO_HAIR_OFFSET);

		xCenterImage(body);
		xCenterImage(top);
		xCenterImage(hair);
		xCenterImage(mouth);
		xCenterImage(eyes);

		hair.setY(body.getTop() + BODY_TO_HAIR_OFFSET - hair.getHeight());
		mouth.setY(MOUTH_OFFSET);
		eyes.setY(EYES_OFFSET);
	}

	static Image getCustomerPartImage(TextureAtlas atlas, String prefix, int id) {
		String name = "customers/" + prefix + "-" + id;
		return new Image(atlas.findRegion(name));
	}

	void xCenterImage(Image image) {
		image.setX((getWidth() - image.getWidth()) / 2);
	}
}
