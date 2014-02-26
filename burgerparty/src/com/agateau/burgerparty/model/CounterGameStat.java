package com.agateau.burgerparty.model;

import com.agateau.burgerparty.utils.NLog;
import com.badlogic.gdx.utils.XmlReader.Element;

public class CounterGameStat extends GameStat {
	private int mValue = 0;

	public CounterGameStat(String id) {
		super(id);
	}

	@Override
	public void reset() {
		mValue = 0;
		changed.emit();
	}

	@Override
	public void load(Element element) {
		mValue = element.getIntAttribute("value", 0);
	}

	@Override
	public void save(Element element) {
		element.setAttribute("value", String.valueOf(mValue));
	}

	public void increase() {
		mValue++;
		NLog.getRoot().i("mValue=%s", mValue);
		changed.emit();
	}
}
