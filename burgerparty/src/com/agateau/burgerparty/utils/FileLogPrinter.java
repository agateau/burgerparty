package com.agateau.burgerparty.utils;

import java.io.IOException;
import java.io.Writer;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class FileLogPrinter extends NLog.Printer {
    private FileHandle mHandle;
    private Writer mWriter;

    public FileLogPrinter(FileHandle logHandle) {
        mHandle = logHandle;
        mWriter = mHandle.writer(true /* append */);
    }

    public static void rotate(FileHandle logHandle, int count) {
        FileHandle dir = logHandle.parent();
        if (!dir.exists()) {
            return;
        }
        String name = logHandle.name();
        for (; count > 1; --count) {
            FileHandle handle = dir.child(name + "." + (count - 1));
            if (!handle.exists()) {
                continue;
            }
            handle.moveTo(dir.child(name + "." + count));
        }
        if (logHandle.exists()) {
            logHandle.moveTo(dir.child(name + ".1"));
        }
    }

    @Override
    protected void doPrint(int level, String tag, String message) {
        try {
            if (level == Application.LOG_DEBUG) {
                mWriter.write("D ");
            } else if (level == Application.LOG_INFO) {
                mWriter.write("I ");
            } else { // LOG_ERROR
                mWriter.write("E ");
            }
            mWriter.write(tag);
            mWriter.write(' ');
            mWriter.write(message);
            mWriter.write('\n');
            mWriter.flush();
        } catch (IOException exc) {
            Gdx.app.error("FileLogPrinter", "Failed to write: " + exc.toString());
        }
    }
}
