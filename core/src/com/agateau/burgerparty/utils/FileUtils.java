package com.agateau.burgerparty.utils;

import java.io.File;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;

public class FileUtils {
    public static FileHandle getUserWritableFile(String name) {
        FileHandle handle;
        if (Gdx.app.getType() == ApplicationType.Desktop) {
            handle = Gdx.files.external(".local/share/burgerparty/" + name);
        } else {
            handle = Gdx.files.local(name);
        }
        return handle;
    }

    public static FileHandle getCacheDir(String appName) {
        FileHandle handle;
        if (Gdx.app.getType() == ApplicationType.Desktop) {
            handle = Gdx.files.external(".cache/" + appName);
        } else {
            if (!Gdx.files.isExternalStorageAvailable()) {
                return null;
            }
            handle = Gdx.files.absolute(Gdx.files.getExternalStoragePath() + "/" + appName);
        }
        handle.mkdirs();
        return handle;
    }

    public static FileHandle assets(String path) {
        FileHandle handle = Gdx.files.internal(path);
        if (Gdx.app.getType() == ApplicationType.Desktop) {
            handle = new FileHandle(new File(handle.path()));
        }
        return handle;
    }

    public static XmlReader.Element parseXml(FileHandle handle) {
        XmlReader reader = new XmlReader();
        XmlReader.Element root = reader.parse(handle);
        if (root == null) {
            NLog.e("Failed to parse xml file from %s. No root element.", handle.path());
            return null;
        }
        return root;
    }
}
