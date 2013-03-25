package com.agateau.burgerparty.model;

import com.badlogic.gdx.utils.Array;

public class Level {
	public static class Definition {
		public Array<String> inventoryItems = new Array<String>();
		public int minStackSize;
		public int maxStackSize;
		public int duration;
		public int customerCount;
	}

	public Definition definition;
}
