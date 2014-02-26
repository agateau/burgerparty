package com.agateau.burgerparty.model;

import com.agateau.burgerparty.utils.Signal0;
import com.badlogic.gdx.utils.XmlReader;

public abstract class GameStat {
	public Signal0 changed = new Signal0();

	private String mId;

	public GameStat(String id) {
		mId = id;
	}

	public String getId() {
		return mId;
	}

	public abstract void reset();

	/**
	 * Reads the GameStat value from element
	 *
	 * @param element
	 */
	public abstract void load(XmlReader.Element element);

	/**
	 * Writes the GameStat value to element. Assumes element is dedicated to this gamestat value.
	 *
	 * @param element
	 */
	public abstract void save(XmlReader.Element element);

	public void onLevelStarted(World world) {}
}
