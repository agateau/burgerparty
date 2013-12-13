package com.agateau.burgerparty.utils;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class FileUtils {
	static public FileHandle getUserWritableFile(String name) {
		FileHandle handle;
		if (Gdx.app.getType() == ApplicationType.Desktop) {
			handle = Gdx.files.external(".local/share/burgerparty/" + name);
		} else {
			handle = Gdx.files.local(name);
		}
		return handle;
	}

}
