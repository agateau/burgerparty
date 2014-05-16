package com.agateau.burgerparty;

import java.nio.IntBuffer;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.utils.BufferUtils;

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
        BurgerPartyGame.setupLog();
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

    private static void parseArgs(String[] args) {
        for (int idx = 0, n = args.length; idx < n; ++idx) {
            String arg = args[idx];
            if (arg.equals("-f") || args.equals("--fullscreen")) {
                sFullScreen = true;
            } else if (arg.equals("--hide-cursor")) {
                sHideCursor = true;
            } else if (arg.equals("--wait")) {
                sWait = true;
            } else if (arg.equals("-h") || arg.equals("--help")) {
                usage();
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
        );
        System.exit(1);
    }
}
