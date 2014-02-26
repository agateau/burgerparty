package com.agateau.burgerparty.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.NLog;
import com.agateau.burgerparty.utils.Signal0;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

public class AchievementManager {
	private HashSet<Object> mHandlers = new HashSet<Object>();

	private HashMap<String, GameStat> mGameStats = new HashMap<String, GameStat>();
	private FileHandle mGameStatsFileHandle;
	private FileHandle mAchievementsFileHandle;

	private NLog log = NLog.getRoot().create("AchievementManager");

	public void addGameStat(GameStat gameStat) {
		mGameStats.put(gameStat.getId(), gameStat);
		gameStat.changed.connect(mHandlers, new Signal0.Handler() {
			@Override
			public void handle() {
				scheduleGameStatsSave();
			}
		});
	}

	public void onLevelStarted(World world) {
		for(GameStat stat: mGameStats.values()) {
			stat.onLevelStarted(world);
		}
	}

	public void loadGameStats(XmlReader.Element root) {
		for(int idx = 0; idx < root.getChildCount(); ++idx) {
			XmlReader.Element element = root.getChild(idx);
			String id = element.getAttribute("id");
			GameStat stat = mGameStats.get(id);
			if (stat == null) {
				log.e("No gamestat with id '%s'", id);
				continue;
			}
			stat.load(element);
		}
	}

	public void saveGameStats() {
		XmlWriter writer = new XmlWriter(mGameStatsFileHandle.writer(false));
		saveGameStats(writer);
	}

	public void saveGameStats(XmlWriter writer) {
		try {
			XmlWriter root = writer.element("gamestats");
			for (GameStat stat: mGameStats.values()) {
				XmlWriter element = root.element("gamestat");
				element.attribute("id", stat.getId());
				stat.save(element);
				element.pop();
			}
			writer.close();
		} catch (IOException e) {
			log.e("saveGameStat: Failed to save gamestats. Exception: %s", e);
		}
	}

	public void load() {
		assert(mGameStatsFileHandle != null);
		if (!mGameStatsFileHandle.exists()) {
			return;
		}
		loadGameStats(FileUtils.parseXml(mGameStatsFileHandle));
	}

	public void setGameStatsFileHandle(FileHandle handle) {
		mGameStatsFileHandle = handle;
	}

	public void setAchievementsFileHandle(FileHandle handle) {
		mAchievementsFileHandle = handle;
	}

	private void scheduleGameStatsSave() {
		// FIXME: Really schedule
		saveGameStats();
	}
}
