package com.agateau.burgerparty.view;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;

public class ComposableCustomerFactory {

	private ComposableCustomer.Ids mIds = new ComposableCustomer.Ids();

	public ComposableCustomerFactory() {
		mIds.bodyId = MathUtils.random(0, ComposableCustomer.BODY_COUNT - 1);
		mIds.eyesId = MathUtils.random(0, ComposableCustomer.EYES_COUNT - 1);
		mIds.hairId = MathUtils.random(0, ComposableCustomer.HAIR_COUNT - 1);
		mIds.mouthId = MathUtils.random(0, ComposableCustomer.MOUTH_COUNT - 1);
		mIds.topId = MathUtils.random(0, ComposableCustomer.TOP_COUNT - 1);
	}

	public Customer create(TextureAtlas atlas) {
		ComposableCustomer customer = new ComposableCustomer(atlas, mIds);
		mIds.bodyId = (mIds.bodyId + 1) % ComposableCustomer.BODY_COUNT;
		mIds.eyesId = (mIds.eyesId + 1) % ComposableCustomer.EYES_COUNT;
		mIds.hairId = (mIds.hairId + 1) % ComposableCustomer.HAIR_COUNT;
		mIds.mouthId = (mIds.mouthId + 1) % ComposableCustomer.MOUTH_COUNT;
		mIds.topId = (mIds.topId + 1) % ComposableCustomer.TOP_COUNT;
		return customer;
	}
}
