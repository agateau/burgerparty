package com.agateau.burgerparty.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.NLog;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

public class AchievementManager {
	private HashSet<Object> mHandlers = new HashSet<Object>();

	public Signal1<Achievement> achievementUnlocked = new Signal1<Achievement>();
	
	private HashMap<String, GameStat> mGameStats = new HashMap<String, GameStat>();
	private HashMap<String, Achievement> mAchievements = new HashMap<String, Achievement>();
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

	public void addAchievement(final Achievement achievement) {
		assert(achievement != null);
		mAchievements.put(achievement.getId(), achievement);
		achievement.unlocked.connect(mHandlers, new Signal0.Handler() {
			@Override
			public void handle() {
				scheduleAchievementsSave();
				achievementUnlocked.emit(achievement);
			}
		});
	}

	public void onLevelStarted(World world) {
		for(GameStat stat: mGameStats.values()) {
			stat.onLevelStarted(world);
		}
	}

	/// Gamestats load/save
	public void setGameStatsFileHandle(FileHandle handle) {
		mGameStatsFileHandle = handle;
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
			log.e("saveGameStats: Failed to save gamestats. Exception: %s", e);
		}
	}

	private void scheduleGameStatsSave() {
		// FIXME: Really schedule
		saveGameStats();
	}
	/// /Gamestats load/save

	/// Achievements load/save
	public void setAchievementsFileHandle(FileHandle handle) {
		mAchievementsFileHandle = handle;
	}

	public void loadAchievements(XmlReader.Element root) {
		for(int idx = 0; idx < root.getChildCount(); ++idx) {
			XmlReader.Element element = root.getChild(idx);
			String id = element.getAttribute("id");
			Achievement achievement = mAchievements.get(id);
			if (achievement == null) {
				log.e("No achievement with id '%s'", id);
				continue;
			}
			achievement.setAlreadyUnlocked(element.getBoolean("unlocked", false));
		}
	}

	public void saveAchievements() {
		XmlWriter writer = new XmlWriter(mAchievementsFileHandle.writer(false));
		saveAchievements(writer);
	}

	public void saveAchievements(XmlWriter writer) {
		try {
			XmlWriter root = writer.element("achievements");
			for (Achievement achievement: mAchievements.values()) {
				if (achievement.isUnlocked()) {
					root.element("achievement")
						.attribute("id", achievement.getId())
						.attribute("unlocked", "true")
					.pop();
				}
			}
			writer.close();
		} catch (IOException e) {
			log.e("saveAchievements: Failed to save achievements. Exception: %s", e);
		}
	}

	private void scheduleAchievementsSave() {
		// FIXME: Really schedule
		saveAchievements();
	}
	/// /Achievements load/save

	public void load() {
		assert(mGameStatsFileHandle != null);
		assert(mAchievementsFileHandle != null);
		if (mGameStatsFileHandle.exists()) {
			loadGameStats(FileUtils.parseXml(mGameStatsFileHandle));
		}
		if (mAchievementsFileHandle.exists()) {
			loadAchievements(FileUtils.parseXml(mAchievementsFileHandle));
		}
	}
}
