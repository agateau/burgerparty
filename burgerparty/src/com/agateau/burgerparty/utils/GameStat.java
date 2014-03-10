package com.agateau.burgerparty.utils;

import java.io.IOException;

import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

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
	 * Writes the GameStat value to writer. Assumes writer is dedicated to this gamestat value.
	 *
	 * @param writer
	 */
	public abstract void save(XmlWriter writer) throws IOException;
}
