package com.agateau.burgerparty.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

public class GameStatManager {
    private HashSet<Object> mHandlers = new HashSet<Object>();

    private FileHandle mFileHandle;
    private HashMap<String, GameStat> mGameStats = new HashMap<String, GameStat>();

    public void setFileHandle(FileHandle handle) {
        mFileHandle = handle;
    }

    public void add(String id, GameStat gameStat) {
        mGameStats.put(id, gameStat);
        gameStat.changed.connect(mHandlers, new Signal0.Handler() {
            @Override
            public void handle() {
                scheduleSave();
            }
        });
    }

    public void load() {
        if (mFileHandle.exists()) {
            load(FileUtils.parseXml(mFileHandle));
        }
    }

    public void load(XmlReader.Element root) {
        for (int idx = 0; idx < root.getChildCount(); ++idx) {
            XmlReader.Element element = root.getChild(idx);
            String id = element.getAttribute("id");
            GameStat stat = mGameStats.get(id);
            if (stat == null) {
                NLog.e("No gamestat with id '%s'", id);
                continue;
            }
            stat.load(element);
        }
    }

    public void save() {
        XmlWriter writer = new XmlWriter(mFileHandle.writer(false));
        save(writer);
    }

    public void save(XmlWriter writer) {
        try {
            XmlWriter root = writer.element("gamestats");
            Iterator<Entry<String, GameStat>> it = mGameStats.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, GameStat> entry = it.next();
                XmlWriter element = root.element("gamestat");
                element.attribute("id", entry.getKey());
                entry.getValue().save(element);
                element.pop();
            }
            writer.close();
        } catch (IOException e) {
            NLog.e("Failed to save gamestats. Exception: %s", e);
        }
    }

    private void scheduleSave() {
        // FIXME: Really schedule
        save();
    }
}
