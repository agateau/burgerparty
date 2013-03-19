package com.agateau.burgerparty.view;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;

public class SimpleCustomerFactory {
	private int mId;

	public SimpleCustomerFactory() {
		mId = MathUtils.random(0, SimpleCustomer.CUSTOMER_COUNT - 1);
	}

	public SimpleCustomer create(TextureAtlas atlas) {
		SimpleCustomer simpleCustomer = new SimpleCustomer(atlas, mId);
		mId = (mId + 1) % SimpleCustomer.CUSTOMER_COUNT;
		return simpleCustomer;
	}
}
