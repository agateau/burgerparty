package com.agateau.burgerparty.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.SharedLibraryLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlatformUtils {
    /** An implementation of Gdx.net.openURI which works on Linux */
    public static void openURI(String uri) {
        List<String> command = new ArrayList<>();
        if (SharedLibraryLoader.isLinux) {
            command.add("xdg-open");
        } else if (SharedLibraryLoader.isWindows) {
            command.add("cmd.exe");
            command.add("/c");
            command.add("start");
            command.add(""); // This is the window title
        } else if (SharedLibraryLoader.isMac) {
            command.add("open");
        } else {
            // Let Gdx handle the Android case
            Gdx.net.openURI(uri);
            return;
        }
        command.add(uri);
        try {
            NLog.i("Trying with '%s'", command);
            new ProcessBuilder(command).start();
        } catch (IOException e) {
            NLog.e("Command failed: %s", e);
        }
    }

}
