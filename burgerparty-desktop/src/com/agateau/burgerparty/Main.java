package com.agateau.burgerparty;

import java.nio.IntBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import com.agateau.burgerparty.utils.FileLogPrinter;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.GdxPrinter;
import com.agateau.burgerparty.utils.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.greenyetilab.linguaj.Translator;

public class Main {
    private static boolean sFullScreen = false;
    private static boolean sHideCursor = false;
    private static boolean sWait = false;

    private static void hideCursor() {
        Cursor sEmptyCursor;
        assert(Mouse.isCreated());

        int min = org.lwjgl.input.Cursor.getMinCursorSize();
        IntBuffer tmp = BufferUtils.newIntBuffer(min * min);
        try {
            sEmptyCursor = new org.lwjgl.input.Cursor(min, min, min / 2, min / 2, 1, tmp, null);
        } catch (LWJGLException e) {
            e.printStackTrace();
            return;
        }

        try {
            Mouse.setNativeCursor(sEmptyCursor);
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "burgerparty";
        cfg.useGL20 = true;

        parseArgs(args);
        if (sFullScreen) {
            DisplayMode mode = LwjglApplicationConfiguration.getDesktopDisplayMode();
            cfg.width = mode.width;
            cfg.height = mode.height;
            cfg.fullscreen = true;
            cfg.vSyncEnabled = true;
        } else {
            cfg.width = 800;
            cfg.height = 480;
        }
        BurgerPartyGame game = new BurgerPartyGame();
        new LwjglApplication(game, cfg);

        // Must be done after creating the app, so that Gdx.app is not null
        setupLog();

        if (sHideCursor) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    hideCursor();
                }
            });
        }
        if (sWait) {
            game.waitInLoadingScreen();
        }
        game.setAdSystem(new DummyAdSystem());
    }

    private static void parseArgs(String[] _args) {
        Array<String> args = new Array<String>(_args);
        while (args.size > 0) {
            String arg = args.removeIndex(0);
            if (arg.equals("-h") || arg.equals("--help")) {
                usage();
            } else if (arg.equals("-f") || args.equals("--fullscreen")) {
                sFullScreen = true;
            } else if (arg.equals("--hide-cursor")) {
                sHideCursor = true;
            } else if (arg.equals("--wait")) {
                sWait = true;
            } else if (arg.equals("--locale")) {
                String locale = args.removeIndex(0);
                Translator.init(locale);
            } else {
                System.err.println("ERROR: Unknown argument: " + arg);
                System.exit(1);
            }
        }
    }

    private static void usage() {
        System.err.println("Usage: burgeparty [OPTIONS]\n"
            + "\n"
            + "Options:\n"
            + "  -h,--help        This screen\n"
            + "  -f,--fullscreen  Start in fullscreen mode\n"
            + "  --hide-cursor    Hide cursor\n"
            + "  --wait           Wait for a click on the loading screen to continue\n"
            + "  --locale LOCALE  Force usage of LOCALE\n"
        );
        System.exit(1);
    }

    private static FileLogPrinter createFileLogPrinter() {
        FileHandle cacheDir = FileUtils.getCacheDir("burgerparty");
        if (cacheDir == null) {
            Gdx.app.error("createFileLogPrinter", "Could not create cache dir");
            return null;
        }
        FileHandle logHandle = cacheDir.child("burgerparty.log");
        FileLogPrinter.rotate(logHandle, 6);
        return new FileLogPrinter(logHandle);
    }

    private static void setupLog() {
        NLog.Printer printer = createFileLogPrinter();
        if (printer != null) {
            NLog.addPrinter(printer);
        }
        NLog.addPrinter(new GdxPrinter());
    }
}
