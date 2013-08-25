package com.agateau.burgerparty.model;

import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;

public abstract class MealItemCollection {
	public Signal1<MealItem> itemAdded = new Signal1<MealItem>();
	public Signal0 initialized = new Signal0();
	public Signal0 cleared = new Signal0();
	public Signal0 trashed = new Signal0();


	public enum CompareResult {
		SUBSET,
		SAME,
		DIFFERENT,
	}
}
