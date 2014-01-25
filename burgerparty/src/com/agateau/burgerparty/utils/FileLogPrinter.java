package com.agateau.burgerparty.utils;

import java.io.IOException;
import java.io.Writer;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class FileLogPrinter extends NLog.Printer {
	public boolean init(String logFileName) {
		FileHandle cacheDir = FileUtils.getCacheDir(logFileName);
		if (cacheDir == null) {
			return false;
		}
		mHandle = cacheDir.child(logFileName + ".log");
		mWriter = mHandle.writer(true /* append */);
		return true;
	}

	@Override
	protected void doPrint(int level, String tag, String message) {
		gdxPrint(level, tag, message);
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

	private static void gdxPrint(int level, String tag, String message) {
		if (level == Application.LOG_DEBUG) {
			Gdx.app.debug(tag, message);
		} else if (level == Application.LOG_INFO) {
			Gdx.app.log(tag, message);
		} else { // LOG_ERROR
			Gdx.app.error(tag, message);
		}
	}

	FileHandle mHandle;
	Writer mWriter;
}
