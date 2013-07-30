package com.agateau.burgerparty.model;

public class Customer {
	public Customer(String type) {
		mType = type;
	}

	public String getType() {
		return mType;
	}

	private String mType;
}
