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
    public Signal0 changed = new Signal0();

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
                achievementUnlocked.emit(achievement);
            }
        });
        achievement.changed.connect(mHandlers, new Signal0.Handler() {
            @Override
            public void handle() {
                scheduleSave();
                changed.emit();
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
        /**
         * <achievements>
         *   <achievement id='foo' unlocked='true' seen='false'/>
         *   <achievement id='bar' unlocked='false'/>
         * </achievements>
         */
        for (int idx = 0; idx < root.getChildCount(); ++idx) {
            XmlReader.Element element = root.getChild(idx);
            String id = element.getAttribute("id");
            Achievement achievement = mAchievementForId.get(id);
            if (achievement == null) {
                log.e("No achievement with id '%s'", id);
                continue;
            }
            achievement.setAlreadyUnlocked(
                element.getBooleanAttribute("unlocked", false)
            );
            achievement.setAlreadySeen(
                // default to true for achievements which were unlocked before the "seen" property got introduced
                element.getBooleanAttribute("seen", true)
            );
        }
    }

    public void save() {
        XmlWriter writer = new XmlWriter(mFileHandle.writer(false));
        save(writer);
    }

    public void save(XmlWriter writer) {
        try {
            XmlWriter root = writer.element("achievements");
            // Use a manual loop rather than a foreach-like loop because if we are called while iterating on achievements
            // (for example marking an achievement as seen while creating a view of the achievement list) Array aborts,
            // complaining its iterator cannot be used recursively
            for (int i = 0, n = mAchievements.size; i < n; ++i) {
                Achievement achievement = mAchievements.get(i);
                if (achievement.isUnlocked()) {
                    root.element("achievement")
                    .attribute("id", achievement.getId())
                    .attribute("unlocked", "true")
                    .attribute("seen", achievement.hasBeenSeen())
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

    public boolean hasUnseenAchievements() {
        for (int i = 0, n = mAchievements.size; i < n; ++i) {
            Achievement achievement = mAchievements.get(i);
            if (achievement.isUnlocked() && !achievement.hasBeenSeen()) {
                return true;
            }
        }
        return false;
    }
}
