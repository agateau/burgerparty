package com.agateau.burgerparty.model;

import java.util.HashSet;

import com.agateau.burgerparty.utils.Signal0;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

public class AchievementManager {
	private HashSet<Object> mHandlers = new HashSet<Object>();

	private Array<GameStat> mGameStats = new Array<GameStat>();

	public void addGameStat(GameStat gameStat) {
		mGameStats.add(gameStat);
		gameStat.changed.connect(mHandlers, new Signal0.Handler() {
			@Override
			public void handle() {
				scheduleSave();
			}
		});
	}

	public void onLevelStarted(World world) {
		for(GameStat stat: mGameStats) {
			stat.onLevelStarted(world);
		}
	}

	private void scheduleSave() {
	}

	void load(XmlReader.Element element) {
		
	}

	void save(XmlReader.Element element) {
		
	}
}
