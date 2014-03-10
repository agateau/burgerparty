package com.agateau.burgerparty.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

public class AchievementManager {
    private HashSet<Object> mHandlers = new HashSet<Object>();

    public Signal1<Achievement> achievementUnlocked = new Signal1<Achievement>();

    private Array<Achievement> mAchievements = new Array<Achievement>();
    private HashMap<String, Achievement> mAchievementForId = new HashMap<String, Achievement>();
    private FileHandle mFileHandle;

    private NLog log = NLog.getRoot().create("AchievementManager");

    public void add(final Achievement achievement) {
        assert(achievement != null);
        mAchievements.add(achievement);
        mAchievementForId.put(achievement.getId(), achievement);
        achievement.unlocked.connect(mHandlers, new Signal0.Handler() {
            @Override
            public void handle() {
                scheduleSave();
                achievementUnlocked.emit(achievement);
            }
        });
    }

    public Array<Achievement> getAchievements() {
        return mAchievements;
    }

    /// Achievements load/save
    public void setFileHandle(FileHandle handle) {
        mFileHandle = handle;
    }

    public void load() {
        assert(mFileHandle != null);
        if (mFileHandle.exists()) {
            load(FileUtils.parseXml(mFileHandle));
        }
    }

    public void load(XmlReader.Element root) {
        for (int idx = 0; idx < root.getChildCount(); ++idx) {
            XmlReader.Element element = root.getChild(idx);
            String id = element.getAttribute("id");
            Achievement achievement = mAchievementForId.get(id);
            if (achievement == null) {
                log.e("No achievement with id '%s'", id);
                continue;
            }
            achievement.setAlreadyUnlocked(element.getBoolean("unlocked", false));
        }
    }

    public void save() {
        XmlWriter writer = new XmlWriter(mFileHandle.writer(false));
        save(writer);
    }

    public void save(XmlWriter writer) {
        try {
            XmlWriter root = writer.element("achievements");
            for (Achievement achievement: mAchievements) {
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

    private void scheduleSave() {
        // FIXME: Really schedule
        save();
    }
}
